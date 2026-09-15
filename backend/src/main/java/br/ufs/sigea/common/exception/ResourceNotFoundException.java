package br.ufs.sigea.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no sistema.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem descritiva do recurso ausente.
     *
     * @param message Mensagem de erro informando a entidade e identificador
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
