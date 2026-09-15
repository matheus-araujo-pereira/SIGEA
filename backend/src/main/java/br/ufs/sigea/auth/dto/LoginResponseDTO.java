package br.ufs.sigea.auth.dto;

import br.ufs.sigea.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resposta de autenticação bem-sucedida contendo o token JWT e os dados do usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de autenticação com token JWT")
public class LoginResponseDTO {

    @Schema(description = "Token de acesso JWT stateless (Bearer)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Dados do usuário autenticado")
    private UserResponseDTO user;
}
