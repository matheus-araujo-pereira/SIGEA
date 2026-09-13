package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoGatilhoRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoGatilhoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoGatilhoSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link SubmissaoGatilho}. */
@Component
public class SubmissaoGatilhoPersistenceAdapter implements SubmissaoGatilhoRepositoryPort {

    private final SubmissaoGatilhoSpringDataRepository repository;

    public SubmissaoGatilhoPersistenceAdapter(SubmissaoGatilhoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SubmissaoGatilho> listarPorSubmissaoId(Long submissaoId) {
        if (submissaoId == null) return List.of();
        return repository.findBySubmissaoId(submissaoId).stream()
                .map(SubmissaoGatilhoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<SubmissaoGatilho> listarTodos() {
        return repository.findAll().stream()
                .map(SubmissaoGatilhoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<SubmissaoGatilho> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(SubmissaoGatilhoPersistenceAdapter::paraDominio);
    }

    @Override
    public SubmissaoGatilho salvar(SubmissaoGatilho achado) {
        SubmissaoGatilhoJpaEntity entity = paraEntidade(achado);
        SubmissaoGatilhoJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluirPorSubmissaoId(Long submissaoId) {
        if (submissaoId != null) {
            repository.deleteBySubmissaoId(submissaoId);
        }
    }

    public static SubmissaoGatilho paraDominio(SubmissaoGatilhoJpaEntity entity) {
        if (entity == null) return null;
        SubmissaoAtividade sub = null;
        if (entity.getSubmissao() != null) {
            sub = new SubmissaoAtividade();
            sub.setId(entity.getSubmissao().getId());
        }
        return new SubmissaoGatilho(
                entity.getId(),
                sub,
                GatilhoGttPersistenceAdapter.paraDominio(entity.getGatilho()),
                CategoriaEventoAdversoPersistenceAdapter.paraDominio(
                        entity.getCategoriaEventoAdverso()),
                entity.getConfirmouDano(),
                entity.getJustificativaDano(),
                entity.getDanoPresenteAdmissao(),
                entity.getGravidade());
    }

    public static SubmissaoGatilhoJpaEntity paraEntidade(SubmissaoGatilho domain) {
        if (domain == null) return null;
        SubmissaoAtividadeJpaEntity sub = null;
        if (domain.getSubmissao() != null) {
            sub = SubmissaoAtividadeJpaEntity.builder().id(domain.getSubmissao().getId()).build();
        }
        return SubmissaoGatilhoJpaEntity.builder()
                .id(domain.getId())
                .submissao(sub)
                .gatilho(GatilhoGttPersistenceAdapter.paraEntidade(domain.getGatilho()))
                .categoriaEventoAdverso(
                        CategoriaEventoAdversoPersistenceAdapter.paraEntidade(
                                domain.getCategoriaEventoAdverso()))
                .confirmouDano(domain.getConfirmouDano())
                .justificativaDano(domain.getJustificativaDano())
                .danoPresenteAdmissao(domain.getDanoPresenteAdmissao())
                .gravidade(domain.getGravidade())
                .build();
    }
}
