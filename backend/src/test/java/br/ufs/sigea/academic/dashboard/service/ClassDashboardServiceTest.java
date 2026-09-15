package br.ufs.sigea.academic.dashboard.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
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
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassDashboardServiceTest {

    @Mock
    private AcademicClassRepository classRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivitySubmissionRepository submissionRepository;

    @InjectMocks
    private ClassDashboardService dashboardService;

    private UUID classId;
    private UUID profId;
    private User professor;
    private User admin;
    private AcademicClass sampleClass;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        profId = UUID.randomUUID();

        professor = User.builder()
                .id(profId)
                .fullName("Prof. Waleska")
                .email("waleska@academico.ufs.br")
                .role(UserRole.PROFESSOR)
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
                .students(new HashSet<>())
                .isClosed(false)
                .build();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se turma não existir")
    void shouldThrowWhenClassNotFound() {
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.getClassDashboard(classId, professor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se usuário for nulo")
    void shouldThrowWhenCurrentUserNull() {
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> dashboardService.getClassDashboard(classId, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuário não autenticado");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException se usuário não for o titular nem admin")
    void shouldThrowWhenUserNotAuthorized() {
        User otherProf = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        assertThatThrownBy(() -> dashboardService.getClassDashboard(classId, otherProf))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Apenas o docente titular da turma ou um administrador");
    }

    @Test
    @DisplayName("Deve retornar dashboard com métricas zeradas quando não há submissões")
    void shouldReturnEmptyDashboardWhenNoSubmissions() {
        sampleClass.setStudents(null);
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(activityRepository.findByAcademicClassId(classId)).thenReturn(Collections.emptyList());
        when(submissionRepository.findByClassId(classId)).thenReturn(Collections.emptyList());

        ClassDashboardDTO dashboard = dashboardService.getClassDashboard(classId, professor);

        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getGttMetrics().getTotalAdmissions()).isEqualTo(0);
        assertThat(dashboard.getGttMetrics().getAdverseEventsPer1000PatientDays()).isEqualTo(0.0);
        assertThat(dashboard.getGttMetrics().getAdverseEventsPer100Admissions()).isEqualTo(0.0);
        assertThat(dashboard.getGttMetrics().getPercentAdmissionsWithAdverseEvents()).isEqualTo(0.0);
        assertThat(dashboard.getPedagogicalMetrics().getClassAverageGrade()).isEqualTo(0.0);
        assertThat(dashboard.getPedagogicalMetrics().getTotalEnrolledStudents()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve calcular corretamente métricas GTT e pedagógicas com submissões")
    void shouldCalculateGttAndPedagogicalMetrics() {
        User student1 = User.builder().id(UUID.randomUUID()).fullName("Aluno 1").build();
        User student2 = User.builder().id(UUID.randomUUID()).fullName("Aluno 2").build();
        sampleClass.setStudents(new HashSet<>(Set.of(student1, student2)));

        Activity act1 = Activity.builder()
                .id(UUID.randomUUID())
                .academicClass(sampleClass)
                .clinicalCaseData(ClinicalCaseData.builder().patientDays(5).build())
                .build();

        Activity act2 = Activity.builder()
                .id(UUID.randomUUID())
                .academicClass(sampleClass)
                .clinicalCaseData(null) // test null clinicalCaseData -> ptDays defaults to 1
                .build();

        Activity act3 = Activity.builder()
                .id(UUID.randomUUID())
                .academicClass(sampleClass)
                .clinicalCaseData(ClinicalCaseData.builder().patientDays(null).build()) // test null patientDays -> ptDays defaults to 1
                .build();

        IdentifiedTriggerData trigger1 = IdentifiedTriggerData.builder()
                .triggerCode("C1")
                .triggerName("Queda de Leito")
                .isHarm(true)
                .harmSeverityLetter("e")
                .build();

        IdentifiedTriggerData trigger2 = IdentifiedTriggerData.builder()
                .triggerCode("M1")
                .triggerName("Anticoagulante com RNI > 5")
                .isHarm(true)
                .harmSeverityLetter(null) // test null severity -> defaults to E
                .build();

        IdentifiedTriggerData trigger3 = IdentifiedTriggerData.builder()
                .triggerCode("C2")
                .triggerName("Úlcera por Pressão")
                .isHarm(false) // Not a harm
                .build();

        IdentifiedTriggerData triggerSpecialSeverity = IdentifiedTriggerData.builder()
                .triggerCode("C1")
                .triggerName(null)
                .isHarm(true)
                .harmSeverityLetter("Z") // test severity letter not in standard map
                .build();

        IdentifiedTriggerData blankCodeTrigger = IdentifiedTriggerData.builder()
                .triggerCode("   ")
                .isHarm(false)
                .build();

        IdentifiedTriggerData nullCodeTrigger = IdentifiedTriggerData.builder()
                .triggerCode(null)
                .isHarm(false)
                .build();

        ActivitySubmission sub1 = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(act1)
                .student(student1)
                .identifiedTriggers(List.of(trigger1, trigger2, trigger3))
                .grade(new BigDecimal("9.00"))
                .submissionDate(Instant.now())
                .build();

        ActivitySubmission sub2 = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(act2)
                .student(student2)
                .identifiedTriggers(List.of(triggerSpecialSeverity, blankCodeTrigger, nullCodeTrigger))
                .grade(null) // Ungraded
                .submissionDate(Instant.now())
                .build();

        ActivitySubmission sub3 = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(act3)
                .student(student2)
                .identifiedTriggers(null) // test null triggers
                .grade(new BigDecimal("7.00"))
                .submissionDate(Instant.now())
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(activityRepository.findByAcademicClassId(classId)).thenReturn(List.of(act1, act2, act3));
        when(submissionRepository.findByClassId(classId)).thenReturn(List.of(sub1, sub2, sub3));

        ClassDashboardDTO dashboard = dashboardService.getClassDashboard(classId, admin);

        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getGttMetrics().getTotalAdmissions()).isEqualTo(3);
        // ptDays: sub1=5, sub2=1, sub3=1 -> total = 7
        assertThat(dashboard.getGttMetrics().getTotalPatientDays()).isEqualTo(7);
        // Total harms: sub1 has trigger1 (E) + trigger2 (E), sub2 has triggerSpecialSeverity (Z) -> 3 EAs
        assertThat(dashboard.getGttMetrics().getTotalAdverseEvents()).isEqualTo(3);
        // Admissions with EA: sub1 and sub2 -> 2
        assertThat(dashboard.getGttMetrics().getAdmissionsWithAdverseEvents()).isEqualTo(2);

        // EA / 1000 pt days = (3 * 1000) / 7 = 428.57
        assertThat(dashboard.getGttMetrics().getAdverseEventsPer1000PatientDays()).isEqualTo(428.57);
        // EA / 100 admissions = (3 * 100) / 3 = 100.0
        assertThat(dashboard.getGttMetrics().getAdverseEventsPer100Admissions()).isEqualTo(100.0);
        // % admissions with EA = (2 * 100) / 3 = 66.67
        assertThat(dashboard.getGttMetrics().getPercentAdmissionsWithAdverseEvents()).isEqualTo(66.67);

        // Pedagogical metrics
        assertThat(dashboard.getPedagogicalMetrics().getTotalEnrolledStudents()).isEqualTo(2);
        assertThat(dashboard.getPedagogicalMetrics().getTotalActivities()).isEqualTo(3);
        assertThat(dashboard.getPedagogicalMetrics().getTotalSubmissions()).isEqualTo(3);
        assertThat(dashboard.getPedagogicalMetrics().getGradedSubmissions()).isEqualTo(2);
        assertThat(dashboard.getPedagogicalMetrics().getPendingGradingSubmissions()).isEqualTo(1);
        // Average grade: (9.00 + 7.00) / 2 = 8.00
        assertThat(dashboard.getPedagogicalMetrics().getClassAverageGrade()).isEqualTo(8.00);

        // Top triggers: C1 appears twice, M1 appears once
        assertThat(dashboard.getPedagogicalMetrics().getTopIdentifiedTriggers()).isNotEmpty();
        assertThat(dashboard.getPedagogicalMetrics().getTopIdentifiedTriggers().get(0).getTriggerCode()).isEqualTo("C1");
        assertThat(dashboard.getPedagogicalMetrics().getTopIdentifiedTriggers().get(0).getCount()).isEqualTo(2);
    }
}
