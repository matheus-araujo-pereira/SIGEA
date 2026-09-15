package br.ufs.sigea.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para redefinição obrigatória de senha no primeiro login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para redefinição obrigatória de senha no primeiro acesso")
public class FirstLoginChangePasswordDTO {

    @NotBlank(message = "A senha provisória atual é obrigatória.")
    @Schema(description = "Senha provisória atual recebida", example = "SigeaUFS@2026")
    private String currentPassword;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, max = 50, message = "A nova senha deve ter entre 8 e 50 caracteres.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "A senha deve conter ao menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)."
    )
    @Schema(description = "Nova senha definitiva", example = "MinhaNovaSenha@2026")
    private String newPassword;

    @NotBlank(message = "A confirmação da nova senha é obrigatória.")
    @Schema(description = "Confirmação idêntica da nova senha", example = "MinhaNovaSenha@2026")
    private String confirmPassword;
}
