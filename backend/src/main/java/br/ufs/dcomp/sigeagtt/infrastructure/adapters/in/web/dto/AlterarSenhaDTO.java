package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload para alteração voluntária de senha pelo próprio usuário autenticado.
 *
 * @param senhaAtual Senha atualmente em uso.
 * @param novaSenha Nova senha desejada (mínimo de 8 caracteres).
 * @param confirmacaoNovaSenha Confirmação idêntica da nova senha.
 */
@Schema(description = "Dados para alteração de senha de usuário autenticado.")
public record AlterarSenhaDTO(
        @Schema(
                        description = "Senha atual do usuário",
                        example = "SenhaAntiga@123",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A senha atual é obrigatória")
                String senhaAtual,
        @Schema(
                        description = "Nova senha segura",
                        example = "NovaSenhaForte@2026",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A nova senha é obrigatória")
                @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres")
                String novaSenha,
        @Schema(
                        description = "Confirmação da nova senha",
                        example = "NovaSenhaForte@2026",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A confirmação da nova senha é obrigatória")
                String confirmacaoNovaSenha) {}
