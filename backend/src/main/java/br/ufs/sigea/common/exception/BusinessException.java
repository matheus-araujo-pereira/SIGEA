package br.ufs.sigea.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma regra de negócio da aplicação é violada.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    /**
     * Construtor com mensagem descritiva da violação de negócio.
     *
     * @param message Mensagem de erro amigável
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem descritiva e causa original.
     *
     * @param message Mensagem de erro amigável
     * @param cause   Exceção raiz causadora
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
