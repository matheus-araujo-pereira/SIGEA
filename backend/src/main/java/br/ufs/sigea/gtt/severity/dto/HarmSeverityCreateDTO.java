package br.ufs.sigea.gtt.severity.dto;

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

/**
 * DTO para cadastro de uma nova Categoria de Gravidade NCC MERP.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição de criação de Categoria de Gravidade de Dano")
public class HarmSeverityCreateDTO {

    @NotBlank(message = "A letra da categoria é obrigatória.")
    @Size(min = 1, max = 1, message = "A letra da categoria deve conter exatamente 1 caractere.")
    @Pattern(regexp = "^[A-Za-z]$", message = "A categoria deve ser uma letra de A a Z.")
    @Schema(description = "Letra identificadora da categoria (A a I)", example = "E")
    private String categoryLetter;

    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres.")
    @Schema(description = "Nome descritivo da categoria", example = "Dano temporário com intervenção")
    private String name;

    @NotBlank(message = "A descrição da categoria é obrigatória.")
    @Schema(description = "Critérios diagnósticos e definições do NCC MERP adaptado")
    private String description;

    @NotNull(message = "O campo isHarm é obrigatório.")
    @Schema(description = "Indica se esta categoria representa dano real ao paciente (True para E-I; False para A-D)", example = "true")
    private Boolean isHarm;
}
