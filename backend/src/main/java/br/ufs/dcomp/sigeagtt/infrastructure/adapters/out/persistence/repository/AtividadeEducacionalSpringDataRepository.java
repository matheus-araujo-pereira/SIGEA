package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.AtividadeEducacionalJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para atividades educacionais. */
@Repository
public interface AtividadeEducacionalSpringDataRepository
        extends JpaRepository<AtividadeEducacionalJpaEntity, Long> {

    List<AtividadeEducacionalJpaEntity> findByTurmaIdOrderByCriadaEmDesc(Long turmaId);

    @Query(
            "SELECT a FROM AtividadeEducacionalJpaEntity a WHERE a.turma.professorResponsavel.id = :professorId ORDER BY a.criadaEm DESC")
    List<AtividadeEducacionalJpaEntity> findByProfessorId(@Param("professorId") Long professorId);

    @Query(
            "SELECT a FROM AtividadeEducacionalJpaEntity a WHERE a.turma.id IN (SELECT ta.turma.id FROM TurmaAlunoJpaEntity ta WHERE ta.aluno.id = :alunoId) AND a.ativa = true ORDER BY a.dataFim ASC")
    List<AtividadeEducacionalJpaEntity> findAtividadesParaAluno(@Param("alunoId") Long alunoId);

    List<AtividadeEducacionalJpaEntity> findAllByOrderByCriadaEmDesc();
}
