package br.ufs.sigea.academic.dashboard.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
import br.ufs.sigea.academic.dashboard.dto.GttMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.PedagogicalMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.TriggerOccurrenceDTO;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço analítico para consolidação dos indicadores oficiais do IHI-GTT e desempenho pedagógico da turma.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassDashboardService {

    private final AcademicClassRepository classRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySubmissionRepository submissionRepository;

    /**
     * Calcula determinísticamente as métricas oficiais do Global Trigger Tool e pedagógicas da turma.
     */
    @Transactional(readOnly = true)
    public ClassDashboardDTO getClassDashboard(UUID classId, User currentUser) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + classId));

        validateAccess(academicClass, currentUser);

        List<Activity> activities = activityRepository.findByAcademicClassId(classId);
        List<ActivitySubmission> submissions = submissionRepository.findByClassId(classId);

        GttMetricsDTO gttMetrics = calculateGttMetrics(submissions);
        PedagogicalMetricsDTO pedagogicalMetrics = calculatePedagogicalMetrics(academicClass, activities, submissions);

        return ClassDashboardDTO.builder()
                .classId(academicClass.getId())
                .className(academicClass.getFormattedName())
                .professorName(academicClass.getProfessor().getFullName())
                .academicPeriod(academicClass.getAcademicPeriod())
                .isClosed(academicClass.getIsClosed())
                .gttMetrics(gttMetrics)
                .pedagogicalMetrics(pedagogicalMetrics)
                .build();
    }

    private GttMetricsDTO calculateGttMetrics(List<ActivitySubmission> submissions) {
        int totalAdmissions = submissions.size();
        int totalPatientDays = 0;
        int totalAdverseEvents = 0;
        int admissionsWithAdverseEvents = 0;

        Map<String, Long> harmDistribution = new HashMap<>();
        harmDistribution.put("E", 0L);
        harmDistribution.put("F", 0L);
        harmDistribution.put("G", 0L);
        harmDistribution.put("H", 0L);
        harmDistribution.put("I", 0L);

        for (ActivitySubmission sub : submissions) {
            int ptDays = (sub.getActivity().getClinicalCaseData() != null &&
                          sub.getActivity().getClinicalCaseData().getPatientDays() != null)
                    ? sub.getActivity().getClinicalCaseData().getPatientDays()
                    : 1;
            totalPatientDays += ptDays;

            List<IdentifiedTriggerData> triggers = sub.getIdentifiedTriggers();
            boolean hasHarmInSubmission = false;

            if (triggers != null) {
                for (IdentifiedTriggerData t : triggers) {
                    if (Boolean.TRUE.equals(t.getIsHarm())) {
                        totalAdverseEvents++;
                        hasHarmInSubmission = true;

                        String letter = (t.getHarmSeverityLetter() != null)
                                ? t.getHarmSeverityLetter().toUpperCase().trim()
                                : "E";

                        if (harmDistribution.containsKey(letter)) {
                            harmDistribution.put(letter, harmDistribution.get(letter) + 1);
                        } else {
                            harmDistribution.put(letter, 1L);
                        }
                    }
                }
            }

            if (hasHarmInSubmission) {
                admissionsWithAdverseEvents++;
            }
        }

        double eaPer1000PatientDays = totalPatientDays > 0
                ? round((totalAdverseEvents * 1000.0) / totalPatientDays, 2)
                : 0.0;

        double eaPer100Admissions = totalAdmissions > 0
                ? round((totalAdverseEvents * 100.0) / totalAdmissions, 2)
                : 0.0;

        double pctAdmissionsWithEa = totalAdmissions > 0
                ? round((admissionsWithAdverseEvents * 100.0) / totalAdmissions, 2)
                : 0.0;

        return GttMetricsDTO.builder()
                .adverseEventsPer1000PatientDays(eaPer1000PatientDays)
                .adverseEventsPer100Admissions(eaPer100Admissions)
                .percentAdmissionsWithAdverseEvents(pctAdmissionsWithEa)
                .totalPatientDays(totalPatientDays)
                .totalAdmissions(totalAdmissions)
                .totalAdverseEvents(totalAdverseEvents)
                .admissionsWithAdverseEvents(admissionsWithAdverseEvents)
                .harmDistribution(harmDistribution)
                .build();
    }

    private PedagogicalMetricsDTO calculatePedagogicalMetrics(
            AcademicClass academicClass,
            List<Activity> activities,
            List<ActivitySubmission> submissions
    ) {
        int totalStudents = (academicClass.getStudents() != null) ? academicClass.getStudents().size() : 0;
        int totalSubmissions = submissions.size();

        List<ActivitySubmission> graded = submissions.stream()
                .filter(ActivitySubmission::isGraded)
                .toList();

        int gradedCount = graded.size();
        int pendingGrading = totalSubmissions - gradedCount;

        double averageGrade = 0.0;
        if (gradedCount > 0) {
            BigDecimal sum = graded.stream()
                    .map(ActivitySubmission::getGrade)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            averageGrade = sum.divide(BigDecimal.valueOf(gradedCount), 2, RoundingMode.HALF_UP).doubleValue();
        }

        // Top gatilhos mais identificados
        Map<String, String> triggerNameMap = new HashMap<>();
        Map<String, Long> triggerCountMap = new HashMap<>();

        for (ActivitySubmission sub : submissions) {
            if (sub.getIdentifiedTriggers() != null) {
                for (IdentifiedTriggerData t : sub.getIdentifiedTriggers()) {
                    String code = t.getTriggerCode();
                    if (code != null && !code.isBlank()) {
                        triggerCountMap.put(code, triggerCountMap.getOrDefault(code, 0L) + 1);
                        if (t.getTriggerName() != null) {
                            triggerNameMap.put(code, t.getTriggerName());
                        }
                    }
                }
            }
        }

        List<TriggerOccurrenceDTO> topTriggers = triggerCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(e -> TriggerOccurrenceDTO.builder()
                        .triggerCode(e.getKey())
                        .triggerName(triggerNameMap.getOrDefault(e.getKey(), e.getKey()))
                        .count(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return PedagogicalMetricsDTO.builder()
                .classAverageGrade(averageGrade)
                .totalEnrolledStudents(totalStudents)
                .totalActivities(activities.size())
                .totalSubmissions(totalSubmissions)
                .gradedSubmissions(gradedCount)
                .pendingGradingSubmissions(pendingGrading)
                .topIdentifiedTriggers(topTriggers)
                .build();
    }

    private void validateAccess(AcademicClass academicClass, User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isClassProfessor = academicClass.getProfessor().getId().equals(currentUser.getId());

        if (!isAdmin && !isClassProfessor) {
            throw new AccessDeniedException("Apenas o docente titular da turma ou um administrador podem acessar o painel de indicadores.");
        }
    }

    private double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
