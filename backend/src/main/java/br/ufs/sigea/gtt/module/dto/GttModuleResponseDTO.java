package br.ufs.sigea.gtt.module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de resposta detalhada de um Módulo GTT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de retorno de um Módulo GTT")
public class GttModuleResponseDTO {

    @Schema(description = "Identificador único (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Código do módulo", example = "C")
    private String code;

    @Schema(description = "Nome do módulo", example = "Cuidados")
    private String name;

    @Schema(description = "Descrição detalhada do escopo assistencial")
    private String description;

    @Schema(description = "Status de ativação", example = "true")
    private Boolean isActive;

    @Schema(description = "Data de cadastro no sistema")
    private OffsetDateTime createdAt;

    @Schema(description = "Quantidade de gatilhos vinculados ao módulo", example = "15")
    private Long triggerCount;
}
