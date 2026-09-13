package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.TurmaSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link Turma}. */
@Component
public class TurmaPersistenceAdapter implements TurmaRepositoryPort {

    private final TurmaSpringDataRepository repository;

    public TurmaPersistenceAdapter(TurmaSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Turma> listarTodas() {
        return repository.findAll().stream().map(TurmaPersistenceAdapter::paraDominio).toList();
    }

    @Override
    public List<Turma> listarPorProfessorId(Long professorId) {
        if (professorId == null) return List.of();
        return repository.findByProfessorResponsavelId(professorId).stream()
                .map(TurmaPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<Turma> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(TurmaPersistenceAdapter::paraDominio);
    }

    @Override
    public Turma salvar(Turma turma) {
        TurmaJpaEntity entity = paraEntidade(turma);
        TurmaJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static Turma paraDominio(TurmaJpaEntity entity) {
        if (entity == null) return null;
        return new Turma(
                entity.getId(),
                UsuarioPersistenceAdapter.paraDominio(entity.getProfessorResponsavel()),
                entity.getCodigoDisciplina(),
                entity.getNomeDisciplina(),
                entity.getPeriodoLetivo(),
                entity.getAnoSemestre(),
                entity.getAtiva(),
                entity.getCriadaEm());
    }

    public static TurmaJpaEntity paraEntidade(Turma domain) {
        if (domain == null) return null;
        return TurmaJpaEntity.builder()
                .id(domain.getId())
                .professorResponsavel(
                        UsuarioPersistenceAdapter.paraEntidade(domain.getProfessorResponsavel()))
                .codigoDisciplina(domain.getCodigoDisciplina())
                .nomeDisciplina(domain.getNomeDisciplina())
                .periodoLetivo(domain.getPeriodoLetivo())
                .anoSemestre(domain.getAnoSemestre())
                .ativa(domain.getAtiva())
                .criadaEm(domain.getCriadaEm())
                .build();
    }
}
