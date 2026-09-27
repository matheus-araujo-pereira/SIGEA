package br.ufs.sigea.academic.sus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO contendo a consolidação estatística e psicométrica das avaliações SUS de uma turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sumário psicométrico consolidado da Escala SUS para uma turma acadêmica")
public class SusClassSummaryDTO {

    @Schema(description = "Identificador da turma", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID classId;

    @Schema(description = "Nome da turma", example = "Enfermagem Cirúrgica - Turma 01 - 2026.1")
    private String className;

    @Schema(description = "Total de avaliações enviadas", example = "28")
    private long totalEvaluations;

    @Schema(description = "Total de estudantes matriculados na turma", example = "30")
    private long enrolledStudentsCount;

    @Schema(description = "Taxa de adesão discente (%)", example = "93.33")
    private double responseRatePercentage;

    @Schema(description = "Escore médio do SUS na turma (0 a 100)", example = "84.50")
    private double averageScore;

    @Schema(description = "Classificação adjetiva do escore médio", example = "Bom")
    private String adjectiveRating;

    @Schema(description = "Grau de aceitabilidade do escore médio", example = "Aceitável")
    private String acceptability;

    @Schema(description = "Conceito escolar médio", example = "B")
    private String gradeLevel;

    @Schema(description = "Distribuição de contagem por classificação adjetiva")
    private Map<String, Long> adjectiveDistribution;

    @Schema(description = "Médias aritméticas individuais de cada questão (Q1 a Q10)")
    private List<Double> questionAverages;

    @Schema(description = "Lista detalhada das avaliações enviadas")
    private List<SusEvaluationResponseDTO> evaluations;
}
