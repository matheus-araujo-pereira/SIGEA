package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para representação do ciclo de melhoria contínua PDCA na submissão.
 *
 * @param planejar Etapa de Planejamento (Plan)
 * @param fazer Etapa de Execução das ações planejadas (Do)
 * @param checar Etapa de Monitoramento e checagem de resultados (Check)
 * @param agir Etapa de Padronização ou correção de desvios (Act)
 */
@Schema(description = "Ciclo PDCA de melhoria da qualidade assistencial")
public record SubmissaoPdcaDTO(
        @Schema(
                        description = "Planejamento (Plan)",
                        example = "Elaborar fluxo de auditoria semanal")
                String planejar,
        @Schema(
                        description = "Execução (Do)",
                        example = "Aplicar formulário de auditoria em 10 prontuários por semana")
                String fazer,
        @Schema(
                        description = "Checagem (Check)",
                        example = "Mensurar percentual de conformidade de prescrições")
                String checar,
        @Schema(
                        description = "Ação / Padronização (Act)",
                        example = "Instituir POP institucional definitivo")
                String agir) {

    /**
     * Converte entidade de domínio para DTO.
     *
     * @param e Entidade de domínio
     * @return DTO preenchido
     */
    public static SubmissaoPdcaDTO deEntidade(SubmissaoPdca e) {
        if (e == null) return null;
        return new SubmissaoPdcaDTO(e.getPlanejar(), e.getFazer(), e.getChecar(), e.getAgir());
    }
}
