package br.ufs.sigea.academic.sus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de resposta contendo os dados e escore processado de uma avaliação SUS.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados detalhados e escore calculado de uma avaliação da Escala SUS")
public class SusEvaluationResponseDTO {

    @Schema(description = "Identificador único da avaliação", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Identificador do discente", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID studentId;

    @Schema(description = "Nome do estudante", example = "Matheus Araujo")
    private String studentName;

    @Schema(description = "E-mail institucional do estudante", example = "matheus@academico.ufs.br")
    private String studentEmail;

    @Schema(description = "Matrícula do estudante", example = "20260001001")
    private String studentRegistration;

    @Schema(description = "Identificador da turma associada", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID academicClassId;

    @Schema(description = "Nome da turma associada", example = "Enfermagem Cirúrgica - Turma 01 - 2026.1")
    private String className;

    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;
    private Integer q6;
    private Integer q7;
    private Integer q8;
    private Integer q9;
    private Integer q10;

    @Schema(description = "Escore global padronizado (0 a 100)", example = "87.5")
    private Double score;

    @Schema(description = "Classificação adjetiva (Bangor et al.)", example = "Melhor Imaginável")
    private String adjectiveRating;

    @Schema(description = "Grau de aceitabilidade", example = "Aceitável")
    private String acceptability;

    @Schema(description = "Conceito escolar equivalente", example = "A")
    private String gradeLevel;

    @Schema(description = "Sugestões de melhoria qualitativas")
    private String suggestions;

    @Schema(description = "Data e hora de envio")
    private Instant createdAt;
}
