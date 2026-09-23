package br.ufs.sigea.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Resposta padronizada para erros e exceções capturadas no SIGEA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estrutura padrão para retorno de erros na API")
public class ErrorResponse {

    @Schema(description = "Indica que a requisição resultou em erro", example = "false")
    @Builder.Default
    private boolean success = false;

    @Schema(description = "Código de status HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Descrição ou categoria do erro", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem detalhada e amigável da falha ocorrida", example = "Dados inválidos fornecidos.")
    private String message;

    @Schema(description = "Caminho (URI) da requisição que gerou o erro", example = "/api/users")
    private String path;

    @Schema(description = "Lista detalhada de violações de validação de campos (se aplicável)")
    private List<FieldErrorDetail> fieldErrors;

    @Schema(description = "Timestamp do instante do erro em formato ISO-8601")
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Detalhe de violação em campo de formulário ou payload.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalhe de validação de campo individual")
    public static class FieldErrorDetail {
        @Schema(description = "Nome do campo com falha", example = "email")
        private String field;

        @Schema(description = "Mensagem de erro associada ao campo", example = "O e-mail deve pertencer ao domínio @academico.ufs.br")
        private String message;

        @Schema(description = "Valor rejeitado na validação")
        private Object rejectedValue;
    }
}
