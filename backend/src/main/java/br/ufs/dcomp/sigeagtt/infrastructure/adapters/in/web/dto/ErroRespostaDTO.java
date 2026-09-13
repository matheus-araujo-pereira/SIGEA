package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Payload estruturado padronizado para representação de erros HTTP no SIGEA-GTT.
 *
 * @param timestamp Data e hora do registro da falha.
 * @param status Código de status HTTP numérico (ex: 400, 401, 403, 404, 500).
 * @param erro Rótulo padronizado da categoria do erro.
 * @param mensagem Mensagem informativa ou diagnóstica da causa do erro.
 * @param campos Mapa com mensagens de inconsistência campo a campo em falhas de validação.
 */
@Schema(
        description =
                "Payload estruturado padronizado para representação de erros HTTP no SIGEA-GTT.")
public record ErroRespostaDTO(
        @Schema(description = "Data e hora do registro do erro", example = "2026-09-12T19:30:00")
                LocalDateTime timestamp,
        @Schema(description = "Código de status HTTP numérico", example = "400") int status,
        @Schema(
                        description = "Rótulo descritivo do erro",
                        example = "Falha de validação nos dados enviados")
                String erro,
        @Schema(
                        description = "Detalhamento explicativo da falha",
                        example = "O e-mail institucional é obrigatório")
                String mensagem,
        @Schema(
                        description =
                                "Detalhamento específico de campos inválidos (em requisições 400)",
                        nullable = true)
                Map<String, String> campos) {

    /** Construtor de conveniência sem campos de validação. */
    public ErroRespostaDTO(LocalDateTime timestamp, int status, String erro, String mensagem) {
        this(timestamp, status, erro, mensagem, null);
    }
}
