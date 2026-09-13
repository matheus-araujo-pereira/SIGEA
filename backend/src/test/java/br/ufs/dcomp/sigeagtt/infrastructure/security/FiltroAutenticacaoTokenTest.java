package br.ufs.dcomp.sigeagtt.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort.DadosToken;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class FiltroAutenticacaoTokenTest {

    @Mock private TokenServicePort tokenServico;
    @Mock private UsuarioRepositoryPort usuarioRepositorio;
    @Mock private FilterChain filterChain;

    @InjectMocks private FiltroAutenticacaoToken filtro;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve passar requisição adiante sem autenticação quando cabeçalho for nulo")
    void devePassarSemCabecalho() throws ServletException, IOException {
        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName(
            "Deve passar requisição adiante sem autenticação quando cabeçalho não começar com Bearer")
    void devePassarSemBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic dXNlcjpzZW5oYQ==");

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve passar requisição adiante sem autenticação quando token for inválido")
    void devePassarTokenInvalido() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token_invalido");
        when(tokenServico.validarToken("token_invalido")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName(
            "Deve autenticar com sucesso quando token for válido e usuário for localizado por ID")
    void deveAutenticarComUsuarioLocalizadoPorId() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token_valido");
        DadosToken dados = new DadosToken(10L, "user@ufs.br", "PROFESSOR");
        when(tokenServico.validarToken("token_valido")).thenReturn(dados);

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("user@ufs.br");
        usuario.setPerfil(PerfilUsuario.PROFESSOR);
        when(usuarioRepositorio.buscarPorId(10L)).thenReturn(Optional.of(usuario));

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(
                usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")));
    }

    @Test
    @DisplayName(
            "Deve autenticar com sucesso quando token não tiver ID e usuário for localizado por email")
    void deveAutenticarComUsuarioLocalizadoPorEmail() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token_valido");
        DadosToken dados = new DadosToken(null, "user@ufs.br", "ALUNO");
        when(tokenServico.validarToken("token_valido")).thenReturn(dados);

        Usuario usuario = new Usuario();
        usuario.setId(20L);
        usuario.setEmail("user@ufs.br");
        usuario.setPerfil(PerfilUsuario.ALUNO);
        when(usuarioRepositorio.buscarPorEmail("user@ufs.br")).thenReturn(Optional.of(usuario));

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(
                usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    @DisplayName(
            "Deve autenticar com email no principal quando usuário não for encontrado no repositório")
    void deveAutenticarComEmailQuandoUsuarioNaoEncontrado() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token_valido");
        DadosToken dados = new DadosToken(99L, "inexistente@ufs.br", "ADMINISTRADOR");
        when(tokenServico.validarToken("token_valido")).thenReturn(dados);
        when(usuarioRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(
                "inexistente@ufs.br",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
