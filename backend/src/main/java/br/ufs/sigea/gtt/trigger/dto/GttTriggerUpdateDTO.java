package br.ufs.sigea.gtt.trigger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para atualização cadastral de um Gatilho IHI-GTT existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição de atualização de Gatilho IHI-GTT")
public class GttTriggerUpdateDTO {

    @NotNull(message = "O ID do módulo vinculado é obrigatório.")
    @Schema(description = "Identificador único (UUID) do módulo pai", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID moduleId;

    @NotBlank(message = "O código do gatilho é obrigatório.")
    @Size(min = 1, max = 10, message = "O código do gatilho deve ter entre 1 e 10 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "O código do gatilho deve conter apenas caracteres alfanuméricos.")
    @Schema(description = "Código do gatilho clínico (ex: C1, M4, S10)", example = "C1")
    private String code;

    @NotBlank(message = "O nome do gatilho é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome do gatilho deve ter entre 3 e 150 caracteres.")
    @Schema(description = "Nome descritivo oficial do gatilho clínico", example = "Transfusão de sangue, hemocomponentes ou hemoderivados")
    private String name;

    @NotBlank(message = "A descrição do gatilho é obrigatória.")
    @Schema(description = "Diretrizes e orientações detalhadas de rastreamento no prontuário")
    private String description;
}
