package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaAlunoRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoIdJpa;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.TurmaAlunoSpringDataRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link TurmaAluno}. */
@Component
public class TurmaAlunoPersistenceAdapter implements TurmaAlunoRepositoryPort {

    private final TurmaAlunoSpringDataRepository repository;

    public TurmaAlunoPersistenceAdapter(TurmaAlunoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public long contarAlunosPorTurmaId(Long turmaId) {
        if (turmaId == null) return 0;
        return repository.countByTurmaId(turmaId);
    }

    @Override
    public List<TurmaAluno> listarPorTurmaId(Long turmaId) {
        if (turmaId == null) return List.of();
        return repository.findByTurmaId(turmaId).stream()
                .map(TurmaAlunoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public boolean existeMatricula(Long turmaId, Long alunoId) {
        if (turmaId == null || alunoId == null) return false;
        return repository.existsByTurmaIdAndAlunoId(turmaId, alunoId);
    }

    @Override
    public TurmaAluno salvar(TurmaAluno turmaAluno) {
        TurmaAlunoJpaEntity entity = paraEntidade(turmaAluno);
        TurmaAlunoJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void desmatricular(Long turmaId, Long alunoId) {
        if (turmaId != null && alunoId != null) {
            repository.deleteByTurmaIdAndAlunoId(turmaId, alunoId);
        }
    }

    public static TurmaAluno paraDominio(TurmaAlunoJpaEntity entity) {
        if (entity == null) return null;
        return new TurmaAluno(
                TurmaPersistenceAdapter.paraDominio(entity.getTurma()),
                UsuarioPersistenceAdapter.paraDominio(entity.getAluno()),
                entity.getMatriculadoEm());
    }

    public static TurmaAlunoJpaEntity paraEntidade(TurmaAluno domain) {
        if (domain == null) return null;
        Long tId = domain.getTurma() != null ? domain.getTurma().getId() : null;
        Long aId = domain.getAluno() != null ? domain.getAluno().getId() : null;
        return TurmaAlunoJpaEntity.builder()
                .id(new TurmaAlunoIdJpa(tId, aId))
                .turma(TurmaPersistenceAdapter.paraEntidade(domain.getTurma()))
                .aluno(UsuarioPersistenceAdapter.paraEntidade(domain.getAluno()))
                .matriculadoEm(domain.getMatriculadoEm())
                .build();
    }
}
