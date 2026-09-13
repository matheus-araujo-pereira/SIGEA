package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para representação de achado de gatilho rastreador na auditoria do discente.
 *
 * @param id Identificador do achado
 * @param gatilhoId Identificador do gatilho no catálogo GTT
 * @param gatilhoCodigo Código de identificação do gatilho (ex: M1, C2)
 * @param gatilhoDescricao Descrição textual da regra do gatilho
 * @param moduloCodigo Código do módulo GTT correspondente
 * @param moduloNome Nome do módulo GTT correspondente
 * @param categoriaEaId Identificador da categoria de evento adverso associada
 * @param categoriaEaNome Nome descritivo da categoria de evento adverso
 * @param confirmouDano Se o discente confirmou a ocorrência de dano ao paciente
 * @param justificativaDano Argumentação clínica do discente para a confirmação de dano
 * @param danoPresenteAdmissao Se o evento/dano já estava presente na admissão do paciente
 * @param gravidade Classificação da severidade segundo o índice NCC MERP
 */
@Schema(description = "Representação de achado de gatilho clínico na submissão")
public record SubmissaoGatilhoDTO(
        @Schema(description = "Identificador do registro", example = "1") Long id,
        @Schema(description = "ID do gatilho rastreador", example = "10") Long gatilhoId,
        @Schema(description = "Código do gatilho", example = "M1") String gatilhoCodigo,
        @Schema(description = "Descrição da regra do gatilho", example = "Uso de Naloxona")
                String gatilhoDescricao,
        @Schema(description = "Código do módulo GTT", example = "MED") String moduloCodigo,
        @Schema(description = "Nome do módulo GTT", example = "Medicamentos") String moduloNome,
        @Schema(description = "ID da categoria do evento adverso", example = "2")
                Long categoriaEaId,
        @Schema(description = "Nome descritivo da categoria", example = "Erro de medicação")
                String categoriaEaNome,
        @Schema(description = "Confirmação de dano clínico", example = "true")
                Boolean confirmouDano,
        @Schema(
                        description = "Justificativa clínica do dano",
                        example =
                                "Houve depressão respiratória aguda após superdosagem de opioide.")
                String justificativaDano,
        @Schema(description = "Dano presente à admissão", example = "false")
                Boolean danoPresenteAdmissao,
        @Schema(description = "Classificação de gravidade NCC MERP", example = "E")
                GravidadeNccMerp gravidade) {

    /**
     * Converte entidade de domínio para DTO.
     *
     * @param e Entidade de domínio
     * @return DTO preenchido
     */
    public static SubmissaoGatilhoDTO deEntidade(SubmissaoGatilho e) {
        if (e == null) return null;
        return new SubmissaoGatilhoDTO(
                e.getId(),
                e.getGatilho() != null ? e.getGatilho().getId() : null,
                e.getGatilho() != null ? e.getGatilho().getCodigo() : null,
                e.getGatilho() != null ? e.getGatilho().getDescricao() : null,
                e.getGatilho() != null && e.getGatilho().getModulo() != null
                        ? e.getGatilho().getModulo().getCodigo()
                        : null,
                e.getGatilho() != null && e.getGatilho().getModulo() != null
                        ? e.getGatilho().getModulo().getNome()
                        : null,
                e.getCategoriaEventoAdverso() != null
                        ? e.getCategoriaEventoAdverso().getId()
                        : null,
                e.getCategoriaEventoAdverso() != null
                        ? e.getCategoriaEventoAdverso().getNome()
                        : null,
                e.getConfirmouDano(),
                e.getJustificativaDano(),
                e.getDanoPresenteAdmissao(),
                e.getGravidade());
    }
}
