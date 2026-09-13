package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoIshikawaRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoIshikawaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoIshikawaSpringDataRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link SubmissaoIshikawa}. */
@Component
public class SubmissaoIshikawaPersistenceAdapter implements SubmissaoIshikawaRepositoryPort {

    private final SubmissaoIshikawaSpringDataRepository repository;

    public SubmissaoIshikawaPersistenceAdapter(SubmissaoIshikawaSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SubmissaoIshikawa> buscarPorSubmissaoId(Long submissaoId) {
        if (submissaoId == null) return Optional.empty();
        return repository
                .findBySubmissaoId(submissaoId)
                .map(SubmissaoIshikawaPersistenceAdapter::paraDominio);
    }

    @Override
    public SubmissaoIshikawa salvar(SubmissaoIshikawa ishikawa) {
        SubmissaoIshikawaJpaEntity entity = paraEntidade(ishikawa);
        SubmissaoIshikawaJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluirPorSubmissaoId(Long submissaoId) {
        if (submissaoId != null) {
            repository.deleteBySubmissaoId(submissaoId);
        }
    }

    public static SubmissaoIshikawa paraDominio(SubmissaoIshikawaJpaEntity entity) {
        if (entity == null) return null;
        SubmissaoAtividade sub = null;
        if (entity.getSubmissao() != null) {
            sub = new SubmissaoAtividade();
            sub.setId(entity.getSubmissao().getId());
        }
        return new SubmissaoIshikawa(
                entity.getId(),
                sub,
                entity.getEfeitoPrincipal(),
                entity.getMetodo(),
                entity.getMaoDeObra(),
                entity.getMaterial(),
                entity.getMedida(),
                entity.getMeioAmbiente(),
                entity.getMaquina());
    }

    public static SubmissaoIshikawaJpaEntity paraEntidade(SubmissaoIshikawa domain) {
        if (domain == null) return null;
        SubmissaoAtividadeJpaEntity sub = null;
        if (domain.getSubmissao() != null) {
            sub = SubmissaoAtividadeJpaEntity.builder().id(domain.getSubmissao().getId()).build();
        }
        return SubmissaoIshikawaJpaEntity.builder()
                .id(domain.getId())
                .submissao(sub)
                .efeitoPrincipal(domain.getEfeitoPrincipal())
                .metodo(domain.getMetodo())
                .maoDeObra(domain.getMaoDeObra())
                .material(domain.getMaterial())
                .medida(domain.getMedida())
                .meioAmbiente(domain.getMeioAmbiente())
                .maquina(domain.getMaquina())
                .build();
    }
}
