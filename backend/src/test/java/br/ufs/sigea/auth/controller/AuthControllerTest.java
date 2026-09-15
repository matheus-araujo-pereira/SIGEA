package br.ufs.sigea.auth.controller;

import br.ufs.sigea.auth.dto.ChangePasswordDTO;
import br.ufs.sigea.auth.dto.FirstLoginChangePasswordDTO;
import br.ufs.sigea.auth.dto.LoginRequestDTO;
import br.ufs.sigea.auth.dto.LoginResponseDTO;
import br.ufs.sigea.auth.service.AuthService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.dto.UserProfileUpdateDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .fullName("Professor Silva")
                .email("professor.silva@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Deve efetuar login com sucesso")
    void shouldLoginSuccessfully() {
        LoginRequestDTO request = new LoginRequestDTO("professor.silva@academico.ufs.br", "Senha@123");
        LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                .token("jwt.token")
                .user(UserResponseDTO.builder().email("professor.silva@academico.ufs.br").build())
                .build();

        when(authService.login(request)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<LoginResponseDTO>> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt.token", response.getBody().getData().getToken());
    }

    @Test
    @DisplayName("Deve trocar senha de primeiro login com sucesso")
    void shouldChangeFirstLoginPasswordSuccessfully() {
        FirstLoginChangePasswordDTO request = new FirstLoginChangePasswordDTO("Sigea@1", "Nova@123", "Nova@123");
        LoginResponseDTO responseDTO = LoginResponseDTO.builder().token("novo.jwt.token").build();

        when(authService.firstLoginChangePassword(userId, request)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<LoginResponseDTO>> response = authController.firstLoginChangePassword(testUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("novo.jwt.token", response.getBody().getData().getToken());
    }

    @Test
    @DisplayName("Deve consultar o próprio perfil com sucesso")
    void shouldGetProfileSuccessfully() {
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .fullName("Professor Silva")
                .email("professor.silva@academico.ufs.br")
                .build();

        when(userService.getUserById(userId)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = authController.getProfile(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody().getData().getId());
    }

    @Test
    @DisplayName("Deve atualizar o próprio perfil com sucesso")
    void shouldUpdateProfileSuccessfully() {
        UserProfileUpdateDTO request = new UserProfileUpdateDTO("Professor Silva Atualizado");
        UserResponseDTO updatedDTO = UserResponseDTO.builder().fullName("Professor Silva Atualizado").build();

        when(userService.updateProfile(userId, request)).thenReturn(updatedDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = authController.updateProfile(testUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Professor Silva Atualizado", response.getBody().getData().getFullName());
    }

    @Test
    @DisplayName("Deve alterar senha voluntariamente com sucesso")
    void shouldChangePasswordSuccessfully() {
        ChangePasswordDTO request = new ChangePasswordDTO("Atual@1", "Nova@123", "Nova@123");

        ResponseEntity<ApiResponse<Void>> response = authController.changePassword(testUser, request);

        verify(authService).changePassword(userId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
