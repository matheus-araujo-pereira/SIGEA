package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para entidades de usuário. */
public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioJpaEntity, Long> {
    Optional<UsuarioJpaEntity> findByEmail(String email);

    Optional<UsuarioJpaEntity> findByEmailAndIdNot(String email, Long id);

    Optional<UsuarioJpaEntity> findByMatriculaSigaa(String matriculaSigaa);

    Optional<UsuarioJpaEntity> findByMatriculaSigaaAndIdNot(String matriculaSigaa, Long id);

    long countByPerfilAndAtivoTrue(PerfilUsuario perfil);
}
