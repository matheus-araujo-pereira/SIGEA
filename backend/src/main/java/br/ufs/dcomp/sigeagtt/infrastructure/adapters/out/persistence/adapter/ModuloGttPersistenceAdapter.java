package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.output.ModuloGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.ModuloGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.ModuloGttSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link ModuloGtt}. */
@Component
public class ModuloGttPersistenceAdapter implements ModuloGttRepositoryPort {

    private final ModuloGttSpringDataRepository repository;

    public ModuloGttPersistenceAdapter(ModuloGttSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ModuloGtt> listarTodos() {
        return repository.findAll().stream().map(ModuloGttPersistenceAdapter::paraDominio).toList();
    }

    @Override
    public Optional<ModuloGtt> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(ModuloGttPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<ModuloGtt> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return repository.findByCodigo(codigo).map(ModuloGttPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<ModuloGtt> buscarPorCodigoEIdDiferente(String codigo, Long id) {
        if (codigo == null || id == null) return Optional.empty();
        return repository
                .findByCodigoAndIdNot(codigo, id)
                .map(ModuloGttPersistenceAdapter::paraDominio);
    }

    @Override
    public ModuloGtt salvar(ModuloGtt modulo) {
        ModuloGttJpaEntity entity = paraEntidade(modulo);
        ModuloGttJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static ModuloGtt paraDominio(ModuloGttJpaEntity entity) {
        if (entity == null) return null;
        return new ModuloGtt(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getAtivo(),
                entity.getCriadoEm());
    }

    public static ModuloGttJpaEntity paraEntidade(ModuloGtt domain) {
        if (domain == null) return null;
        return ModuloGttJpaEntity.builder()
                .id(domain.getId())
                .codigo(domain.getCodigo())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .ativo(domain.getAtivo())
                .criadoEm(domain.getCriadoEm())
                .build();
    }
}
