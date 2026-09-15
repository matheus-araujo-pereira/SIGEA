package br.ufs.sigea.auth.dto;

import br.ufs.sigea.common.validation.UfsEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitação de login no sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciais para autenticação")
public class LoginRequestDTO {

    @NotBlank(message = "O e-mail institucional é obrigatório.")
    @UfsEmail
    @Schema(description = "E-mail institucional (@academico.ufs.br)", example = "admin.sigea@academico.ufs.br")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(description = "Senha do usuário", example = "SigeaUFS@2026")
    private String password;
}
