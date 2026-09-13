package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPlano5w3hJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para Planos de Ação 5W3H na submissão. */
@Repository
public interface SubmissaoPlano5w3hSpringDataRepository
        extends JpaRepository<SubmissaoPlano5w3hJpaEntity, Long> {
    List<SubmissaoPlano5w3hJpaEntity> findBySubmissaoId(Long submissaoId);

    void deleteBySubmissaoId(Long submissaoId);
}
