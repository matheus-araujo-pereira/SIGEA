package br.ufs.sigea.common.dto;

import br.ufs.sigea.auth.dto.ChangePasswordDTO;
import br.ufs.sigea.auth.dto.FirstLoginChangePasswordDTO;
import br.ufs.sigea.auth.dto.LoginRequestDTO;
import br.ufs.sigea.auth.dto.LoginResponseDTO;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.dto.UserCreateDTO;
import br.ufs.sigea.user.dto.UserCreateResponseDTO;
import br.ufs.sigea.user.dto.UserProfileUpdateDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.dto.UserStatusUpdateDTO;
import br.ufs.sigea.user.dto.UserUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoAndDomainTest {

    @Test
    @DisplayName("Deve testar ApiResponse")
    void testApiResponse() {
        ApiResponse<String> resp1 = ApiResponse.ok("msg");
        assertTrue(resp1.isSuccess());
        assertEquals("msg", resp1.getMessage());
        assertNotNull(resp1.getTimestamp());

        ApiResponse<Integer> resp2 = ApiResponse.ok(123, "sucesso");
        assertEquals(123, resp2.getData());
        assertEquals("sucesso", resp2.getMessage());

        ApiResponse<Void> resp3 = new ApiResponse<>();
        resp3.setSuccess(false);
        resp3.setMessage("erro");
        resp3.setData(null);
        resp3.setTimestamp(Instant.EPOCH);

        assertFalse(resp3.isSuccess());
        assertEquals("erro", resp3.getMessage());
        assertEquals(Instant.EPOCH, resp3.getTimestamp());
        assertNotNull(resp3.toString());
        assertNotNull(resp2.hashCode());
    }

    @Test
    @DisplayName("Deve testar PageResponse")
    void testPageResponse() {
        Page<String> page = new PageImpl<>(List.of("item1"), PageRequest.of(0, 10), 1);
        PageResponse<String> response = PageResponse.from(page);

        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isFirst());
        assertTrue(response.isLast());

        PageResponse<String> custom = new PageResponse<>();
        custom.setContent(Collections.emptyList());
        custom.setPage(1);
        custom.setSize(20);
        custom.setTotalElements(100);
        custom.setTotalPages(5);
        custom.setFirst(false);
        custom.setLast(false);

        assertEquals(1, custom.getPage());
        assertNotNull(custom.toString());
    }

    @Test
    @DisplayName("Deve testar ErrorResponse e FieldErrorDetail")
    void testErrorResponse() {
        ErrorResponse.FieldErrorDetail detail = new ErrorResponse.FieldErrorDetail("campo", "msg", "valor");
        assertEquals("campo", detail.getField());
        assertEquals("msg", detail.getMessage());
        assertEquals("valor", detail.getRejectedValue());
        assertNotNull(detail.toString());

        ErrorResponse resp = ErrorResponse.builder()
                .success(false)
                .status(400)
                .error("Bad Request")
                .message("Msg")
                .path("/path")
                .fieldErrors(List.of(detail))
                .timestamp(Instant.now())
                .build();

        assertEquals(400, resp.getStatus());
        assertEquals("Bad Request", resp.getError());
        assertNotNull(resp.toString());

        ErrorResponse noArg = new ErrorResponse();
        noArg.setStatus(500);
        assertEquals(500, noArg.getStatus());
    }

    @Test
    @DisplayName("Deve testar User entity e PrePersist/PreUpdate")
    void testUserEntity() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setFullName("Teste");
        user.setEmail("teste@academico.ufs.br");
        user.setPasswordHash("hash");
        user.setRole(UserRole.STUDENT);
        user.setRegistrationNumber("123");
        user.setIsActive(null);
        user.setMustChangePassword(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        user.prePersist();
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getIsActive());
        assertTrue(user.getMustChangePassword());

        user.preUpdate();
        assertNotNull(user.getUpdatedAt());

        assertEquals(id, user.getId());
        assertEquals("Teste", user.getFullName());
        assertEquals("teste@academico.ufs.br", user.getEmail());
        assertEquals("hash", user.getPasswordHash());
        assertEquals(UserRole.STUDENT, user.getRole());
        assertEquals("123", user.getRegistrationNumber());

        User userWithFields = User.builder()
                .isActive(true)
                .mustChangePassword(false)
                .createdAt(Instant.now())
                .build();
        userWithFields.prePersist();
        assertFalse(userWithFields.getMustChangePassword());
    }

    @Test
    @DisplayName("Deve testar todos os DTOs do pacote auth e user")
    void testAllDTOs() {
        LoginRequestDTO loginReq = new LoginRequestDTO();
        loginReq.setEmail("a@academico.ufs.br");
        loginReq.setPassword("p");
        assertEquals("a@academico.ufs.br", loginReq.getEmail());
        assertEquals("p", loginReq.getPassword());
        assertNotNull(loginReq.toString());

        LoginResponseDTO loginResp = new LoginResponseDTO("token", "Bearer", null);
        assertEquals("token", loginResp.getToken());
        assertEquals("Bearer", loginResp.getTokenType());
        assertNull(loginResp.getUser());
        assertNotNull(loginResp.toString());

        FirstLoginChangePasswordDTO fl = new FirstLoginChangePasswordDTO("c", "n", "n");
        assertEquals("c", fl.getCurrentPassword());
        assertEquals("n", fl.getNewPassword());
        assertEquals("n", fl.getConfirmPassword());

        ChangePasswordDTO cp = new ChangePasswordDTO("c", "n", "n");
        assertEquals("c", cp.getCurrentPassword());
        assertEquals("n", cp.getNewPassword());
        assertEquals("n", cp.getConfirmPassword());

        UserResponseDTO ur = new UserResponseDTO(UUID.randomUUID(), "f", "e", UserRole.ADMIN, null, true, false, Instant.now());
        assertEquals("f", ur.getFullName());
        assertNotNull(ur.toString());

        UserCreateDTO uc = new UserCreateDTO("f", "e", UserRole.STUDENT, "r");
        assertEquals("r", uc.getRegistrationNumber());

        UserCreateResponseDTO ucr = new UserCreateResponseDTO(UUID.randomUUID(), "f", "e", UserRole.STUDENT, "r", true, true, "pwd", Instant.now());
        assertEquals("pwd", ucr.getProvisionalPassword());

        UserUpdateDTO uu = new UserUpdateDTO("f", "e", UserRole.PROFESSOR, null);
        assertEquals(UserRole.PROFESSOR, uu.getRole());

        UserStatusUpdateDTO us = new UserStatusUpdateDTO(true);
        assertTrue(us.getIsActive());

        UserProfileUpdateDTO up = new UserProfileUpdateDTO("fn");
        assertEquals("fn", up.getFullName());

        BusinessException be = new BusinessException("msg", new RuntimeException());
        assertEquals("msg", be.getMessage());
    }
}
