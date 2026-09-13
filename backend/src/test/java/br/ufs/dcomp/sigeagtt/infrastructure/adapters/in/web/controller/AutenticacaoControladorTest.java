package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AutenticacaoUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AutenticacaoUseCase.ResultadoAutenticacao;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.LoginRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.LoginRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.PrimeiroAcessoRequisicaoDTO;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.context.SecurityContextRepository;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControladorTest {

    @Mock private AutenticacaoUseCase autenticacaoUseCase;
    @Mock private SecurityContextRepository securityContextRepository;
    @Mock private UsuarioRepositoryPort usuarioRepositorio;

    @InjectMocks private AutenticacaoControlador controlador;

    @Test
    @DisplayName("Deve realizar login com sucesso e salvar contexto de seguranca")
    void deveEntrarComSucesso() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail("user@academico.ufs.br");
        u.setPerfil(PerfilUsuario.PROFESSOR);

        ResultadoAutenticacao res = new ResultadoAutenticacao(u, "jwt.token");
        when(autenticacaoUseCase.autenticar("user@academico.ufs.br", "123456")).thenReturn(res);
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(u));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<LoginRespostaDTO> resposta =
                controlador.entrar(
                        new LoginRequisicaoDTO("user@academico.ufs.br", "123456"),
                        request,
                        response);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("jwt.token", resposta.getBody().token());
        assertEquals(1L, resposta.getBody().id());
        verify(securityContextRepository).saveContext(any(), any(), any());
    }

    @Test
    @DisplayName("Deve redefinir senha no primeiro acesso com sucesso")
    void devePrimeiroAcessoComSucesso() {
        Usuario u = new Usuario();
        u.setId(10L);
        u.setEmail("user@academico.ufs.br");
        u.setPerfil(PerfilUsuario.ALUNO);

        ResultadoAutenticacao res = new ResultadoAutenticacao(u, "novo.token");
        when(autenticacaoUseCase.redefinirSenhaPrimeiroAcesso(10L, "temp", "nova", "nova"))
                .thenReturn(res);
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(u));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<LoginRespostaDTO> resposta =
                controlador.primeiroAcesso(
                        new PrimeiroAcessoRequisicaoDTO(10L, "temp", "nova", "nova"),
                        request,
                        response);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("novo.token", resposta.getBody().token());
    }

    @Test
    @DisplayName("Deve encerrar sessao com sucesso quando existir sessao ativa")
    void deveSairComSucesso() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true); // cria sessao ativa

        ResponseEntity<Map<String, String>> resp = controlador.sair(request);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Sessão finalizada.", resp.getBody().get("mensagem"));
    }

    @Test
    @DisplayName("Deve encerrar sessao com sucesso quando sessao for nula")
    void deveSairSemSessao() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<Map<String, String>> resp = controlador.sair(request);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Sessão finalizada.", resp.getBody().get("mensagem"));
    }

    @Test
    @DisplayName(
            "Deve autenticar sessao com id nulo buscando por email e caindo no principal de email")
    void deveAutenticarComIdNuloEUsuarioNaoEncontrado() {
        Usuario u = new Usuario();
        u.setId(null);
        u.setEmail("user@ufs.br");
        u.setPerfil(PerfilUsuario.ALUNO);

        ResultadoAutenticacao res = new ResultadoAutenticacao(u, "jwt.token");
        when(autenticacaoUseCase.autenticar("user@ufs.br", "123456")).thenReturn(res);
        when(usuarioRepositorio.buscarPorEmail("user@ufs.br")).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<LoginRespostaDTO> resposta =
                controlador.entrar(
                        new LoginRequisicaoDTO("user@ufs.br", "123456"), request, response);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(securityContextRepository).saveContext(any(), any(), any());
    }

    @Test
    @DisplayName("Deve autenticar sessao quando usuarioRepositorio.buscarPorId retornar vazio")
    void deveAutenticarQuandoBuscarPorIdRetornarVazio() {
        Usuario u = new Usuario();
        u.setId(99L);
        u.setEmail("user99@ufs.br");
        u.setPerfil(PerfilUsuario.ADMINISTRADOR);

        ResultadoAutenticacao res = new ResultadoAutenticacao(u, "jwt.token");
        when(autenticacaoUseCase.autenticar("user99@ufs.br", "123456")).thenReturn(res);
        when(usuarioRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<LoginRespostaDTO> resposta =
                controlador.entrar(
                        new LoginRequisicaoDTO("user99@ufs.br", "123456"), request, response);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(securityContextRepository).saveContext(any(), any(), any());
    }
}
