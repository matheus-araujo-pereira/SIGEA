package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Payload de resposta com dados consolidados do usuário.
 *
 * @param id Identificador numérico único do usuário.
 * @param nomeCompleto Nome civil completo.
 * @param email Endereço de e-mail institucional.
 * @param matriculaSigaa Matrícula acadêmica do SIGAA.
 * @param perfil Perfil de acesso do usuário.
 * @param primeiroAcesso Indicador de pendência de alteração de senha inicial.
 * @param ativo Status de atividade cadastral.
 * @param criadoEm Data e hora de criação do usuário.
 */
@Schema(description = "Dados detalhados do usuário cadastrado no sistema.")
public record UsuarioRespostaDTO(
        @Schema(description = "Identificador único do usuário", example = "10") Long id,
        @Schema(description = "Nome completo do usuário", example = "Ana Waleska")
                String nomeCompleto,
        @Schema(description = "E-mail institucional", example = "anawaleska@academico.ufs.br")
                String email,
        @Schema(description = "Matrícula do SIGAA", example = "202612345678", nullable = true)
                String matriculaSigaa,
        @Schema(description = "Perfil de autorização", example = "PROFESSOR") PerfilUsuario perfil,
        @Schema(description = "Indica necessidade de alteração de senha inicial", example = "false")
                Boolean primeiroAcesso,
        @Schema(description = "Status ativo do usuário", example = "true") Boolean ativo,
        @Schema(description = "Data e hora de criação", example = "2026-09-12T10:00:00")
                LocalDateTime criadoEm) {

    /**
     * Fábrica de conversão da entidade de domínio para DTO de resposta.
     *
     * @param usuario Entidade de domínio do usuário.
     * @return DTO de resposta construído.
     */
    public static UsuarioRespostaDTO deEntidade(Usuario usuario) {
        return new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getMatriculaSigaa(),
                usuario.getPerfil(),
                usuario.getPrimeiroAcesso(),
                usuario.getAtivo(),
                usuario.getCriadoEm());
    }
}
