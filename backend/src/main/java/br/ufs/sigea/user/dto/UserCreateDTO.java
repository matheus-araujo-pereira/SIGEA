package br.ufs.sigea.user.dto;

import br.ufs.sigea.common.validation.UfsEmail;
import br.ufs.sigea.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cadastro de um novo usuário pelo Administrador.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro de novo usuário")
public class UserCreateDTO {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome completo deve conter entre 3 e 150 caracteres.")
    @Schema(description = "Nome completo do usuário", example = "Ana Waleska de Menezes Seixas Souza")
    private String fullName;

    @NotBlank(message = "O e-mail institucional é obrigatório.")
    @UfsEmail
    @Size(max = 120, message = "O e-mail não pode exceder 120 caracteres.")
    @Schema(description = "E-mail institucional (@academico.ufs.br)", example = "ana.waleska@academico.ufs.br")
    private String email;

    @NotNull(message = "O perfil do usuário é obrigatório.")
    @Schema(description = "Perfil de acesso (ADMIN, PROFESSOR, STUDENT)", example = "PROFESSOR")
    private UserRole role;

    @Size(max = 30, message = "A matrícula não pode exceder 30 caracteres.")
    @Schema(description = "Matrícula institucional (obrigatória apenas para STUDENT, proibida para ADMIN e PROFESSOR)", example = "20260005678")
    private String registrationNumber;
}
