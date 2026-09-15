package br.ufs.sigea.user.dto;

import br.ufs.sigea.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de representação dos dados públicos do usuário retornado pela API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de visualização de usuário")
public class UserResponseDTO {

    @Schema(description = "Identificador único (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Nome completo do usuário", example = "Matheus Araujo Pereira")
    private String fullName;

    @Schema(description = "E-mail institucional com domínio @academico.ufs.br", example = "matheus.araujo@academico.ufs.br")
    private String email;

    @Schema(description = "Perfil de acesso no sistema", example = "STUDENT")
    private UserRole role;

    @Schema(description = "Número de matrícula institucional (presente apenas para STUDENT)", example = "20260001234")
    private String registrationNumber;

    @Schema(description = "Status de ativação da conta", example = "true")
    private Boolean isActive;

    @Schema(description = "Indica se o usuário deve obrigatoriamente redefinir a senha no próximo acesso", example = "false")
    private Boolean mustChangePassword;

    @Schema(description = "Data e hora de cadastro no sistema")
    private Instant createdAt;
}
