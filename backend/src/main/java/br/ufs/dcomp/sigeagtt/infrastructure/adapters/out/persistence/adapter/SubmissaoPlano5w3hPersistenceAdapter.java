package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPlano5w3hRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPlano5w3hJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoPlano5w3hSpringDataRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link SubmissaoPlano5w3h}. */
@Component
public class SubmissaoPlano5w3hPersistenceAdapter implements SubmissaoPlano5w3hRepositoryPort {

    private final SubmissaoPlano5w3hSpringDataRepository repository;

    public SubmissaoPlano5w3hPersistenceAdapter(SubmissaoPlano5w3hSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SubmissaoPlano5w3h> listarPorSubmissaoId(Long submissaoId) {
        if (submissaoId == null) return List.of();
        return repository.findBySubmissaoId(submissaoId).stream()
                .map(SubmissaoPlano5w3hPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public SubmissaoPlano5w3h salvar(SubmissaoPlano5w3h plano) {
        SubmissaoPlano5w3hJpaEntity entity = paraEntidade(plano);
        SubmissaoPlano5w3hJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluirPorSubmissaoId(Long submissaoId) {
        if (submissaoId != null) {
            repository.deleteBySubmissaoId(submissaoId);
        }
    }

    public static SubmissaoPlano5w3h paraDominio(SubmissaoPlano5w3hJpaEntity entity) {
        if (entity == null) return null;
        SubmissaoAtividade sub = null;
        if (entity.getSubmissao() != null) {
            sub = new SubmissaoAtividade();
            sub.setId(entity.getSubmissao().getId());
        }
        return new SubmissaoPlano5w3h(
                entity.getId(),
                sub,
                entity.getOQue(),
                entity.getPorQue(),
                entity.getQuem(),
                entity.getOnde(),
                entity.getQuando(),
                entity.getComo(),
                entity.getQuantoCusta(),
                entity.getComoMedir());
    }

    public static SubmissaoPlano5w3hJpaEntity paraEntidade(SubmissaoPlano5w3h domain) {
        if (domain == null) return null;
        SubmissaoAtividadeJpaEntity sub = null;
        if (domain.getSubmissao() != null) {
            sub = SubmissaoAtividadeJpaEntity.builder().id(domain.getSubmissao().getId()).build();
        }
        return SubmissaoPlano5w3hJpaEntity.builder()
                .id(domain.getId())
                .submissao(sub)
                .oQue(domain.getOQue())
                .porQue(domain.getPorQue())
                .quem(domain.getQuem())
                .onde(domain.getOnde())
                .quando(domain.getQuando())
                .como(domain.getComo())
                .quantoCusta(domain.getQuantoCusta())
                .comoMedir(domain.getComoMedir())
                .build();
    }
}
