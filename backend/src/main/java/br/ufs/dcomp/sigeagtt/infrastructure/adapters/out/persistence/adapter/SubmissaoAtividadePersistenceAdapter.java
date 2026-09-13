package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoAtividadeSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link SubmissaoAtividade}. */
@Component
public class SubmissaoAtividadePersistenceAdapter implements SubmissaoAtividadeRepositoryPort {

    private final SubmissaoAtividadeSpringDataRepository repository;

    public SubmissaoAtividadePersistenceAdapter(SubmissaoAtividadeSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SubmissaoAtividade> listarTodas() {
        return repository.findAll().stream()
                .map(SubmissaoAtividadePersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<SubmissaoAtividade> listarPorAtividadeId(Long atividadeId) {
        if (atividadeId == null) return List.of();
        return repository.findByAtividadeId(atividadeId).stream()
                .map(SubmissaoAtividadePersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<SubmissaoAtividade> listarPorAlunoId(Long alunoId) {
        if (alunoId == null) return List.of();
        return repository.findByAlunoIdOrderByDataInicioDesc(alunoId).stream()
                .map(SubmissaoAtividadePersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<SubmissaoAtividade> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(SubmissaoAtividadePersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<SubmissaoAtividade> buscarPorAtividadeEAluno(Long atividadeId, Long alunoId) {
        if (atividadeId == null || alunoId == null) return Optional.empty();
        return repository
                .findByAtividadeIdAndAlunoId(atividadeId, alunoId)
                .map(SubmissaoAtividadePersistenceAdapter::paraDominio);
    }

    @Override
    public SubmissaoAtividade salvar(SubmissaoAtividade submissao) {
        SubmissaoAtividadeJpaEntity entity = paraEntidade(submissao);
        SubmissaoAtividadeJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    public static SubmissaoAtividade paraDominio(SubmissaoAtividadeJpaEntity entity) {
        if (entity == null) return null;
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.setId(entity.getId());
        sub.setAtividade(AtividadeEducacionalPersistenceAdapter.paraDominio(entity.getAtividade()));
        sub.setAluno(UsuarioPersistenceAdapter.paraDominio(entity.getAluno()));
        sub.setStatus(entity.getStatus());
        sub.setTempoGastoSegundos(entity.getTempoGastoSegundos());
        sub.setDataInicio(entity.getDataInicio());
        sub.setDataSubmissao(entity.getDataSubmissao());
        sub.setProfessorCorretor(
                UsuarioPersistenceAdapter.paraDominio(entity.getProfessorCorretor()));
        sub.setNota(entity.getNota());
        sub.setParecerDocente(entity.getParecerDocente());
        sub.setDataAvaliacao(entity.getDataAvaliacao());
        return sub;
    }

    public static SubmissaoAtividadeJpaEntity paraEntidade(SubmissaoAtividade domain) {
        if (domain == null) return null;
        return SubmissaoAtividadeJpaEntity.builder()
                .id(domain.getId())
                .atividade(
                        AtividadeEducacionalPersistenceAdapter.paraEntidade(domain.getAtividade()))
                .aluno(UsuarioPersistenceAdapter.paraEntidade(domain.getAluno()))
                .status(domain.getStatus())
                .tempoGastoSegundos(domain.getTempoGastoSegundos())
                .dataInicio(domain.getDataInicio())
                .dataSubmissao(domain.getDataSubmissao())
                .professorCorretor(
                        UsuarioPersistenceAdapter.paraEntidade(domain.getProfessorCorretor()))
                .nota(domain.getNota())
                .parecerDocente(domain.getParecerDocente())
                .dataAvaliacao(domain.getDataAvaliacao())
                .build();
    }
}
