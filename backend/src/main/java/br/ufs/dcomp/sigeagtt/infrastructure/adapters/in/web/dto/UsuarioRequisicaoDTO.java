package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para cadastro de novos usuários no SIGEA-GTT.
 *
 * @param nomeCompleto Nome completo do usuário.
 * @param email Endereço de e-mail institucional no domínio @academico.ufs.br.
 * @param matriculaSigaa Matrícula acadêmica do SIGAA (obrigatória para alunos, 12 dígitos).
 * @param perfil Perfil institucional de acesso (ADMINISTRADOR, PROFESSOR, ALUNO).
 */
@Schema(description = "Dados para cadastro de novo usuário.")
public record UsuarioRequisicaoDTO(
        @Schema(
                        description = "Nome completo do usuário",
                        example = "Ana Waleska",
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
        @Schema(
                        description = "Matrícula do SIGAA (exatamente 12 dígitos numéricos)",
                        example = "202612345678",
                        nullable = true)
                @Size(
                        min = 12,
                        max = 12,
                        message = "A Matrícula do SIGAA deve ter exatamente 12 dígitos numéricos")
                String matriculaSigaa,
        @Schema(
                        description = "Perfil de acesso",
                        example = "PROFESSOR",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O perfil de acesso é obrigatório")
                PerfilUsuario perfil) {}
