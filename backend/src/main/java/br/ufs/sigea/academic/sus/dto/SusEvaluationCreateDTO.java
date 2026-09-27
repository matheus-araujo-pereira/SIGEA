package br.ufs.sigea.academic.sus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para submissão de respostas do questionário da Escala SUS (Likert 1 a 5).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para envio de avaliação da Escala de Usabilidade do Sistema (SUS)")
public class SusEvaluationCreateDTO {

    @Schema(description = "Identificador da turma acadêmica associada (opcional)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID academicClassId;

    @NotNull(message = "O item 1 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q1: Eu acho que gostaria de usar este sistema com frequência", example = "5")
    private Integer q1;

    @NotNull(message = "O item 2 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q2: Eu achei o sistema desnecessariamente complexo", example = "1")
    private Integer q2;

    @NotNull(message = "O item 3 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q3: Eu achei o sistema fácil de usar", example = "5")
    private Integer q3;

    @NotNull(message = "O item 4 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q4: Eu acho que precisaria de ajuda de uma pessoa com conhecimentos técnicos", example = "1")
    private Integer q4;

    @NotNull(message = "O item 5 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q5: Eu achei que as várias funções deste sistema estavam bem integradas", example = "5")
    private Integer q5;

    @NotNull(message = "O item 6 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q6: Eu achei que havia muita inconsistência neste sistema", example = "1")
    private Integer q6;

    @NotNull(message = "O item 7 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q7: Eu imagino que a maioria das pessoas aprenderia a usar este sistema muito rapidamente", example = "5")
    private Integer q7;

    @NotNull(message = "O item 8 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q8: Eu achei o sistema muito incômodo / complicado de usar", example = "1")
    private Integer q8;

    @NotNull(message = "O item 9 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q9: Eu me senti muito confiante usando o sistema", example = "5")
    private Integer q9;

    @NotNull(message = "O item 10 é obrigatório")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    @Schema(description = "Q10: Eu precisei aprender muitas coisas antes de conseguir usar este sistema", example = "1")
    private Integer q10;

    @Size(max = 2000, message = "As sugestões podem conter no máximo 2000 caracteres")
    @Schema(description = "Sugestões de melhoria qualitativas e observações (opcional)", example = "Excelente interface e clareza no rastreamento de gatilhos.")
    private String suggestions;
}
