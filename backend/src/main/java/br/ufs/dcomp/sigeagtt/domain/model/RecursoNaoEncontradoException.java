package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Exceção de domínio lançada quando uma entidade requerida no contexto do SIGEA-GTT não é
 * localizada.
 *
 * <p>Exemplos: busca por ID de usuário inexistente, prontuário simulado não encontrado, turma não
 * cadastrada.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    /**
     * Construtor da exceção com a descrição do recurso ausente.
     *
     * @param mensagem Mensagem informativa do recurso não localizado.
     */
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
