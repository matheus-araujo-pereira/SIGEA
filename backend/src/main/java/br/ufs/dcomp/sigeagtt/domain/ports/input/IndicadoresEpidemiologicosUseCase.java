package br.ufs.dcomp.sigeagtt.domain.ports.input;

import java.time.LocalDate;
import java.util.Map;

/**
 * Porta de entrada (Input Port / Use Case) para cálculo e extração dos Indicadores Epidemiológicos
 * Hospitalares da metodologia Global Trigger Tool (IHI-GTT).
 */
public interface IndicadoresEpidemiologicosUseCase {

    /**
     * Calcula indicadores de taxa de eventos adversos (por 1.000 pacientes-dia e percentual de
     * internações com dano) com filtros operacionais.
     *
     * @param turmaId Filtro por turma.
     * @param periodoLetivo Filtro por período acadêmico.
     * @param cenarioId Filtro por caso clínico.
     * @param unidadeId Filtro por unidade hospitalar.
     * @param dataInicio Filtro por data inicial.
     * @param dataFim Filtro por data final.
     * @param moduloCodigo Filtro por código de módulo GTT.
     * @param gravidade Filtro por gravidade NCC MERP.
     * @param danoPresenteAdmissao Filtro por evento adverso prévio à admissão.
     * @return Mapa analítico com os indicadores calculados.
     */
    Map<String, Object> calcularIndicadoresIndividuais(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String moduloCodigo,
            String gravidade,
            Boolean danoPresenteAdmissao);

    /**
     * Gera o quadro resumo analítico das auditorias com paginação e busca textual.
     *
     * @param turmaId Filtro por turma.
     * @param periodoLetivo Filtro por período.
     * @param cenarioId Filtro por caso.
     * @param unidadeId Filtro por unidade.
     * @param dataInicio Filtro inicial.
     * @param dataFim Filtro final.
     * @param moduloCodigo Filtro por módulo.
     * @param gravidade Filtro por gravidade.
     * @param danoPresenteAdmissao Filtro de admissão.
     * @param apenasComDano Se deve filtrar apenas auditorias com dano confirmado.
     * @param busca Termo textual de pesquisa.
     * @param pagina Índice da página de resultados.
     * @param tamanho Quantidade de registros por página.
     * @return Mapa estruturado contendo a listagem paginada e os totais.
     */
    Map<String, Object> obterQuadroResumo(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String moduloCodigo,
            String gravidade,
            Boolean danoPresenteAdmissao,
            Boolean apenasComDano,
            String busca,
            Integer pagina,
            Integer tamanho);

    /**
     * Avalia o desempenho dos gatilhos GTT (taxa de rendimento positivo e valor preditivo
     * positivo).
     *
     * @param turmaId Filtro por turma.
     * @param periodoLetivo Filtro por período.
     * @param cenarioId Filtro por caso clínico.
     * @param unidadeId Filtro por unidade hospitalar.
     * @param dataInicio Data de início.
     * @param dataFim Data final.
     * @return Desempenho estatístico consolidado de cada gatilho.
     */
    Map<String, Object> obterDesempenhoGatilhos(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim);
}
