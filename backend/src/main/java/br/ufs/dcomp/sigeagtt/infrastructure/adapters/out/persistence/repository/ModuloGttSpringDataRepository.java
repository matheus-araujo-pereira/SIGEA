package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.ModuloGttJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para módulos GTT. */
@Repository
public interface ModuloGttSpringDataRepository extends JpaRepository<ModuloGttJpaEntity, Long> {
    Optional<ModuloGttJpaEntity> findByCodigo(String codigo);

    Optional<ModuloGttJpaEntity> findByCodigoAndIdNot(String codigo, Long id);
}
