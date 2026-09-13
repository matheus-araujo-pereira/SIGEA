package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload retornado após autenticação bem-sucedida no SIGEA-GTT.
 *
 * @param id Identificador único numérico do usuário.
 * @param nomeCompleto Nome completo do usuário.
 * @param email Endereço de e-mail institucional.
 * @param matriculaSigaa Matrícula acadêmica do SIGAA (para alunos).
 * @param perfil Perfil de autorização concedido (ADMINISTRADOR, PROFESSOR, ALUNO).
 * @param primeiroAcesso Indicador de obrigatoriedade de redefinição de senha inicial.
 * @param ativo Status de atividade cadastral da conta.
 * @param token Token JWT gerado para a sessão.
 */
@Schema(description = "Dados do usuário autenticado e token de acesso JWT.")
public record LoginRespostaDTO(
        @Schema(description = "Identificador único do usuário", example = "1") Long id,
        @Schema(description = "Nome completo do usuário", example = "Administrador do Sistema")
                String nomeCompleto,
        @Schema(description = "E-mail institucional", example = "admin@academico.ufs.br")
                String email,
        @Schema(description = "Matrícula do SIGAA", example = "202600012345", nullable = true)
                String matriculaSigaa,
        @Schema(description = "Perfil de autorização", example = "ADMINISTRADOR")
                PerfilUsuario perfil,
        @Schema(description = "Indica necessidade de alteração de senha inicial", example = "false")
                Boolean primeiroAcesso,
        @Schema(description = "Status ativo do usuário", example = "true") Boolean ativo,
        @Schema(
                        description = "Token Bearer JWT",
                        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String token) {

    /**
     * Fábrica para construção do DTO a partir da entidade de domínio e token.
     *
     * @param usuario Entidade de domínio do usuário.
     * @param token Token JWT gerado.
     * @return DTO preenchido.
     */
    public static LoginRespostaDTO deEntidade(Usuario usuario, String token) {
        return new LoginRespostaDTO(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getMatriculaSigaa(),
                usuario.getPerfil(),
                usuario.getPrimeiroAcesso(),
                usuario.getAtivo(),
                token);
    }

    /**
     * Fábrica para construção do DTO sem token associado.
     *
     * @param usuario Entidade de domínio do usuário.
     * @return DTO preenchido sem token.
     */
    public static LoginRespostaDTO deEntidade(Usuario usuario) {
        return deEntidade(usuario, null);
    }
}
