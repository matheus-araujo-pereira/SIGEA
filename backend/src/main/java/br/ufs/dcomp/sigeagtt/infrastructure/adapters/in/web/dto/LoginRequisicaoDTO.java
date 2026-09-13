package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload para requisição de login e autenticação de usuários no SIGEA-GTT.
 *
 * @param email Endereço de e-mail institucional do usuário no domínio @academico.ufs.br.
 * @param senha Senha em texto puro do usuário.
 */
@Schema(description = "Credenciais necessárias para autenticação no SIGEA-GTT.")
public record LoginRequisicaoDTO(
        @Schema(
                        description = "E-mail institucional no domínio @academico.ufs.br",
                        example = "admin@academico.ufs.br",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O e-mail institucional é obrigatório")
                @Email(message = "Formato de e-mail inválido")
                @JsonAlias({"identificador", "email"})
                String email,
        @Schema(
                        description = "Senha de acesso do usuário",
                        example = "Admin@123",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A senha é obrigatória")
                String senha) {}
