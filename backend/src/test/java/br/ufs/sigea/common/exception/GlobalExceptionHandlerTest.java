package br.ufs.sigea.common.exception;

import br.ufs.sigea.common.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException retornando status 400 e lista de erros")
    void shouldHandleValidationException() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("setUp");
        MethodParameter parameter = new MethodParameter(method, -1);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "invalid", false, null, null, "E-mail inválido"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(1, response.getBody().getFieldErrors().size());
        assertEquals("email", response.getBody().getFieldErrors().get(0).getField());
        assertEquals("E-mail inválido", response.getBody().getFieldErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Deve tratar BusinessException retornando status 400")
    void shouldHandleBusinessException() {
        BusinessException ex = new BusinessException("Regra de negócio violada.");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Regra de negócio violada.", response.getBody().getMessage());
        assertEquals("Regra de Negócio", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException retornando status 404")
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado.");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso não encontrado.", response.getBody().getMessage());
        assertEquals("Recurso Não Encontrado", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve tratar BadCredentialsException retornando status 401")
    void shouldHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Credenciais inválidas");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("E-mail institucional ou senha incorretos.", response.getBody().getMessage());
        assertEquals("Não Autorizado", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve tratar DisabledException retornando status 403")
    void shouldHandleDisabledException() {
        DisabledException ex = new DisabledException("Conta inativa");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDisabledException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("inativo"));
        assertEquals("Acesso Negado", response.getBody().getError());
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("Deve tratar AccessDeniedException retornando status 403")
    void shouldHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Sem acesso");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Você não possui permissão para executar esta ação.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica retornando status 500")
    void shouldHandleGenericException() {
        RuntimeException ex = new RuntimeException("Erro inesperado");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro interno inesperado no servidor.", response.getBody().getMessage());
    }
}
