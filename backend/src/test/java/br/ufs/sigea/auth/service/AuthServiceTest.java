package br.ufs.sigea.auth.service;

import br.ufs.sigea.auth.dto.ChangePasswordDTO;
import br.ufs.sigea.auth.dto.FirstLoginChangePasswordDTO;
import br.ufs.sigea.auth.dto.LoginRequestDTO;
import br.ufs.sigea.auth.dto.LoginResponseDTO;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.mapper.UserMapper;
import br.ufs.sigea.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .fullName("Admin Teste")
                .email("admin@academico.ufs.br")
                .passwordHash("hashed-pass")
                .role(UserRole.ADMIN)
                .registrationNumber(null)
                .isActive(true)
                .mustChangePassword(true)
                .build();
    }

    @Test
    @DisplayName("Deve autenticar com sucesso com credenciais válidas")
    void shouldLoginSuccessfully() {
        LoginRequestDTO dto = new LoginRequestDTO("admin@academico.ufs.br", "SigeaUFS@2026");

        when(userRepository.findByEmailIgnoreCase("admin@academico.ufs.br")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SigeaUFS@2026", "hashed-pass")).thenReturn(true);
        when(jwtTokenService.generateToken(testUser)).thenReturn("jwt.token.mock");

        LoginResponseDTO response = authService.login(dto);

        assertNotNull(response);
        assertEquals("jwt.token.mock", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("admin@academico.ufs.br", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Deve rejeitar login com e-mail de domínio não institucional")
    void shouldRejectLoginWithNonInstitutionalEmail() {
        LoginRequestDTO dto = new LoginRequestDTO("admin@gmail.com", "senha123");

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        assertTrue(ex.getMessage().contains("@academico.ufs.br"));
    }

    @Test
    @DisplayName("Deve rejeitar login quando usuário não existe")
    void shouldRejectLoginWhenUserNotFound() {
        LoginRequestDTO dto = new LoginRequestDTO("naoexiste@academico.ufs.br", "senha123");

        when(userRepository.findByEmailIgnoreCase("naoexiste@academico.ufs.br")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
    }

    @Test
    @DisplayName("Deve rejeitar login com senha incorreta")
    void shouldRejectLoginWithWrongPassword() {
        LoginRequestDTO dto = new LoginRequestDTO("admin@academico.ufs.br", "senhaErrada");

        when(userRepository.findByEmailIgnoreCase("admin@academico.ufs.br")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("senhaErrada", "hashed-pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
    }

    @Test
    @DisplayName("Deve rejeitar login de usuário inativo")
    void shouldRejectLoginWhenUserIsInactive() {
        testUser.setIsActive(false);
        LoginRequestDTO dto = new LoginRequestDTO("admin@academico.ufs.br", "SigeaUFS@2026");

        when(userRepository.findByEmailIgnoreCase("admin@academico.ufs.br")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SigeaUFS@2026", "hashed-pass")).thenReturn(true);

        assertThrows(DisabledException.class, () -> authService.login(dto));
    }

    @Test
    @DisplayName("Deve alterar senha no primeiro login com sucesso e gerar novo token")
    void shouldChangePasswordOnFirstLoginSuccessfully() {
        FirstLoginChangePasswordDTO dto = new FirstLoginChangePasswordDTO(
                "SigeaUFS@2026", "NovaSenha@2026", "NovaSenha@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SigeaUFS@2026", "hashed-pass")).thenReturn(true);
        when(passwordEncoder.matches("NovaSenha@2026", "hashed-pass")).thenReturn(false);
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("new-hash");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenService.generateToken(testUser)).thenReturn("new.jwt.token");

        LoginResponseDTO response = authService.firstLoginChangePassword(userId, dto);

        assertNotNull(response);
        assertEquals("new.jwt.token", response.getToken());
        assertFalse(testUser.getMustChangePassword());
        assertEquals("new-hash", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Deve falhar alteração no primeiro login se a senha provisória estiver errada")
    void shouldFailFirstLoginWhenCurrentPasswordWrong() {
        FirstLoginChangePasswordDTO dto = new FirstLoginChangePasswordDTO(
                "SenhaErrada", "NovaSenha@2026", "NovaSenha@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SenhaErrada", "hashed-pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.firstLoginChangePassword(userId, dto));
    }

    @Test
    @DisplayName("Deve falhar alteração no primeiro login se confirmação de senha não coincidir")
    void shouldFailFirstLoginWhenConfirmationMismatch() {
        FirstLoginChangePasswordDTO dto = new FirstLoginChangePasswordDTO(
                "SigeaUFS@2026", "NovaSenha@2026", "OutraSenha@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SigeaUFS@2026", "hashed-pass")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                authService.firstLoginChangePassword(userId, dto));
        assertTrue(ex.getMessage().contains("não conferem"));
    }

    @Test
    @DisplayName("Deve falhar alteração no primeiro login se nova senha for idêntica à provisória")
    void shouldFailFirstLoginWhenNewPasswordSameAsCurrent() {
        FirstLoginChangePasswordDTO dto = new FirstLoginChangePasswordDTO(
                "SigeaUFS@2026", "SigeaUFS@2026", "SigeaUFS@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SigeaUFS@2026", "hashed-pass")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                authService.firstLoginChangePassword(userId, dto));
        assertTrue(ex.getMessage().contains("não pode ser idêntica"));
    }

    @Test
    @DisplayName("Deve alterar senha voluntariamente no perfil com sucesso")
    void shouldChangePasswordVoluntarilySuccessfully() {
        ChangePasswordDTO dto = new ChangePasswordDTO(
                "SenhaAtual@2026", "NovaSenha@2026", "NovaSenha@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SenhaAtual@2026", "hashed-pass")).thenReturn(true);
        when(passwordEncoder.matches("NovaSenha@2026", "hashed-pass")).thenReturn(false);
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("updated-hash");

        authService.changePassword(userId, dto);

        verify(userRepository).save(testUser);
        assertEquals("updated-hash", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Deve falhar alteração voluntária com senha atual incorreta")
    void shouldFailChangePasswordWithWrongCurrentPassword() {
        ChangePasswordDTO dto = new ChangePasswordDTO(
                "SenhaIncorreta", "NovaSenha@2026", "NovaSenha@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SenhaIncorreta", "hashed-pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.changePassword(userId, dto));
    }

    @Test
    @DisplayName("Deve falhar alteração voluntária se confirmação de senha não coincidir")
    void shouldFailChangePasswordWhenConfirmationMismatch() {
        ChangePasswordDTO dto = new ChangePasswordDTO(
                "SenhaAtual@2026", "NovaSenha@2026", "Diferente@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SenhaAtual@2026", "hashed-pass")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.changePassword(userId, dto));
    }

    @Test
    @DisplayName("Deve falhar alteração voluntária se nova senha for idêntica à atual")
    void shouldFailChangePasswordWhenNewSameAsCurrent() {
        ChangePasswordDTO dto = new ChangePasswordDTO(
                "SenhaAtual@2026", "SenhaAtual@2026", "SenhaAtual@2026"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SenhaAtual@2026", "hashed-pass")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.changePassword(userId, dto));
    }

    @Test
    @DisplayName("Deve falhar alteração de senha quando usuário não é encontrado")
    void shouldFailChangePasswordWhenUserNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        ChangePasswordDTO dto = new ChangePasswordDTO("a", "b", "b");
        assertThrows(ResourceNotFoundException.class, () -> authService.changePassword(randomId, dto));

        FirstLoginChangePasswordDTO firstDto = new FirstLoginChangePasswordDTO("a", "b", "b");
        assertThrows(ResourceNotFoundException.class, () -> authService.firstLoginChangePassword(randomId, firstDto));
    }
}
