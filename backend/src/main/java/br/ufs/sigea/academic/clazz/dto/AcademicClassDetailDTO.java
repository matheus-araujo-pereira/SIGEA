package br.ufs.sigea.academic.clazz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO detalhado de uma Turma Acadêmica com a lista de estudantes matriculados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClassDetailDTO {
    private UUID id;
    private String subjectName;
    private String classCode;
    private String academicPeriod;
    private String formattedName;
    private UUID professorId;
    private String professorName;
    private String professorEmail;
    private Boolean isClosed;
    private Integer studentCount;
    private Integer activityCount;
    private Instant createdAt;

    @Builder.Default
    private List<ClassStudentSummaryDTO> students = new ArrayList<>();
}
