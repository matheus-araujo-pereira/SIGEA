package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.TurmaUseCase.ItemTurmaComTotal;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaAlunoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @Mock private TurmaRepositoryPort turmaRepositorio;
    @Mock private TurmaAlunoRepositoryPort turmaAlunoRepositorio;
    @Mock private UsuarioRepositoryPort usuarioRepositorio;

    @InjectMocks private TurmaService service;

    @Test
    @DisplayName("Deve listar turmas por professor ou todas quando professor nulo")
    void deveListarTurmas() {
        Turma t = new Turma();
        t.setId(1L);

        when(turmaRepositorio.listarPorProfessorId(10L)).thenReturn(List.of(t));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(5L);

        List<ItemTurmaComTotal> listaProf = service.listar(10L);
        assertEquals(1, listaProf.size());
        assertEquals(5L, listaProf.get(0).totalAlunos());

        when(turmaRepositorio.listarTodas()).thenReturn(List.of(t));
        List<ItemTurmaComTotal> listaTodas = service.listar(null);
        assertEquals(1, listaTodas.size());
    }

    @Test
    @DisplayName("Deve buscar turma por ID com total de alunos")
    void deveBuscarPorIdComSucesso() {
        Turma t = new Turma();
        t.setId(2L);
        when(turmaRepositorio.buscarPorId(2L)).thenReturn(Optional.of(t));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(2L)).thenReturn(10L);

        ItemTurmaComTotal res = service.buscarPorId(2L);
        assertEquals(2L, res.turma().getId());
        assertEquals(10L, res.totalAlunos());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar turma inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(turmaRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar turma com professor ou administrador")
    void deveCadastrarTurmaComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(prof));
        when(turmaRepositorio.salvar(any(Turma.class)))
                .thenAnswer(
                        inv -> {
                            Turma arg = inv.getArgument(0);
                            arg.setId(1L);
                            return arg;
                        });

        ItemTurmaComTotal res = service.cadastrar(10L, "med001", "2026.1", "2026/1");
        assertEquals("MED001", res.turma().getCodigoDisciplina());
        assertEquals("2026.1", res.turma().getPeriodoLetivo());
        assertEquals(0L, res.totalAlunos());

        // Testar com Administrador
        prof.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.cadastrar(10L, "MED002", "2026.1", "2026/1"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar turma com professor inexistente ou nao docente")
    void deveLancarExcecaoAoCadastrarTurmaInvalida() {
        when(usuarioRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.cadastrar(99L, "MED001", "2026.1", "2026/1"));

        Usuario aluno = new Usuario();
        aluno.setId(5L);
        aluno.setPerfil(PerfilUsuario.ALUNO);
        when(usuarioRepositorio.buscarPorId(5L)).thenReturn(Optional.of(aluno));

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.cadastrar(5L, "MED001", "2026.1", "2026/1"));
        assertTrue(ex.getMessage().contains("Apenas usuários com perfil docente"));
    }

    @Test
    @DisplayName("Deve editar turma com sucesso")
    void deveEditarTurmaComSucesso() {
        Turma t = new Turma();
        t.setId(1L);
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(prof));
        when(turmaRepositorio.salvar(any(Turma.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(3L);

        ItemTurmaComTotal res = service.editar(1L, 10L, "med002", "2026.2", "2026/2");
        assertEquals("MED002", res.turma().getCodigoDisciplina());
        assertEquals(3L, res.totalAlunos());
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar turma inexistente ou docente invalido")
    void deveLancarExcecaoAoEditarInvalido() {
        when(turmaRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(99L, 10L, "MED", "2026.1", "2026/1"));

        Turma t = new Turma();
        t.setId(1L);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(88L)).thenReturn(Optional.empty());
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(1L, 88L, "MED", "2026.1", "2026/1"));

        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);
        when(usuarioRepositorio.buscarPorId(77L)).thenReturn(Optional.of(aluno));
        assertThrows(
                RegraNegocioException.class,
                () -> service.editar(1L, 77L, "MED", "2026.1", "2026/1"));
    }

    @Test
    @DisplayName("Deve excluir turma com sucesso")
    void deveExcluirComSucesso() {
        Turma t = new Turma();
        t.setId(1L);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        doNothing().when(turmaRepositorio).excluir(1L);

        assertDoesNotThrow(() -> service.excluir(1L));
        verify(turmaRepositorio).excluir(1L);
    }

    @Test
    @DisplayName("Deve alternar status da turma")
    void deveAlternarStatus() {
        Turma t = new Turma();
        t.setId(1L);
        t.setAtiva(true);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(turmaRepositorio.salvar(any(Turma.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(0L);

        ItemTurmaComTotal res = service.alternarStatus(1L);
        assertFalse(res.turma().getAtiva());

        // Alternar quando inativa
        t.setAtiva(false);
        ItemTurmaComTotal res2 = service.alternarStatus(1L);
        assertTrue(res2.turma().getAtiva());
    }

    @Test
    @DisplayName("Deve alternar status e excluir lancando excecao se turma inexistente")
    void deveLancarExcecaoAoAlternarStatusOuExcluirInexistente() {
        when(turmaRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.alternarStatus(99L));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluir(99L));
    }

    @Test
    @DisplayName("Deve editar turma com administrador com sucesso")
    void deveEditarTurmaComAdministrador() {
        Turma t = new Turma();
        t.setId(1L);
        Usuario admin = new Usuario();
        admin.setId(10L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(admin));
        when(turmaRepositorio.salvar(any(Turma.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turmaAlunoRepositorio.contarAlunosPorTurmaId(1L)).thenReturn(0L);

        ItemTurmaComTotal res = service.editar(1L, 10L, "MED001", "2026.1", "2026/1");
        assertNotNull(res);
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar ou editar turma com campos obrigatorios nulos")
    void deveFalharCadastrarOuEditarComCamposNulos() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(prof));

        assertThrows(
                RegraNegocioException.class,
                () -> service.cadastrar(10L, null, "2026.1", "2026/1"));
        assertThrows(
                RegraNegocioException.class, () -> service.cadastrar(10L, "MED", null, "2026/1"));

        // anoSemestre nulo é tratado como string vazia e não impede o cadastro
        when(turmaRepositorio.salvar(any(Turma.class))).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> service.cadastrar(10L, "MED", "2026.1", null));

        Turma t = new Turma();
        t.setId(1L);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));

        assertThrows(
                RegraNegocioException.class,
                () -> service.editar(1L, 10L, null, "2026.1", "2026/1"));
        assertThrows(
                RegraNegocioException.class, () -> service.editar(1L, 10L, "MED", null, "2026/1"));
        assertDoesNotThrow(() -> service.editar(1L, 10L, "MED", "2026.1", null));
    }

    @Test
    @DisplayName("Deve listar alunos de uma turma")
    void deveListarAlunosDaTurma() {
        Usuario aluno = new Usuario();
        aluno.setId(20L);
        TurmaAluno ta = new TurmaAluno(new Turma(), aluno);

        when(turmaAlunoRepositorio.listarPorTurmaId(1L)).thenReturn(List.of(ta));

        List<Usuario> alunos = service.listarAlunosDaTurma(1L);
        assertEquals(1, alunos.size());
        assertEquals(20L, alunos.get(0).getId());
    }

    @Test
    @DisplayName("Deve matricular aluno com sucesso")
    void deveMatricularAlunoComSucesso() {
        Turma t = new Turma();
        t.setId(1L);
        Usuario aluno = new Usuario();
        aluno.setId(20L);
        aluno.setPerfil(PerfilUsuario.ALUNO);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(20L)).thenReturn(Optional.of(aluno));
        when(turmaAlunoRepositorio.existeMatricula(1L, 20L)).thenReturn(false);
        when(turmaAlunoRepositorio.salvar(any(TurmaAluno.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.matricularAluno(1L, 20L));
        verify(turmaAlunoRepositorio).salvar(any(TurmaAluno.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao matricular aluno quando turma ou aluno inexistentes")
    void deveLancarExcecaoAoMatricularInexistentes() {
        when(turmaRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.matricularAluno(99L, 1L));

        Turma t = new Turma();
        t.setId(1L);
        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.matricularAluno(1L, 99L));
    }

    @Test
    @DisplayName("Deve lancar excecao ao matricular usuario com perfil nao aluno")
    void deveLancarExcecaoAoMatricularPerfilInvalido() {
        Turma t = new Turma();
        t.setId(1L);
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(prof));

        assertThrows(RegraNegocioException.class, () -> service.matricularAluno(1L, 10L));
    }

    @Test
    @DisplayName("Deve lancar excecao ao matricular aluno ja matriculado")
    void deveLancarExcecaoAoMatricularDuplicado() {
        Turma t = new Turma();
        t.setId(1L);
        Usuario aluno = new Usuario();
        aluno.setNomeCompleto("João");
        aluno.setPerfil(PerfilUsuario.ALUNO);

        when(turmaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepositorio.buscarPorId(20L)).thenReturn(Optional.of(aluno));
        when(turmaAlunoRepositorio.existeMatricula(1L, 20L)).thenReturn(true);

        assertThrows(ConflitoDadosException.class, () -> service.matricularAluno(1L, 20L));
    }

    @Test
    @DisplayName("Deve desmatricular aluno com sucesso")
    void deveDesmatricularComSucesso() {
        when(turmaAlunoRepositorio.existeMatricula(1L, 20L)).thenReturn(true);
        doNothing().when(turmaAlunoRepositorio).desmatricular(1L, 20L);

        assertDoesNotThrow(() -> service.desmatricularAluno(1L, 20L));
        verify(turmaAlunoRepositorio).desmatricular(1L, 20L);
    }

    @Test
    @DisplayName("Deve lancar excecao ao desmatricular aluno que nao esta matriculado")
    void deveLancarExcecaoAoDesmatricularNaoMatriculado() {
        when(turmaAlunoRepositorio.existeMatricula(1L, 20L)).thenReturn(false);

        assertThrows(RegraNegocioException.class, () -> service.desmatricularAluno(1L, 20L));
    }
}
