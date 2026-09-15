package br.ufs.sigea.academic.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicadores oficiais do método IHI Global Trigger Tool calculados para a turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GttMetricsDTO {

    /**
     * Eventos Adversos por 1.000 pacientes-dia: (Total EAs / Total Pacientes-Dia) * 1000.
     */
    private Double adverseEventsPer1000PatientDays;

    /**
     * Eventos Adversos por 100 admissões: (Total EAs / Total Admissões) * 100.
     */
    private Double adverseEventsPer100Admissions;

    /**
     * % de admissões com pelo menos um evento adverso: (Admissões com >= 1 EA / Total Admissões) * 100.
     */
    private Double percentAdmissionsWithAdverseEvents;

    private Integer totalPatientDays;
    private Integer totalAdmissions;
    private Integer totalAdverseEvents;
    private Integer admissionsWithAdverseEvents;

    /**
     * Distribuição de eventos adversos por categoria de severidade de dano (E, F, G, H, I).
     */
    @Builder.Default
    private Map<String, Long> harmDistribution = new HashMap<>();
}
