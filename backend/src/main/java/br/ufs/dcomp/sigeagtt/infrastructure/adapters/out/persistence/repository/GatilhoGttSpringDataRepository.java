package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.GatilhoGttJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para gatilhos clínicos GTT. */
@Repository
public interface GatilhoGttSpringDataRepository extends JpaRepository<GatilhoGttJpaEntity, Long> {
    Optional<GatilhoGttJpaEntity> findByCodigo(String codigo);

    Optional<GatilhoGttJpaEntity> findByCodigoAndIdNot(String codigo, Long id);

    @Query(
            "SELECT g FROM GatilhoGttJpaEntity g JOIN FETCH g.modulo WHERE g.modulo.id = :moduloId ORDER BY LENGTH(g.codigo) ASC, g.codigo ASC")
    List<GatilhoGttJpaEntity> findAllByModuloId(@Param("moduloId") Long moduloId);

    @Query(
            "SELECT g FROM GatilhoGttJpaEntity g JOIN FETCH g.modulo ORDER BY g.modulo.codigo ASC, LENGTH(g.codigo) ASC, g.codigo ASC")
    List<GatilhoGttJpaEntity> findAllOrderByCodigo();
}
