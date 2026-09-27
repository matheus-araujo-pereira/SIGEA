package br.ufs.sigea.academic.sus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO contendo o sumário psicométrico global de todas as avaliações SUS no sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sumário psicométrico global da Escala SUS no sistema SIGEA")
public class SusGeneralSummaryDTO {

    @Schema(description = "Total de avaliações enviadas no sistema", example = "60")
    private long totalEvaluations;

    @Schema(description = "Escore médio global do SUS (0 a 100)", example = "86.25")
    private double averageScore;

    @Schema(description = "Classificação adjetiva média", example = "Melhor Imaginável")
    private String adjectiveRating;

    @Schema(description = "Grau de aceitabilidade médio", example = "Aceitável")
    private String acceptability;

    @Schema(description = "Conceito escolar médio", example = "A")
    private String gradeLevel;

    @Schema(description = "Distribuição global por classificação adjetiva")
    private Map<String, Long> adjectiveDistribution;

    @Schema(description = "Médias aritméticas globais de cada questão (Q1 a Q10)")
    private List<Double> questionAverages;
}
