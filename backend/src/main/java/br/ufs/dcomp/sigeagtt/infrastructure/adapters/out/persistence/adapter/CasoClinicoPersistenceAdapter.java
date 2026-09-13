package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CasoClinicoRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CasoClinicoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.CasoClinicoSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adaptador de persistência para {@link CasoClinico}. */
@Component
public class CasoClinicoPersistenceAdapter implements CasoClinicoRepositoryPort {

    private final CasoClinicoSpringDataRepository repository;

    public CasoClinicoPersistenceAdapter(CasoClinicoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CasoClinico> listarTodos() {
        return repository.findAllByOrderByCriadoEmDesc().stream()
                .map(CasoClinicoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<CasoClinico> listarPorProfessorCriadorId(Long professorId) {
        if (professorId == null) return List.of();
        return repository.findByProfessorCriadorIdOrderByCriadoEmDesc(professorId).stream()
                .map(CasoClinicoPersistenceAdapter::paraDominio)
                .toList();
    }

    @Override
    public Optional<CasoClinico> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(CasoClinicoPersistenceAdapter::paraDominio);
    }

    @Override
    public CasoClinico salvar(CasoClinico caso) {
        CasoClinicoJpaEntity entity = paraEntidade(caso);
        CasoClinicoJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    @Override
    public void excluir(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public static CasoClinico paraDominio(CasoClinicoJpaEntity entity) {
        if (entity == null) return null;
        return new CasoClinico(
                entity.getId(),
                UsuarioPersistenceAdapter.paraDominio(entity.getProfessorCriador()),
                UnidadeHospitalarPersistenceAdapter.paraDominio(entity.getUnidadeHospitalar()),
                entity.getTitulo(),
                entity.getDescricaoCaso(),
                entity.getObjetivosAprendizagem(),
                entity.getNumeroAtendimento(),
                entity.getIdadePaciente(),
                entity.getDataAdmissao(),
                entity.getDataAlta(),
                entity.getTempoPermanenciaDias(),
                entity.getSumarioAlta(),
                entity.getPrescricoesMedicas(),
                entity.getExamesLaboratoriais(),
                entity.getRelatorioCirurgico(),
                entity.getEvolucoesMultiprofissionais(),
                entity.getCriadoEm());
    }

    public static CasoClinicoJpaEntity paraEntidade(CasoClinico domain) {
        if (domain == null) return null;
        return CasoClinicoJpaEntity.builder()
                .id(domain.getId())
                .professorCriador(
                        UsuarioPersistenceAdapter.paraEntidade(domain.getProfessorCriador()))
                .unidadeHospitalar(
                        UnidadeHospitalarPersistenceAdapter.paraEntidade(
                                domain.getUnidadeHospitalar()))
                .titulo(domain.getTitulo())
                .descricaoCaso(domain.getDescricaoCaso())
                .objetivosAprendizagem(domain.getObjetivosAprendizagem())
                .numeroAtendimento(domain.getNumeroAtendimento())
                .idadePaciente(domain.getIdadePaciente())
                .dataAdmissao(domain.getDataAdmissao())
                .dataAlta(domain.getDataAlta())
                .tempoPermanenciaDias(domain.getTempoPermanenciaDias())
                .sumarioAlta(domain.getSumarioAlta())
                .prescricoesMedicas(domain.getPrescricoesMedicas())
                .examesLaboratoriais(domain.getExamesLaboratoriais())
                .relatorioCirurgico(domain.getRelatorioCirurgico())
                .evolucoesMultiprofissionais(domain.getEvolucoesMultiprofissionais())
                .criadoEm(domain.getCriadoEm())
                .build();
    }
}
