package br.ufs.sigea.academic.activity.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.dto.ActivityCreateDTO;
import br.ufs.sigea.academic.activity.dto.ActivityDetailDTO;
import br.ufs.sigea.academic.activity.dto.ActivityResponseDTO;
import br.ufs.sigea.academic.activity.dto.ActivityUpdateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionResponseDTO;
import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de regras de negócio para Atividades Avaliativas com Casos Clínicos Simulados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AcademicClassRepository classRepository;
    private final ActivitySubmissionRepository submissionRepository;

    /**
     * Cria uma nova atividade com caso clínico na turma.
     */
    @Transactional
    public ActivityDetailDTO createActivity(ActivityCreateDTO dto, User currentUser) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + dto.getClassId()));

        validateProfessorOrAdmin(academicClass, currentUser);

        if (Boolean.TRUE.equals(academicClass.getIsClosed())) {
            throw new BusinessException("Não é possível criar atividades em uma turma já encerrada.");
        }

        if (dto.getDeadline().isBefore(Instant.now())) {
            throw new BusinessException("O prazo de entrega da atividade deve ser uma data futura.");
        }

        Activity activity = Activity.builder()
                .academicClass(academicClass)
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .clinicalCaseData(dto.getClinicalCaseData())
                .deadline(dto.getDeadline())
                .build();

        Activity saved = activityRepository.save(activity);
        log.info("Atividade criada: id={}, título={}, turma={}", saved.getId(), saved.getTitle(), academicClass.getFormattedName());
        return toDetailDTO(saved, null);
    }

    /**
     * Atualiza dados de uma atividade existente.
     */
    @Transactional
    public ActivityDetailDTO updateActivity(UUID id, ActivityUpdateDTO dto, User currentUser) {
        Activity activity = activityRepository.findByIdWithClass(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + id));

        validateProfessorOrAdmin(activity.getAcademicClass(), currentUser);

        activity.setTitle(dto.getTitle().trim());
        activity.setDescription(dto.getDescription().trim());
        activity.setClinicalCaseData(dto.getClinicalCaseData());
        activity.setDeadline(dto.getDeadline());

        Activity updated = activityRepository.save(activity);
        log.info("Atividade atualizada: id={}, título={}", updated.getId(), updated.getTitle());
        return toDetailDTO(updated, null);
    }

    /**
     * Obtém os detalhes de uma atividade, anexando a submissão do estudante se aplicável.
     */
    @Transactional(readOnly = true)
    public ActivityDetailDTO getActivityById(UUID id, User currentUser) {
        Activity activity = activityRepository.findByIdWithClass(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + id));

        SubmissionResponseDTO studentSubmissionDTO = null;
        if (currentUser != null && currentUser.getRole() == UserRole.STUDENT) {
            Optional<ActivitySubmission> sub = submissionRepository.findByActivityIdAndStudentId(id, currentUser.getId());
            studentSubmissionDTO = sub.map(this::toSubmissionResponseDTO).orElse(null);
        }

        return toDetailDTO(activity, studentSubmissionDTO);
    }

    /**
     * Lista atividades vinculadas a uma turma acadêmica.
     */
    @Transactional(readOnly = true)
    public Page<ActivityResponseDTO> listActivitiesByClass(UUID classId, Pageable pageable) {
        return activityRepository.findByAcademicClassId(classId, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Exclui uma atividade.
     */
    @Transactional
    public void deleteActivity(UUID id, User currentUser) {
        Activity activity = activityRepository.findByIdWithClass(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + id));

        validateProfessorOrAdmin(activity.getAcademicClass(), currentUser);

        activityRepository.delete(activity);
        log.info("Atividade {} excluída com sucesso", id);
    }

    private void validateProfessorOrAdmin(AcademicClass academicClass, User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isClassProfessor = academicClass.getProfessor().getId().equals(currentUser.getId());

        if (!isAdmin && !isClassProfessor) {
            throw new AccessDeniedException("Apenas o professor titular da turma ou um administrador podem gerenciar atividades.");
        }
    }

    public ActivityResponseDTO toResponseDTO(Activity a) {
        long subCount = submissionRepository.countByActivityId(a.getId());
        boolean isExpired = Instant.now().isAfter(a.getDeadline());

        return ActivityResponseDTO.builder()
                .id(a.getId())
                .classId(a.getAcademicClass().getId())
                .className(a.getAcademicClass().getFormattedName())
                .title(a.getTitle())
                .description(a.getDescription())
                .deadline(a.getDeadline())
                .isExpired(isExpired)
                .submissionCount((int) subCount)
                .createdAt(a.getCreatedAt())
                .build();
    }

    public ActivityDetailDTO toDetailDTO(Activity a, SubmissionResponseDTO studentSubmission) {
        long subCount = submissionRepository.countByActivityId(a.getId());
        boolean isExpired = Instant.now().isAfter(a.getDeadline());

        return ActivityDetailDTO.builder()
                .id(a.getId())
                .classId(a.getAcademicClass().getId())
                .className(a.getAcademicClass().getFormattedName())
                .title(a.getTitle())
                .description(a.getDescription())
                .clinicalCaseData(a.getClinicalCaseData())
                .deadline(a.getDeadline())
                .isExpired(isExpired)
                .submissionCount((int) subCount)
                .createdAt(a.getCreatedAt())
                .studentSubmission(studentSubmission)
                .build();
    }

    private SubmissionResponseDTO toSubmissionResponseDTO(ActivitySubmission s) {
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
