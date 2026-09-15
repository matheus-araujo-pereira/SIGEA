package br.ufs.sigea.academic.activity.dto;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload para criação de uma Atividade com Caso Clínico Simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateDTO {

    @NotNull(message = "A turma vinculada (classId) é obrigatória")
    private UUID classId;

    @NotBlank(message = "O título da atividade é obrigatório")
    @Size(max = 150, message = "O título não pode exceder 150 caracteres")
    private String title;

    @NotBlank(message = "A descrição das instruções da atividade é obrigatória")
    private String description;

    @NotNull(message = "Os dados do caso clínico e prontuário simulado são obrigatórios")
    private ClinicalCaseData clinicalCaseData;

    @NotNull(message = "A data/hora limite para entrega (deadline) é obrigatória")
    @Future(message = "O prazo de entrega deve ser uma data futura")
    private Instant deadline;
}
