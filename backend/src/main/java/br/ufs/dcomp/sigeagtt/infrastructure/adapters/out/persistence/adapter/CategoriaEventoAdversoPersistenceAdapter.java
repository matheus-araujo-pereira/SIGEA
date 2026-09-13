package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CategoriaEventoAdversoRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CategoriaEventoAdversoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.CategoriaEventoAdversoSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link CategoriaEventoAdverso}. */
@Component
public class CategoriaEventoAdversoPersistenceAdapter
        implements CategoriaEventoAdversoRepositoryPort {

    private final CategoriaEventoAdversoSpringDataRepository repository;

    public CategoriaEventoAdversoPersistenceAdapter(
            CategoriaEventoAdversoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CategoriaEventoAdverso> listarTodas() {
        return repository.findAll().stream()
                .map(CategoriaEventoAdversoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<CategoriaEventoAdverso> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(CategoriaEventoAdversoPersistenceAdapter::paraDominio);
    }

    @Override
    public CategoriaEventoAdverso salvar(CategoriaEventoAdverso categoria) {
        CategoriaEventoAdversoJpaEntity entity = paraEntidade(categoria);
        CategoriaEventoAdversoJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    public static CategoriaEventoAdverso paraDominio(CategoriaEventoAdversoJpaEntity entity) {
        if (entity == null) return null;
        return new CategoriaEventoAdverso(
                entity.getId(),
                entity.getNome(),
                entity.getDefinicaoOperacional(),
                entity.getAtiva());
    }

    public static CategoriaEventoAdversoJpaEntity paraEntidade(CategoriaEventoAdverso domain) {
        if (domain == null) return null;
        return CategoriaEventoAdversoJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .definicaoOperacional(domain.getDefinicaoOperacional())
                .ativa(domain.getAtiva())
                .build();
    }
}
