package br.ufs.sigea.academic.activity.dto;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO detalhado de uma Atividade contendo todo o caso clínico e histórico de submissão do aluno.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailDTO {
    private UUID id;
    private UUID classId;
    private String className;
    private String title;
    private String description;
    private ClinicalCaseData clinicalCaseData;
    private Instant deadline;
    private Boolean isExpired;
    private Integer submissionCount;
    private Instant createdAt;

    /**
     * Resolução prévia do estudante (se houver).
     */
    private SubmissionResponseDTO studentSubmission;
}
