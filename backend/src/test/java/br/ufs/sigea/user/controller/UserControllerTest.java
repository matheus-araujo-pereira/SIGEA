package br.ufs.sigea.user.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.dto.UserCreateDTO;
import br.ufs.sigea.user.dto.UserCreateResponseDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.dto.UserStatusUpdateDTO;
import br.ufs.sigea.user.dto.UserUpdateDTO;
import br.ufs.sigea.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User adminUser;
    private UUID adminId;
    private UUID targetUserId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();

        adminUser = User.builder()
                .id(adminId)
                .fullName("Admin Principal")
                .email("admin@academico.ufs.br")
                .role(UserRole.ADMIN)
                .build();
    }

    @Test
    @DisplayName("Deve listar usuários paginados")
    void shouldListUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponse<UserResponseDTO> pageResponse = PageResponse.<UserResponseDTO>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .build();

        when(userService.listUsers("termo", UserRole.STUDENT, true, pageable)).thenReturn(pageResponse);

        ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> response =
                userController.listUsers("termo", UserRole.STUDENT, true, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getData().getTotalElements());
    }

    @Test
    @DisplayName("Deve criar usuário com status 201 Created")
    void shouldCreateUser() {
        UserCreateDTO request = UserCreateDTO.builder()
                .fullName("Novo Aluno")
                .email("novo.aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("12345")
                .build();

        UserCreateResponseDTO responseDTO = UserCreateResponseDTO.builder()
                .id(targetUserId)
                .email("novo.aluno@academico.ufs.br")
                .provisionalPassword("Temp@123")
                .build();

        when(userService.createUser(request)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<UserCreateResponseDTO>> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(targetUserId, response.getBody().getData().getId());
        assertEquals("Temp@123", response.getBody().getData().getProvisionalPassword());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void shouldGetUserById() {
        UserResponseDTO userResponseDTO = UserResponseDTO.builder().id(targetUserId).build();
        when(userService.getUserById(targetUserId)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserById(targetUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(targetUserId, response.getBody().getData().getId());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUser() {
        UserUpdateDTO request = UserUpdateDTO.builder()
                .fullName("Nome Atualizado")
                .email("atualizado@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        UserResponseDTO userResponseDTO = UserResponseDTO.builder().id(targetUserId).build();
        when(userService.updateUser(targetUserId, request)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.updateUser(targetUserId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(targetUserId, response.getBody().getData().getId());
    }

    @Test
    @DisplayName("Deve alterar status de ativação com sucesso")
    void shouldUpdateStatus() {
        UserStatusUpdateDTO request = new UserStatusUpdateDTO(false);
        UserResponseDTO userResponseDTO = UserResponseDTO.builder().id(targetUserId).isActive(false).build();
        when(userService.updateStatus(targetUserId, request, adminId)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.updateStatus(targetUserId, request, adminUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody().getData().getIsActive());
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void shouldDeleteUser() {
        ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(targetUserId, adminUser);

        verify(userService).deleteUser(targetUserId, adminId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
