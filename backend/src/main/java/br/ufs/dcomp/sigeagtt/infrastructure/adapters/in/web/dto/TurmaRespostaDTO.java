package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Payload de resposta com dados detalhados da turma acadêmica e total de alunos matriculados.
 *
 * @param id Identificador único numérico da turma.
 * @param professorResponsavelId Identificador do docente responsável.
 * @param professorResponsavelNome Nome do docente responsável.
 * @param codigoDisciplina Código da disciplina no SIGAA.
 * @param periodoLetivo Período acadêmico de oferta.
 * @param anoSemestre Identificador do ano/semestre.
 * @param ativa Indicador de vigência e atividade da turma.
 * @param criadaEm Data de cadastro da turma no sistema.
 * @param totalAlunos Quantidade de discentes atualmente matriculados.
 */
@Schema(description = "Dados detalhados de turma acadêmica.")
public record TurmaRespostaDTO(
        @Schema(description = "Identificador único da turma", example = "10") Long id,
        @Schema(description = "ID do professor responsável", example = "2")
                Long professorResponsavelId,
        @Schema(description = "Nome do professor responsável", example = "Prof. Dr. Marcos")
                String professorResponsavelNome,
        @Schema(description = "Código da disciplina", example = "MED001") String codigoDisciplina,
        @Schema(description = "Período letivo", example = "2026.1") String periodoLetivo,
        @Schema(description = "Ano/Semestre", example = "2026/1") String anoSemestre,
        @Schema(description = "Indica se a turma está ativa", example = "true") Boolean ativa,
        @Schema(description = "Data de criação da turma", example = "2026-09-12T08:00:00")
                LocalDateTime criadaEm,
        @Schema(description = "Total de discentes matriculados", example = "35") long totalAlunos) {

    /**
     * Fábrica de construção de DTO a partir da entidade de domínio e total de alunos.
     *
     * @param turma Entidade de domínio da turma.
     * @param totalAlunos Contagem de alunos matriculados.
     * @return DTO correspondente.
     */
    public static TurmaRespostaDTO deEntidade(Turma turma, long totalAlunos) {
        return new TurmaRespostaDTO(
                turma.getId(),
                turma.getProfessorResponsavel() != null
                        ? turma.getProfessorResponsavel().getId()
                        : null,
                turma.getProfessorResponsavel() != null
                        ? turma.getProfessorResponsavel().getNomeCompleto()
                        : null,
                turma.getCodigoDisciplina(),
                turma.getPeriodoLetivo(),
                turma.getAnoSemestre(),
                turma.getAtiva(),
                turma.getCriadaEm(),
                totalAlunos);
    }
}
