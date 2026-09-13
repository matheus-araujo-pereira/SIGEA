package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Exceção de domínio lançada quando ocorre violação de unicidade ou conflito de integridade nos
 * dados hospitalares/acadêmicos do SIGEA-GTT.
 *
 * <p>Exemplos: duplicidade de e-mail institucional, matrícula de discente repetida, código de
 * gatilho ou módulo GTT já existente.
 */
public class ConflitoDadosException extends RuntimeException {

    /**
     * Construtor da exceção com a descrição do conflito identificado.
     *
     * @param mensagem Mensagem informativa da duplicidade ou conflito.
     */
    public ConflitoDadosException(String mensagem) {
        super(mensagem);
    }
}
