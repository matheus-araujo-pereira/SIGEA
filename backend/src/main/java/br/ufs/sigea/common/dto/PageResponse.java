package br.ufs.sigea.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Resposta padronizada para coleções paginadas no SIGEA-GTT.
 * Padrão da plataforma: 10 registros por página.
 *
 * @param <T> Tipo de dado do elemento da página
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estrutura padronizada de resposta paginada")
public class PageResponse<T> {

    @Schema(description = "Lista dos elementos retornados na página atual")
    private List<T> content;

    @Schema(description = "Número da página atual (índice baseado em zero)", example = "0")
    private int page;

    @Schema(description = "Quantidade de registros por página", example = "10")
    private int size;

    @Schema(description = "Total de registros encontrados em todas as páginas", example = "42")
    private long totalElements;

    @Schema(description = "Total de páginas disponíveis", example = "5")
    private int totalPages;

    @Schema(description = "Indica se é a primeira página", example = "true")
    private boolean first;

    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;

    /**
     * Converte um objeto Page do Spring Data em PageResponse padrão.
     *
     * @param springPage Página original do Spring Data
     * @param <T>        Tipo dos elementos
     * @return Instância mapeada de PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> springPage) {
        return PageResponse.<T>builder()
                .content(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }
}
