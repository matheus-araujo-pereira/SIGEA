package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;

/**
 * Porta de entrada (Input Port / Use Case) para autenticação de usuários e primeiro acesso no
 * SIGEA-GTT.
 */
public interface AutenticacaoUseCase {

    /**
     * Autentica o usuário validando credenciais institucionais e atividade da conta.
     *
     * @param email E-mail institucional do usuário.
     * @param senha Senha em texto puro.
     * @return Resultado da autenticação contendo usuário e token JWT gerado.
     */
    ResultadoAutenticacao autenticar(String email, String senha);

    /**
     * Redefine a senha temporária no primeiro acesso do usuário ao sistema.
     *
     * @param usuarioId Identificador único do usuário.
     * @param senhaAtual Senha temporária atual.
     * @param novaSenha Nova senha desejada.
     * @param confirmacaoNovaSenha Confirmação idêntica da nova senha.
     * @return Resultado contendo o usuário atualizado e o novo token de sessão.
     */
    ResultadoAutenticacao redefinirSenhaPrimeiroAcesso(
            Long usuarioId, String senhaAtual, String novaSenha, String confirmacaoNovaSenha);

    /**
     * DTO de resultado da autenticação.
     *
     * @param usuario Entidade de domínio do usuário autenticado.
     * @param token Token JWT gerado para a sessão.
     */
    record ResultadoAutenticacao(Usuario usuario, String token) {}
}
