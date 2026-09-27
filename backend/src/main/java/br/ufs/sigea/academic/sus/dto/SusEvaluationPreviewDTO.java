package br.ufs.sigea.academic.sus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para retorno de pré-visualização calculada dos escores e classificações SUS em tempo real.
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 */
@Schema(description = "Diagnóstico prévio calculado de usabilidade com base nas 10 respostas da escala SUS")
public record SusEvaluationPreviewDTO(
        @Schema(description = "Escore SUS normalizado de 0.0 a 100.0", example = "82.5")
        double score,

        @Schema(description = "Classificação adjetiva segundo Bangor et al. (2008)", example = "Bom")
        String adjectiveRating,

        @Schema(description = "Nível de aceitabilidade do sistema", example = "Aceitável")
        String acceptability,

        @Schema(description = "Conceito escolar internacional equivalente (A a F)", example = "B")
        String gradeLevel
) {}
