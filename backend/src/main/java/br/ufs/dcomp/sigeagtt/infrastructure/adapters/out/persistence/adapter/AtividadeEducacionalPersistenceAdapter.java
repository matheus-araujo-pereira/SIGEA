package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.ports.output.AtividadeEducacionalRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.AtividadeEducacionalJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.AtividadeEducacionalSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link AtividadeEducacional}. */
@Component
public class AtividadeEducacionalPersistenceAdapter implements AtividadeEducacionalRepositoryPort {

    private final AtividadeEducacionalSpringDataRepository repository;

    public AtividadeEducacionalPersistenceAdapter(
            AtividadeEducacionalSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AtividadeEducacional> listarTodas() {
        return repository.findAllByOrderByCriadaEmDesc().stream()
                .map(AtividadeEducacionalPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<AtividadeEducacional> listarPorProfessorId(Long professorId) {
        if (professorId == null) return List.of();
        return repository.findByProfessorId(professorId).stream()
                .map(AtividadeEducacionalPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<AtividadeEducacional> listarParaAluno(Long alunoId) {
        if (alunoId == null) return List.of();
        return repository.findAtividadesParaAluno(alunoId).stream()
                .map(AtividadeEducacionalPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<AtividadeEducacional> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(AtividadeEducacionalPersistenceAdapter::paraDominio);
    }

    @Override
    public AtividadeEducacional salvar(AtividadeEducacional atividade) {
        AtividadeEducacionalJpaEntity entity = paraEntidade(atividade);
        AtividadeEducacionalJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static AtividadeEducacional paraDominio(AtividadeEducacionalJpaEntity entity) {
        if (entity == null) return null;
        return new AtividadeEducacional(
                entity.getId(),
                TurmaPersistenceAdapter.paraDominio(entity.getTurma()),
                CasoClinicoPersistenceAdapter.paraDominio(entity.getCasoClinico()),
                entity.getTitulo(),
                entity.getOrientacoesPedagogicas(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getTempoLimiteMinutos(),
                entity.getAtiva(),
                entity.getCriadaEm());
    }

    public static AtividadeEducacionalJpaEntity paraEntidade(AtividadeEducacional domain) {
        if (domain == null) return null;
        return AtividadeEducacionalJpaEntity.builder()
                .id(domain.getId())
                .turma(TurmaPersistenceAdapter.paraEntidade(domain.getTurma()))
                .casoClinico(CasoClinicoPersistenceAdapter.paraEntidade(domain.getCasoClinico()))
                .titulo(domain.getTitulo())
                .orientacoesPedagogicas(domain.getOrientacoesPedagogicas())
                .dataInicio(domain.getDataInicio())
                .dataFim(domain.getDataFim())
                .tempoLimiteMinutos(domain.getTempoLimiteMinutos())
                .ativa(domain.getAtiva())
                .criadaEm(domain.getCriadaEm())
                .build();
    }
}
