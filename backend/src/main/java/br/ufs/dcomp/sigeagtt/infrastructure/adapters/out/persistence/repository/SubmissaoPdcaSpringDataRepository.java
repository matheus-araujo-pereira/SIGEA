package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPdcaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para Ciclo PDCA na submissão. */
public interface SubmissaoPdcaSpringDataRepository
        extends JpaRepository<SubmissaoPdcaJpaEntity, Long> {
    Optional<SubmissaoPdcaJpaEntity> findBySubmissaoId(Long submissaoId);

    void deleteBySubmissaoId(Long submissaoId);
}
