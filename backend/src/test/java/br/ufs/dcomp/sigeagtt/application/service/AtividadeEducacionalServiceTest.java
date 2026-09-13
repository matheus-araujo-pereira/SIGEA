package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosPainelAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosSalvarAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.ItemAtividadeResumo;
import br.ufs.dcomp.sigeagtt.domain.ports.output.AtividadeEducacionalRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CasoClinicoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaAlunoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtividadeEducacionalServiceTest {

    @Mock private AtividadeEducacionalRepositoryPort atividadeRepositorio;
    @Mock private TurmaRepositoryPort turmaRepositorio;
    @Mock private TurmaAlunoRepositoryPort turmaAlunoRepositorio;
    @Mock private CasoClinicoRepositoryPort casoRepositorio;
    @Mock private SubmissaoAtividadeRepositoryPort submissaoRepositorio;

    @InjectMocks private AtividadeEducacionalService service;

    private AtividadeEducacional criarAtividadeMock(Long id, Long profId) {
        Usuario prof = new Usuario();
        prof.setId(profId);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setProfessorResponsavel(prof);

        CasoClinico caso = new CasoClinico();
        caso.setId(2L);
        caso.setProfessorCriador(prof);

        AtividadeEducacional a = new AtividadeEducacional();
        a.setId(id);
        a.setTurma(turma);
        a.setCasoClinico(caso);
        a.setTitulo("Atividade 1");
        return a;
    }

    @Test
    @DisplayName("Deve listar atividades para admin e para professor com contadores")
    void deveListarAtividades() {
        AtividadeEducacional a = criarAtividadeMock(1L, 10L);
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        when(atividadeRepositorio.listarTodas()).thenReturn(List.of(a));
        when(atividadeRepositorio.listarPorProfessorId(10L)).thenReturn(List.of(a));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(20L);

        SubmissaoAtividade s1 = new SubmissaoAtividade();
        s1.setStatus(StatusSubmissao.SUBMETIDA);
        SubmissaoAtividade s2 = new SubmissaoAtividade();
        s2.setStatus(StatusSubmissao.AVALIADA);
        SubmissaoAtividade s3 = new SubmissaoAtividade();
        s3.setStatus(StatusSubmissao.EM_ANDAMENTO);

        when(submissaoRepositorio.listarPorAtividadeId(1L)).thenReturn(List.of(s1, s2, s3));

        List<ItemAtividadeResumo> listaAdmin = service.listar(admin);
        assertEquals(1, listaAdmin.size());
        assertEquals(20, listaAdmin.get(0).totalAlunos());
        assertEquals(2, listaAdmin.get(0).totalSubmissoes());
        assertEquals(1, listaAdmin.get(0).totalAvaliadas());

        List<ItemAtividadeResumo> listaProf = service.listar(prof);
        assertEquals(1, listaProf.size());
    }

    @Test
    @DisplayName("Deve buscar atividade por ID com contadores")
    void deveBuscarPorIdComSucesso() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(15L);
        SubmissaoAtividade s1 = new SubmissaoAtividade(a, new Usuario());
        s1.setStatus(StatusSubmissao.EM_ANDAMENTO);
        SubmissaoAtividade s2 = new SubmissaoAtividade(a, new Usuario());
        s2.setStatus(StatusSubmissao.SUBMETIDA);
        SubmissaoAtividade s3 = new SubmissaoAtividade(a, new Usuario());
        s3.setStatus(StatusSubmissao.AVALIADA);
        when(submissaoRepositorio.listarPorAtividadeId(5L)).thenReturn(List.of(s1, s2, s3));

        ItemAtividadeResumo resumo = service.buscarPorId(5L);
        assertEquals(5L, resumo.atividade().getId());
        assertEquals(15, resumo.totalAlunos());
        assertEquals(2, resumo.totalSubmissoes());
        assertEquals(1, resumo.totalAvaliadas());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar atividade inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(atividadeRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar painel da atividade com progresso e media de notas")
    void deveBuscarPainelAtividade() {
        AtividadeEducacional a = criarAtividadeMock(1L, 10L);
        Usuario prof = a.getTurma().getProfessorResponsavel();

        when(atividadeRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a));

        Usuario a1 = new Usuario();
        a1.setId(101L);
        a1.setNomeCompleto("Aluno 1");
        Usuario a2 = new Usuario();
        a2.setId(102L);
        a2.setNomeCompleto("Aluno 2");
        Usuario a3 = new Usuario();
        a3.setId(103L);
        a3.setNomeCompleto("Aluno 3");

        TurmaAluno ta1 = new TurmaAluno(a.getTurma(), a1);
        TurmaAluno ta2 = new TurmaAluno(a.getTurma(), a2);
        TurmaAluno ta3 = new TurmaAluno(a.getTurma(), a3);

        when(turmaAlunoRepositorio.listarPorTurmaId(1L)).thenReturn(List.of(ta1, ta2, ta3));

        SubmissaoAtividade sub1 = new SubmissaoAtividade(a, a1);
        sub1.setId(201L);
        sub1.setStatus(StatusSubmissao.SUBMETIDA);

        SubmissaoAtividade sub2 = new SubmissaoAtividade(a, a2);
        sub2.setId(202L);
        sub2.setStatus(StatusSubmissao.AVALIADA);
        sub2.setNota(new BigDecimal("8.50"));

        when(submissaoRepositorio.listarPorAtividadeId(1L)).thenReturn(List.of(sub1, sub2));

        DadosPainelAtividade painel = service.buscarPainelAtividade(1L, prof);
        assertEquals(3, painel.totalAlunosTurma());
        assertEquals(2, painel.totalSubmissoes());
        assertEquals(1, painel.totalPendentesCorrecao());
        assertEquals(1, painel.totalAvaliadas());
        assertEquals(new BigDecimal("8.50"), painel.mediaNotas());
        assertEquals(3, painel.alunos().size());

        // Testar com Administrador
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.buscarPainelAtividade(1L, admin));
    }

    @Test
    @DisplayName("Deve lancar excecao no painel se outro professor tentar acessar")
    void deveLancarExcecaoPainelOutroProfessor() {
        AtividadeEducacional a = criarAtividadeMock(1L, 10L);
        Usuario outroProf = new Usuario();
        outroProf.setId(20L);
        outroProf.setPerfil(PerfilUsuario.PROFESSOR);

        when(atividadeRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a));

        assertThrows(
                AcessoProibidoException.class, () -> service.buscarPainelAtividade(1L, outroProf));
    }

    @Test
    @DisplayName("Deve salvar atividade com sucesso")
    void deveSalvarAtividadeComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setProfessorResponsavel(prof);

        CasoClinico caso = new CasoClinico();
        caso.setId(2L);
        caso.setProfessorCriador(prof);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(caso));
        when(atividadeRepositorio.salvar(any(AtividadeEducacional.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(10L);

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(
                        1L,
                        2L,
                        "Auditoria Sepse",
                        "Orientações",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(5),
                        25,
                        true);

        ItemAtividadeResumo salvo = service.salvar(prof, cmd);
        assertEquals("Auditoria Sepse", salvo.atividade().getTitulo());
        assertEquals(10, salvo.totalAlunos());
    }

    @Test
    @DisplayName("Deve lancar excecao ao salvar atividade com turma ou caso de outro professor")
    void deveLancarExcecaoAoSalvarTurmaOuCasoDeOutro() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Usuario outroProf = new Usuario();
        outroProf.setId(20L);

        Turma turmaDeOutro = new Turma();
        turmaDeOutro.setId(1L);
        turmaDeOutro.setProfessorResponsavel(outroProf);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(turmaDeOutro));

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(1L, 2L, "Titulo", "Ori", null, null, 20, true);

        assertThrows(RegraNegocioException.class, () -> service.salvar(prof, cmd));

        Turma turmaMinha = new Turma();
        turmaMinha.setId(1L);
        turmaMinha.setProfessorResponsavel(prof);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(turmaMinha));

        CasoClinico casoDeOutro = new CasoClinico();
        casoDeOutro.setId(2L);
        casoDeOutro.setProfessorCriador(outroProf);
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(casoDeOutro));

        assertThrows(RegraNegocioException.class, () -> service.salvar(prof, cmd));
    }

    @Test
    @DisplayName("Deve atualizar atividade com sucesso")
    void deveAtualizarAtividadeComSucesso() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario prof = a.getTurma().getProfessorResponsavel();

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a.getTurma()));
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(a.getCasoClinico()));
        when(atividadeRepositorio.salvar(any(AtividadeEducacional.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(10L);

        SubmissaoAtividade s1 = new SubmissaoAtividade(a, new Usuario());
        s1.setStatus(StatusSubmissao.EM_ANDAMENTO);
        SubmissaoAtividade s2 = new SubmissaoAtividade(a, new Usuario());
        s2.setStatus(StatusSubmissao.SUBMETIDA);
        SubmissaoAtividade s3 = new SubmissaoAtividade(a, new Usuario());
        s3.setStatus(StatusSubmissao.AVALIADA);
        when(submissaoRepositorio.listarPorAtividadeId(5L)).thenReturn(List.of(s1, s2, s3));

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(
                        1L,
                        2L,
                        "Novo Titulo",
                        "Novas Orientações",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3),
                        20,
                        false);

        ItemAtividadeResumo atualizado = service.atualizar(5L, prof, cmd);
        assertEquals("Novo Titulo", atualizado.atividade().getTitulo());
        assertFalse(atualizado.atividade().getAtiva());
        assertEquals(2, atualizado.totalSubmissoes());
        assertEquals(1, atualizado.totalAvaliadas());
    }

    @Test
    @DisplayName("Deve atualizar atividade como Administrador e com caso sem criador e ativa nula")
    void deveAtualizarAtividadeComoAdminECasoSemCriador() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        CasoClinico casoSemCriador = new CasoClinico();
        casoSemCriador.setId(2L);
        casoSemCriador.setProfessorCriador(null);

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a.getTurma()));
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(casoSemCriador));
        when(atividadeRepositorio.salvar(any(AtividadeEducacional.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(10L);
        when(submissaoRepositorio.listarPorAtividadeId(5L)).thenReturn(List.of());

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(
                        1L,
                        2L,
                        "Novo Titulo",
                        "Novas Orientações",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3),
                        null,
                        null);

        ItemAtividadeResumo atualizado = service.atualizar(5L, admin, cmd);
        assertEquals("Novo Titulo", atualizado.atividade().getTitulo());
    }

    @Test
    @DisplayName("Deve atualizar atividade com professor quando caso nao tiver professorCriador")
    void deveAtualizarAtividadeComProfessorQuandoCasoSemCriador() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario prof = a.getTurma().getProfessorResponsavel();

        CasoClinico casoSemCriador = new CasoClinico();
        casoSemCriador.setId(2L);
        casoSemCriador.setProfessorCriador(null);

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a.getTurma()));
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(casoSemCriador));
        when(atividadeRepositorio.salvar(any(AtividadeEducacional.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(10L);
        when(submissaoRepositorio.listarPorAtividadeId(5L)).thenReturn(List.of());

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(
                        1L,
                        2L,
                        "Novo Titulo",
                        "Novas Orientações",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3),
                        30,
                        true);

        ItemAtividadeResumo atualizado = service.atualizar(5L, prof, cmd);
        assertEquals("Novo Titulo", atualizado.atividade().getTitulo());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar atividade se usuario nao tiver permissao")
    void deveLancarExcecaoAoAtualizarSemPermissao() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario outroProf = new Usuario();
        outroProf.setId(99L);
        outroProf.setPerfil(PerfilUsuario.PROFESSOR);

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));

        DadosSalvarAtividade cmd =
                new DadosSalvarAtividade(1L, 2L, "Titulo", "Ori", null, null, 20, true);

        assertThrows(AcessoProibidoException.class, () -> service.atualizar(5L, outroProf, cmd));
    }

    @Test
    @DisplayName("Deve excluir atividade com sucesso pelo criador ou admin")
    void deveExcluirComSucesso() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario prof = a.getTurma().getProfessorResponsavel();

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        doNothing().when(atividadeRepositorio).excluir(5L);

        assertDoesNotThrow(() -> service.excluir(5L, prof));

        // Testar com Administrador
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.excluir(5L, admin));
    }

    @Test
    @DisplayName("Deve lancar excecao ao excluir por outro professor")
    void deveLancarExcecaoAoExcluirPorOutroProfessor() {
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        Usuario outroProf = new Usuario();
        outroProf.setId(99L);
        outroProf.setPerfil(PerfilUsuario.PROFESSOR);

        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));

        assertThrows(AcessoProibidoException.class, () -> service.excluir(5L, outroProf));
    }

    @Test
    @DisplayName(
            "Deve cobrir casos limites de buscarPainelAtividade (nota nula, total avaliadas zero, submissão em andamento)")
    void deveCobrirCasosLimitesPainel() {
        AtividadeEducacional a = criarAtividadeMock(1L, 10L);
        Usuario prof = a.getTurma().getProfessorResponsavel();

        when(atividadeRepositorio.buscarPorId(1L)).thenReturn(Optional.of(a));

        Usuario a1 = new Usuario();
        a1.setId(101L);
        TurmaAluno ta1 = new TurmaAluno(a.getTurma(), a1);
        when(turmaAlunoRepositorio.listarPorTurmaId(1L)).thenReturn(List.of(ta1));

        // Submissão AVALIADA mas com nota nula
        SubmissaoAtividade subSemNota = new SubmissaoAtividade(a, a1);
        subSemNota.setId(201L);
        subSemNota.setStatus(StatusSubmissao.AVALIADA);
        subSemNota.setNota(null);

        when(submissaoRepositorio.listarPorAtividadeId(1L)).thenReturn(List.of(subSemNota));

        DadosPainelAtividade painel = service.buscarPainelAtividade(1L, prof);
        assertEquals(1, painel.totalAvaliadas());
        assertEquals(new BigDecimal("0.00"), painel.mediaNotas());

        // Submissão em andamento
        subSemNota.setStatus(StatusSubmissao.EM_ANDAMENTO);
        DadosPainelAtividade painelEmAndamento = service.buscarPainelAtividade(1L, prof);
        assertEquals(0, painelEmAndamento.totalAvaliadas());
        assertNull(painelEmAndamento.mediaNotas());

        // Atividade não encontrada
        when(atividadeRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPainelAtividade(999L, prof));
    }

    @Test
    @DisplayName(
            "Deve cobrir salvar e atualizar com valores nulos, admin e casos sem professor criador")
    void deveCobrirSalvarEAtualizarRamosFaltantes() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setProfessorResponsavel(prof);

        CasoClinico casoSemCriador = new CasoClinico();
        casoSemCriador.setId(2L);
        casoSemCriador.setProfessorCriador(null);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(casoRepositorio.buscarPorId(2L)).thenReturn(Optional.of(casoSemCriador));
        when(atividadeRepositorio.salvar(any(AtividadeEducacional.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(5L);

        // Salvar com tempoLimiteMinutos e ativa nulos, e caso sem criador
        DadosSalvarAtividade cmdNulos =
                new DadosSalvarAtividade(
                        1L,
                        2L,
                        "Titulo",
                        "Ori",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        null,
                        null);
        ItemAtividadeResumo res = service.salvar(prof, cmdNulos);
        assertEquals(20, res.atividade().getTempoLimiteMinutos());
        assertTrue(res.atividade().getAtiva());

        // Salvar por admin
        assertDoesNotThrow(() -> service.salvar(admin, cmdNulos));

        // Atualizar por admin
        AtividadeEducacional a = criarAtividadeMock(5L, 10L);
        when(atividadeRepositorio.buscarPorId(5L)).thenReturn(Optional.of(a));
        when(submissaoRepositorio.listarPorAtividadeId(5L)).thenReturn(List.of());
        assertDoesNotThrow(() -> service.atualizar(5L, admin, cmdNulos));

        // Atualizar falhando por turma de outro ou caso de outro
        Usuario outroProf = new Usuario();
        outroProf.setId(99L);
        Turma turmaDeOutro = new Turma();
        turmaDeOutro.setId(10L);
        turmaDeOutro.setProfessorResponsavel(outroProf);
        when(turmaRepositorio.buscarPorId(10L)).thenReturn(Optional.of(turmaDeOutro));

        DadosSalvarAtividade cmdTurmaOutro =
                new DadosSalvarAtividade(10L, 2L, "T", "O", null, null, 20, true);
        assertThrows(RegraNegocioException.class, () -> service.atualizar(5L, prof, cmdTurmaOutro));

        CasoClinico casoDeOutro = new CasoClinico();
        casoDeOutro.setId(20L);
        casoDeOutro.setProfessorCriador(outroProf);
        when(casoRepositorio.buscarPorId(20L)).thenReturn(Optional.of(casoDeOutro));

        DadosSalvarAtividade cmdCasoOutro =
                new DadosSalvarAtividade(1L, 20L, "T", "O", null, null, 20, true);
        assertThrows(RegraNegocioException.class, () -> service.atualizar(5L, prof, cmdCasoOutro));

        // Erros de recursos não encontrados
        when(turmaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        DadosSalvarAtividade cmdTurmaInexistente =
                new DadosSalvarAtividade(999L, 2L, "T", "O", null, null, 20, true);
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.salvar(prof, cmdTurmaInexistente));
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.atualizar(5L, prof, cmdTurmaInexistente));

        when(casoRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        DadosSalvarAtividade cmdCasoInexistente =
                new DadosSalvarAtividade(1L, 999L, "T", "O", null, null, 20, true);
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.salvar(prof, cmdCasoInexistente));
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.atualizar(5L, prof, cmdCasoInexistente));

        when(atividadeRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());
        assertThrows(
                RecursoNaoEncontradoException.class, () -> service.atualizar(999L, prof, cmdNulos));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluir(999L, prof));
    }
}
