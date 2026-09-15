package br.ufs.sigea.user.repository;

import br.ufs.sigea.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para operações de persistência de Usuários.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /**
     * Busca usuário pelo endereço de e-mail desconsiderando maiúsculas/minúsculas.
     *
     * @param email Endereço de e-mail institucional
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Verifica a existência prévia de um usuário com o e-mail informado.
     *
     * @param email Endereço de e-mail institucional
     * @return true se já existir usuário com este e-mail
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Verifica a existência de outro usuário cadastrado com o mesmo e-mail, excluindo o ID informado.
     *
     * @param email Endereço de e-mail institucional
     * @param id    Identificador do usuário sob edição
     * @return true se outro usuário já possuir o e-mail
     */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
