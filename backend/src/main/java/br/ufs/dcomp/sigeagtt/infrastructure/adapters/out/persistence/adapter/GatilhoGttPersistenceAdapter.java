package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.output.GatilhoGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.GatilhoGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.GatilhoGttSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link GatilhoGtt}. */
@Component
public class GatilhoGttPersistenceAdapter implements GatilhoGttRepositoryPort {

    private final GatilhoGttSpringDataRepository repository;

    public GatilhoGttPersistenceAdapter(GatilhoGttSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GatilhoGtt> listarTodos() {
        return repository.findAllOrderByCodigo().stream()
                .map(GatilhoGttPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<GatilhoGtt> listarPorModuloId(Long moduloId) {
        return repository.findAllByModuloId(moduloId).stream()
                .map(GatilhoGttPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<GatilhoGtt> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(GatilhoGttPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<GatilhoGtt> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return repository.findByCodigo(codigo).map(GatilhoGttPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<GatilhoGtt> buscarPorCodigoEIdDiferente(String codigo, Long id) {
        if (codigo == null || id == null) return Optional.empty();
        return repository
                .findByCodigoAndIdNot(codigo, id)
                .map(GatilhoGttPersistenceAdapter::paraDominio);
    }

    @Override
    public GatilhoGtt salvar(GatilhoGtt gatilho) {
        GatilhoGttJpaEntity entity = paraEntidade(gatilho);
        GatilhoGttJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static GatilhoGtt paraDominio(GatilhoGttJpaEntity entity) {
        if (entity == null) return null;
        return new GatilhoGtt(
                entity.getId(),
                entity.getCodigo(),
                ModuloGttPersistenceAdapter.paraDominio(entity.getModulo()),
                entity.getDescricao(),
                entity.getLimiarReferencia(),
                entity.getAtivo());
    }

    public static GatilhoGttJpaEntity paraEntidade(GatilhoGtt domain) {
        if (domain == null) return null;
        return GatilhoGttJpaEntity.builder()
                .id(domain.getId())
                .codigo(domain.getCodigo())
                .modulo(ModuloGttPersistenceAdapter.paraEntidade(domain.getModulo()))
                .descricao(domain.getDescricao())
                .limiarReferencia(domain.getLimiarReferencia())
                .ativo(domain.getAtivo())
                .build();
    }
}
