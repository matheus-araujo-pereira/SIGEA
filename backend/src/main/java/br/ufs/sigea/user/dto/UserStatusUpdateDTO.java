package br.ufs.sigea.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ativação ou inativação do status de um usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para alteração de status ativo/inativo")
public class UserStatusUpdateDTO {

    @NotNull(message = "O status de ativação é obrigatório.")
    @Schema(description = "Novo status da conta (true para ativo, false para inativo)", example = "false")
    private Boolean isActive;
}
