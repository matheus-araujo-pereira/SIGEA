package br.ufs.sigea.gtt.module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para cadastro de um novo Módulo GTT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição de criação de Módulo GTT")
public class GttModuleCreateDTO {

    @NotBlank(message = "O código do módulo é obrigatório.")
    @Size(min = 1, max = 10, message = "O código do módulo deve ter entre 1 e 10 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "O código do módulo deve conter apenas caracteres alfanuméricos.")
    @Schema(description = "Código abreviado do módulo (ex: C, M, S, I, P, E)", example = "C")
    private String code;

    @NotBlank(message = "O nome do módulo é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome do módulo deve ter entre 2 e 100 caracteres.")
    @Schema(description = "Nome descritivo do módulo", example = "Cuidados")
    private String name;

    @Schema(description = "Descrição detalhada do escopo assistencial do módulo", example = "Gatilhos relacionados aos cuidados gerais prestados ao paciente internado.")
    private String description;
}
