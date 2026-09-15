package br.ufs.sigea.academic.activity.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payload para atribuição de nota e parecer pedagógico pelo professor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionGradeDTO {

    @NotNull(message = "A nota é obrigatória")
    @DecimalMin(value = "0.00", message = "A nota não pode ser inferior a 0.0")
    @DecimalMax(value = "10.00", message = "A nota não pode ser superior a 10.0")
    private BigDecimal grade;

    private String professorFeedback;
}
