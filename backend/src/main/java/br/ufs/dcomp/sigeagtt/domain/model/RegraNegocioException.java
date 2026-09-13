package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Exceção de domínio lançada quando uma regra de negócio ou invariante do SIGEA-GTT é violada.
 *
 * <p>Exemplos: tentativa de inativar o único administrador, domínio de e-mail institucional
 * inválido, nota fora da escala permitida (0.00 a 10.00).
 */
public class RegraNegocioException extends RuntimeException {

    /**
     * Construtor da exceção com mensagem descritiva da violação da regra.
     *
     * @param mensagem Detalhamento da regra de negócio infringida.
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
