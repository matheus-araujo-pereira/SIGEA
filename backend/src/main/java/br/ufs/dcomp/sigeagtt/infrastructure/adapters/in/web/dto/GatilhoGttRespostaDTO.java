package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload de resposta com dados consolidados do gatilho clínico GTT.
 *
 * @param id Identificador único do gatilho.
 * @param moduloId Identificador do módulo pai.
 * @param moduloNome Nome do módulo pai.
 * @param codigo Código alfanumérico (ex: C1).
 * @param descricao Descrição clínica operacional.
 * @param limiarReferencia Critérios de corte clínico.
 * @param ativo Status de ativação.
 */
@Schema(description = "Dados detalhados do gatilho clínico GTT.")
public record GatilhoGttRespostaDTO(
        @Schema(description = "Identificador único do gatilho", example = "1") Long id,
        @Schema(description = "ID do módulo pai", example = "1") Long moduloId,
        @Schema(description = "Nome do módulo pai", example = "Cuidados Gerais") String moduloNome,
        @Schema(description = "Código do gatilho", example = "C1") String codigo,
        @Schema(
                        description = "Descrição clínica do gatilho",
                        example = "Parada cardiorrespiratória")
                String descricao,
        @Schema(description = "Limiar de referência", example = "Evento agudo")
                String limiarReferencia,
        @Schema(description = "Indica se o gatilho está ativo", example = "true") Boolean ativo) {

    /**
     * Converte entidade de domínio em DTO de resposta.
     *
     * @param gatilho Entidade de domínio do gatilho.
     * @return DTO correspondente.
     */
    public static GatilhoGttRespostaDTO deEntidade(GatilhoGtt gatilho) {
        return new GatilhoGttRespostaDTO(
                gatilho.getId(),
                gatilho.getModulo() != null ? gatilho.getModulo().getId() : null,
                gatilho.getModulo() != null ? gatilho.getModulo().getNome() : null,
                gatilho.getCodigo(),
                gatilho.getDescricao(),
                gatilho.getLimiarReferencia(),
                gatilho.getAtivo());
    }
}
