package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;

/**
 * DTO para recebimento de dados de salvamento (rascunho ou entrega) de submissão discente.
 *
 * @param tempoGastoSegundos Tempo acumulado pelo discente na resolução em segundos
 * @param finalizar Se verdadeiro, conclui e homologa a entrega final; se falso, salva como rascunho
 * @param achadosGatilhos Lista de achados de gatilhos clínicos identificados
 * @param ishikawa Análise de causa-raiz no Diagrama de Ishikawa 6M
 * @param planos5w3h Lista de ações planejadas na matriz 5W3H
 * @param pdca Ciclo de melhoria contínua PDCA
 */
@Schema(description = "Payload para salvar rascunho ou submeter resolução discente")
public record SalvarSubmissaoDTO(
        @Schema(description = "Tempo total de resolução em segundos", example = "900")
                Integer tempoGastoSegundos,
        @Schema(description = "Indica se deve finalizar e entregar a atividade", example = "false")
                Boolean finalizar,
        @Schema(description = "Achados clínicos de gatilhos rastreadores")
                List<SubmissaoGatilhoDTO> achadosGatilhos,
        @Schema(description = "Diagrama de Ishikawa preenchido") SubmissaoIshikawaDTO ishikawa,
        @Schema(description = "Ações do Plano 5W3H") List<SubmissaoPlano5w3hDTO> planos5w3h,
        @Schema(description = "Ciclo PDCA") SubmissaoPdcaDTO pdca) {

    /**
     * Converte o DTO para o comando de caso de uso de domínio.
     *
     * @return Comando DadosSalvarSubmissao
     */
    public SubmissaoAtividadeUseCase.DadosSalvarSubmissao paraComando() {
        List<SubmissaoAtividadeUseCase.DadosGatilho> gatilhos =
                achadosGatilhos == null
                        ? Collections.emptyList()
                        : achadosGatilhos.stream()
                                .map(
                                        g ->
                                                new SubmissaoAtividadeUseCase.DadosGatilho(
                                                        g.gatilhoId(),
                                                        g.categoriaEaId(),
                                                        g.confirmouDano(),
                                                        g.justificativaDano(),
                                                        g.danoPresenteAdmissao(),
                                                        g.gravidade()))
                                .toList();

        SubmissaoAtividadeUseCase.DadosIshikawa ish =
                ishikawa == null
                        ? null
                        : new SubmissaoAtividadeUseCase.DadosIshikawa(
                                ishikawa.efeitoPrincipal(),
                                ishikawa.metodo(),
                                ishikawa.maoDeObra(),
                                ishikawa.material(),
                                ishikawa.medida(),
                                ishikawa.meioAmbiente(),
                                ishikawa.maquina());

        List<SubmissaoAtividadeUseCase.DadosPlano5w3h> planos =
                planos5w3h == null
                        ? Collections.emptyList()
                        : planos5w3h.stream()
                                .map(
                                        p ->
                                                new SubmissaoAtividadeUseCase.DadosPlano5w3h(
                                                        p.oQue(),
                                                        p.porQue(),
                                                        p.quem(),
                                                        p.onde(),
                                                        p.quando(),
                                                        p.como(),
                                                        p.quantoCusta(),
                                                        p.comoMedir()))
                                .toList();

        SubmissaoAtividadeUseCase.DadosPdca cicloPdca =
                pdca == null
                        ? null
                        : new SubmissaoAtividadeUseCase.DadosPdca(
                                pdca.planejar(), pdca.fazer(), pdca.checar(), pdca.agir());

        return new SubmissaoAtividadeUseCase.DadosSalvarSubmissao(
                finalizar != null && finalizar,
                tempoGastoSegundos != null ? tempoGastoSegundos : 0,
                gatilhos,
                ish,
                planos,
                cicloPdca);
    }
}
