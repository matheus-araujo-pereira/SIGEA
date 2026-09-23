package br.ufs.sigea.academic.clazz.service;

import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCreateDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassDetailDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassResponseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassUpdateDTO;
import br.ufs.sigea.academic.clazz.dto.ClassStudentSummaryDTO;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de regras de negócio para gestão de Turmas Acadêmicas no SIGEA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicClassService {

    private final AcademicClassRepository classRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySubmissionRepository submissionRepository;

    /**
     * Lista turmas com filtros administrativos e paginação.
     */
    @Transactional(readOnly = true)
    public Page<AcademicClassResponseDTO> listClasses(
            String search,
            String academicPeriod,
            Boolean isClosed,
            Pageable pageable
    ) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanPeriod = (academicPeriod != null && !academicPeriod.trim().isEmpty()) ? academicPeriod.trim() : null;

        return classRepository.findByFilters(cleanSearch, cleanPeriod, isClosed, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Obtém os detalhes completos de uma turma, incluindo estudantes matriculados.
     */
    @Transactional(readOnly = true)
    public AcademicClassDetailDTO getClassById(UUID id) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada com o ID informado: " + id));

        return toDetailDTO(academicClass);
    }

    /**
     * Lista turmas associadas a um professor titular.
     */
    @Transactional(readOnly = true)
    public Page<AcademicClassResponseDTO> listClassesByProfessor(UUID professorId, Pageable pageable) {
        return classRepository.findByProfessorId(professorId, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Lista turmas nas quais um estudante está matriculado.
     */
    @Transactional(readOnly = true)
    public Page<AcademicClassResponseDTO> listClassesByStudent(UUID studentId, Pageable pageable) {
        return classRepository.findByStudentId(studentId, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Cadastra uma nova turma acadêmica com professor obrigatório e estudantes validados.
     */
    @Transactional
    public AcademicClassDetailDTO createClass(AcademicClassCreateDTO dto) {
        String cleanSubject = dto.getSubjectName().trim();
        String cleanCode = dto.getClassCode().trim().toUpperCase();
        String cleanPeriod = dto.getAcademicPeriod().trim();

        if (classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                cleanSubject, cleanCode, cleanPeriod)) {
            throw new BusinessException("Já existe uma turma cadastrada com esta disciplina, código e período letivo.");
        }

        User professor = validateAndGetProfessor(dto.getProfessorId());
        Set<User> students = validateAndGetStudents(dto.getStudentIds());

        AcademicClass academicClass = AcademicClass.builder()
                .subjectName(cleanSubject)
                .classCode(cleanCode)
                .academicPeriod(cleanPeriod)
                .professor(professor)
                .students(students)
                .isClosed(false)
                .build();

        AcademicClass saved = classRepository.save(academicClass);
        log.info("Turma criada com sucesso: id={}, nome={}", saved.getId(), saved.getFormattedName());
        return toDetailDTO(saved);
    }

    /**
     * Atualiza dados de uma turma, permitindo substituição do docente e gestão de alunos.
     */
    @Transactional
    public AcademicClassDetailDTO updateClass(UUID id, AcademicClassUpdateDTO dto) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + id));

        String cleanSubject = dto.getSubjectName().trim();
        String cleanCode = dto.getClassCode().trim().toUpperCase();
        String cleanPeriod = dto.getAcademicPeriod().trim();

        boolean changedIdentifier = !academicClass.getSubjectName().equalsIgnoreCase(cleanSubject) ||
                                    !academicClass.getClassCode().equalsIgnoreCase(cleanCode) ||
                                    !academicClass.getAcademicPeriod().equalsIgnoreCase(cleanPeriod);

        if (changedIdentifier && classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                cleanSubject, cleanCode, cleanPeriod)) {
            throw new BusinessException("Já existe outra turma com esta disciplina, código e período letivo.");
        }

        User professor = validateAndGetProfessor(dto.getProfessorId());
        Set<User> students = validateAndGetStudents(dto.getStudentIds());

        academicClass.setSubjectName(cleanSubject);
        academicClass.setClassCode(cleanCode);
        academicClass.setAcademicPeriod(cleanPeriod);
        academicClass.setProfessor(professor);
        academicClass.setStudents(students);

        AcademicClass updated = classRepository.save(academicClass);
        log.info("Turma atualizada com sucesso: id={}, nome={}", updated.getId(), updated.getFormattedName());
        return toDetailDTO(updated);
    }

    /**
     * Encerra ou reabre uma turma acadêmica com validação de correções pendentes.
     */
    @Transactional
    public AcademicClassResponseDTO updateClassClosedStatus(UUID id, Boolean isClosed) {
        AcademicClass academicClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + id));

        if (Boolean.TRUE.equals(isClosed)) {
            long pendingGrading = submissionRepository.countUngradedSubmissionsByClassId(id);
            if (pendingGrading > 0) {
                throw new BusinessException(String.format(
                        "A turma não pode ser encerrada pois possui %d atividade(s) submetida(s) com correção pendente.",
                        pendingGrading
                ));
            }
        }

        academicClass.setIsClosed(isClosed);
        AcademicClass saved = classRepository.save(academicClass);
        log.info("Status de encerramento da turma {} alterado para isClosed={}", id, isClosed);
        return toResponseDTO(saved);
    }

    /**
     * Exclui uma turma acadêmica.
     */
    @Transactional
    public void deleteClass(UUID id) {
        AcademicClass academicClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + id));

        classRepository.delete(academicClass);
        log.info("Turma {} excluída com sucesso", id);
    }

    private User validateAndGetProfessor(UUID professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor titular não encontrado com o ID: " + professorId));

        if (professor.getRole() != UserRole.PROFESSOR && professor.getRole() != UserRole.ADMIN) {
            throw new BusinessException("O usuário selecionado para lecionar a turma deve possuir perfil de PROFESSOR ou ADMIN.");
        }
        return professor;
    }

    private Set<User> validateAndGetStudents(Set<UUID> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return new HashSet<>();
        }

        List<User> users = userRepository.findAllById(studentIds);
        for (User user : users) {
            if (user.getRole() != UserRole.STUDENT) {
                throw new BusinessException(String.format(
                        "O usuário %s (%s) não possui perfil de STUDENT e não pode ser matriculado na turma.",
                        user.getFullName(), user.getEmail()
                ));
            }
        }
        return new HashSet<>(users);
    }

    private AcademicClassResponseDTO toResponseDTO(AcademicClass c) {
        long actCount = activityRepository.countByAcademicClassId(c.getId());
        int studentCount = (c.getStudents() != null) ? c.getStudents().size() : 0;

        return AcademicClassResponseDTO.builder()
                .id(c.getId())
                .subjectName(c.getSubjectName())
                .classCode(c.getClassCode())
                .academicPeriod(c.getAcademicPeriod())
                .formattedName(c.getFormattedName())
                .professorId(c.getProfessor().getId())
                .professorName(c.getProfessor().getFullName())
                .professorEmail(c.getProfessor().getEmail())
                .isClosed(c.getIsClosed())
                .studentCount(studentCount)
                .activityCount((int) actCount)
                .createdAt(c.getCreatedAt())
                .build();
    }

    private AcademicClassDetailDTO toDetailDTO(AcademicClass c) {
        long actCount = activityRepository.countByAcademicClassId(c.getId());
        List<ClassStudentSummaryDTO> students = (c.getStudents() != null)
                ? c.getStudents().stream()
                .map(s -> ClassStudentSummaryDTO.builder()
                        .id(s.getId())
                        .fullName(s.getFullName())
                        .email(s.getEmail())
                        .registrationNumber(s.getRegistrationNumber())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return AcademicClassDetailDTO.builder()
                .id(c.getId())
                .subjectName(c.getSubjectName())
                .classCode(c.getClassCode())
                .academicPeriod(c.getAcademicPeriod())
                .formattedName(c.getFormattedName())
                .professorId(c.getProfessor().getId())
                .professorName(c.getProfessor().getFullName())
                .professorEmail(c.getProfessor().getEmail())
                .isClosed(c.getIsClosed())
                .studentCount(students.size())
                .activityCount((int) actCount)
                .createdAt(c.getCreatedAt())
                .students(students)
                .build();
    }
}
