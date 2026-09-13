package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosGatilho;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosIshikawa;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosPdca;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosPlano5w3h;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosSalvarSubmissao;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.ItemMinhaAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.output.AtividadeEducacionalRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CategoriaEventoAdversoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.GatilhoGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoGatilhoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoIshikawaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPdcaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPlano5w3hRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissaoAtividadeServiceTest {

    @Mock private SubmissaoAtividadeRepositoryPort submissaoRepositorio;
    @Mock private AtividadeEducacionalRepositoryPort atividadeRepositorio;
    @Mock private GatilhoGttRepositoryPort gatilhoRepositorio;
    @Mock private CategoriaEventoAdversoRepositoryPort categoriaRepositorio;
    @Mock private SubmissaoGatilhoRepositoryPort submissaoGatilhoRepositorio;
    @Mock private SubmissaoIshikawaRepositoryPort submissaoIshikawaRepositorio;
    @Mock private SubmissaoPlano5w3hRepositoryPort submissaoPlano5w3hRepositorio;
    @Mock private SubmissaoPdcaRepositoryPort submissaoPdcaRepositorio;

    @InjectMocks private SubmissaoAtividadeService service;

    private AtividadeEducacional criarAtividadeCompleta(Long id) {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setNomeCompleto("Dr. Santos");

        Turma turma = new Turma();
        turma.setId(100L);
        turma.setCodigoDisciplina("MED001");
        turma.setProfessorResponsavel(prof);

        UnidadeHospitalar unid = new UnidadeHospitalar();
        unid.setSigla("UTI-A");

        CasoClinico caso = new CasoClinico();
        caso.setId(200L);
        caso.setTitulo("Caso Teste");
        caso.setUnidadeHospitalar(unid);

        AtividadeEducacional a = new AtividadeEducacional();
        a.setId(id);
        a.setTitulo("Auditoria 1");
        a.setTurma(turma);
        a.setCasoClinico(caso);
        return a;
    }

    @Test
    @DisplayName("Deve listar minhas atividades como aluno mapeando dados e submissoes")
    void deveListarMinhasAtividades() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);

        AtividadeEducacional a1 = criarAtividadeCompleta(10L);
        AtividadeEducacional a2 = criarAtividadeCompleta(20L);

        SubmissaoAtividade sub = new SubmissaoAtividade(a1, aluno);
        sub.setId(500L);
        sub.setStatus(StatusSubmissao.SUBMETIDA);

        when(atividadeRepositorio.listarParaAluno(1L)).thenReturn(List.of(a1, a2));
        when(submissaoRepositorio.listarPorAlunoId(1L)).thenReturn(List.of(sub));

        List<ItemMinhaAtividade> lista = service.listarMinhasAtividades(aluno);
        assertEquals(2, lista.size());
        assertEquals(500L, lista.get(0).submissaoId());
        assertEquals(StatusSubmissao.SUBMETIDA, lista.get(0).status());
        assertNull(lista.get(1).submissaoId());
    }

    @Test
    @DisplayName("Deve iniciar ou continuar submissao")
    void deveIniciarOuContinuarSubmissao() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);
        AtividadeEducacional a = criarAtividadeCompleta(10L);

        when(atividadeRepositorio.buscarPorId(10L)).thenReturn(Optional.of(a));

        // Caso 1: já existe
        SubmissaoAtividade existente = new SubmissaoAtividade(a, aluno);
        when(submissaoRepositorio.buscarPorAtividadeEAluno(10L, 1L))
                .thenReturn(Optional.of(existente));
        SubmissaoAtividade res1 = service.iniciarOuContinuar(10L, aluno);
        assertEquals(existente, res1);

        // Caso 2: nova
        when(submissaoRepositorio.buscarPorAtividadeEAluno(10L, 1L)).thenReturn(Optional.empty());
        when(submissaoRepositorio.salvar(any(SubmissaoAtividade.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        SubmissaoAtividade res2 = service.iniciarOuContinuar(10L, aluno);
        assertNotNull(res2);
        assertEquals(a, res2.getAtividade());
    }

    @Test
    @DisplayName("Deve lancar excecao ao iniciar atividade inexistente")
    void deveLancarExcecaoAoIniciarAtividadeInexistente() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);
        when(atividadeRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class, () -> service.iniciarOuContinuar(99L, aluno));
    }

    @Test
    @DisplayName("Deve buscar submissao por ID com todos os componentes vinculados")
    void deveBuscarPorIdComSucesso() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);
        aluno.setPerfil(PerfilUsuario.ALUNO);

        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), aluno);
        sub.setId(10L);

        when(submissaoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(sub));
        when(submissaoGatilhoRepositorio.listarPorSubmissaoId(10L))
                .thenReturn(List.of(new SubmissaoGatilho()));
        when(submissaoIshikawaRepositorio.buscarPorSubmissaoId(10L))
                .thenReturn(Optional.of(new SubmissaoIshikawa()));
        when(submissaoPlano5w3hRepositorio.listarPorSubmissaoId(10L))
                .thenReturn(List.of(new SubmissaoPlano5w3h()));
        when(submissaoPdcaRepositorio.buscarPorSubmissaoId(10L))
                .thenReturn(Optional.of(new SubmissaoPdca()));

        SubmissaoAtividade res = service.buscarPorId(10L, aluno);
        assertEquals(1, res.getAchadosGatilhos().size());
        assertNotNull(res.getIshikawa());
        assertEquals(1, res.getPlanos5w3h().size());
        assertNotNull(res.getPdca());

        // Professor e Administrador também podem visualizar
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        assertDoesNotThrow(() -> service.buscarPorId(10L, prof));

        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.buscarPorId(10L, admin));
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar submissao por outro aluno")
    void deveLancarExcecaoAoBuscarPorOutroAluno() {
        Usuario alunoDono = new Usuario();
        alunoDono.setId(1L);

        Usuario outroAluno = new Usuario();
        outroAluno.setId(2L);
        outroAluno.setPerfil(PerfilUsuario.ALUNO);

        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), alunoDono);
        sub.setId(10L);

        when(submissaoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(sub));

        assertThrows(AcessoProibidoException.class, () -> service.buscarPorId(10L, outroAluno));
    }

    @Test
    @DisplayName("Deve salvar ou submeter atividade com os 4 componentes")
    void deveSalvarOuSubmeterComSucesso() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);

        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), aluno);
        sub.setId(10L);
        sub.setStatus(StatusSubmissao.EM_ANDAMENTO);

        when(submissaoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(sub));

        GatilhoGtt gat = new GatilhoGtt();
        gat.setId(100L);
        when(gatilhoRepositorio.buscarPorId(100L)).thenReturn(Optional.of(gat));

        CategoriaEventoAdverso cat = new CategoriaEventoAdverso();
        cat.setId(200L);
        when(categoriaRepositorio.buscarPorId(200L)).thenReturn(Optional.of(cat));

        when(submissaoGatilhoRepositorio.salvar(any(SubmissaoGatilho.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(submissaoIshikawaRepositorio.salvar(any(SubmissaoIshikawa.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(submissaoPlano5w3hRepositorio.salvar(any(SubmissaoPlano5w3h.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(submissaoPdcaRepositorio.salvar(any(SubmissaoPdca.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(submissaoRepositorio.salvar(any(SubmissaoAtividade.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DadosGatilho gDto =
                new DadosGatilho(
                        100L, 200L, true, "Sangramento", false, GravidadeNccMerp.CATEGORIA_E);
        DadosIshikawa iDto = new DadosIshikawa("Efeito", "Met", "Mao", "Mat", "Med", "Amb", "Maq");
        DadosPlano5w3h pDto =
                new DadosPlano5w3h(
                        "Ação",
                        "Por que",
                        "Quem",
                        "Onde",
                        "Quando",
                        "Como",
                        new BigDecimal("100"),
                        "Medir");
        DadosPdca pdcaDto = new DadosPdca("P", "D", "C", "A");

        DadosSalvarSubmissao cmd =
                new DadosSalvarSubmissao(true, 900, List.of(gDto), iDto, List.of(pDto), pdcaDto);

        SubmissaoAtividade finalizada = service.salvarOuSubmeter(10L, aluno, cmd);
        assertEquals(StatusSubmissao.SUBMETIDA, finalizada.getStatus());
        assertEquals(900, finalizada.getTempoGastoSegundos());
        assertNotNull(finalizada.getDataSubmissao());
    }

    @Test
    @DisplayName("Deve lancar excecao se submissao ja estiver finalizada ao tentar salvar")
    void deveLancarExcecaoSeJaFinalizada() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);

        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), aluno);
        sub.setId(10L);
        sub.setStatus(StatusSubmissao.SUBMETIDA);

        when(submissaoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(sub));

        DadosSalvarSubmissao cmd = new DadosSalvarSubmissao(false, 100, null, null, null, null);
        assertThrows(RegraNegocioException.class, () -> service.salvarOuSubmeter(10L, aluno, cmd));
    }

    @Test
    @DisplayName("Deve avaliar submissao com sucesso")
    void deveAvaliarComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), new Usuario());
        sub.setId(100L);
        sub.setStatus(StatusSubmissao.SUBMETIDA);

        when(submissaoRepositorio.buscarPorId(100L)).thenReturn(Optional.of(sub));
        when(submissaoRepositorio.salvar(any(SubmissaoAtividade.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SubmissaoAtividade avaliada =
                service.avaliar(100L, prof, new BigDecimal("9.50"), "Excelente");
        assertEquals(StatusSubmissao.AVALIADA, avaliada.getStatus());
        assertEquals(new BigDecimal("9.50"), avaliada.getNota());
        assertEquals("Excelente", avaliada.getParecerDocente());
    }

    @Test
    @DisplayName("Deve listar pendentes de correcao para professor e admin")
    void deveListarPendentesCorrecao() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Turma turmaProf = new Turma();
        turmaProf.setProfessorResponsavel(prof);

        AtividadeEducacional ativ = new AtividadeEducacional();
        ativ.setTurma(turmaProf);

        SubmissaoAtividade sub1 = new SubmissaoAtividade(ativ, new Usuario());
        sub1.setId(1L);
        sub1.setStatus(StatusSubmissao.SUBMETIDA);

        SubmissaoAtividade sub2 = new SubmissaoAtividade(ativ, new Usuario());
        sub2.setId(2L);
        sub2.setStatus(StatusSubmissao.EM_ANDAMENTO);

        when(submissaoRepositorio.listarTodas()).thenReturn(List.of(sub1, sub2));

        List<SubmissaoAtividade> pendentes = service.listarPendentesCorrecao(prof);
        assertEquals(1, pendentes.size());
        assertEquals(1L, pendentes.get(0).getId());

        // Testar com Administrador
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        List<SubmissaoAtividade> pendentesAdmin = service.listarPendentesCorrecao(admin);
        assertEquals(1, pendentesAdmin.size());
    }

    @Test
    @DisplayName("Deve listar categorias ativas")
    void deveListarCategoriasAtivas() {
        CategoriaEventoAdverso c1 = new CategoriaEventoAdverso(1L, "IRAS", "Infecção", true);
        CategoriaEventoAdverso c2 = new CategoriaEventoAdverso(2L, "LPP", "Lesão", false);

        when(categoriaRepositorio.listarTodas()).thenReturn(List.of(c1, c2));

        List<CategoriaEventoAdverso> ativas = service.listarCategoriasAtivas();
        assertEquals(1, ativas.size());
        assertEquals("IRAS", ativas.get(0).getNome());
    }

    @Test
    @DisplayName("Deve cobrir ramos de erro e nulos em buscarPorId, salvarOuSubmeter e avaliar")
    void deveCobrirRamosNulosEErros() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);
        aluno.setPerfil(PerfilUsuario.ALUNO);

        Usuario outroAluno = new Usuario();
        outroAluno.setId(2L);
        outroAluno.setPerfil(PerfilUsuario.ALUNO);

        when(submissaoRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(999L, aluno));
        assertThrows(
                RecursoNaoEncontradoException.class,
                () ->
                        service.salvarOuSubmeter(
                                999L,
                                aluno,
                                new DadosSalvarSubmissao(false, 0, null, null, null, null)));
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.avaliar(999L, aluno, BigDecimal.TEN, "Parecer"));

        // Aluno tentando salvar submissão de outro
        SubmissaoAtividade subDeOutro =
                new SubmissaoAtividade(new AtividadeEducacional(), outroAluno);
        subDeOutro.setId(10L);
        when(submissaoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(subDeOutro));
        assertThrows(
                AcessoProibidoException.class,
                () ->
                        service.salvarOuSubmeter(
                                10L,
                                aluno,
                                new DadosSalvarSubmissao(false, 0, null, null, null, null)));

        // Salvar com componentes nulos, tempoGasto nulo e finalizar false
        SubmissaoAtividade subMinha = new SubmissaoAtividade(new AtividadeEducacional(), aluno);
        subMinha.setId(11L);
        subMinha.setStatus(StatusSubmissao.EM_ANDAMENTO);
        when(submissaoRepositorio.buscarPorId(11L)).thenReturn(Optional.of(subMinha));
        when(submissaoRepositorio.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        DadosSalvarSubmissao cmdVazio =
                new DadosSalvarSubmissao(false, null, null, null, null, null);
        SubmissaoAtividade salva = service.salvarOuSubmeter(11L, aluno, cmdVazio);
        assertEquals(StatusSubmissao.EM_ANDAMENTO, salva.getStatus());

        // Salvar com gatilho sem categoria, ishikawa sem efeito principal, plano 5w3h
        // com oQue em branco e nulo
        GatilhoGtt gat = new GatilhoGtt();
        gat.setId(100L);
        when(gatilhoRepositorio.buscarPorId(100L)).thenReturn(Optional.of(gat));

        DadosGatilho gSemCat = new DadosGatilho(100L, null, false, null, false, null);
        DadosGatilho gCatInexistente = new DadosGatilho(100L, 888L, false, null, false, null);
        when(categoriaRepositorio.buscarPorId(888L)).thenReturn(Optional.empty());

        DadosIshikawa ishSemEfeito = new DadosIshikawa(null, "m", "m", "m", "m", "a", "m");
        DadosPlano5w3h pEmBranco = new DadosPlano5w3h("  ", "p", "q", "o", "q", "c", null, null);
        DadosPlano5w3h pNulo = new DadosPlano5w3h(null, "p", "q", "o", "q", "c", null, null);

        DadosSalvarSubmissao cmdMisto =
                new DadosSalvarSubmissao(
                        false,
                        100,
                        List.of(gSemCat, gCatInexistente),
                        ishSemEfeito,
                        List.of(pEmBranco, pNulo),
                        null);
        SubmissaoAtividade salvaMista = service.salvarOuSubmeter(11L, aluno, cmdMisto);
        assertNotNull(salvaMista);

        // Gatilho não encontrado
        when(gatilhoRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        DadosGatilho gInexistente = new DadosGatilho(999L, null, false, null, false, null);
        DadosSalvarSubmissao cmdGatInexistente =
                new DadosSalvarSubmissao(false, 100, List.of(gInexistente), null, null, null);
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.salvarOuSubmeter(11L, aluno, cmdGatInexistente));
    }

    @Test
    @DisplayName(
            "Deve filtrar submissoes com campos intermediários nulos em listarPendentesCorrecao")
    void deveFiltrarSubmissoesCamposNulosPendentesCorrecao() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Usuario outroProf = new Usuario();
        outroProf.setId(20L);

        // sSemAtiv: atividade nula
        SubmissaoAtividade sSemAtiv = new SubmissaoAtividade();
        sSemAtiv.setStatus(StatusSubmissao.SUBMETIDA);

        // sSemTurma: turma nula
        SubmissaoAtividade sSemTurma = new SubmissaoAtividade();
        sSemTurma.setStatus(StatusSubmissao.SUBMETIDA);
        sSemTurma.setAtividade(new AtividadeEducacional());

        // sSemProf: turma sem professor
        SubmissaoAtividade sSemProf = new SubmissaoAtividade();
        sSemProf.setStatus(StatusSubmissao.SUBMETIDA);
        AtividadeEducacional atvSemProf = new AtividadeEducacional();
        atvSemProf.setTurma(new Turma());
        sSemProf.setAtividade(atvSemProf);

        // sDeOutroProf: turma de outro professor
        SubmissaoAtividade sDeOutroProf = new SubmissaoAtividade();
        sDeOutroProf.setStatus(StatusSubmissao.SUBMETIDA);
        AtividadeEducacional atvDeOutro = new AtividadeEducacional();
        Turma turmaOutro = new Turma();
        turmaOutro.setProfessorResponsavel(outroProf);
        atvDeOutro.setTurma(turmaOutro);
        sDeOutroProf.setAtividade(atvDeOutro);

        when(submissaoRepositorio.listarTodas())
                .thenReturn(List.of(sSemAtiv, sSemTurma, sSemProf, sDeOutroProf));

        List<SubmissaoAtividade> pendentes = service.listarPendentesCorrecao(prof);
        assertTrue(pendentes.isEmpty());
    }
}
