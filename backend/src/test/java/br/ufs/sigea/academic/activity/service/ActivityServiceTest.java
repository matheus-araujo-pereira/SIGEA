package br.ufs.sigea.academic.activity.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.activity.dto.ActivityCreateDTO;
import br.ufs.sigea.academic.activity.dto.ActivityDetailDTO;
import br.ufs.sigea.academic.activity.dto.ActivityResponseDTO;
import br.ufs.sigea.academic.activity.dto.ActivityUpdateDTO;
import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AcademicClassRepository classRepository;

    @Mock
    private ActivitySubmissionRepository submissionRepository;

    @InjectMocks
    private ActivityService activityService;

    private UUID classId;
    private UUID activityId;
    private UUID profId;
    private UUID studentId;
    private User professor;
    private User student;
    private User admin;
    private AcademicClass sampleClass;
    private Activity sampleActivity;
    private ClinicalCaseData sampleClinicalCase;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        activityId = UUID.randomUUID();
        profId = UUID.randomUUID();
        studentId = UUID.randomUUID();

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
                .isClosed(false)
                .build();

        sampleClinicalCase = ClinicalCaseData.builder()
                .patientName("Maria Souza")
                .age(64)
                .gender("F")
                .patientDays(6)
                .admissionNotes("Pneumonia Comunitária")
                .build();

        sampleActivity = Activity.builder()
                .id(activityId)
                .academicClass(sampleClass)
                .title("Estudo de Caso 1")
                .description("Identifique os gatilhos e aplique ferramentas")
                .clinicalCaseData(sampleClinicalCase)
                .deadline(Instant.now().plus(7, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar atividade com sucesso quando usuário for o professor titular")
    void shouldCreateActivityAsProfessor() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .classId(classId)
                .title("Estudo de Caso 1")
                .description("Descrição")
                .clinicalCaseData(sampleClinicalCase)
                .deadline(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> {
            Activity a = inv.getArgument(0);
            a.setId(activityId);
            return a;
        });

        ActivityDetailDTO result = activityService.createActivity(dto, professor);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Estudo de Caso 1");
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve criar atividade com sucesso quando usuário for administrador")
    void shouldCreateActivityAsAdmin() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .classId(classId)
                .title("Estudo de Caso 1")
                .description("Descrição")
                .clinicalCaseData(sampleClinicalCase)
                .deadline(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(activityRepository.save(any(Activity.class))).thenReturn(sampleActivity);

        ActivityDetailDTO result = activityService.createActivity(dto, admin);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar atividade para turma inexistente")
    void shouldThrowWhenClassNotFoundOnCreate() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .classId(classId)
                .title("Atividade")
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.createActivity(dto, professor))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma acadêmica não encontrada");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se usuário não autenticado")
    void shouldThrowWhenUserNotAuthenticated() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder().classId(classId).build();
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> activityService.createActivity(dto, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário não autenticado");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se usuário não for o titular da turma nem admin")
    void shouldThrowWhenUserNotClassProfessorOrAdmin() {
        User anotherProf = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();
        ActivityCreateDTO dto = ActivityCreateDTO.builder().classId(classId).build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> activityService.createActivity(dto, anotherProf))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Apenas o professor titular da turma ou um administrador");
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar atividade em turma encerrada")
    void shouldThrowWhenCreatingInClosedClass() {
        sampleClass.setIsClosed(true);
        ActivityCreateDTO dto = ActivityCreateDTO.builder().classId(classId).build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> activityService.createActivity(dto, professor))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não é possível criar atividades em uma turma já encerrada");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o prazo for uma data no passado")
    void shouldThrowWhenDeadlineInPast() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .classId(classId)
                .title("Atividade")
                .description("Desc")
                .clinicalCaseData(sampleClinicalCase)
                .deadline(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> activityService.createActivity(dto, professor))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("deve ser uma data futura");
    }

    @Test
    @DisplayName("Deve atualizar atividade com sucesso")
    void shouldUpdateActivitySuccessfully() {
        ActivityUpdateDTO dto = ActivityUpdateDTO.builder()
                .title("Estudo Atualizado")
                .description("Nova Descrição")
                .clinicalCaseData(sampleClinicalCase)
                .deadline(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();

        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(activityRepository.save(sampleActivity)).thenReturn(sampleActivity);

        ActivityDetailDTO result = activityService.updateActivity(activityId, dto, professor);

        assertThat(result).isNotNull();
        assertThat(sampleActivity.getTitle()).isEqualTo("Estudo Atualizado");
        assertThat(sampleActivity.getDescription()).isEqualTo("Nova Descrição");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar atividade inexistente")
    void shouldThrowWhenUpdatingNonExistentActivity() {
        ActivityUpdateDTO dto = ActivityUpdateDTO.builder().title("T").build();
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.updateActivity(activityId, dto, professor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve buscar detalhes da atividade como estudante com submissão realizada")
    void shouldGetActivityDetailForStudentWithSubmission() {
        ActivitySubmission submission = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(sampleActivity)
                .student(student)
                .submissionDate(Instant.now())
                .grade(new BigDecimal("9.50"))
                .professorFeedback("Excelente trabalho")
                .gradedAt(Instant.now())
                .build();

        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityIdAndStudentId(activityId, studentId))
                .thenReturn(Optional.of(submission));

        ActivityDetailDTO result = activityService.getActivityById(activityId, student);

        assertThat(result).isNotNull();
        assertThat(result.getStudentSubmission()).isNotNull();
        assertThat(result.getStudentSubmission().getGrade()).isEqualByComparingTo("9.50");
        assertThat(result.getStudentSubmission().getIsGraded()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar detalhes da atividade como estudante sem submissão")
    void shouldGetActivityDetailForStudentWithoutSubmission() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));
        when(submissionRepository.findByActivityIdAndStudentId(activityId, studentId))
                .thenReturn(Optional.empty());

        ActivityDetailDTO result = activityService.getActivityById(activityId, student);

        assertThat(result).isNotNull();
        assertThat(result.getStudentSubmission()).isNull();
    }

    @Test
    @DisplayName("Deve buscar detalhes da atividade para docente ou admin (sem consultar submissão de aluno)")
    void shouldGetActivityDetailForProfessor() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        ActivityDetailDTO result = activityService.getActivityById(activityId, professor);

        assertThat(result).isNotNull();
        assertThat(result.getStudentSubmission()).isNull();
    }

    @Test
    @DisplayName("Deve buscar detalhes da atividade quando currentUser for nulo")
    void shouldGetActivityDetailWhenCurrentUserNull() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        ActivityDetailDTO result = activityService.getActivityById(activityId, null);

        assertThat(result).isNotNull();
        assertThat(result.getStudentSubmission()).isNull();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar atividade inexistente por ID")
    void shouldThrowWhenActivityNotFoundById() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.getActivityById(activityId, professor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar atividades por turma de forma paginada")
    void shouldListActivitiesByClass() {
        Pageable pageable = PageRequest.of(0, 10);
        when(activityRepository.findByAcademicClassId(classId, pageable))
                .thenReturn(new PageImpl<>(List.of(sampleActivity), pageable, 1));
        when(submissionRepository.countByActivityId(activityId)).thenReturn(5L);

        Page<ActivityResponseDTO> result = activityService.listActivitiesByClass(classId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSubmissionCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve excluir atividade com sucesso")
    void shouldDeleteActivitySuccessfully() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.of(sampleActivity));

        activityService.deleteActivity(activityId, professor);

        verify(activityRepository).delete(sampleActivity);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao excluir atividade inexistente")
    void shouldThrowWhenDeletingNonExistentActivity() {
        when(activityRepository.findByIdWithClass(activityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.deleteActivity(activityId, professor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve verificar flag isExpired quando atividade estiver expirada")
    void shouldIdentifyExpiredActivity() {
        sampleActivity.setDeadline(Instant.now().minus(2, ChronoUnit.HOURS));

        ActivityResponseDTO resp = activityService.toResponseDTO(sampleActivity);
        assertThat(resp.getIsExpired()).isTrue();

        ActivityDetailDTO detail = activityService.toDetailDTO(sampleActivity, null);
        assertThat(detail.getIsExpired()).isTrue();
    }
}
