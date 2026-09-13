package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository;

import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CategoriaEventoAdversoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório Spring Data JPA para categorias de eventos adversos. */
public interface CategoriaEventoAdversoSpringDataRepository
        extends JpaRepository<CategoriaEventoAdversoJpaEntity, Long> {
    List<CategoriaEventoAdversoJpaEntity> findByAtivaTrue();
}
