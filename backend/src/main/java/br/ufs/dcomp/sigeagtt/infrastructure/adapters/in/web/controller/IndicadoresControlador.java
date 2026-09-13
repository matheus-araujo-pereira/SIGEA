package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.ports.input.IndicadoresEpidemiologicosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para cálculo e extração dos indicadores epidemiológicos hospitalares (IHI-GTT).
 */
@RestController
@RequestMapping("/api/indicadores")
@Tag(
        name = "Indicadores Epidemiológicos",
        description =
                "Endpoints de inteligência epidemiológica hospitalar, taxas de eventos adversos (1.000 pacientes-dia, % de internações com dano) e rendimento de gatilhos")
@SecurityRequirement(name = "bearerAuth")
public class IndicadoresControlador {

    private final IndicadoresEpidemiologicosUseCase servico;

    public IndicadoresControlador(IndicadoresEpidemiologicosUseCase servico) {
        this.servico = servico;
    }

    /**
     * Calcula indicadores epidemiológicos consolidados segundo os critérios oficiais do IHI-GTT.
     *
     * @param turmaId Filtro por turma
     * @param periodoLetivo Filtro por período letivo
     * @param cenarioId Filtro por caso clínico
     * @param unidadeId Filtro por unidade hospitalar
     * @param dataInicio Data de início
     * @param dataFim Data final
     * @param moduloCodigo Filtro por módulo GTT
     * @param gravidade Filtro por severidade NCC MERP
     * @param danoPresenteAdmissao Filtro de presença na admissão
     * @return Mapa de indicadores epidemiológicos
     */
    @Operation(
            summary = "Calcular indicadores de eventos adversos",
            description =
                    "Calcula a taxa de eventos adversos por 1.000 pacientes-dia, taxa de eventos por 100 admissões e percentual de internações com dano.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Indicadores calculados com sucesso")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> calcular(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) String periodoLetivo,
            @RequestParam(required = false) Long cenarioId,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String moduloCodigo,
            @RequestParam(required = false) String gravidade,
            @RequestParam(required = false) Boolean danoPresenteAdmissao) {
        return ResponseEntity.ok(
                servico.calcularIndicadoresIndividuais(
                        turmaId,
                        periodoLetivo,
                        cenarioId,
                        unidadeId,
                        dataInicio,
                        dataFim,
                        moduloCodigo,
                        gravidade,
                        danoPresenteAdmissao));
    }

    /**
     * Recupera o quadro resumo analítico das auditorias com paginação e busca textual.
     *
     * @param turmaId Filtro por turma
     * @param periodoLetivo Filtro por período
     * @param cenarioId Filtro por caso clínico
     * @param unidadeId Filtro por unidade
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @param moduloCodigo Código do módulo
     * @param gravidade Gravidade
     * @param danoPresenteAdmissao Presença na admissão
     * @param apenasComDano Filtrar apenas registros com dano confirmado
     * @param busca Termo textual de pesquisa
     * @param pagina Página atual
     * @param tamanho Tamanho da página
     * @return Quadro resumo paginado
     */
    @Operation(
            summary = "Quadro resumo de auditorias",
            description =
                    "Retorna o detalhamento consolidado das auditorias finalizadas para visualização em tabela com paginação.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quadro resumo retornado com sucesso")
    })
    @GetMapping("/quadro-resumo")
    public ResponseEntity<Map<String, Object>> obterQuadroResumo(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) String periodoLetivo,
            @RequestParam(required = false) Long cenarioId,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String moduloCodigo,
            @RequestParam(required = false) String gravidade,
            @RequestParam(required = false) Boolean danoPresenteAdmissao,
            @RequestParam(required = false) Boolean apenasComDano,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "15") Integer tamanho) {
        return ResponseEntity.ok(
                servico.obterQuadroResumo(
                        turmaId,
                        periodoLetivo,
                        cenarioId,
                        unidadeId,
                        dataInicio,
                        dataFim,
                        moduloCodigo,
                        gravidade,
                        danoPresenteAdmissao,
                        apenasComDano,
                        busca,
                        pagina,
                        tamanho));
    }

    /**
     * Obtém o desempenho estatístico e valor preditivo positivo dos gatilhos clínicos.
     *
     * @param turmaId Filtro por turma
     * @param periodoLetivo Filtro por período
     * @param cenarioId Filtro por caso
     * @param unidadeId Filtro por unidade
     * @param dataInicio Data de início
     * @param dataFim Data final
     * @return Desempenho estatístico de cada gatilho
     */
    @Operation(
            summary = "Desempenho dos gatilhos clínicos",
            description =
                    "Calcula o rendimento de identificação e o valor preditivo positivo (VPP) para cada gatilho da metodologia.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Estatísticas de gatilhos retornadas com sucesso")
    })
    @GetMapping("/gatilhos")
    public ResponseEntity<Map<String, Object>> obterDesempenhoGatilhos(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) String periodoLetivo,
            @RequestParam(required = false) Long cenarioId,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        return ResponseEntity.ok(
                servico.obterDesempenhoGatilhos(
                        turmaId, periodoLetivo, cenarioId, unidadeId, dataInicio, dataFim));
    }
}
