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
 * Resposta de criação de usuário contendo a senha provisória gerada automaticamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de criação com senha provisória gerada")
public class UserCreateResponseDTO {

    @Schema(description = "Identificador único (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Nome completo do usuário", example = "Ana Waleska de Menezes Seixas Souza")
    private String fullName;

    @Schema(description = "E-mail institucional com domínio @academico.ufs.br", example = "ana.waleska@academico.ufs.br")
    private String email;

    @Schema(description = "Perfil de acesso no sistema", example = "PROFESSOR")
    private UserRole role;

    @Schema(description = "Número de matrícula institucional (apenas para STUDENT)", example = "null")
    private String registrationNumber;

    @Schema(description = "Status de ativação da conta", example = "true")
    private Boolean isActive;

    @Schema(description = "Indica se o usuário deve obrigatoriamente redefinir a senha no próximo acesso", example = "true")
    private Boolean mustChangePassword;

    @Schema(description = "Senha provisória gerada automaticamente pelo sistema para repasse ao usuário", example = "P@ssw0rd99")
    private String provisionalPassword;

    @Schema(description = "Data e hora de cadastro no sistema")
    private Instant createdAt;
}
