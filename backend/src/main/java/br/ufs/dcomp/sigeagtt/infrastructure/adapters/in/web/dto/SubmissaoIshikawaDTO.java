package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para representação da análise de causa-raiz no Diagrama de Ishikawa (6M).
 *
 * @param efeitoPrincipal Efeito ou evento adverso central investigado
 * @param metodo Falhas no método de trabalho ou protocolos operacionais
 * @param maoDeObra Fatores humanos, capacitação ou dimensionamento de equipe
 * @param material Problemas relacionados a insumos, medicamentos ou prontuários
 * @param medida Falhas de monitoramento, verificação de sinais vitais ou dosagens
 * @param meioAmbiente Fatores ambientais, ruído, iluminação ou desorganização
 * @param maquina Falhas em equipamentos médicos, bombas de infusão ou monitores
 */
@Schema(description = "Diagrama de Causa e Efeito (Ishikawa - 6M)")
public record SubmissaoIshikawaDTO(
        @Schema(
                        description = "Efeito adverso central",
                        example = "Parada cardiorrespiratória por sobredose")
                String efeitoPrincipal,
        @Schema(
                        description = "Método de trabalho",
                        example = "Ausência de dupla checagem na administração")
                String metodo,
        @Schema(
                        description = "Mão de obra / Fator humano",
                        example = "Sobrecarga de trabalho na escala noturna")
                String maoDeObra,
        @Schema(
                        description = "Material / Insumos",
                        example = "Ampolas de apresentação visual semelhante")
                String material,
        @Schema(
                        description = "Medida / Monitoramento",
                        example = "Intervalo prolongado na aferição de oximetria")
                String medida,
        @Schema(description = "Meio ambiente", example = "Ruído excessivo no posto de enfermagem")
                String meioAmbiente,
        @Schema(description = "Máquina / Equipamento", example = "Bomba de infusão descalibrada")
                String maquina) {

    /**
     * Converte entidade de domínio para DTO.
     *
     * @param e Entidade de domínio
     * @return DTO preenchido
     */
    public static SubmissaoIshikawaDTO deEntidade(SubmissaoIshikawa e) {
        if (e == null) return null;
        return new SubmissaoIshikawaDTO(
                e.getEfeitoPrincipal(),
                e.getMetodo(),
                e.getMaoDeObra(),
                e.getMaterial(),
                e.getMedida(),
                e.getMeioAmbiente(),
                e.getMaquina());
    }
}
