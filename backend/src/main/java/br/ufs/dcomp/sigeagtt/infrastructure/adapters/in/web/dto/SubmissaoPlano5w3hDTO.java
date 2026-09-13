package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO para representação de item da matriz de Plano de Ação 5W3H.
 *
 * @param id Identificador do item
 * @param oQue Ação interventiva proposta (What)
 * @param porQue Justificativa clínica ou de segurança do paciente (Why)
 * @param quem Responsável técnico ou equipe encarregada (Who)
 * @param onde Setor ou unidade assistencial de implementação (Where)
 * @param quando Prazo estipulado para conclusão (When)
 * @param como Método e procedimento operacional de execução (How)
 * @param quantoCusta Estimativa de custo orçamentário (How much)
 * @param comoMedir Indicador de qualidade ou monitoramento de eficácia (How measure)
 */
@Schema(description = "Item da matriz do Plano de Ação 5W3H")
public record SubmissaoPlano5w3hDTO(
        @Schema(description = "Identificador do registro", example = "1") Long id,
        @Schema(
                        description = "O que será feito (What)",
                        example = "Implantar protocolo de dupla checagem de eletrólitos")
                String oQue,
        @Schema(
                        description = "Por que será feito (Why)",
                        example = "Evitar novas trocas de soluções hipertônicas")
                String porQue,
        @Schema(description = "Quem fará (Who)", example = "Comissão de Farmácia e Terapêutica")
                String quem,
        @Schema(description = "Onde será feito (Where)", example = "UTI Adulto") String onde,
        @Schema(description = "Quando será concluído (When)", example = "30 dias após homologação")
                String quando,
        @Schema(
                        description = "Como será executado (How)",
                        example = "Treinamento em serviço e revisão de POP assistencial")
                String como,
        @Schema(description = "Quanto custará (How much)", example = "1500.00")
                BigDecimal quantoCusta,
        @Schema(
                        description = "Como será medido (How measure)",
                        example = "Taxa de adesão à dupla checagem")
                String comoMedir) {

    /**
     * Converte entidade de domínio para DTO.
     *
     * @param e Entidade de domínio
     * @return DTO preenchido
     */
    public static SubmissaoPlano5w3hDTO deEntidade(SubmissaoPlano5w3h e) {
        if (e == null) return null;
        return new SubmissaoPlano5w3hDTO(
                e.getId(),
                e.getOQue(),
                e.getPorQue(),
                e.getQuem(),
                e.getOnde(),
                e.getQuando(),
                e.getComo(),
                e.getQuantoCusta(),
                e.getComoMedir());
    }
}
