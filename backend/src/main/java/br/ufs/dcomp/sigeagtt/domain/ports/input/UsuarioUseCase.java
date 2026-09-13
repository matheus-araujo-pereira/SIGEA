package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.util.List;

/** Porta de entrada (Input Port / Use Case) para gerenciamento de usuários no SIGEA-GTT. */
public interface UsuarioUseCase {

    /**
     * Lista todos os usuários cadastrados.
     *
     * @return Lista com todos os usuários.
     */
    List<Usuario> listarTodos();

    /**
     * Busca um usuário pelo ID.
     *
     * @param id Identificador do usuário.
     * @return Entidade do usuário localizado.
     */
    Usuario buscarPorId(Long id);

    /**
     * Cadastra um novo usuário no sistema com senha padrão inicial.
     *
     * @param nomeCompleto Nome completo do usuário.
     * @param email E-mail institucional.
     * @param matriculaSigaa Matrícula acadêmica (se discente).
     * @param perfil Perfil de autorização.
     * @return Usuário cadastrado.
     */
    Usuario cadastrar(
            String nomeCompleto, String email, String matriculaSigaa, PerfilUsuario perfil);

    /**
     * Atualiza os dados cadastrais de um usuário existente.
     *
     * @param id Identificador do usuário a editar.
     * @param nomeCompleto Novo nome completo.
     * @param email Novo e-mail institucional.
     * @param matriculaSigaa Nova matrícula SIGAA.
     * @param perfil Novo perfil atribuído.
     * @return Usuário atualizado.
     */
    Usuario editar(
            Long id,
            String nomeCompleto,
            String email,
            String matriculaSigaa,
            PerfilUsuario perfil);

    /**
     * Reseta a senha de um usuário para a senha inicial padrão "Sigea@123".
     *
     * @param id Identificador do usuário.
     * @return Usuário atualizado com senha resetada.
     */
    Usuario resetarSenha(Long id);

    /**
     * Inativa um usuário impedindo novos logins.
     *
     * @param id Identificador do usuário.
     * @return Usuário com status inativo.
     */
    Usuario inativar(Long id);

    /**
     * Reativa um usuário inativado.
     *
     * @param id Identificador do usuário.
     * @return Usuário reativado.
     */
    Usuario reativar(Long id);

    /**
     * Altera a senha do próprio usuário mediante fornecimento da senha atual.
     *
     * @param id Identificador do usuário.
     * @param senhaAtual Senha atual em vigor.
     * @param novaSenha Nova senha a ser configurada.
     * @param confirmacaoNovaSenha Confirmação idêntica da nova senha.
     * @return Usuário com a senha alterada.
     */
    Usuario alterarSenha(Long id, String senhaAtual, String novaSenha, String confirmacaoNovaSenha);
}
