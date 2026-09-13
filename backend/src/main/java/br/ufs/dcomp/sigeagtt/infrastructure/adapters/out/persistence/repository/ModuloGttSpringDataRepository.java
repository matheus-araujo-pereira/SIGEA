package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.ModuloGttJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para módulos GTT. */
public interface ModuloGttSpringDataRepository extends JpaRepository<ModuloGttJpaEntity, Long> {
    Optional<ModuloGttJpaEntity> findByCodigo(String codigo);

    Optional<ModuloGttJpaEntity> findByCodigoAndIdNot(String codigo, Long id);
}
