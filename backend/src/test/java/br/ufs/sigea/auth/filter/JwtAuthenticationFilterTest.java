package br.ufs.sigea.auth.filter;

import br.ufs.sigea.auth.service.JwtTokenService;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private User activeUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        activeUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Usuario Teste")
                .email("teste@academico.ufs.br")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando token Bearer válido é fornecido")
    void shouldAuthenticateWhenValidBearerTokenProvided() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("valid.token.here")).thenReturn(true);
        when(jwtTokenService.extractEmail("valid.token.here")).thenReturn("teste@academico.ufs.br");
        when(userRepository.findByEmailIgnoreCase("teste@academico.ufs.br")).thenReturn(Optional.of(activeUser));

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(activeUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar quando email extraído for nulo")
    void shouldIgnoreWhenExtractedEmailIsNull() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("valid.token.here")).thenReturn(true);
        when(jwtTokenService.extractEmail("valid.token.here")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar quando já houver autenticação presente no SecurityContext")
    void shouldIgnoreWhenAuthenticationAlreadyPresentInContext() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken("alreadyAuth", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("valid.token.here")).thenReturn(true);
        when(jwtTokenService.extractEmail("valid.token.here")).thenReturn("teste@academico.ufs.br");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar requisição sem cabeçalho Authorization")
    void shouldIgnoreRequestWithoutAuthorizationHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar cabeçalho Authorization sem prefixo Bearer")
    void shouldIgnoreNonBearerHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar quando o token JWT for inválido")
    void shouldIgnoreWhenTokenInvalid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("invalid.token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar quando usuário do token não é encontrado")
    void shouldIgnoreWhenUserNotFound() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("valid.token")).thenReturn(true);
        when(jwtTokenService.extractEmail("valid.token")).thenReturn("teste@academico.ufs.br");
        when(userRepository.findByEmailIgnoreCase("teste@academico.ufs.br")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar autenticação se o usuário estiver inativo")
    void shouldIgnoreWhenUserIsInactive() throws ServletException, IOException {
        activeUser.setIsActive(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenService.validateToken("valid.token")).thenReturn(true);
        when(jwtTokenService.extractEmail("valid.token")).thenReturn("teste@academico.ufs.br");
        when(userRepository.findByEmailIgnoreCase("teste@academico.ufs.br")).thenReturn(Optional.of(activeUser));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
