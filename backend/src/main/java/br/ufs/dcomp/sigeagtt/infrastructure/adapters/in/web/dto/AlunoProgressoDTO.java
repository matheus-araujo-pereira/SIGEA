package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payload com o status e notas de um aluno em determinada atividade no painel docente.
 *
 * @param alunoId Identificador do aluno matriculado.
 * @param alunoNome Nome completo do discente.
 * @param alunoEmail E-mail institucional do discente.
 * @param alunoMatricula Matrícula SIGAA do discente.
 * @param submissaoId Identificador da submissão (se iniciada).
 * @param status Status pedagógico da submissão (EM_ANDAMENTO, SUBMETIDA, AVALIADA).
 * @param tempoGastoSegundos Tempo cronometrado na auditoria em segundos.
 * @param dataSubmissao Data e hora do envio final.
 * @param nota Nota atribuída pelo docente (0.00 a 10.00).
 * @param parecerDocente Parecer qualitativo do professor.
 * @param dataAvaliacao Data e hora da homologação da avaliação.
 */
@Schema(description = "Progresso de resolução e avaliação individual de um aluno na atividade.")
public record AlunoProgressoDTO(
        @Schema(description = "ID do aluno", example = "50") Long alunoId,
        @Schema(description = "Nome do aluno", example = "Lucas Sampaio") String alunoNome,
        @Schema(description = "E-mail do aluno", example = "lucas.sampaio@academico.ufs.br")
                String alunoEmail,
        @Schema(description = "Matrícula do SIGAA", example = "202612345678") String alunoMatricula,
        @Schema(description = "ID da submissão", example = "300", nullable = true) Long submissaoId,
        @Schema(description = "Status da resolução", example = "SUBMETIDA", nullable = true)
                StatusSubmissao status,
        @Schema(description = "Tempo cronometrado em segundos", example = "840", nullable = true)
                Integer tempoGastoSegundos,
        @Schema(description = "Data de envio", example = "2026-09-12T14:30:00", nullable = true)
                LocalDateTime dataSubmissao,
        @Schema(
                        description = "Nota atribuída pelo professor (0 a 10)",
                        example = "9.50",
                        nullable = true)
                BigDecimal nota,
        @Schema(
                        description = "Parecer formativo do professor",
                        example = "Excelente identificação dos gatilhos...",
                        nullable = true)
                String parecerDocente,
        @Schema(
                        description = "Data de avaliação docente",
                        example = "2026-09-12T16:00:00",
                        nullable = true)
                LocalDateTime dataAvaliacao) {}
