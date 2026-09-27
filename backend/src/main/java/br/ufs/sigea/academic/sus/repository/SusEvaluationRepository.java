package br.ufs.sigea.academic.sus.repository;

import br.ufs.sigea.academic.sus.domain.SusEvaluation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para a entidade SusEvaluation.
 */
@Repository
public interface SusEvaluationRepository extends JpaRepository<SusEvaluation, UUID> {

    @EntityGraph(attributePaths = {"student", "academicClass"})
    Optional<SusEvaluation> findByStudentIdAndAcademicClassId(UUID studentId, UUID academicClassId);

    @EntityGraph(attributePaths = {"student", "academicClass"})
    Optional<SusEvaluation> findByStudentIdAndAcademicClassIsNull(UUID studentId);

    boolean existsByStudentIdAndAcademicClassId(UUID studentId, UUID academicClassId);

    boolean existsByStudentIdAndAcademicClassIsNull(UUID studentId);

    @EntityGraph(attributePaths = {"student", "academicClass"})
    @Query("SELECT s FROM SusEvaluation s WHERE s.academicClass.id = :classId ORDER BY s.createdAt DESC")
    List<SusEvaluation> findByAcademicClassIdWithDetails(@Param("classId") UUID classId);

    @EntityGraph(attributePaths = {"student", "academicClass"})
    @Query("SELECT s FROM SusEvaluation s WHERE s.student.id = :studentId ORDER BY s.createdAt DESC")
    List<SusEvaluation> findByStudentIdWithDetails(@Param("studentId") UUID studentId);

    @EntityGraph(attributePaths = {"student", "academicClass"})
    @Query("SELECT s FROM SusEvaluation s ORDER BY s.createdAt DESC")
    List<SusEvaluation> findAllWithDetails();

    @Query("SELECT AVG(s.score) FROM SusEvaluation s WHERE s.academicClass.id = :classId")
    Double calculateAverageScoreByClassId(@Param("classId") UUID classId);

    @Query("SELECT AVG(s.score) FROM SusEvaluation s")
    Double calculateGlobalAverageScore();

    @Query("SELECT COUNT(s) FROM SusEvaluation s WHERE s.academicClass.id = :classId")
    long countByAcademicClassId(@Param("classId") UUID classId);
}
