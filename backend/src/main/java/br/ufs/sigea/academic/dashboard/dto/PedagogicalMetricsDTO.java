package br.ufs.sigea.academic.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Métricas pedagógicas de desempenho da turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedagogicalMetricsDTO {
    private Double classAverageGrade;
    private Integer totalEnrolledStudents;
    private Integer totalActivities;
    private Integer totalSubmissions;
    private Integer gradedSubmissions;
    private Integer pendingGradingSubmissions;

    @Builder.Default
    private List<TriggerOccurrenceDTO> topIdentifiedTriggers = new ArrayList<>();
}
