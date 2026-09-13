package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AutenticacaoUseCase.ResultadoAutenticacao;
import br.ufs.dcomp.sigeagtt.domain.ports.output.PasswordEncoderPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock private UsuarioRepositoryPort usuarioRepositorio;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenServicePort tokenService;

    @InjectMocks private AutenticacaoService service;

    @Test
    @DisplayName("Deve autenticar com sucesso quando credenciais corretas e usuario ativo")
    void deveAutenticarComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("prof@academico.ufs.br");
        usuario.setSenha("hash_senha");
        usuario.setAtivo(true);
        usuario.setPerfil(PerfilUsuario.PROFESSOR);

        when(usuarioRepositorio.buscarPorEmail("prof@academico.ufs.br"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("senha123", "hash_senha")).thenReturn(true);
        when(tokenService.gerarToken(usuario)).thenReturn("jwt.token.aqui");

        ResultadoAutenticacao resultado = service.autenticar("Prof@academico.ufs.br ", "senha123");

        assertNotNull(resultado);
        assertEquals(usuario, resultado.usuario());
        assertEquals("jwt.token.aqui", resultado.token());
    }

    @Test
    @DisplayName("Deve lancar excecao quando email nao for localizado")
    void deveLancarExcecaoQuandoEmailNaoLocalizado() {
        when(usuarioRepositorio.buscarPorEmail("inexistente@academico.ufs.br"))
                .thenReturn(Optional.empty());

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.autenticar("inexistente@academico.ufs.br", "123456"));
        assertTrue(ex.getMessage().contains("e-mail institucional não localizado"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando email for nulo")
    void deveLancarExcecaoQuandoEmailNulo() {
        when(usuarioRepositorio.buscarPorEmail("")).thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class, () -> service.autenticar(null, "123456"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario estiver inativo")
    void deveLancarExcecaoQuandoUsuarioInativo() {
        Usuario usuario = new Usuario();
        usuario.setEmail("inativo@academico.ufs.br");
        usuario.setAtivo(false);

        when(usuarioRepositorio.buscarPorEmail("inativo@academico.ufs.br"))
                .thenReturn(Optional.of(usuario));

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.autenticar("inativo@academico.ufs.br", "123456"));
        assertTrue(ex.getMessage().contains("conta deste usuário está inativa"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando senha for incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("prof@academico.ufs.br");
        usuario.setSenha("hash_correta");
        usuario.setAtivo(true);

        when(usuarioRepositorio.buscarPorEmail("prof@academico.ufs.br"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("senha_errada", "hash_correta")).thenReturn(false);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.autenticar("prof@academico.ufs.br", "senha_errada"));
        assertTrue(ex.getMessage().contains("senha incorreta"));
    }

    @Test
    @DisplayName("Deve redefinir senha no primeiro acesso com sucesso")
    void deveRedefinirSenhaPrimeiroAcessoComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setSenha("hash_temp");
        usuario.setPrimeiroAcesso(true);

        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("temp123", "hash_temp")).thenReturn(true);
        when(passwordEncoder.corresponde("novaSenha@2026", "hash_temp")).thenReturn(false);
        when(passwordEncoder.codificar("novaSenha@2026")).thenReturn("hash_novo");
        when(usuarioRepositorio.salvar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenService.gerarToken(any(Usuario.class))).thenReturn("novo.jwt.token");

        ResultadoAutenticacao resultado =
                service.redefinirSenhaPrimeiroAcesso(
                        10L, "temp123", "novaSenha@2026", "novaSenha@2026");

        assertNotNull(resultado);
        assertFalse(resultado.usuario().getPrimeiroAcesso());
        assertEquals("hash_novo", resultado.usuario().getSenha());
        assertEquals("novo.jwt.token", resultado.token());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario nao encontrado ao redefinir senha")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoRedefinir() {
        when(usuarioRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.redefinirSenhaPrimeiroAcesso(99L, "atual", "nova", "nova"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando senha temporaria atual estiver incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setSenha("hash_correta");
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("errada", "hash_correta")).thenReturn(false);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.redefinirSenhaPrimeiroAcesso(1L, "errada", "nova", "nova"));
        assertTrue(ex.getMessage().contains("senha temporária atual informada está incorreta"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando nova senha e confirmacao nao coincidirem")
    void deveLancarExcecaoQuandoConfirmacaoDiverge() {
        Usuario usuario = new Usuario();
        usuario.setSenha("hash_temp");
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("temp", "hash_temp")).thenReturn(true);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.redefinirSenhaPrimeiroAcesso(1L, "temp", "nova1", "nova2"));
        assertTrue(ex.getMessage().contains("confirmação não coincidem"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando nova senha for identica a senha temporaria")
    void deveLancarExcecaoQuandoNovaSenhaIdenticaAtual() {
        Usuario usuario = new Usuario();
        usuario.setSenha("hash_temp");
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.corresponde("temp", "hash_temp")).thenReturn(true);

        RegraNegocioException ex =
                assertThrows(
                        RegraNegocioException.class,
                        () -> service.redefinirSenhaPrimeiroAcesso(1L, "temp", "temp", "temp"));
        assertTrue(ex.getMessage().contains("não pode ser idêntica à senha temporária"));
    }
}
