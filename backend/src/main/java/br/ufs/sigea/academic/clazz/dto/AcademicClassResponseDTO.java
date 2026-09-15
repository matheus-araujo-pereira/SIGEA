package br.ufs.sigea.academic.clazz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de resposta para listagens de Turmas Acadêmicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClassResponseDTO {
    private UUID id;
    private String subjectName;
    private String classCode;
    private String academicPeriod;
    private String formattedName; // Ex: "Física 3 - T06 - 2026.2"
    private UUID professorId;
    private String professorName;
    private String professorEmail;
    private Boolean isClosed;
    private Integer studentCount;
    private Integer activityCount;
    private Instant createdAt;
}
