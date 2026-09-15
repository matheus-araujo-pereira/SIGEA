package br.ufs.sigea.gtt.module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para alteração do status (ativo/inativo) de um Módulo GTT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição de alteração de status de Módulo GTT")
public class GttModuleStatusUpdateDTO {

    @NotNull(message = "O campo isActive é obrigatório.")
    @Schema(description = "Indicador de status ativo", example = "true")
    private Boolean isActive;
}
