package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Payload de resposta com dados consolidados do módulo GTT.
 *
 * @param id Identificador único numérico do módulo.
 * @param codigo Código do módulo (ex: CUIDADOS).
 * @param nome Nome amigável do módulo.
 * @param descricao Descrição do escopo de auditoria.
 * @param ativo Status de ativação.
 * @param criadoEm Data de criação.
 */
@Schema(description = "Dados detalhados de módulo GTT.")
public record ModuloGttRespostaDTO(
        @Schema(description = "Identificador único do módulo", example = "1") Long id,
        @Schema(description = "Código identificador do módulo", example = "CUIDADOS") String codigo,
        @Schema(description = "Nome do módulo", example = "Cuidados Gerais") String nome,
        @Schema(
                        description = "Descrição conceitual do módulo",
                        example = "Gatilhos relacionados a cuidados hospitalares gerais")
                String descricao,
        @Schema(description = "Indica se o módulo está ativo", example = "true") Boolean ativo,
        @Schema(description = "Data de cadastro", example = "2026-09-12T08:00:00")
                LocalDateTime criadoEm) {

    /**
     * Converte entidade de domínio em DTO de resposta.
     *
     * @param modulo Entidade de domínio.
     * @return DTO correspondente.
     */
    public static ModuloGttRespostaDTO deEntidade(ModuloGtt modulo) {
        return new ModuloGttRespostaDTO(
                modulo.getId(),
                modulo.getCodigo(),
                modulo.getNome(),
                modulo.getDescricao(),
                modulo.getAtivo(),
                modulo.getCriadoEm());
    }
}
