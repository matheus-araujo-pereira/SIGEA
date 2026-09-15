package br.ufs.sigea.gtt.trigger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de resposta detalhada de um Gatilho IHI-GTT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de retorno de um Gatilho IHI-GTT")
public class GttTriggerResponseDTO {

    @Schema(description = "Identificador único (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "ID do módulo pai")
    private UUID moduleId;

    @Schema(description = "Código do módulo pai", example = "C")
    private String moduleCode;

    @Schema(description = "Nome do módulo pai", example = "Cuidados")
    private String moduleName;

    @Schema(description = "Código do gatilho clínico", example = "C1")
    private String code;

    @Schema(description = "Nome descritivo oficial do gatilho clínico", example = "Transfusão de sangue, hemocomponentes ou hemoderivados")
    private String name;

    @Schema(description = "Diretrizes e orientações clínicas de investigação")
    private String description;

    @Schema(description = "Status de ativação", example = "true")
    private Boolean isActive;

    @Schema(description = "Data de cadastro no sistema")
    private OffsetDateTime createdAt;
}
