package br.ufs.sigea.academic.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Painel Analítico Integrado da Turma Acadêmica com métricas IHI-GTT e pedagógicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassDashboardDTO {
    private UUID classId;
    private String className;
    private String professorName;
    private String academicPeriod;
    private Boolean isClosed;
    private GttMetricsDTO gttMetrics;
    private PedagogicalMetricsDTO pedagogicalMetrics;
}
