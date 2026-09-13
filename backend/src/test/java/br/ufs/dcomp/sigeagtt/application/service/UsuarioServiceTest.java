package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.PasswordEncoderPort;
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
class UsuarioServiceTest {

    @Mock private UsuarioRepositoryPort repositorio;
    @Mock private PasswordEncoderPort passwordEncoder;

    @InjectMocks private UsuarioService service;

    @Test
    @DisplayName("Deve listar todos os usuarios")
    void deveListarTodos() {
        when(repositorio.listarTodos()).thenReturn(List.of(new Usuario(), new Usuario()));
        List<Usuario> lista = service.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Deve buscar usuario por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        Usuario u = new Usuario();
        u.setId(5L);
        when(repositorio.buscarPorId(5L)).thenReturn(Optional.of(u));

        Usuario encontrado = service.buscarPorId(5L);
        assertEquals(5L, encontrado.getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar professor com sucesso")
    void deveCadastrarProfessorComSucesso() {
        when(repositorio.buscarPorEmail("prof@academico.ufs.br")).thenReturn(Optional.empty());
        when(passwordEncoder.codificar("Sigea@123")).thenReturn("hash123");
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario u =
                service.cadastrar(
                        "Prof. Silva", "prof@academico.ufs.br", null, PerfilUsuario.PROFESSOR);

        assertNotNull(u);
        assertEquals("Prof. Silva", u.getNomeCompleto());
        assertEquals("prof@academico.ufs.br", u.getEmail());
        assertEquals(PerfilUsuario.PROFESSOR, u.getPerfil());
        assertTrue(u.getPrimeiroAcesso());
        assertTrue(u.getAtivo());
    }

    @Test
    @DisplayName("Deve cadastrar aluno com matricula valida")
    void deveCadastrarAlunoComSucesso() {
        when(repositorio.buscarPorEmail("aluno@academico.ufs.br")).thenReturn(Optional.empty());
        when(repositorio.buscarPorMatriculaSigaa("202612345678")).thenReturn(Optional.empty());
        when(passwordEncoder.codificar(anyString())).thenReturn("hash");
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario u =
                service.cadastrar(
                        "Aluno Souza",
                        "aluno@academico.ufs.br",
                        "202612345678",
                        PerfilUsuario.ALUNO);
        assertNotNull(u);
        assertEquals("202612345678", u.getMatriculaSigaa());
    }

    @Test
    @DisplayName("Deve lancar excecao quando email invalido no cadastro")
    void deveLancarExcecaoQuandoEmailInvalidoNoCadastro() {
        assertThrows(
                RegraNegocioException.class,
                () -> service.cadastrar("Nome", null, null, PerfilUsuario.PROFESSOR));

        assertThrows(
                RegraNegocioException.class,
                () -> service.cadastrar("Nome", "  ", null, PerfilUsuario.PROFESSOR));

        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.cadastrar(
                                "Nome", "usuario@gmail.com", null, PerfilUsuario.PROFESSOR));
    }

    @Test
    @DisplayName("Deve lancar excecao quando email ja cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(repositorio.buscarPorEmail("duplicado@academico.ufs.br"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                ConflitoDadosException.class,
                () ->
                        service.cadastrar(
                                "Nome",
                                "duplicado@academico.ufs.br",
                                null,
                                PerfilUsuario.PROFESSOR));
    }

    @Test
    @DisplayName("Deve lancar excecao quando matricula de aluno for invalida no cadastro")
    void deveLancarExcecaoQuandoMatriculaAlunoInvalidaNoCadastro() {
        when(repositorio.buscarPorEmail("aluno@academico.ufs.br")).thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.cadastrar(
                                "Aluno", "aluno@academico.ufs.br", null, PerfilUsuario.ALUNO));

        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.cadastrar(
                                "Aluno", "aluno@academico.ufs.br", "123", PerfilUsuario.ALUNO));
    }

    @Test
    @DisplayName("Deve lancar excecao quando matricula de aluno ja existir no cadastro")
    void deveLancarExcecaoQuandoMatriculaAlunoJaExistirNoCadastro() {
        when(repositorio.buscarPorEmail("aluno@academico.ufs.br")).thenReturn(Optional.empty());
        when(repositorio.buscarPorMatriculaSigaa("202612345678"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                ConflitoDadosException.class,
                () ->
                        service.cadastrar(
                                "Aluno",
                                "aluno@academico.ufs.br",
                                "202612345678",
                                PerfilUsuario.ALUNO));
    }

    @Test
    @DisplayName("Deve editar usuario com sucesso")
    void deveEditarUsuarioComSucesso() {
        Usuario existente = new Usuario();
        existente.setId(10L);
        existente.setPerfil(PerfilUsuario.PROFESSOR);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorEmailEIdDiferente("novo.email@academico.ufs.br", 10L))
                .thenReturn(Optional.empty());
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario editado =
                service.editar(
                        10L,
                        "Prof Novo",
                        "novo.email@academico.ufs.br",
                        null,
                        PerfilUsuario.PROFESSOR);
        assertEquals("Prof Novo", editado.getNomeCompleto());
        assertEquals("novo.email@academico.ufs.br", editado.getEmail());
    }

    @Test
    @DisplayName("Deve lancar excecao quando editar usuario com email pertencente a outro")
    void deveLancarExcecaoQuandoEmailPertencerAOutroAoEditar() {
        Usuario existente = new Usuario();
        existente.setId(10L);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorEmailEIdDiferente("outro@academico.ufs.br", 10L))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                ConflitoDadosException.class,
                () ->
                        service.editar(
                                10L,
                                "Nome",
                                "outro@academico.ufs.br",
                                null,
                                PerfilUsuario.PROFESSOR));
    }

    @Test
    @DisplayName("Deve editar aluno validando matricula e unicidade")
    void deveEditarAlunoValidandoMatricula() {
        Usuario existente = new Usuario();
        existente.setId(10L);
        existente.setPerfil(PerfilUsuario.ALUNO);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorEmailEIdDiferente("aluno@academico.ufs.br", 10L))
                .thenReturn(Optional.empty());

        // Matrícula inválida
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.editar(
                                10L,
                                "Aluno",
                                "aluno@academico.ufs.br",
                                "abc",
                                PerfilUsuario.ALUNO));

        // Matrícula duplicada em outro discente
        when(repositorio.buscarPorMatriculaEIdDiferente("202612345678", 10L))
                .thenReturn(Optional.of(new Usuario()));
        assertThrows(
                ConflitoDadosException.class,
                () ->
                        service.editar(
                                10L,
                                "Aluno",
                                "aluno@academico.ufs.br",
                                "202612345678",
                                PerfilUsuario.ALUNO));

        // Matrícula ok
        when(repositorio.buscarPorMatriculaEIdDiferente("202612345678", 10L))
                .thenReturn(Optional.empty());
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario editado =
                service.editar(
                        10L,
                        "Aluno",
                        "aluno@academico.ufs.br",
                        "202612345678",
                        PerfilUsuario.ALUNO);
        assertEquals("202612345678", editado.getMatriculaSigaa());
    }

    @Test
    @DisplayName("Deve impedir rebaixamento de perfil do unico administrador ativo")
    void deveImpedirRebaixamentoDoUnicoAdministrador() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(admin));
        when(repositorio.buscarPorEmailEIdDiferente(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        when(repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR)).thenReturn(1L);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () ->
                                service.editar(
                                        1L,
                                        "Admin",
                                        "admin@academico.ufs.br",
                                        null,
                                        PerfilUsuario.PROFESSOR));
        assertTrue(ex.getMessage().contains("manter ao menos um Administrador ativo"));

        // Caso com mais de 1 administrador ativo - deve permitir rebaixamento
        when(repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR)).thenReturn(2L);
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        Usuario editadoAdmin =
                service.editar(
                        1L, "Admin", "admin@academico.ufs.br", null, PerfilUsuario.PROFESSOR);
        assertEquals(PerfilUsuario.PROFESSOR, editadoAdmin.getPerfil());
    }

    @Test
    @DisplayName("Deve permitir inativação de administrador quando houver mais de um ativo")
    void devePermitirInativacaoQuandoHouverMaisDeUmAdmin() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(admin));
        when(repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR)).thenReturn(2L);
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario inativado = service.inativar(1L);
        assertFalse(inativado.getAtivo());
    }

    @Test
    @DisplayName("Deve tratar matricula em branco para aluno no cadastro e edicao")
    void deveTratarMatriculaEmBrancoParaAluno() {
        when(repositorio.buscarPorEmail("aluno@academico.ufs.br")).thenReturn(Optional.empty());
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.cadastrar(
                                "Aluno", "aluno@academico.ufs.br", "   ", PerfilUsuario.ALUNO));

        Usuario aluno = new Usuario();
        aluno.setId(10L);
        aluno.setPerfil(PerfilUsuario.ALUNO);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(aluno));
        when(repositorio.buscarPorEmailEIdDiferente("aluno@academico.ufs.br", 10L))
                .thenReturn(Optional.empty());
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.editar(
                                10L,
                                "Aluno",
                                "aluno@academico.ufs.br",
                                "   ",
                                PerfilUsuario.ALUNO));
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.editar(
                                10L, "Aluno", "aluno@academico.ufs.br", null, PerfilUsuario.ALUNO));
    }

    @Test
    @DisplayName("Deve resetar senha para Sigea@123 com flag primeiroAcesso true")
    void deveResetarSenha() {
        Usuario u = new Usuario();
        u.setId(20L);
        when(repositorio.buscarPorId(20L)).thenReturn(Optional.of(u));
        when(passwordEncoder.codificar("Sigea@123")).thenReturn("novo_hash");
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resetado = service.resetarSenha(20L);
        assertEquals("novo_hash", resetado.getSenha());
        assertTrue(resetado.getPrimeiroAcesso());
    }

    @Test
    @DisplayName("Deve impedir inativacao do unico administrador ativo")
    void deveImpedirInativacaoDoUnicoAdmin() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(admin));
        when(repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR)).thenReturn(1L);

        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, () -> service.inativar(1L));
        assertTrue(ex.getMessage().contains("único Administrador ativo"));
    }

    @Test
    @DisplayName("Deve inativar usuario com sucesso")
    void deveInativarUsuarioComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(2L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        prof.setAtivo(true);
        when(repositorio.buscarPorId(2L)).thenReturn(Optional.of(prof));
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario inativado = service.inativar(2L);
        assertFalse(inativado.getAtivo());
    }

    @Test
    @DisplayName("Deve reativar usuario com sucesso")
    void deveReativarUsuarioComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(2L);
        prof.setAtivo(false);
        when(repositorio.buscarPorId(2L)).thenReturn(Optional.of(prof));
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario reativado = service.reativar(2L);
        assertTrue(reativado.getAtivo());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() {
        Usuario u = new Usuario();
        u.setId(10L);
        u.setSenha("hash_antiga");
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(u));
        when(passwordEncoder.corresponde("antiga", "hash_antiga")).thenReturn(true);
        when(passwordEncoder.corresponde("nova123", "hash_antiga")).thenReturn(false);
        when(passwordEncoder.codificar("nova123")).thenReturn("hash_nova");
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario alterado = service.alterarSenha(10L, "antiga", "nova123", "nova123");
        assertEquals("hash_nova", alterado.getSenha());
        assertFalse(alterado.getPrimeiroAcesso());
    }

    @Test
    @DisplayName("Deve lancar excecao ao alterar senha com senha atual incorreta")
    void deveLancarExcecaoAoAlterarSenhaAtualIncorreta() {
        Usuario u = new Usuario();
        u.setSenha("hash");
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.corresponde("errada", "hash")).thenReturn(false);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.alterarSenha(1L, "errada", "nova", "nova"));
        assertTrue(ex.getMessage().contains("senha atual informada está incorreta"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao alterar senha quando confirmacao diverge")
    void deveLancarExcecaoAoAlterarSenhaConfirmacaoDivergente() {
        Usuario u = new Usuario();
        u.setSenha("hash");
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.corresponde("atual", "hash")).thenReturn(true);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.alterarSenha(1L, "atual", "nova1", "nova2"));
        assertTrue(ex.getMessage().contains("confirmação da nova senha não confere"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao alterar senha quando nova senha for igual a atual")
    void deveLancarExcecaoAoAlterarSenhaNovaIgualAtual() {
        Usuario u = new Usuario();
        u.setSenha("hash");
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.corresponde("atual", "hash")).thenReturn(true);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.alterarSenha(1L, "atual", "atual", "atual"));
        assertTrue(ex.getMessage().contains("deve ser diferente da senha atual"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar ou editar usuario com nome completo nulo")
    void deveLancarExcecaoQuandoNomeCompletoNulo() {
        when(repositorio.buscarPorEmail("aluno@academico.ufs.br")).thenReturn(Optional.empty());
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.cadastrar(
                                null, "aluno@academico.ufs.br", null, PerfilUsuario.PROFESSOR));

        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(prof));
        when(repositorio.buscarPorEmailEIdDiferente(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.editar(
                                10L,
                                null,
                                "aluno@academico.ufs.br",
                                null,
                                PerfilUsuario.PROFESSOR));
    }

    @Test
    @DisplayName("Deve editar administrador mantendo perfil administrador")
    void deveEditarMantendoPerfilAdministrador() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(admin));
        when(repositorio.buscarPorEmailEIdDiferente("admin@academico.ufs.br", 1L))
                .thenReturn(Optional.empty());
        when(repositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario editado =
                service.editar(
                        1L,
                        "Novo Nome Admin",
                        "admin@academico.ufs.br",
                        null,
                        PerfilUsuario.ADMINISTRADOR);
        assertEquals(PerfilUsuario.ADMINISTRADOR, editado.getPerfil());
        assertEquals("Novo Nome Admin", editado.getNomeCompleto());
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar aluno com matricula nula")
    void deveLancarExcecaoAoEditarAlunoComMatriculaNula() {
        Usuario aluno = new Usuario();
        aluno.setId(10L);
        aluno.setPerfil(PerfilUsuario.ALUNO);
        when(repositorio.buscarPorId(10L)).thenReturn(Optional.of(aluno));
        when(repositorio.buscarPorEmailEIdDiferente(anyString(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () ->
                        service.editar(
                                10L, "Aluno", "aluno@academico.ufs.br", null, PerfilUsuario.ALUNO));
    }
}
