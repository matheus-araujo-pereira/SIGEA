package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CasoClinicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para casos clínicos simulados. */
public interface CasoClinicoSpringDataRepository extends JpaRepository<CasoClinicoJpaEntity, Long> {
    List<CasoClinicoJpaEntity> findByProfessorCriadorIdOrderByCriadoEmDesc(Long professorCriadorId);

    List<CasoClinicoJpaEntity> findAllByOrderByCriadoEmDesc();
}
