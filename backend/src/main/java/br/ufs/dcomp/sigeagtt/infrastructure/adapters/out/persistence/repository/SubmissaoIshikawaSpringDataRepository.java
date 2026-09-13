package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoIshikawaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para Diagrama de Ishikawa 6M na submissão. */
@Repository
public interface SubmissaoIshikawaSpringDataRepository
        extends JpaRepository<SubmissaoIshikawaJpaEntity, Long> {
    Optional<SubmissaoIshikawaJpaEntity> findBySubmissaoId(Long submissaoId);

    void deleteBySubmissaoId(Long submissaoId);
}
