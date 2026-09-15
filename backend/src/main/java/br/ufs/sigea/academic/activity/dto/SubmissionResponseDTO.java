package br.ufs.sigea.academic.activity.dto;

import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta detalhada da submissão da atividade.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponseDTO {
    private UUID id;
    private UUID activityId;
    private String activityTitle;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private String studentRegistrationNumber;

    @Builder.Default
    private List<IdentifiedTriggerData> identifiedTriggers = new ArrayList<>();

    private QualityToolsData qualityToolsData;
    private Instant submissionDate;
    private BigDecimal grade;
    private String professorFeedback;
    private Instant gradedAt;
    private Boolean isGraded;
}
