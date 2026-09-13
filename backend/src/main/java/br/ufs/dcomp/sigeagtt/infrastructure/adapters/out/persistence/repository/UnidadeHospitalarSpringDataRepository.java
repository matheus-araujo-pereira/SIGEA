package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UnidadeHospitalarJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para unidades hospitalares. */
@Repository
public interface UnidadeHospitalarSpringDataRepository
        extends JpaRepository<UnidadeHospitalarJpaEntity, Long> {
    Optional<UnidadeHospitalarJpaEntity> findBySigla(String sigla);

    Optional<UnidadeHospitalarJpaEntity> findBySiglaAndIdNot(String sigla, Long id);

    List<UnidadeHospitalarJpaEntity> findByAtivaTrue();
}
