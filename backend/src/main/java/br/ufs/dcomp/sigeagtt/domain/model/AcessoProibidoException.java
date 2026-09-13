package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Exceção de domínio lançada quando um usuário tenta executar uma operação ou acessar dados sem ter
 * os privilégios ou a autoria necessária.
 *
 * <p>Exemplos: docente tentando editar caso clínico ou turma de outro docente sem perfil de
 * administrador.
 */
public class AcessoProibidoException extends RuntimeException {

    /**
     * Construtor da exceção com o motivo da restrição de acesso.
     *
     * @param mensagem Descrição do motivo da negação de acesso.
     */
    public AcessoProibidoException(String mensagem) {
        super(mensagem);
    }
}
