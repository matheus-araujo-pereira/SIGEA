package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UnidadeHospitalarRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UnidadeHospitalarJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.UnidadeHospitalarSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link UnidadeHospitalar}. */
@Component
public class UnidadeHospitalarPersistenceAdapter implements UnidadeHospitalarRepositoryPort {

    private final UnidadeHospitalarSpringDataRepository repository;

    public UnidadeHospitalarPersistenceAdapter(UnidadeHospitalarSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UnidadeHospitalar> listarTodas() {
        return repository.findAll().stream()
                .map(UnidadeHospitalarPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<UnidadeHospitalar> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(UnidadeHospitalarPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<UnidadeHospitalar> buscarPorSigla(String sigla) {
        if (sigla == null) return Optional.empty();
        return repository.findBySigla(sigla).map(UnidadeHospitalarPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<UnidadeHospitalar> buscarPorSiglaEIdDiferente(String sigla, Long id) {
        if (sigla == null || id == null) return Optional.empty();
        return repository
                .findBySiglaAndIdNot(sigla, id)
                .map(UnidadeHospitalarPersistenceAdapter::paraDominio);
    }

    @Override
    public UnidadeHospitalar salvar(UnidadeHospitalar unidade) {
        UnidadeHospitalarJpaEntity entity = paraEntidade(unidade);
        UnidadeHospitalarJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static UnidadeHospitalar paraDominio(UnidadeHospitalarJpaEntity entity) {
        if (entity == null) return null;
        return new UnidadeHospitalar(
                entity.getId(), entity.getNome(), entity.getSigla(), entity.getAtiva());
    }

    public static UnidadeHospitalarJpaEntity paraEntidade(UnidadeHospitalar domain) {
        if (domain == null) return null;
        return UnidadeHospitalarJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .sigla(domain.getSigla())
                .ativa(domain.getAtiva())
                .build();
    }
}
