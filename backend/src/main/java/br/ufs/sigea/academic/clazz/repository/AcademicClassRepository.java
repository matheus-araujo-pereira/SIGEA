package br.ufs.sigea.academic.clazz.repository;

import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para gerenciamento de Turmas Acadêmicas.
 */
@Repository
public interface AcademicClassRepository extends JpaRepository<AcademicClass, UUID> {

    boolean existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
            String subjectName,
            String classCode,
            String academicPeriod
    );

    @EntityGraph(attributePaths = {"professor"})
    @Query("SELECT c FROM AcademicClass c " +
           "WHERE (CAST(:search AS string) IS NULL OR " +
           "       LOWER(c.subjectName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
           "       LOWER(c.classCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
           "       LOWER(c.academicPeriod) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "  AND (CAST(:academicPeriod AS string) IS NULL OR c.academicPeriod = :academicPeriod) " +
           "  AND (CAST(:isClosed AS boolean) IS NULL OR c.isClosed = :isClosed)")
    Page<AcademicClass> findByFilters(
            @Param("search") String search,
            @Param("academicPeriod") String academicPeriod,
            @Param("isClosed") Boolean isClosed,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"professor"})
    @Query("SELECT c FROM AcademicClass c WHERE c.professor.id = :professorId")
    Page<AcademicClass> findByProfessorId(@Param("professorId") UUID professorId, Pageable pageable);

    @EntityGraph(attributePaths = {"professor"})
    @Query("SELECT c FROM AcademicClass c JOIN c.students s WHERE s.id = :studentId")
    Page<AcademicClass> findByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

    @Query("SELECT c FROM AcademicClass c LEFT JOIN FETCH c.students LEFT JOIN FETCH c.professor WHERE c.id = :id")
    Optional<AcademicClass> findByIdWithStudents(@Param("id") UUID id);
}
