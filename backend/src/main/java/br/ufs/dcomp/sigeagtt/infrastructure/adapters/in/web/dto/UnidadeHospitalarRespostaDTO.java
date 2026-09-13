package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload de resposta com dados detalhados da unidade hospitalar.
 *
 * @param id Identificador único numérico da unidade.
 * @param sigla Código mnemônico ou sigla (ex: UTI-A).
 * @param nome Nome por extenso do setor.
 * @param ativa Status de atividade da unidade hospitalar.
 */
@Schema(description = "Dados de uma unidade hospitalar cadastrada.")
public record UnidadeHospitalarRespostaDTO(
        @Schema(description = "Identificador único da unidade", example = "1") Long id,
        @Schema(description = "Sigla da unidade", example = "UTI-A") String sigla,
        @Schema(description = "Nome da unidade", example = "Unidade de Terapia Intensiva Adulto")
                String nome,
        @Schema(description = "Indica se a unidade está ativa", example = "true") Boolean ativa) {

    /**
     * Converte entidade de domínio em DTO de resposta.
     *
     * @param unidade Entidade de domínio.
     * @return DTO correspondente.
     */
    public static UnidadeHospitalarRespostaDTO deEntidade(UnidadeHospitalar unidade) {
        return new UnidadeHospitalarRespostaDTO(
                unidade.getId(), unidade.getSigla(), unidade.getNome(), unidade.getAtiva());
    }
}
