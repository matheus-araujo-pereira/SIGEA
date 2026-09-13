package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositório Spring Data JPA para submissões discentes. */
public interface SubmissaoAtividadeSpringDataRepository
        extends JpaRepository<SubmissaoAtividadeJpaEntity, Long> {

    Optional<SubmissaoAtividadeJpaEntity> findByAtividadeIdAndAlunoId(
            Long atividadeId, Long alunoId);

    List<SubmissaoAtividadeJpaEntity> findByAtividadeId(Long atividadeId);

    List<SubmissaoAtividadeJpaEntity> findByAlunoIdOrderByDataInicioDesc(Long alunoId);

    @Query(
            "SELECT s FROM SubmissaoAtividadeJpaEntity s WHERE s.atividade.turma.professorResponsavel.id = :professorId AND s.status = :status ORDER BY s.dataSubmissao ASC")
    List<SubmissaoAtividadeJpaEntity> findByProfessorAndStatus(
            @Param("professorId") Long professorId, @Param("status") StatusSubmissao status);

    @Query(
            "SELECT s FROM SubmissaoAtividadeJpaEntity s WHERE s.atividade.turma.professorResponsavel.id = :professorId ORDER BY s.dataSubmissao DESC")
    List<SubmissaoAtividadeJpaEntity> findByProfessor(@Param("professorId") Long professorId);

    @Query("SELECT s FROM SubmissaoAtividadeJpaEntity s WHERE s.status = 'AVALIADA'")
    List<SubmissaoAtividadeJpaEntity> findTodasAvaliadas();
}
