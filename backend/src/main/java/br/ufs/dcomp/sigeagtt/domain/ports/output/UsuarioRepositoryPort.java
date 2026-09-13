package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Output Port) para operações de persistência e consulta da entidade {@link
 * Usuario}.
 *
 * <p>Isola o domínio das tecnologias de persistência (Spring Data JPA, PostgreSQL), permitindo que
 * o caso de uso acesse os dados por meio desta abstração.
 */
public interface UsuarioRepositoryPort {

    /**
     * Recupera todos os usuários cadastrados no sistema.
     *
     * @return Lista contendo todos os usuários.
     */
    List<Usuario> listarTodos();

    /**
     * Busca um usuário pelo seu identificador único.
     *
     * @param id Identificador numérico do usuário.
     * @return {@link Optional} contendo o usuário, ou vazio se não localizado.
     */
    Optional<Usuario> buscarPorId(Long id);

    /**
     * Busca um usuário pelo endereço de e-mail institucional.
     *
     * @param email E-mail institucional do usuário.
     * @return {@link Optional} contendo o usuário caso localizado.
     */
    Optional<Usuario> buscarPorEmail(String email);

    /**
     * Busca um discente pela sua matrícula acadêmica do SIGAA.
     *
     * @param matriculaSigaa Matrícula com 12 dígitos.
     * @return {@link Optional} contendo o discente caso localizado.
     */
    Optional<Usuario> buscarPorMatriculaSigaa(String matriculaSigaa);

    /**
     * Verifica a existência de outro usuário com o mesmo e-mail, excluindo determinado ID.
     *
     * @param email E-mail institucional a ser checado.
     * @param id Identificador do usuário em edição.
     * @return {@link Optional} com usuário conflitante se houver.
     */
    Optional<Usuario> buscarPorEmailEIdDiferente(String email, Long id);

    /**
     * Verifica a existência de outro discente com a mesma matrícula SIGAA, excluindo determinado
     * ID.
     *
     * @param matriculaSigaa Matrícula a ser checada.
     * @param id Identificador do usuário em edição.
     * @return {@link Optional} com usuário conflitante se houver.
     */
    Optional<Usuario> buscarPorMatriculaEIdDiferente(String matriculaSigaa, Long id);

    /**
     * Conta a quantidade de usuários ativos com determinado perfil de acesso.
     *
     * @param perfil Perfil a ser quantificado (ex: ADMINISTRADOR).
     * @return Total de usuários ativos com o perfil informado.
     */
    long contarPorPerfilEAtivo(PerfilUsuario perfil);

    /**
     * Persiste ou atualiza os dados do usuário.
     *
     * @param usuario Entidade de domínio do usuário a ser salva.
     * @return Usuário persistido com identificador atribuído.
     */
    Usuario salvar(Usuario usuario);
}
