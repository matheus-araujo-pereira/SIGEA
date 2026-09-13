package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para edição de dados cadastrais de um usuário existente.
 *
 * @param nomeCompleto Novo nome civil do usuário.
 * @param email Novo e-mail institucional no domínio @academico.ufs.br.
 * @param matriculaSigaa Nova matrícula acadêmica do SIGAA (para discentes).
 * @param perfil Novo perfil atribuído ao usuário.
 */
@Schema(description = "Dados para atualização de usuário existente.")
public record UsuarioEdicaoDTO(
        @Schema(
                        description = "Nome completo do usuário",
                        example = "Ana Waleska Soares",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O nome completo é obrigatório")
                @Size(max = 150, message = "O nome completo não pode exceder 150 caracteres")
                String nomeCompleto,
        @Schema(
                        description = "E-mail institucional",
                        example = "anawaleska@academico.ufs.br",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O e-mail institucional é obrigatório")
                @Email(message = "O e-mail informado é inválido")
                @Size(max = 150, message = "O e-mail não pode exceder 150 caracteres")
                String email,
        @Schema(description = "Matrícula do SIGAA", example = "202612345678", nullable = true)
                @Size(
                        min = 12,
                        max = 12,
                        message = "A Matrícula do SIGAA deve ter exatamente 12 dígitos numéricos")
                String matriculaSigaa,
        @Schema(
                        description = "Perfil de autorização",
                        example = "PROFESSOR",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O perfil de acesso é obrigatório")
                PerfilUsuario perfil) {}
