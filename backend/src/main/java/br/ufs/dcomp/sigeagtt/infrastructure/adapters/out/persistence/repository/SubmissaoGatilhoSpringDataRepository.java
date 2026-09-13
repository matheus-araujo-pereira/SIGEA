package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoGatilhoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para achados de gatilhos clínicos na submissão. */
public interface SubmissaoGatilhoSpringDataRepository
        extends JpaRepository<SubmissaoGatilhoJpaEntity, Long> {
    List<SubmissaoGatilhoJpaEntity> findBySubmissaoId(Long submissaoId);

    void deleteBySubmissaoId(Long submissaoId);
}
