package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoGatilhoRepositoryPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndicadoresEpidemiologicosServiceTest {

    @Mock private SubmissaoAtividadeRepositoryPort submissaoRepositorio;
    @Mock private SubmissaoGatilhoRepositoryPort gatilhoAchadoRepositorio;

    @InjectMocks private IndicadoresEpidemiologicosService service;

    private SubmissaoAtividade criarSubmissaoAvaliadaMock(
            Long id,
            Long turmaId,
            String periodo,
            Long cenarioId,
            Long unidId,
            int dias,
            LocalDateTime data) {
        Turma turma = new Turma();
        turma.setId(turmaId);
        turma.setCodigoDisciplina("MED001");
        turma.setPeriodoLetivo(periodo);

        UnidadeHospitalar unid = new UnidadeHospitalar();
        unid.setId(unidId);
        unid.setSigla("UTI");

        CasoClinico caso = new CasoClinico();
        caso.setId(cenarioId);
        caso.setTitulo("Caso de Teste");
        caso.setNumeroAtendimento("ATD-" + cenarioId);
        caso.setTempoPermanenciaDias(dias);
        caso.setUnidadeHospitalar(unid);

        AtividadeEducacional ativ = new AtividadeEducacional();
        ativ.setId(100L);
        ativ.setTurma(turma);
        ativ.setCasoClinico(caso);
        ativ.setDataInicio(data);

        Usuario aluno = new Usuario();
        aluno.setId(1L);
        aluno.setNomeCompleto("Discente Teste");

        SubmissaoAtividade sub = new SubmissaoAtividade(ativ, aluno);
        sub.setId(id);
        sub.setStatus(StatusSubmissao.AVALIADA);
        sub.setDataSubmissao(data);
        return sub;
    }

    @Test
    @DisplayName("Deve calcular indicadores epidemiologicos com todas as metricas e modulos IHI")
    void deveCalcularIndicadoresIndividuaisCompletos() {
        LocalDateTime agora = LocalDateTime.of(2026, 3, 15, 10, 0);
        SubmissaoAtividade sub1 =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 5, agora);
        SubmissaoAtividade sub2 =
                criarSubmissaoAvaliadaMock(
                        2L, 10L, "2026.1", 101L, 1000L, 0, agora); // dias 0 => testar fallback
        SubmissaoAtividade sub3 =
                criarSubmissaoAvaliadaMock(3L, 10L, "2026.1", 102L, 1000L, 3, null);
        sub3.setDataSubmissao(null); // dataSubmissao nula, usa dataInicio da atividade
        sub3.getAtividade().setDataInicio(agora);
        SubmissaoAtividade sub4 =
                criarSubmissaoAvaliadaMock(4L, 10L, "2026.1", 103L, 1000L, 2, null);
        sub4.setDataSubmissao(null);
        sub4.getAtividade().setDataInicio(null); // ambas nulas => fallback LocalDate.now()
        sub4.getAtividade()
                .getCasoClinico()
                .setTempoPermanenciaDias(null); // dias nulo => fallback 1

        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub1, sub2, sub3, sub4));

        ModuloGtt mC = new ModuloGtt(1L, "CUIDADOS", "Cuidados", "", true, null);
        ModuloGtt mM = new ModuloGtt(2L, "MEDICACAO", "Medicamentos", "", true, null);
        ModuloGtt mS = new ModuloGtt(3L, "CIRURGICO", "Cirúrgico", "", true, null);
        ModuloGtt mI = new ModuloGtt(4L, "UTI", "Terapia Intensiva", "", true, null);
        ModuloGtt mTI = new ModuloGtt(5L, "TERAPIA_INTENSIVA", "UTI Geral", "", true, null);
        ModuloGtt mP = new ModuloGtt(6L, "MATERNO", "Perinatal", "", true, null);
        ModuloGtt mPN = new ModuloGtt(7L, "PERINATAL", "Perinatal Neo", "", true, null);
        ModuloGtt mU = new ModuloGtt(8L, "OUTRO", "Urgência", "", true, null);
        ModuloGtt mSemCodigo = new ModuloGtt(9L, null, "Sem Codigo", "", true, null);

        GatilhoGtt g1 = new GatilhoGtt(1L, "C1", mC, "PCR", null, true);
        GatilhoGtt g2 = new GatilhoGtt(2L, "M1", mM, "Vit K", null, true);
        GatilhoGtt g3 = new GatilhoGtt(3L, "S1", mS, "Retorno CCIR", null, true);
        GatilhoGtt g4 = new GatilhoGtt(4L, "I1", mI, "Intubação", null, true);
        GatilhoGtt g5 = new GatilhoGtt(5L, "P1", mP, "Apgar baixo", null, true);
        GatilhoGtt g6 = new GatilhoGtt(6L, "U1", mU, "Reinternação", null, true);
        GatilhoGtt g7 = new GatilhoGtt(7L, "TI1", mTI, "Dialise", null, true);
        GatilhoGtt g8 = new GatilhoGtt(8L, "PN1", mPN, "Asfixia", null, true);
        GatilhoGtt g9 = new GatilhoGtt(9L, "SC1", mSemCodigo, "Sem Cod", null, true);
        GatilhoGtt gSemMod = new GatilhoGtt(10L, "SM1", null, "Sem Mod", null, true);

        // Achados na submissao 1: dano presente na admissao e intrahospitalar
        SubmissaoGatilho a1 =
                new SubmissaoGatilho(
                        1L, sub1, g1, null, true, "Dano", true, GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho a2 =
                new SubmissaoGatilho(
                        2L, sub1, g2, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_F);
        SubmissaoGatilho a3 =
                new SubmissaoGatilho(
                        3L,
                        sub1,
                        g3,
                        null,
                        true,
                        "Dano Cirurg",
                        false,
                        GravidadeNccMerp.CATEGORIA_E);

        // Achados na submissao 2
        SubmissaoGatilho a4 =
                new SubmissaoGatilho(
                        4L, sub2, g4, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_G);
        SubmissaoGatilho a5 =
                new SubmissaoGatilho(
                        5L, sub2, g5, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_H);
        SubmissaoGatilho a6 =
                new SubmissaoGatilho(
                        6L, sub2, g6, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_I);

        // Achados na submissao 3: cobrir TI, PN, sem codigo, sem modulo, sem gatilho, sem
        // gravidade, e sem dano/nulo
        SubmissaoGatilho a7 =
                new SubmissaoGatilho(
                        7L, sub3, g7, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho a8 = new SubmissaoGatilho(8L, sub3, g8, null, true, "Dano", false, null);
        SubmissaoGatilho a9 = new SubmissaoGatilho(9L, sub3, g9, null, true, "Dano", false, null);
        SubmissaoGatilho a10 =
                new SubmissaoGatilho(10L, sub3, gSemMod, null, true, "Dano", false, null);
        SubmissaoGatilho a11 =
                new SubmissaoGatilho(11L, sub3, null, null, true, "Dano", false, null);
        SubmissaoGatilho a12 =
                new SubmissaoGatilho(12L, sub3, null, null, false, null, false, null);
        SubmissaoGatilho a13 = new SubmissaoGatilho(13L, sub3, null, null, null, null, false, null);

        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(1L)).thenReturn(List.of(a1, a2, a3));
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(2L)).thenReturn(List.of(a4, a5, a6));
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(3L))
                .thenReturn(List.of(a7, a8, a9, a10, a11, a12, a13));
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(4L)).thenReturn(List.of());

        Map<String, Object> ind =
                service.calcularIndicadoresIndividuais(
                        10L,
                        "2026.1",
                        null,
                        null,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31),
                        null,
                        null,
                        null);

        assertEquals(3, ind.get("totalProntuariosAuditados"));
        assertTrue((int) ind.get("totalEventosAdversos") > 0);
        assertNotNull(ind.get("taxaEaPorMilDias"));
        assertNotNull(ind.get("taxaEaPorCemInternacoes"));
        assertNotNull(ind.get("percentualInternacoesComEa"));
        assertNotNull(ind.get("distribuicaoSeveridadeNccMerp"));
        assertNotNull(ind.get("distribuicaoModulos"));
        assertNotNull(ind.get("serieTemporal"));

        // Sem filtro de datas para processar sub4 (fallback LocalDate.now())
        Map<String, Object> indSemFiltroData =
                service.calcularIndicadoresIndividuais(
                        10L, "2026.1", null, null, null, null, null, null, null);
        assertEquals(4, indSemFiltroData.get("totalProntuariosAuditados"));
    }

    @Test
    @DisplayName(
            "Deve filtrar indicadores por modulo, gravidade e danoPresenteAdmissao cobrindo todos os ramos")
    void deveFiltrarIndicadoresPorFiltrosEspecificos() {
        LocalDateTime agora = LocalDateTime.of(2026, 3, 15, 10, 0);
        SubmissaoAtividade sub =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, agora);
        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub));

        ModuloGtt m = new ModuloGtt(1L, "CUIDADOS", "Cuidados", "", true, null);
        ModuloGtt mCirurg = new ModuloGtt(2L, "CIRURGICO", "Cirúrgico", "", true, null);
        GatilhoGtt g = new GatilhoGtt(1L, "C1", m, "PCR", null, true);
        GatilhoGtt gSemMod = new GatilhoGtt(2L, "C2", null, "PCR", null, true);
        GatilhoGtt gCirurg = new GatilhoGtt(3L, "S1", mCirurg, "Cirurg", null, true);

        SubmissaoGatilho a1 =
                new SubmissaoGatilho(
                        1L, sub, g, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho a2 =
                new SubmissaoGatilho(2L, sub, gSemMod, null, true, "Dano", true, null);
        SubmissaoGatilho a3 =
                new SubmissaoGatilho(
                        3L, sub, null, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_F);
        SubmissaoGatilho a4 =
                new SubmissaoGatilho(
                        4L,
                        sub,
                        gCirurg,
                        null,
                        true,
                        "Dano Cirurg",
                        false,
                        GravidadeNccMerp.CATEGORIA_E);

        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(1L)).thenReturn(List.of(a1, a2, a3, a4));

        // Filtro correto com modulo, gravidade e danoPresenteAdmissao = false
        Map<String, Object> ind1 =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, "CUIDADOS", "CATEGORIA_E", false);
        assertEquals(1, ind1.get("totalEventosAdversos"));

        // Filtro CIRURGICO
        Map<String, Object> indCirurg =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, "CIRURGICO", "CATEGORIA_E", false);
        assertEquals(1, indCirurg.get("totalEventosAdversos"));

        // Filtros especiais: "TODOS", "TODAS", null
        Map<String, Object> indTodos =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, "TODOS", "TODAS", null);
        assertEquals(4, indTodos.get("totalEventosAdversos"));

        // Filtros vazios: "   "
        Map<String, Object> indBranco =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, "   ", "   ", null);
        assertEquals(4, indBranco.get("totalEventosAdversos"));

        // Filtro por gravidade com divergência e achado sem gravidade
        Map<String, Object> indGrav =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, null, "CATEGORIA_I", null);
        assertEquals(0, indGrav.get("totalEventosAdversos"));

        // Filtro danoPresenteAdmissao = true
        Map<String, Object> indAdm =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, null, null, true);
        assertEquals(1, indAdm.get("totalEventosAdversos"));
    }

    @Test
    @DisplayName("Deve obter quadro resumo com paginacao, filtro de busca e apenasComDano")
    void deveObterQuadroResumo() {
        LocalDateTime agora = LocalDateTime.of(2026, 3, 15, 10, 0);
        SubmissaoAtividade sub1 =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, agora);
        SubmissaoAtividade sub2 =
                criarSubmissaoAvaliadaMock(2L, 10L, "2026.1", 101L, 1000L, 2, agora);
        SubmissaoAtividade sub3 =
                criarSubmissaoAvaliadaMock(3L, 10L, "2026.1", 102L, 1000L, 2, agora);

        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub1, sub2, sub3));

        ModuloGtt m = new ModuloGtt(1L, "CUIDADOS", "Cuidados", "", true, null);
        GatilhoGtt g1 = new GatilhoGtt(1L, "C1", m, "PCR", null, true);
        GatilhoGtt gSemMod = new GatilhoGtt(2L, "G2", null, "Desc", null, true);

        SubmissaoGatilho a1 =
                new SubmissaoGatilho(
                        1L, sub1, g1, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho aSemMod =
                new SubmissaoGatilho(2L, sub1, gSemMod, null, false, null, false, null);
        SubmissaoGatilho aSemGat =
                new SubmissaoGatilho(3L, sub1, null, null, false, null, false, null);

        SubmissaoGatilho a3 =
                new SubmissaoGatilho(
                        4L, sub3, g1, null, true, "Dano Adm", true, GravidadeNccMerp.CATEGORIA_G);

        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(1L))
                .thenReturn(List.of(a1, aSemMod, aSemGat));
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(2L)).thenReturn(List.of());
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(3L)).thenReturn(List.of(a3));

        // Com apenasComDano = true (sub2 deve ser ignorada)
        Map<String, Object> quadro1 =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        "Discente",
                        0,
                        10);
        assertEquals(2, quadro1.get("total"));

        // Filtro com TODOS e TODAS
        Map<String, Object> qTodos =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, "TODOS", "TODAS", null, false, null, 0,
                        10);
        assertEquals(3, qTodos.get("total"));

        // Filtro com em branco
        Map<String, Object> qBranco =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, "   ", "   ", null, false, null, 0, 10);
        assertEquals(3, qBranco.get("total"));

        // Filtro danoPresenteAdmissao = true em caso com dano
        Map<String, Object> qAdm =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, true, false, null, 0, 10);
        assertEquals(2, qAdm.get("total")); // sub2 nao tem dano, sub3 tem adm=true

        // Filtro danoPresenteAdmissao = false (sub3 só tem adm=true, entao é descartada)
        Map<String, Object> qSemAdm =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, false, false, null, 0, 10);
        assertEquals(2, qSemAdm.get("total")); // sub1 tem dano hospitalar, sub2 nao tem dano

        // Busca com espacos em branco e pagina/tamanho nulos
        Map<String, Object> qBuscaEspaco =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, null, false, "   ", null,
                        null);
        assertEquals(3, qBuscaEspaco.get("total"));
        assertEquals(0, qBuscaEspaco.get("pagina"));
        assertEquals(15, qBuscaEspaco.get("tamanho"));

        // Busca sem resultado
        Map<String, Object> quadro2 =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        "TextoQueNaoExiste",
                        0,
                        10);
        assertEquals(0, quadro2.get("total"));
    }

    @Test
    @DisplayName(
            "Deve obter desempenho de gatilhos calculando VPP e ordenando por danosConfirmados")
    void deveObterDesempenhoGatilhos() {
        SubmissaoAtividade sub =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, LocalDateTime.now());
        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub));

        ModuloGtt m = new ModuloGtt(1L, "CUIDADOS", "Cuidados", "", true, null);
        GatilhoGtt g1 = new GatilhoGtt(1L, "C1", m, "PCR", null, true);
        GatilhoGtt g2 = new GatilhoGtt(2L, "M1", null, "Medicacao", null, true);

        SubmissaoGatilho a1 =
                new SubmissaoGatilho(
                        1L, sub, g1, null, true, "Dano", false, GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho a2 =
                new SubmissaoGatilho(2L, sub, g1, null, false, "Sem dano", false, null);
        SubmissaoGatilho a3 = new SubmissaoGatilho(3L, sub, g2, null, true, "Dano 2", false, null);
        SubmissaoGatilho a4 = new SubmissaoGatilho(4L, sub, g2, null, true, "Dano 3", false, null);
        SubmissaoGatilho aSemSub =
                new SubmissaoGatilho(5L, null, g1, null, true, "Sem sub", false, null);
        SubmissaoGatilho aSemGat =
                new SubmissaoGatilho(6L, sub, null, null, false, null, false, null);

        SubmissaoAtividade outraSub =
                criarSubmissaoAvaliadaMock(
                        999L, 10L, "2026.1", 100L, 1000L, 4, LocalDateTime.now());
        SubmissaoGatilho aOutraSub =
                new SubmissaoGatilho(7L, outraSub, g1, null, true, "Outra sub", false, null);

        when(gatilhoAchadoRepositorio.listarTodos())
                .thenReturn(List.of(a1, a2, a3, a4, aSemSub, aSemGat, aOutraSub));

        Map<String, Object> desp =
                service.obterDesempenhoGatilhos(null, null, null, null, null, null);
        assertEquals(2, desp.get("totalGatilhosIdentificados"));
        List<?> gatilhos = (List<?>) desp.get("gatilhos");
        assertEquals(2, gatilhos.size());

        // Caso de lista vazia
        when(submissaoRepositorio.listarTodas()).thenReturn(List.of());
        Map<String, Object> despVazio =
                service.obterDesempenhoGatilhos(null, null, null, null, null, null);
        assertEquals(0, despVazio.get("totalGatilhos"));
    }

    @Test
    @DisplayName(
            "Deve cobrir filtros de cenario, unidade, periodo e datas nulas em filtrarSubmissoesAvaliadas")
    void deveCobrirFiltrosSubmissoesAvaliadas() {
        LocalDateTime agora = LocalDateTime.of(2026, 5, 10, 10, 0);
        SubmissaoAtividade sub =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, agora);

        // Submissão não avaliada (deve ser descartada)
        SubmissaoAtividade subNaoAvaliada =
                criarSubmissaoAvaliadaMock(2L, 10L, "2026.1", 100L, 1000L, 4, agora);
        subNaoAvaliada.setStatus(StatusSubmissao.SUBMETIDA);

        // Submissão com datas nulas
        SubmissaoAtividade subSemData =
                criarSubmissaoAvaliadaMock(3L, 10L, "2026.1", 100L, 1000L, 4, null);
        subSemData.setDataSubmissao(null);
        subSemData.getAtividade().setDataInicio(null);

        // Submissao sem atividade
        SubmissaoAtividade subSemAtiv = new SubmissaoAtividade();
        subSemAtiv.setId(4L);
        subSemAtiv.setStatus(StatusSubmissao.AVALIADA);

        // Submissao sem turma
        SubmissaoAtividade subSemTurma =
                criarSubmissaoAvaliadaMock(5L, 10L, "2026.1", 100L, 1000L, 4, agora);
        subSemTurma.getAtividade().setTurma(null);

        // Submissao sem caso
        SubmissaoAtividade subSemCaso =
                criarSubmissaoAvaliadaMock(6L, 10L, "2026.1", 100L, 1000L, 4, agora);
        subSemCaso.getAtividade().setCasoClinico(null);

        // Submissao sem unidade
        SubmissaoAtividade subSemUnid =
                criarSubmissaoAvaliadaMock(7L, 10L, "2026.1", 100L, 1000L, 4, agora);
        subSemUnid.getAtividade().getCasoClinico().setUnidadeHospitalar(null);

        // Submissao com turma divergente
        SubmissaoAtividade subTurmaDiferente =
                criarSubmissaoAvaliadaMock(8L, 999L, "2026.1", 100L, 1000L, 4, agora);

        // Submissao com periodo divergente
        SubmissaoAtividade subPeriodoDiferente =
                criarSubmissaoAvaliadaMock(9L, 10L, "2025.2", 100L, 1000L, 4, agora);

        // Submissao com cenario divergente
        SubmissaoAtividade subCenarioDiferente =
                criarSubmissaoAvaliadaMock(10L, 10L, "2026.1", 888L, 1000L, 4, agora);

        // Submissao com unidade divergente
        SubmissaoAtividade subUnidadeDiferente =
                criarSubmissaoAvaliadaMock(11L, 10L, "2026.1", 100L, 777L, 4, agora);

        // Submissao com data antes do inicio
        SubmissaoAtividade subDataAntes =
                criarSubmissaoAvaliadaMock(
                        12L, 10L, "2026.1", 100L, 1000L, 4, LocalDateTime.of(2025, 1, 1, 10, 0));

        // Submissao com data depois do fim
        SubmissaoAtividade subDataDepois =
                criarSubmissaoAvaliadaMock(
                        13L, 10L, "2026.1", 100L, 1000L, 4, LocalDateTime.of(2027, 1, 1, 10, 0));

        // Submissao com dataSubmissao nula mas com dataInicio valida
        SubmissaoAtividade subDataSubmissaoNulaComDataInicio =
                criarSubmissaoAvaliadaMock(14L, 10L, "2026.1", 100L, 1000L, 4, agora);
        subDataSubmissaoNulaComDataInicio.setDataSubmissao(null);
        subDataSubmissaoNulaComDataInicio.getAtividade().setDataInicio(agora);

        when(submissaoRepositorio.listarTodas())
                .thenReturn(
                        List.of(
                                sub,
                                subNaoAvaliada,
                                subSemData,
                                subSemAtiv,
                                subSemTurma,
                                subSemCaso,
                                subSemUnid,
                                subTurmaDiferente,
                                subPeriodoDiferente,
                                subCenarioDiferente,
                                subUnidadeDiferente,
                                subDataAntes,
                                subDataDepois,
                                subDataSubmissaoNulaComDataInicio));
        when(gatilhoAchadoRepositorio.listarTodos()).thenReturn(List.of());

        // 1. Apenas filtro turmaId:
        Map<String, Object> rTurma =
                service.obterDesempenhoGatilhos(10L, null, null, null, null, null);
        assertNotNull(rTurma);

        // 2. Apenas filtro periodoLetivo:
        Map<String, Object> rPeriodo =
                service.obterDesempenhoGatilhos(null, "2026.1", null, null, null, null);
        assertNotNull(rPeriodo);

        // 3. Filtro periodoLetivo em branco (isBlank):
        Map<String, Object> rPeriodoBranco =
                service.obterDesempenhoGatilhos(null, "   ", null, null, null, null);
        assertNotNull(rPeriodoBranco);

        // 4. Apenas filtro cenarioId:
        Map<String, Object> rCenario =
                service.obterDesempenhoGatilhos(null, null, 100L, null, null, null);
        assertNotNull(rCenario);

        // 5. Apenas filtro unidadeId:
        Map<String, Object> rUnidade =
                service.obterDesempenhoGatilhos(null, null, null, 1000L, null, null);
        assertNotNull(rUnidade);

        // 6. Apenas filtro dataInicio:
        Map<String, Object> rDataInicio =
                service.obterDesempenhoGatilhos(
                        null, null, null, null, LocalDate.of(2026, 1, 1), null);
        assertNotNull(rDataInicio);

        // 7. Apenas filtro dataFim:
        Map<String, Object> rDataFim =
                service.obterDesempenhoGatilhos(
                        null, null, null, null, null, LocalDate.of(2026, 12, 31));
        assertNotNull(rDataFim);

        // 8. Filtrando com todos os filtros ativos: passam sub e subDataSubmissaoNulaComDataInicio
        Map<String, Object> r1 =
                service.obterDesempenhoGatilhos(
                        10L,
                        "2026.1",
                        100L,
                        1000L,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31));
        assertNotNull(r1);

        // 9. Filtro com divergência de cenario e unidade
        Map<String, Object> r2 =
                service.obterDesempenhoGatilhos(
                        10L,
                        "2026.2",
                        999L,
                        9999L,
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 6, 30));
        assertEquals(0, r2.get("totalGatilhos"));
    }

    @Test
    @DisplayName("Deve cobrir ramos de busca, datas e fallback de atendimento em obterQuadroResumo")
    void deveCobrirRamosQuadroResumo() {
        LocalDateTime agora = LocalDateTime.of(2026, 3, 15, 10, 0);
        SubmissaoAtividade sub =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, agora);
        sub.getAtividade().getCasoClinico().setNumeroAtendimento(null); // fallback "PRT-" + id
        sub.getAtividade().getCasoClinico().setTempoPermanenciaDias(null); // fallback 1
        sub.setDataSubmissao(null); // dataSubmissao nula

        ModuloGtt mod = new ModuloGtt(1L, "CUIDADOS", "Cuidados", "", true, null);
        GatilhoGtt gSemModulo = new GatilhoGtt(2L, "G2", null, "Desc", null, true);
        GatilhoGtt gComModulo = new GatilhoGtt(1L, "C1", mod, "PCR", null, true);

        SubmissaoGatilho aSemModulo =
                new SubmissaoGatilho(1L, sub, gSemModulo, null, true, "Dano", true, null);
        SubmissaoGatilho aComModulo =
                new SubmissaoGatilho(
                        2L,
                        sub,
                        gComModulo,
                        null,
                        true,
                        "Dano",
                        false,
                        GravidadeNccMerp.CATEGORIA_E);
        SubmissaoGatilho aSemDano =
                new SubmissaoGatilho(3L, sub, null, null, false, null, false, null);

        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub));
        when(gatilhoAchadoRepositorio.listarPorSubmissaoId(1L))
                .thenReturn(List.of(aSemModulo, aComModulo, aSemDano));

        // Busca por número de atendimento fallback "PRT-100", caso "Caso de Teste", unidade "UTI",
        // auditor "Discente"
        Map<String, Object> qPront =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "CUIDADOS",
                        "CATEGORIA_E",
                        false,
                        false,
                        "prt-100",
                        -1,
                        -1);
        assertEquals(1, qPront.get("total"));

        Map<String, Object> qCaso =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        "caso de teste",
                        0,
                        10);
        assertEquals(1, qCaso.get("total"));

        Map<String, Object> qUnid =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, null, false, "uti", 0, 10);
        assertEquals(1, qUnid.get("total"));

        // Paginação além do total de registros
        Map<String, Object> qAlem =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, null, false, null, 5, 10);
        assertEquals(1, qAlem.get("total"));
        assertTrue(((List<?>) qAlem.get("itens")).isEmpty());

        // Testar filtros com divergência em obterQuadroResumo
        Map<String, Object> qDivMod =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "OUTRO_MOD",
                        null,
                        null,
                        false,
                        null,
                        0,
                        10);
        assertEquals(0, qDivMod.get("total"));

        Map<String, Object> qDivGrav =
                service.obterQuadroResumo(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "CATEGORIA_I",
                        null,
                        false,
                        null,
                        0,
                        10);
        assertEquals(0, qDivGrav.get("total"));

        Map<String, Object> qDivAdm =
                service.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, true, true, null, 0, 10);
        assertNotNull(qDivAdm);
    }

    @Test
    @DisplayName("Deve cobrir branches de modulo nulo e gatilho nulo em obterDesempenhoGatilhos")
    void deveCobrirGatilhoNuloEmDesempenho() {
        SubmissaoAtividade sub =
                criarSubmissaoAvaliadaMock(1L, 10L, "2026.1", 100L, 1000L, 4, LocalDateTime.now());
        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub));

        GatilhoGtt gSemModulo = new GatilhoGtt(1L, "G1", null, "Descricao", null, true);
        SubmissaoGatilho a1 =
                new SubmissaoGatilho(1L, sub, gSemModulo, null, true, "Dano", false, null);
        SubmissaoGatilho aSemGatilho =
                new SubmissaoGatilho(2L, sub, null, null, false, null, false, null);

        when(gatilhoAchadoRepositorio.listarTodos()).thenReturn(List.of(a1, aSemGatilho));

        Map<String, Object> desp =
                service.obterDesempenhoGatilhos(null, null, null, null, null, null);
        assertEquals(1, desp.get("totalGatilhosIdentificados"));
    }

    @Test
    @DisplayName(
            "Deve cobrir indicadores quando não houver submissões nem gatilhos (divisões por zero)")
    void deveCobrirDivisoesPorZeroEmIndicadores() {
        when(submissaoRepositorio.listarTodas()).thenReturn(List.of());

        Map<String, Object> res =
                service.calcularIndicadoresIndividuais(
                        null, null, null, null, null, null, null, null, null);
        assertEquals(0, res.get("totalProntuariosAuditados"));
        assertEquals(0, res.get("totalDiasInternacao"));
        assertEquals(0.0, res.get("taxaEaPorMilDias"));
        assertEquals(0.0, res.get("taxaEaPorCemInternacoes"));
        assertEquals(0.0, res.get("percentualInternacoesComEa"));
        assertEquals(0.0, res.get("razaoRendimentoGatilho"));
    }
}
