package br.ufs.sigea.academic.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para listagem de Atividades da Turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDTO {
    private UUID id;
    private UUID classId;
    private String className;
    private String title;
    private String description;
    private Instant deadline;
    private Boolean isExpired;
    private Integer submissionCount;
    private Instant createdAt;
}
