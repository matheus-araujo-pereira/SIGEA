package br.ufs.sigea.academic.activity.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.dto.SubmissionCreateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionGradeDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionResponseDTO;
import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de regras de negócio para submissão de resoluções de atividades e avaliação pedagógica docente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivitySubmissionService {

    private final ActivitySubmissionRepository submissionRepository;
    private final ActivityRepository activityRepository;

    /**
     * Registra ou atualiza a submissão de uma atividade pelo estudante matriculado.
     */
    @Transactional
    public SubmissionResponseDTO submitActivity(UUID activityId, SubmissionCreateDTO dto, User currentStudent) {
        if (currentStudent == null || currentStudent.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Apenas estudantes podem enviar resoluções de atividades.");
        }

        Activity activity = activityRepository.findByIdWithClass(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + activityId));

        AcademicClass academicClass = activity.getAcademicClass();

        if (Boolean.TRUE.equals(academicClass.getIsClosed())) {
            throw new BusinessException("A turma desta atividade encontra-se encerrada e não aceita novos envios.");
        }

        boolean isEnrolled = academicClass.getStudents().stream()
                .anyMatch(s -> s.getId().equals(currentStudent.getId()));

        if (!isEnrolled) {
            throw new BusinessException("O estudante não está matriculado na turma desta atividade.");
        }

        if (Instant.now().isAfter(activity.getDeadline())) {
            throw new BusinessException("O prazo limite para entrega desta atividade já expirou.");
        }

        Optional<ActivitySubmission> existingOpt = submissionRepository.findByActivityIdAndStudentId(activityId, currentStudent.getId());

        ActivitySubmission submission;
        if (existingOpt.isPresent()) {
            submission = existingOpt.get();
            if (submission.isGraded()) {
                throw new BusinessException("Esta atividade já foi avaliada pelo professor e não pode ser reenviada.");
            }
            submission.setIdentifiedTriggers(dto.getIdentifiedTriggers());
            submission.setQualityToolsData(dto.getQualityToolsData());
            submission.setSubmissionDate(Instant.now());
            log.info("Submissão atualizada: id={}, aluno={}", submission.getId(), currentStudent.getEmail());
        } else {
            submission = ActivitySubmission.builder()
                    .activity(activity)
                    .student(currentStudent)
                    .identifiedTriggers(dto.getIdentifiedTriggers())
                    .qualityToolsData(dto.getQualityToolsData())
                    .submissionDate(Instant.now())
                    .build();
            log.info("Nova submissão criada: atividade={}, aluno={}", activity.getTitle(), currentStudent.getEmail());
        }

        ActivitySubmission saved = submissionRepository.save(submission);
        return toDTO(saved);
    }

    /**
     * Atribui nota e parecer pedagógico a uma submissão de estudante.
     */
    @Transactional
    public SubmissionResponseDTO gradeSubmission(UUID submissionId, SubmissionGradeDTO dto, User currentProfessor) {
        ActivitySubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada: " + submissionId));

        AcademicClass academicClass = submission.getActivity().getAcademicClass();
        validateProfessorOrAdmin(academicClass, currentProfessor);

        BigDecimal grade = dto.getGrade();
        if (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(BigDecimal.valueOf(10.0)) > 0) {
            throw new BusinessException("A nota atribuída deve estar no intervalo entre 0.00 e 10.00.");
        }

        submission.setGrade(grade);
        submission.setProfessorFeedback(dto.getProfessorFeedback() != null ? dto.getProfessorFeedback().trim() : null);
        submission.setGradedAt(Instant.now());

        ActivitySubmission saved = submissionRepository.save(submission);
        log.info("Submissão {} avaliada com nota {} pelo professor {}", submissionId, grade, currentProfessor.getEmail());
        return toDTO(saved);
    }

    /**
     * Obtém os detalhes de uma submissão por ID com verificação de autorização.
     */
    @Transactional(readOnly = true)
    public SubmissionResponseDTO getSubmissionById(UUID id, User currentUser) {
        ActivitySubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada: " + id));

        boolean isStudentOwner = submission.getStudent().getId().equals(currentUser.getId());
        boolean isClassProfessor = submission.getActivity().getAcademicClass().getProfessor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isStudentOwner && !isClassProfessor && !isAdmin) {
            throw new AccessDeniedException("Você não possui permissão para visualizar esta submissão.");
        }

        return toDTO(submission);
    }

    /**
     * Lista todas as submissões enviadas para uma determinada atividade.
     */
    @Transactional(readOnly = true)
    public Page<SubmissionResponseDTO> listSubmissionsByActivity(UUID activityId, User currentUser, Pageable pageable) {
        Activity activity = activityRepository.findByIdWithClass(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + activityId));

        validateProfessorOrAdmin(activity.getAcademicClass(), currentUser);

        return submissionRepository.findByActivityId(activityId, pageable)
                .map(this::toDTO);
    }

    /**
     * Lista todas as submissões de um estudante.
     */
    @Transactional(readOnly = true)
    public Page<SubmissionResponseDTO> listSubmissionsByStudent(UUID studentId, User currentUser, Pageable pageable) {
        boolean isSelf = currentUser.getId().equals(studentId);
        boolean isStaff = currentUser.getRole() == UserRole.PROFESSOR || currentUser.getRole() == UserRole.ADMIN;

        if (!isSelf && !isStaff) {
            throw new AccessDeniedException("Você não possui autorização para consultar as submissões deste estudante.");
        }

        return submissionRepository.findByStudentId(studentId, pageable)
                .map(this::toDTO);
    }

    private void validateProfessorOrAdmin(AcademicClass academicClass, User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isClassProfessor = academicClass.getProfessor().getId().equals(currentUser.getId());

        if (!isAdmin && !isClassProfessor) {
            throw new AccessDeniedException("Apenas o professor titular da turma ou um administrador podem avaliar ou visualizar estas submissões.");
        }
    }

    public SubmissionResponseDTO toDTO(ActivitySubmission s) {
        return SubmissionResponseDTO.builder()
                .id(s.getId())
                .activityId(s.getActivity().getId())
                .activityTitle(s.getActivity().getTitle())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getFullName())
                .studentEmail(s.getStudent().getEmail())
                .studentRegistrationNumber(s.getStudent().getRegistrationNumber())
                .identifiedTriggers(s.getIdentifiedTriggers())
                .qualityToolsData(s.getQualityToolsData())
                .submissionDate(s.getSubmissionDate())
                .grade(s.getGrade())
                .professorFeedback(s.getProfessorFeedback())
                .gradedAt(s.getGradedAt())
                .isGraded(s.isGraded())
                .build();
    }
}
