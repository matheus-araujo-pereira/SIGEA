package br.ufs.sigea.academic.activity.repository;

import br.ufs.sigea.academic.activity.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para gerenciamento de Atividades com Casos Clínicos Simulados.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Page<Activity> findByAcademicClassId(UUID classId, Pageable pageable);

    List<Activity> findByAcademicClassId(UUID classId);

    long countByAcademicClassId(UUID classId);

    @Query("SELECT a FROM Activity a JOIN FETCH a.academicClass WHERE a.id = :id")
    Optional<Activity> findByIdWithClass(@Param("id") UUID id);
}
