package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositório Spring Data JPA para turmas acadêmicas. */
public interface TurmaSpringDataRepository extends JpaRepository<TurmaJpaEntity, Long> {

    @Query("SELECT t FROM TurmaJpaEntity t JOIN FETCH t.professorResponsavel WHERE t.professorResponsavel.id = :professorId")
    List<TurmaJpaEntity> findByProfessorResponsavelId(@Param("professorId") Long professorId);

    @Query("SELECT t FROM TurmaJpaEntity t JOIN FETCH t.professorResponsavel WHERE t.ativa = true")
    List<TurmaJpaEntity> findByAtivaTrue();
}
