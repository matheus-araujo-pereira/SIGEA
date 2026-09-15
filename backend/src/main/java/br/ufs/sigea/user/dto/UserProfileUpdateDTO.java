package br.ufs.sigea.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização das informações básicas do próprio perfil do usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização do perfil do usuário autenticado")
public class UserProfileUpdateDTO {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome completo deve conter entre 3 e 150 caracteres.")
    @Schema(description = "Nome completo atualizado", example = "Matheus Araujo Pereira")
    private String fullName;
}
