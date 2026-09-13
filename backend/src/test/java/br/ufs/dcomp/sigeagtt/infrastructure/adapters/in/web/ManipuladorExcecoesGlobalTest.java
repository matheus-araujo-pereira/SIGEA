package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ManipuladorExcecoesGlobalTest {

    private final ManipuladorExcecoesGlobal manipulador = new ManipuladorExcecoesGlobal();

    @Test
    @DisplayName(
            "Deve tratar RegraNegocioException, IllegalArgumentException e IllegalStateException com status 400")
    void deveTratarRegraDeNegocio() {
        ResponseEntity<ErroRespostaDTO> resp1 =
                manipulador.tratarRegraDeNegocio(new RegraNegocioException("Regra violada"));
        assertEquals(HttpStatus.BAD_REQUEST, resp1.getStatusCode());
        assertEquals("Regra de negócio violada", resp1.getBody().erro());
        assertEquals("Regra violada", resp1.getBody().mensagem());

        ResponseEntity<ErroRespostaDTO> resp2 =
                manipulador.tratarRegraDeNegocio(
                        new IllegalArgumentException("Argumento inválido"));
        assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());

        ResponseEntity<ErroRespostaDTO> resp3 =
                manipulador.tratarRegraDeNegocio(new IllegalStateException("Estado inválido"));
        assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
    }

    @Test
    @DisplayName(
            "Deve tratar RecursoNaoEncontradoException e NoSuchElementException com status 404")
    void deveTratarNaoEncontrado() {
        ResponseEntity<ErroRespostaDTO> resp1 =
                manipulador.tratarNaoEncontrado(
                        new RecursoNaoEncontradoException("ID 10 não existe"));
        assertEquals(HttpStatus.NOT_FOUND, resp1.getStatusCode());
        assertEquals("Recurso não encontrado", resp1.getBody().erro());
        assertEquals("ID 10 não existe", resp1.getBody().mensagem());

        ResponseEntity<ErroRespostaDTO> resp2 =
                manipulador.tratarNaoEncontrado(new NoSuchElementException("Sem elemento"));
        assertEquals(HttpStatus.NOT_FOUND, resp2.getStatusCode());
    }

    @Test
    @DisplayName(
            "Deve tratar ConflitoDadosException e DataIntegrityViolationException com status 409")
    void deveTratarIntegridadeDados() {
        ResponseEntity<ErroRespostaDTO> resp1 =
                manipulador.tratarIntegridadeDados(new ConflitoDadosException("E-mail duplicado"));
        assertEquals(HttpStatus.CONFLICT, resp1.getStatusCode());
        assertEquals("Conflito de integridade de dados", resp1.getBody().erro());
        assertEquals("E-mail duplicado", resp1.getBody().mensagem());

        ResponseEntity<ErroRespostaDTO> resp2 =
                manipulador.tratarIntegridadeDados(
                        new DataIntegrityViolationException("Erro de FK"));
        assertEquals(HttpStatus.CONFLICT, resp2.getStatusCode());
        assertTrue(
                resp2.getBody()
                        .mensagem()
                        .contains("Registro duplicado ou operação viola integridade referencial"));
    }

    @Test
    @DisplayName("Deve tratar AcessoProibidoException e AccessDeniedException com status 403")
    void deveTratarAcessoNegado() {
        ResponseEntity<ErroRespostaDTO> resp1 =
                manipulador.tratarAcessoNegado(new AcessoProibidoException("Sem permissão"));
        assertEquals(HttpStatus.FORBIDDEN, resp1.getStatusCode());
        assertEquals("Acesso não autorizado", resp1.getBody().erro());
        assertEquals("Sem permissão", resp1.getBody().mensagem());

        ResponseEntity<ErroRespostaDTO> resp2 =
                manipulador.tratarAcessoNegado(new AccessDeniedException(null));
        assertEquals(HttpStatus.FORBIDDEN, resp2.getStatusCode());
        assertEquals(
                "Você não possui permissão para executar esta operação.",
                resp2.getBody().mensagem());
    }

    @Test
    @DisplayName("Deve tratar BadCredentialsException com status 401")
    void deveTratarCredenciaisInvalidas() {
        ResponseEntity<ErroRespostaDTO> resp =
                manipulador.tratarCredenciaisInvalidas(
                        new BadCredentialsException("Senha incorreta"));
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("Credenciais inválidas", resp.getBody().erro());
        assertEquals("Senha incorreta", resp.getBody().mensagem());
    }

    @Test
    @DisplayName("Deve tratar HttpMessageNotReadableException com status 400")
    void deveTratarMensagemIlegivel() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ResponseEntity<ErroRespostaDTO> resp = manipulador.tratarMensagemIlegivel(ex);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Requisição mal formatada", resp.getBody().erro());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com status 400 e lista de campos")
    void deveTratarValidacoesCampos() {
        org.springframework.validation.BeanPropertyBindingResult bindingResult =
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "nome", "O nome é obrigatório"));
        bindingResult.addError(new FieldError("obj", "email", "O e-mail é obrigatório"));

        java.lang.reflect.Method method = this.getClass().getDeclaredMethods()[0];
        org.springframework.core.MethodParameter parameter =
                new org.springframework.core.MethodParameter(method, -1);
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErroRespostaDTO> resp = manipulador.tratarValidacoesCampos(ex);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Falha de validação nos dados enviados", resp.getBody().erro());
        assertNotNull(resp.getBody().campos());
        assertEquals(2, resp.getBody().campos().size());
        assertEquals("O nome é obrigatório", resp.getBody().campos().get("nome"));
    }

    @Test
    @DisplayName("Deve tratar Exception generica com status 500")
    void deveTratarErroInesperado() {
        ResponseEntity<ErroRespostaDTO> resp =
                manipulador.tratarErroInesperado(new RuntimeException("Falha catastrófica"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("Erro interno do servidor", resp.getBody().erro());
        assertTrue(resp.getBody().mensagem().contains("Ocorreu um erro interno inesperado"));
    }
}
