package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para definição obrigatória de nova senha no primeiro acesso ao SIGEA-GTT.
 *
 * @param usuarioId Identificador único do usuário.
 * @param senhaAtual Senha temporária atual fornecida pela administração.
 * @param novaSenha Nova senha pessoal escolhida (mínimo de 8 caracteres).
 * @param confirmacaoNovaSenha Confirmação da nova senha.
 */
@Schema(description = "Dados para redefinição de senha no primeiro acesso ao sistema.")
public record PrimeiroAcessoRequisicaoDTO(
        @Schema(
                        description = "Identificador do usuário",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O ID do usuário é obrigatório")
                Long usuarioId,
        @Schema(
                        description = "Senha temporária atual",
                        example = "Sigea@123",
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
