package br.ufs.sigea.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Resposta padronizada para requisições bem-sucedidas no SIGEA.
 *
 * @param <T> Tipo de dado encapsulado no corpo da resposta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estrutura padrão de resposta de sucesso")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi realizada com sucesso", example = "true")
    @Builder.Default
    private boolean success = true;

    @Schema(description = "Mensagem amigável de retorno", example = "Operação realizada com sucesso.")
    private String message;

    @Schema(description = "Dados resultantes da operação")
    private T data;

    @Schema(description = "Timestamp do instante de resposta no formato ISO-8601")
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Cria uma resposta de sucesso simples sem dados.
     *
     * @param message Mensagem explicativa
     * @param <T>     Tipo do payload
     * @return Instância de ApiResponse
     */
    public static <T> ApiResponse<T> ok(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Cria uma resposta de sucesso com dados.
     *
     * @param data    Objeto contendo os dados
     * @param message Mensagem explicativa
     * @param <T>     Tipo do payload
     * @return Instância de ApiResponse
     */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
}
