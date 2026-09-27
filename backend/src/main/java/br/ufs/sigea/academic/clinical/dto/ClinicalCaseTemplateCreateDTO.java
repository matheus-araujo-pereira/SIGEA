package br.ufs.sigea.academic.clinical.dto;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cadastro de novo modelo de caso clínico simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro de novo modelo de caso clínico simulado")
public class ClinicalCaseTemplateCreateDTO {

    @NotBlank(message = "O título do caso clínico é obrigatório.")
    @Size(max = 200, message = "O título não pode exceder 200 caracteres.")
    @Schema(description = "Título do caso clínico", example = "Caso Clínico: Intoxicação por Sedativos")
    private String title;

    @NotBlank(message = "A descrição do caso clínico é obrigatória.")
    @Schema(description = "Descrição detalhada do caso clínico", example = "Análise de sedação excessiva em enfermaria cirúrgica pós-administração de Diazepam.")
    private String description;

    @NotBlank(message = "O código do módulo GTT é obrigatório.")
    @Size(max = 20, message = "O código do módulo não pode exceder 20 caracteres.")
    @Schema(description = "Código do módulo GTT (C, M, S, I, P, E)", example = "M")
    private String moduleCode;

    @NotBlank(message = "O código do gatilho clínico é obrigatório.")
    @Size(max = 10, message = "O código do gatilho não pode exceder 10 caracteres.")
    @Schema(description = "Código do gatilho primário esperado", example = "M8")
    private String primaryTriggerCode;

    @NotBlank(message = "A gravidade esperada é obrigatória.")
    @Size(max = 5, message = "A gravidade não pode exceder 5 caracteres.")
    @Schema(description = "Gravidade NCC MERP esperada", example = "E")
    private String expectedSeverity;

    @NotNull(message = "Os dados do prontuário simulado são obrigatórios.")
    @Valid
    @Schema(description = "Prontuário simulado estruturado")
    private ClinicalCaseData clinicalCaseData;
}
