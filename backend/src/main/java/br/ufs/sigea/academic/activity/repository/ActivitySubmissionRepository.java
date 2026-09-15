package br.ufs.sigea.academic.activity.repository;

import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
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
 * Repositório Spring Data JPA para gerenciamento de Submissões e Correções de Atividades.
 */
@Repository
public interface ActivitySubmissionRepository extends JpaRepository<ActivitySubmission, UUID> {

    Optional<ActivitySubmission> findByActivityIdAndStudentId(UUID activityId, UUID studentId);

    boolean existsByActivityIdAndStudentId(UUID activityId, UUID studentId);

    Page<ActivitySubmission> findByActivityId(UUID activityId, Pageable pageable);

    List<ActivitySubmission> findByActivityId(UUID activityId);

    long countByActivityId(UUID activityId);

    Page<ActivitySubmission> findByStudentId(UUID studentId, Pageable pageable);

    @Query("SELECT s FROM ActivitySubmission s WHERE s.activity.academicClass.id = :classId")
    List<ActivitySubmission> findByClassId(@Param("classId") UUID classId);

    @Query("SELECT COUNT(s) FROM ActivitySubmission s WHERE s.activity.academicClass.id = :classId AND s.grade IS NULL")
    long countUngradedSubmissionsByClassId(@Param("classId") UUID classId);
}
