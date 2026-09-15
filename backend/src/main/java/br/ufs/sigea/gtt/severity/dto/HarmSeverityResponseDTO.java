package br.ufs.sigea.gtt.severity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO de resposta detalhada de uma Categoria de Gravidade NCC MERP.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de retorno de uma Categoria de Gravidade de Dano")
public class HarmSeverityResponseDTO {

    @Schema(description = "Identificador único (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Letra da categoria (A a I)", example = "E")
    private String categoryLetter;

    @Schema(description = "Nome descritivo da categoria", example = "Dano temporário com intervenção")
    private String name;

    @Schema(description = "Critérios diagnósticos e definições do NCC MERP")
    private String description;

    @Schema(description = "Indica se esta categoria representa dano real ao paciente", example = "true")
    private Boolean isHarm;

    @Schema(description = "Status de ativação", example = "true")
    private Boolean isActive;
}
