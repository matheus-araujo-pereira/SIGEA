package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoIdJpa;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositório Spring Data JPA para relação turma-alunos. */
public interface TurmaAlunoSpringDataRepository
        extends JpaRepository<TurmaAlunoJpaEntity, TurmaAlunoIdJpa> {

    @Query("SELECT ta FROM TurmaAlunoJpaEntity ta JOIN FETCH ta.aluno WHERE ta.turma.id = :turmaId")
    List<TurmaAlunoJpaEntity> findByTurmaId(@Param("turmaId") Long turmaId);

    boolean existsByTurmaIdAndAlunoId(Long turmaId, Long alunoId);

    void deleteByTurmaIdAndAlunoId(Long turmaId, Long alunoId);

    long countByTurmaId(Long turmaId);
}
