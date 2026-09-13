package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPdcaRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPdcaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoPdcaSpringDataRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link SubmissaoPdca}. */
@Component
public class SubmissaoPdcaPersistenceAdapter implements SubmissaoPdcaRepositoryPort {

    private final SubmissaoPdcaSpringDataRepository repository;

    public SubmissaoPdcaPersistenceAdapter(SubmissaoPdcaSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SubmissaoPdca> buscarPorSubmissaoId(Long submissaoId) {
        if (submissaoId == null) return Optional.empty();
        return repository
                .findBySubmissaoId(submissaoId)
                .map(SubmissaoPdcaPersistenceAdapter::paraDominio);
    }

    @Override
    public SubmissaoPdca salvar(SubmissaoPdca pdca) {
        SubmissaoPdcaJpaEntity entity = paraEntidade(pdca);
        SubmissaoPdcaJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluirPorSubmissaoId(Long submissaoId) {
        if (submissaoId != null) {
            repository.deleteBySubmissaoId(submissaoId);
        }
    }

    public static SubmissaoPdca paraDominio(SubmissaoPdcaJpaEntity entity) {
        if (entity == null) return null;
        SubmissaoAtividade sub = null;
        if (entity.getSubmissao() != null) {
            sub = new SubmissaoAtividade();
            sub.setId(entity.getSubmissao().getId());
        }
        return new SubmissaoPdca(
                entity.getId(),
                sub,
                entity.getPlanejar(),
                entity.getFazer(),
                entity.getChecar(),
                entity.getAgir());
    }

    public static SubmissaoPdcaJpaEntity paraEntidade(SubmissaoPdca domain) {
        if (domain == null) return null;
        SubmissaoAtividadeJpaEntity sub = null;
        if (domain.getSubmissao() != null) {
            sub = SubmissaoAtividadeJpaEntity.builder().id(domain.getSubmissao().getId()).build();
        }
        return SubmissaoPdcaJpaEntity.builder()
                .id(domain.getId())
                .submissao(sub)
                .planejar(domain.getPlanejar())
                .fazer(domain.getFazer())
                .checar(domain.getChecar())
                .agir(domain.getAgir())
                .build();
    }
}
