package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPdcaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para Ciclo PDCA na submissão. */
@Repository
public interface SubmissaoPdcaSpringDataRepository
        extends JpaRepository<SubmissaoPdcaJpaEntity, Long> {
    Optional<SubmissaoPdcaJpaEntity> findBySubmissaoId(Long submissaoId);

    void deleteBySubmissaoId(Long submissaoId);
}
