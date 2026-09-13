package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Interceptador global de exceções da camada Web REST do SIGEA-GTT. Mapeia exceções de domínio e do
 * framework para respostas HTTP estruturadas padronizadas em {@link ErroRespostaDTO}.
 */
@RestControllerAdvice
public class ManipuladorExcecoesGlobal {

    private static final Logger log = LoggerFactory.getLogger(ManipuladorExcecoesGlobal.class);

    /**
     * Trata violações de regras de negócio do domínio.
     *
     * @param ex Exceção capturada
     * @return Resposta HTTP 400 com ErroRespostaDTO
     */
    @ExceptionHandler({
        RegraNegocioException.class,
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public ResponseEntity<ErroRespostaDTO> tratarRegraDeNegocio(RuntimeException ex) {
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Regra de negócio violada",
                        ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    /**
     * Trata recursos e entidades não localizados.
     *
     * @param ex Exceção de não encontrado
     * @return Resposta HTTP 404 com ErroRespostaDTO
     */
    @ExceptionHandler({
        RecursoNaoEncontradoException.class,
        NoSuchElementException.class,
        NoResourceFoundException.class
    })
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(Exception ex) {
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso não encontrado",
                        ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    /**
     * Trata violações de integridade de dados e conflitos de chave única.
     *
     * @param ex Exceção de integridade ou conflito
     * @return Resposta HTTP 409 com ErroRespostaDTO
     */
    @ExceptionHandler({ConflitoDadosException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErroRespostaDTO> tratarIntegridadeDados(RuntimeException ex) {
        String msg =
                ex instanceof ConflitoDadosException
                        ? ex.getMessage()
                        : "Registro duplicado ou operação viola integridade referencial do banco de dados.";
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        "Conflito de integridade de dados",
                        msg);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }

    /**
     * Trata acessos negados por falta de autorização/permissão de perfil.
     *
     * @param ex Exceção de autorização
     * @return Resposta HTTP 403 com ErroRespostaDTO
     */
    @ExceptionHandler({AcessoProibidoException.class, AccessDeniedException.class})
    public ResponseEntity<ErroRespostaDTO> tratarAcessoNegado(RuntimeException ex) {
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.FORBIDDEN.value(),
                        "Acesso não autorizado",
                        ex.getMessage() != null
                                ? ex.getMessage()
                                : "Você não possui permissão para executar esta operação.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(corpo);
    }

    /**
     * Trata credenciais inválidas ou falhas de autenticação.
     *
     * @param ex Exceção de credenciais inválidas
     * @return Resposta HTTP 401 com ErroRespostaDTO
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroRespostaDTO> tratarCredenciaisInvalidas(BadCredentialsException ex) {
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Credenciais inválidas",
                        ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corpo);
    }

    /**
     * Trata requisições com JSON ilegível ou tipos de campos incompatíveis.
     *
     * @param ex Exceção de conversão HTTP
     * @return Resposta HTTP 400 com ErroRespostaDTO
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDTO> tratarMensagemIlegivel(
            HttpMessageNotReadableException ex) {
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Requisição mal formatada",
                        "Corpo da requisição ausente ou dados com tipos/valores inválidos.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    /**
     * Trata falhas de validação de anotações Bean Validation (@Valid).
     *
     * @param ex Exceção de validação de argumentos
     * @return Resposta HTTP 400 com mapa de campos detalhado
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacoesCampos(
            MethodArgumentNotValidException ex) {
        Map<String, String> camposInvalidos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            camposInvalidos.put(erro.getField(), erro.getDefaultMessage());
        }

        String mensagemConsolidada = String.join("; ", camposInvalidos.values());
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Falha de validação nos dados enviados",
                        mensagemConsolidada,
                        camposInvalidos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    /**
     * Trata erros internos genéricos não previstos.
     *
     * @param ex Exceção inesperada
     * @return Resposta HTTP 500 com ErroRespostaDTO
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado no servidor: ", ex);
        ErroRespostaDTO corpo =
                new ErroRespostaDTO(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno do servidor",
                        "Ocorreu um erro interno inesperado ao processar a operação.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(corpo);
    }
}
