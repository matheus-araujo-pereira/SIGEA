package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para avaliação pedagógica docente de uma submissão de auditoria clínica.
 *
 * @param nota Nota atribuída pelo docente avaliador (escala de 0.00 a 10.00)
 * @param parecerDocente Parecer formativo com orientações e considerações sobre a resolução
 */
@Schema(description = "Payload para avaliação e correção pedagógica da atividade")
public record AvaliarSubmissaoDTO(
        @Schema(
                        description = "Nota atribuída de 0.00 a 10.00",
                        example = "8.50",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A nota é obrigatória")
                @DecimalMin(value = "0.00", message = "A nota mínima é 0.0")
                @DecimalMax(value = "10.00", message = "A nota máxima é 10.0")
                BigDecimal nota,
        @Schema(
                        description = "Parecer formativo e feedback pedagógico",
                        example =
                                "Excelente análise de causa-raiz. O plano 5W3H foi bem delimitado.",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O parecer formativo é obrigatório")
                String parecerDocente) {}
