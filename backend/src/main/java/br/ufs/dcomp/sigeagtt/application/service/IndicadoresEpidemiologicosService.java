package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.ports.input.IndicadoresEpidemiologicosUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoGatilhoRepositoryPort;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link IndicadoresEpidemiologicosUseCase}.
 *
 * <p>Calcula taxas epidemiológicas hospitalares da metodologia IHI Global Trigger Tool (IHI-GTT),
 * incluindo taxas por 1.000 pacientes-dia, percentuais de internação com dano, distribuição por
 * severidade NCC MERP e séries temporais para Run Charts.
 */
@Service
public class IndicadoresEpidemiologicosService implements IndicadoresEpidemiologicosUseCase {

    private final SubmissaoAtividadeRepositoryPort submissaoRepositorio;
    private final SubmissaoGatilhoRepositoryPort gatilhoAchadoRepositorio;

    /**
     * Construtor com injeção das portas de persistência de submissões e gatilhos.
     *
     * @param submissaoRepositorio Porta de saída para submissões.
     * @param gatilhoAchadoRepositorio Porta de saída para achados de gatilhos.
     */
    public IndicadoresEpidemiologicosService(
            SubmissaoAtividadeRepositoryPort submissaoRepositorio,
            SubmissaoGatilhoRepositoryPort gatilhoAchadoRepositorio) {
        this.submissaoRepositorio = submissaoRepositorio;
        this.gatilhoAchadoRepositorio = gatilhoAchadoRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calcularIndicadoresIndividuais(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String moduloCodigo,
            String gravidade,
            Boolean danoPresenteAdmissao) {

        List<SubmissaoAtividade> submissoes =
                filtrarSubmissoesAvaliadas(
                        turmaId, periodoLetivo, cenarioId, unidadeId, dataInicio, dataFim);

        int totalDias = 0;
        int totalEventos = 0;
        int eventosIntrahospitalares = 0;
        int eventosPresentesAdmissao = 0;
        int casosComDano = 0;
        int totalGatilhosRastreados = 0;
        int totalDanosConfirmadosGeral = 0;

        Map<String, Integer> severidades = new LinkedHashMap<>();
        severidades.put("CATEGORIA_E", 0);
        severidades.put("CATEGORIA_F", 0);
        severidades.put("CATEGORIA_G", 0);
        severidades.put("CATEGORIA_H", 0);
        severidades.put("CATEGORIA_I", 0);

        Map<String, Integer> distribuicaoModulos = new LinkedHashMap<>();
        distribuicaoModulos.put("CUIDADOS", 0);
        distribuicaoModulos.put("MEDICACAO", 0);
        distribuicaoModulos.put("CIRURGICO", 0);
        distribuicaoModulos.put("TERAPIA_INTENSIVA", 0);
        distribuicaoModulos.put("PERINATAL", 0);
        distribuicaoModulos.put("URGENCIA", 0);

        Map<YearMonth, AgregadoTemporal> agregadoTemporal = new TreeMap<>();

        for (SubmissaoAtividade sub : submissoes) {
            Integer dias = sub.getAtividade().getCasoClinico().getTempoPermanenciaDias();
            int diasValidos = dias != null && dias > 0 ? dias : 1;
            totalDias += diasValidos;

            LocalDate dataRef = extrairDataReferencia(sub);
            if (dataRef == null) {
                dataRef = LocalDate.now();
            }
            YearMonth ym = YearMonth.from(dataRef);
            AgregadoTemporal agg =
                    agregadoTemporal.computeIfAbsent(ym, k -> new AgregadoTemporal());
            agg.prontuarios++;
            agg.dias += diasValidos;

            List<SubmissaoGatilho> achados =
                    gatilhoAchadoRepositorio.listarPorSubmissaoId(sub.getId());
            boolean danoNoCaso = false;

            for (SubmissaoGatilho achado : achados) {
                totalGatilhosRastreados++;

                if (Boolean.TRUE.equals(achado.getConfirmouDano())) {
                    totalDanosConfirmadosGeral++;

                    if (moduloCodigo != null
                            && !moduloCodigo.isBlank()
                            && !moduloCodigo.equalsIgnoreCase("TODOS")) {
                        if (achado.getGatilho() == null
                                || achado.getGatilho().getModulo() == null
                                || !achado.getGatilho()
                                        .getModulo()
                                        .getCodigo()
                                        .equalsIgnoreCase(moduloCodigo)) {
                            continue;
                        }
                    }

                    if (gravidade != null
                            && !gravidade.isBlank()
                            && !gravidade.equalsIgnoreCase("TODAS")) {
                        if (achado.getGravidade() == null
                                || !achado.getGravidade().name().equalsIgnoreCase(gravidade)) {
                            continue;
                        }
                    }

                    if (danoPresenteAdmissao != null) {
                        boolean adm = Boolean.TRUE.equals(achado.getDanoPresenteAdmissao());
                        if (adm != danoPresenteAdmissao) {
                            continue;
                        }
                    }

                    totalEventos++;
                    agg.eventos++;
                    danoNoCaso = true;

                    if (Boolean.TRUE.equals(achado.getDanoPresenteAdmissao())) {
                        eventosPresentesAdmissao++;
                    } else {
                        eventosIntrahospitalares++;
                    }

                    if (achado.getGravidade() != null) {
                        String chave = achado.getGravidade().name();
                        severidades.put(chave, severidades.getOrDefault(chave, 0) + 1);
                    }

                    if (achado.getGatilho() != null && achado.getGatilho().getModulo() != null) {
                        String mod = achado.getGatilho().getModulo().getCodigo();
                        if (mod != null) {
                            String chaveMod = mod.toUpperCase();
                            if (chaveMod.contains("CUIDADO")) chaveMod = "CUIDADOS";
                            else if (chaveMod.contains("MEDIC")) chaveMod = "MEDICACAO";
                            else if (chaveMod.contains("CIRURG")) chaveMod = "CIRURGICO";
                            else if (chaveMod.contains("INTENSIV") || chaveMod.contains("UTI"))
                                chaveMod = "TERAPIA_INTENSIVA";
                            else if (chaveMod.contains("PERINATAL") || chaveMod.contains("MATERN"))
                                chaveMod = "PERINATAL";
                            else chaveMod = "URGENCIA";

                            distribuicaoModulos.put(
                                    chaveMod, distribuicaoModulos.getOrDefault(chaveMod, 0) + 1);
                        }
                    }
                }
            }

            if (danoNoCaso) {
                casosComDano++;
                agg.casosComDano++;
            }
        }

        int totalProntuarios = submissoes.size();

        double taxaEaPorMilDias =
                totalDias > 0 ? ((double) totalEventos / totalDias) * 1000.0 : 0.0;
        double taxaEaPorCemInternacoes =
                totalProntuarios > 0 ? ((double) totalEventos / totalProntuarios) * 100.0 : 0.0;
        double percentualInternacoesComEa =
                totalProntuarios > 0 ? ((double) casosComDano / totalProntuarios) * 100.0 : 0.0;
        double mediaDiasPermanencia =
                totalProntuarios > 0 ? (double) totalDias / totalProntuarios : 0.0;
        double razaoRendimentoGatilho =
                totalGatilhosRastreados > 0
                        ? ((double) totalDanosConfirmadosGeral / totalGatilhosRastreados) * 100.0
                        : 0.0;

        List<Map<String, Object>> serieTemporal = new ArrayList<>();
        DateTimeFormatter rotuloFormatter = DateTimeFormatter.ofPattern("MMM/yy");
        for (Map.Entry<YearMonth, AgregadoTemporal> entry : agregadoTemporal.entrySet()) {
            YearMonth ym = entry.getKey();
            AgregadoTemporal agg = entry.getValue();

            double taxaMil = ((double) agg.eventos / agg.dias) * 1000.0;
            double taxaCem = ((double) agg.eventos / agg.prontuarios) * 100.0;
            double percDano = ((double) agg.casosComDano / agg.prontuarios) * 100.0;

            Map<String, Object> ponto = new LinkedHashMap<>();
            ponto.put("periodo", ym.toString());
            ponto.put("rotulo", ym.format(rotuloFormatter));
            ponto.put("eventos", agg.eventos);
            ponto.put("prontuarios", agg.prontuarios);
            ponto.put("dias", agg.dias);
            ponto.put("taxaPorMilDias", arredondar(taxaMil));
            ponto.put("taxaPorCemInternacoes", arredondar(taxaCem));
            ponto.put("percentualComDano", arredondar(percDano));
            serieTemporal.add(ponto);
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("totalProntuariosAuditados", totalProntuarios);
        resultado.put("totalDiasInternacao", totalDias);
        resultado.put("mediaDiasPermanencia", arredondar(mediaDiasPermanencia));
        resultado.put("totalEventosAdversos", totalEventos);
        resultado.put("eventosIntrahospitalares", eventosIntrahospitalares);
        resultado.put("eventosPresentesAdmissao", eventosPresentesAdmissao);
        resultado.put("prontuariosComDano", casosComDano);
        resultado.put("taxaEaPorMilDias", arredondar(taxaEaPorMilDias));
        resultado.put("taxaEaPorCemInternacoes", arredondar(taxaEaPorCemInternacoes));
        resultado.put("percentualInternacoesComEa", arredondar(percentualInternacoesComEa));
        resultado.put("razaoRendimentoGatilho", arredondar(razaoRendimentoGatilho));
        resultado.put("distribuicaoSeveridadeNccMerp", severidades);
        resultado.put("distribuicaoModulos", distribuicaoModulos);
        resultado.put("serieTemporal", serieTemporal);

        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obterQuadroResumo(
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
            Integer tamanho) {

        List<SubmissaoAtividade> submissoes =
                filtrarSubmissoesAvaliadas(
                        turmaId, periodoLetivo, cenarioId, unidadeId, dataInicio, dataFim);

        List<Map<String, Object>> linhas = new ArrayList<>();

        for (SubmissaoAtividade sub : submissoes) {
            List<SubmissaoGatilho> achados =
                    gatilhoAchadoRepositorio.listarPorSubmissaoId(sub.getId());

            int gatilhosDetectados = achados.size();
            long eventosAdversos =
                    achados.stream().filter(a -> Boolean.TRUE.equals(a.getConfirmouDano())).count();
            boolean temDano = eventosAdversos > 0;

            if (Boolean.TRUE.equals(apenasComDano) && !temDano) {
                continue;
            }

            if (moduloCodigo != null
                    && !moduloCodigo.isBlank()
                    && !moduloCodigo.equalsIgnoreCase("TODOS")) {
                boolean contemModulo =
                        achados.stream()
                                .anyMatch(
                                        a ->
                                                a.getGatilho() != null
                                                        && a.getGatilho().getModulo() != null
                                                        && moduloCodigo.equalsIgnoreCase(
                                                                a.getGatilho()
                                                                        .getModulo()
                                                                        .getCodigo()));
                if (!contemModulo) continue;
            }

            if (gravidade != null && !gravidade.isBlank() && !gravidade.equalsIgnoreCase("TODAS")) {
                boolean contemGravidade =
                        achados.stream()
                                .anyMatch(
                                        a ->
                                                a.getGravidade() != null
                                                        && a.getGravidade()
                                                                .name()
                                                                .equalsIgnoreCase(gravidade));
                if (!contemGravidade) continue;
            }

            if (danoPresenteAdmissao != null && temDano) {
                boolean atendeAdm =
                        achados.stream()
                                .filter(a -> Boolean.TRUE.equals(a.getConfirmouDano()))
                                .anyMatch(
                                        a ->
                                                Boolean.TRUE.equals(a.getDanoPresenteAdmissao())
                                                        == danoPresenteAdmissao);
                if (!atendeAdm) continue;
            }

            String maxGravidade =
                    achados.stream()
                            .filter(a -> Boolean.TRUE.equals(a.getConfirmouDano()))
                            .map(a -> a.getGravidade())
                            .filter(java.util.Objects::nonNull)
                            .map(g -> g.name())
                            .max((s1, s2) -> s1.compareTo(s2))
                            .orElse("-");

            List<String> codigosGatilhos =
                    achados.stream()
                            .map(a -> a.getGatilho() != null ? a.getGatilho().getCodigo() : "")
                            .filter(s -> !s.isBlank())
                            .distinct()
                            .toList();

            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("submissaoId", sub.getId());
            linha.put(
                    "prontuario",
                    sub.getAtividade().getCasoClinico().getNumeroAtendimento() != null
                            ? sub.getAtividade().getCasoClinico().getNumeroAtendimento()
                            : "PRT-" + sub.getAtividade().getCasoClinico().getId());
            linha.put("casoClinicoTitulo", sub.getAtividade().getCasoClinico().getTitulo());
            linha.put(
                    "unidade",
                    sub.getAtividade().getCasoClinico().getUnidadeHospitalar().getSigla());
            linha.put(
                    "tempoPermanenciaDias",
                    sub.getAtividade().getCasoClinico().getTempoPermanenciaDias() != null
                            ? sub.getAtividade().getCasoClinico().getTempoPermanenciaDias()
                            : 1);
            linha.put("auditorDiscente", sub.getAluno().getNomeCompleto());
            linha.put("turma", sub.getAtividade().getTurma().getCodigoDisciplina());
            linha.put("periodoLetivo", sub.getAtividade().getTurma().getPeriodoLetivo());
            linha.put(
                    "dataAuditoria",
                    sub.getDataSubmissao() != null ? sub.getDataSubmissao().toLocalDate() : null);
            linha.put("gatilhosDetectados", gatilhosDetectados);
            linha.put("eventosAdversosConfirmados", eventosAdversos);
            linha.put("gravidadeMaxima", maxGravidade);
            linha.put("codigosGatilhos", codigosGatilhos);

            if (busca != null && !busca.isBlank()) {
                String termo = busca.trim().toLowerCase();
                String pront = String.valueOf(linha.get("prontuario")).toLowerCase();
                String caso = String.valueOf(linha.get("casoClinicoTitulo")).toLowerCase();
                String audit = String.valueOf(linha.get("auditorDiscente")).toLowerCase();
                String un = String.valueOf(linha.get("unidade")).toLowerCase();

                if (!pront.contains(termo)
                        && !caso.contains(termo)
                        && !audit.contains(termo)
                        && !un.contains(termo)) {
                    continue;
                }
            }

            linhas.add(linha);
        }

        int totalRegistros = linhas.size();
        int pag = (pagina != null && pagina >= 0) ? pagina : 0;
        int tam = (tamanho != null && tamanho > 0) ? tamanho : 15;
        int inicio = Math.min(pag * tam, totalRegistros);
        int fim = Math.min(inicio + tam, totalRegistros);

        List<Map<String, Object>> paginaLinhas =
                inicio < totalRegistros ? linhas.subList(inicio, fim) : Collections.emptyList();

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("total", totalRegistros);
        resposta.put("pagina", pag);
        resposta.put("tamanho", tam);
        resposta.put("totalPaginas", (int) Math.ceil((double) totalRegistros / tam));
        resposta.put("itens", paginaLinhas);

        return resposta;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obterDesempenhoGatilhos(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim) {

        List<SubmissaoAtividade> submissoes =
                filtrarSubmissoesAvaliadas(
                        turmaId, periodoLetivo, cenarioId, unidadeId, dataInicio, dataFim);
        List<Long> submissoesIds = submissoes.stream().map(s -> s.getId()).toList();

        if (submissoesIds.isEmpty()) {
            return Map.of("totalGatilhos", 0, "gatilhos", Collections.emptyList());
        }

        List<SubmissaoGatilho> todosAchados =
                gatilhoAchadoRepositorio.listarTodos().stream()
                        .filter(
                                a ->
                                        a.getSubmissao() != null
                                                && submissoesIds.contains(a.getSubmissao().getId()))
                        .toList();

        Map<String, DesempenhoAcumulador> mapa = new HashMap<>();

        for (SubmissaoGatilho achado : todosAchados) {
            if (achado.getGatilho() == null) continue;
            String cod = achado.getGatilho().getCodigo();
            String desc = achado.getGatilho().getDescricao();
            String mod =
                    achado.getGatilho().getModulo() != null
                            ? achado.getGatilho().getModulo().getNome()
                            : "Geral";

            DesempenhoAcumulador acc =
                    mapa.computeIfAbsent(cod, k -> new DesempenhoAcumulador(cod, desc, mod));
            acc.rastreamentos++;

            if (Boolean.TRUE.equals(achado.getConfirmouDano())) {
                acc.danosConfirmados++;
            }
        }

        List<Map<String, Object>> listaGatilhos =
                mapa.values().stream()
                        .map(
                                acc -> {
                                    double vpp =
                                            ((double) acc.danosConfirmados / acc.rastreamentos)
                                                    * 100.0;
                                    Map<String, Object> item = new LinkedHashMap<>();
                                    item.put("codigo", acc.codigo);
                                    item.put("descricao", acc.descricao);
                                    item.put("modulo", acc.modulo);
                                    item.put("rastreamentos", acc.rastreamentos);
                                    item.put("danosConfirmados", acc.danosConfirmados);
                                    item.put("valorPreditivoPositivo", arredondar(vpp));
                                    return item;
                                })
                        .sorted(
                                (m1, m2) ->
                                        Integer.compare(
                                                (Integer) m2.get("danosConfirmados"),
                                                (Integer) m1.get("danosConfirmados")))
                        .collect(Collectors.toList());

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("totalGatilhosIdentificados", listaGatilhos.size());
        resposta.put("gatilhos", listaGatilhos);

        return resposta;
    }

    private List<SubmissaoAtividade> filtrarSubmissoesAvaliadas(
            Long turmaId,
            String periodoLetivo,
            Long cenarioId,
            Long unidadeId,
            LocalDate dataInicio,
            LocalDate dataFim) {

        return submissaoRepositorio.listarTodas().stream()
                .filter(s -> s.getStatus() == StatusSubmissao.AVALIADA)
                .filter(
                        s ->
                                turmaId == null
                                        || (s.getAtividade() != null
                                                && s.getAtividade().getTurma() != null
                                                && turmaId.equals(
                                                        s.getAtividade().getTurma().getId())))
                .filter(
                        s ->
                                periodoLetivo == null
                                        || periodoLetivo.isBlank()
                                        || (s.getAtividade() != null
                                                && s.getAtividade().getTurma() != null
                                                && periodoLetivo.equalsIgnoreCase(
                                                        s.getAtividade()
                                                                .getTurma()
                                                                .getPeriodoLetivo())))
                .filter(
                        s ->
                                cenarioId == null
                                        || (s.getAtividade() != null
                                                && s.getAtividade().getCasoClinico() != null
                                                && cenarioId.equals(
                                                        s.getAtividade().getCasoClinico().getId())))
                .filter(
                        s ->
                                unidadeId == null
                                        || (s.getAtividade() != null
                                                && s.getAtividade().getCasoClinico() != null
                                                && s.getAtividade()
                                                                .getCasoClinico()
                                                                .getUnidadeHospitalar()
                                                        != null
                                                && unidadeId.equals(
                                                        s.getAtividade()
                                                                .getCasoClinico()
                                                                .getUnidadeHospitalar()
                                                                .getId())))
                .filter(
                        s -> {
                            if (dataInicio == null) return true;
                            LocalDate dataRef = extrairDataReferencia(s);
                            return dataRef != null && !dataRef.isBefore(dataInicio);
                        })
                .filter(
                        s -> {
                            if (dataFim == null) return true;
                            LocalDate dataRef = extrairDataReferencia(s);
                            return dataRef != null && !dataRef.isAfter(dataFim);
                        })
                .toList();
    }

    private LocalDate extrairDataReferencia(SubmissaoAtividade s) {
        if (s.getDataSubmissao() != null) {
            return s.getDataSubmissao().toLocalDate();
        }
        if (s.getAtividade() != null && s.getAtividade().getDataInicio() != null) {
            return s.getAtividade().getDataInicio().toLocalDate();
        }
        return null;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static class AgregadoTemporal {
        int prontuarios = 0;
        int dias = 0;
        int eventos = 0;
        int casosComDano = 0;
    }

    private static class DesempenhoAcumulador {
        String codigo;
        String descricao;
        String modulo;
        int rastreamentos = 0;
        int danosConfirmados = 0;

        DesempenhoAcumulador(String codigo, String descricao, String modulo) {
            this.codigo = codigo;
            this.descricao = descricao;
            this.modulo = modulo;
        }
    }
}
