package br.ufs.sigea.academic.activity.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivitySubmissionServiceTest {

    @Mock
    private ActivitySubmissionRepository submissionRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivitySubmissionService submissionService;

    private UUID activityId;
    private UUID classId;
    private UUID profId;
    private UUID studentId;
    private UUID submissionId;

    private User professor;
    private User student;
    private User admin;
    private AcademicClass sampleClass;
    private Activity sampleActivity;
    private ActivitySubmission sampleSubmission;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();
        classId = UUID.randomUUID();
        profId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        submissionId = UUID.randomUUID();

        professor = User.builder()
                .id(profId)
                .fullName("Prof. Waleska")
                .email("waleska@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        student = User.builder()
                .id(studentId)
                .fullName("Aluno Silva")
                .email("aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("2026001")
                .build();

        admin = User.builder()
                .id(UUID.randomUUID())
                .fullName("Admin")
                .email("admin@academico.ufs.br")
                .role(UserRole.ADMIN)
                .build();

        sampleClass = AcademicClass.builder()
                .id(classId)
                .subjectName("Enfermagem")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professor(professor)
                .students(new HashSet<>(Set.of(student)))
                .isClosed(false)
                .build();

        sampleActivity = Activity.builder()
                .id(activityId)
                .academicClass(sampleClass)
                .title("Estudo 1")
                .deadline(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        sampleSubmission = ActivitySubmission.builder()
                .id(submissionId)
                .activity(sampleActivity)
                .student(student)
                .submissionDate(Instant.now())
                .identifiedTriggers(new ArrayList<>())
                .qualityToolsData(new QualityToolsData())
                .build();
    }

    @Test
    @DisplayName("Deve submeter atividade pela primeira vez com sucesso")
    void shouldSubmitActivityFirstTime() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder()
                .identifiedTriggers(List.of(IdentifiedTriggerData.builder().triggerCode("C1").build()))
                .qualityToolsData(new QualityToolsData())
                .build();

        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityIdAndStudentId(activityId, studentId)).thenReturn(Optional.empty());
        when(submissionRepository.save(any(ActivitySubmission.class))).thenAnswer(inv -> {
            ActivitySubmission s = inv.getArgument(0);
            s.setId(submissionId);
            return s;
        });

        SubmissionResponseDTO result = submissionService.submitActivity(activityId, dto, student);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(submissionId);
        verify(submissionRepository).save(any(ActivitySubmission.class));
    }

    @Test
    @DisplayName("Deve atualizar submissão não corrigida")
    void shouldUpdateUnGradedSubmission() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder()
                .identifiedTriggers(List.of(IdentifiedTriggerData.builder().triggerCode("C2").build()))
                .qualityToolsData(new QualityToolsData())
                .build();

        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityIdAndStudentId(activityId, studentId)).thenReturn(Optional.of(sampleSubmission));
        when(submissionRepository.save(any(ActivitySubmission.class))).thenReturn(sampleSubmission);

        SubmissionResponseDTO result = submissionService.submitActivity(activityId, dto, student);

        assertThat(result).isNotNull();
        assertThat(sampleSubmission.getIdentifiedTriggers()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se usuário não for STUDENT ao submeter")
    void shouldThrowWhenUserNotStudentOnSubmit() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, professor))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Apenas estudantes podem enviar resoluções");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se atividade não existir ao submeter")
    void shouldThrowWhenActivityNotFoundOnSubmit() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, student))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar BusinessException se a turma estiver encerrada ao submeter")
    void shouldThrowWhenClassClosedOnSubmit() {
        sampleClass.setIsClosed(true);
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, student))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("encontra-se encerrada");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se aluno não estiver matriculado na turma")
    void shouldThrowWhenStudentNotEnrolled() {
        User notEnrolled = User.builder().id(UUID.randomUUID()).role(UserRole.STUDENT).build();
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, notEnrolled))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não está matriculado na turma");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o prazo da atividade já expirou")
    void shouldThrowWhenDeadlineExpiredOnSubmit() {
        sampleActivity.setDeadline(Instant.now().minus(1, ChronoUnit.HOURS));
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, student))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("prazo limite para entrega desta atividade já expirou");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se a submissão já tiver sido avaliada pelo professor")
    void shouldThrowWhenSubmissionAlreadyGraded() {
        sampleSubmission.setGrade(new BigDecimal("8.00"));
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();

        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityIdAndStudentId(activityId, studentId)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, student))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já foi avaliada pelo professor e não pode ser reenviada");
    }

    @Test
    @DisplayName("Deve avaliar submissão com nota e parecer pedagógico")
    void shouldGradeSubmissionSuccessfully() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder()
                .grade(new BigDecimal("9.50"))
                .professorFeedback("  Ótima fundamentação no Ishikawa.  ")
                .build();

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));
        when(submissionRepository.save(sampleSubmission)).thenReturn(sampleSubmission);

        SubmissionResponseDTO result = submissionService.gradeSubmission(submissionId, dto, professor);

        assertThat(result).isNotNull();
        assertThat(sampleSubmission.getGrade()).isEqualByComparingTo("9.50");
        assertThat(sampleSubmission.getProfessorFeedback()).isEqualTo("Ótima fundamentação no Ishikawa.");
    }

    @Test
    @DisplayName("Deve avaliar submissão com feedback nulo")
    void shouldGradeSubmissionWithNullFeedback() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder()
                .grade(new BigDecimal("10.00"))
                .professorFeedback(null)
                .build();

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));
        when(submissionRepository.save(sampleSubmission)).thenReturn(sampleSubmission);

        SubmissionResponseDTO result = submissionService.gradeSubmission(submissionId, dto, professor);

        assertThat(result).isNotNull();
        assertThat(sampleSubmission.getProfessorFeedback()).isNull();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se submissão não encontrada ao avaliar")
    void shouldThrowWhenSubmissionNotFoundOnGrade() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder().grade(new BigDecimal("7.00")).build();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.gradeSubmission(submissionId, dto, professor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se outro docente tentar avaliar")
    void shouldThrowWhenOtherProfessorAttemptsToGrade() {
        User otherProf = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder().grade(new BigDecimal("7.00")).build();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.gradeSubmission(submissionId, dto, otherProf))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Apenas o professor titular da turma ou um administrador");
    }

    @Test
    @DisplayName("Deve permitir ADMIN avaliar qualquer submissão")
    void shouldAllowAdminToGrade() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder().grade(new BigDecimal("8.00")).build();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));
        when(submissionRepository.save(sampleSubmission)).thenReturn(sampleSubmission);

        SubmissionResponseDTO result = submissionService.gradeSubmission(submissionId, dto, admin);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar BusinessException se nota for menor que 0 ou maior que 10")
    void shouldThrowWhenGradeOutOfRange() {
        SubmissionGradeDTO lowGrade = SubmissionGradeDTO.builder().grade(new BigDecimal("-0.10")).build();
        SubmissionGradeDTO highGrade = SubmissionGradeDTO.builder().grade(new BigDecimal("10.01")).build();

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.gradeSubmission(submissionId, lowGrade, professor))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("intervalo entre 0.00 e 10.00");

        assertThatThrownBy(() -> submissionService.gradeSubmission(submissionId, highGrade, professor))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("intervalo entre 0.00 e 10.00");
    }

    @Test
    @DisplayName("Deve buscar submissão por ID quando estudante for o dono")
    void shouldGetSubmissionByIdAsStudentOwner() {
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        SubmissionResponseDTO result = submissionService.getSubmissionById(submissionId, student);
        assertThat(result.getId()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("Deve buscar submissão por ID quando for o professor da turma")
    void shouldGetSubmissionByIdAsProfessor() {
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        SubmissionResponseDTO result = submissionService.getSubmissionById(submissionId, professor);
        assertThat(result.getId()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("Deve buscar submissão por ID quando for ADMIN")
    void shouldGetSubmissionByIdAsAdmin() {
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        SubmissionResponseDTO result = submissionService.getSubmissionById(submissionId, admin);
        assertThat(result.getId()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao buscar submissão de outro estudante")
    void shouldThrowWhenOtherStudentAttemptsToViewSubmission() {
        User otherStudent = User.builder().id(UUID.randomUUID()).role(UserRole.STUDENT).build();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.getSubmissionById(submissionId, otherStudent))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não possui permissão");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar submissão inexistente por ID")
    void shouldThrowWhenSubmissionNotFoundById() {
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.getSubmissionById(submissionId, student))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar submissões por atividade para o professor")
    void shouldListSubmissionsByActivity() {
        Pageable pageable = PageRequest.of(0, 10);
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityId(activityId, pageable))
                .thenReturn(new PageImpl<>(List.of(sampleSubmission), pageable, 1));

        Page<SubmissionResponseDTO> result = submissionService.listSubmissionsByActivity(activityId, professor, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar submissões do próprio estudante")
    void shouldListSubmissionsByStudentAsSelf() {
        Pageable pageable = PageRequest.of(0, 10);
        when(submissionRepository.findByStudentId(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(sampleSubmission), pageable, 1));

        Page<SubmissionResponseDTO> result = submissionService.listSubmissionsByStudent(studentId, student, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve permitir docente consultar submissões de um estudante")
    void shouldListSubmissionsByStudentAsProfessor() {
        Pageable pageable = PageRequest.of(0, 10);
        when(submissionRepository.findByStudentId(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(sampleSubmission), pageable, 1));

        Page<SubmissionResponseDTO> result = submissionService.listSubmissionsByStudent(studentId, professor, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve permitir administrador listar submissões de qualquer estudante")
    void shouldListSubmissionsByStudentAsAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        when(submissionRepository.findByStudentId(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(sampleSubmission), pageable, 1));

        Page<SubmissionResponseDTO> result = submissionService.listSubmissionsByStudent(studentId, admin, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se outro estudante tentar listar submissões")
    void shouldThrowWhenOtherStudentListsSubmissions() {
        User otherStudent = User.builder().id(UUID.randomUUID()).role(UserRole.STUDENT).build();
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> submissionService.listSubmissionsByStudent(studentId, otherStudent, pageable))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não possui autorização");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao listar submissões de atividade inexistente")
    void shouldThrowWhenActivityNotFoundOnListSubmissions() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.listSubmissionsByActivity(activityId, professor, PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se estudante for nulo ao submeter")
    void shouldThrowWhenStudentNullOnSubmit() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();

        assertThatThrownBy(() -> submissionService.submitActivity(activityId, dto, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Apenas estudantes podem enviar resoluções");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao avaliar com docente nulo")
    void shouldThrowWhenProfessorNullOnGrade() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder().grade(BigDecimal.ONE).build();
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.gradeSubmission(submissionId, dto, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário não autenticado");
    }
}
