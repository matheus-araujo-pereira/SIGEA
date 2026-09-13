package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Payload de resposta com dados resumidos de atividade educacional e contagens de participação.
 *
 * @param id Identificador único numérico da atividade.
 * @param turmaId Identificador da turma acadêmica associada.
 * @param turmaCodigoDisciplina Código da disciplina no SIGAA.
 * @param turmaPeriodoLetivo Período acadêmico da turma.
 * @param professorResponsavelNome Nome do docente responsável pela atividade.
 * @param casoClinicoId Identificador do caso clínico / prontuário simulado vinculado.
 * @param casoClinicoTitulo Título do caso clínico.
 * @param casoClinicoUnidadeSigla Sigla da unidade hospitalar de internação do caso.
 * @param titulo Título da atividade educacional.
 * @param orientacoesPedagogicas Orientações e diretrizes da atividade.
 * @param dataInicio Início do período de execução.
 * @param dataFim Prazo final de entrega.
 * @param tempoLimiteMinutos Limite de tempo cronometrado (padrão 20 min).
 * @param ativa Status de ativação da atividade.
 * @param criadaEm Data de criação.
 * @param totalAlunos Quantidade de alunos matriculados na turma.
 * @param totalSubmissoes Total de submissões entregues ou avaliadas.
 * @param totalAvaliadas Total de submissões homologadas pelo docente.
 */
@Schema(description = "Dados consolidados de atividade educacional de auditoria.")
public record AtividadeEducacionalDTO(
        @Schema(description = "Identificador único da atividade", example = "100") Long id,
        @Schema(description = "ID da turma", example = "10") Long turmaId,
        @Schema(description = "Código da disciplina", example = "MED001")
                String turmaCodigoDisciplina,
        @Schema(description = "Código da disciplina / turma", example = "MED001")
                String turmaCodigo,
        @Schema(
                        description = "Nome da disciplina da turma",
                        example = "Segurança do Paciente e Auditoria Clínica")
                String turmaDisciplina,
        @Schema(description = "Período letivo", example = "2026.1") String turmaPeriodoLetivo,
        @Schema(description = "Período letivo da turma", example = "2026.1") String periodoLetivo,
        @Schema(description = "Nome do professor responsável", example = "Prof. Dr. Marcos")
                String professorResponsavelNome,
        @Schema(description = "ID do caso clínico", example = "200") Long casoClinicoId,
        @Schema(description = "Título do caso clínico", example = "Sepse Grave com Choque Séptico")
                String casoClinicoTitulo,
        @Schema(description = "Sigla da unidade hospitalar", example = "UTI-A")
                String casoClinicoUnidadeSigla,
        @Schema(
                        description = "Título da atividade",
                        example = "Auditoria Retrospectiva GTT - Módulo UTI")
                String titulo,
        @Schema(
                        description = "Orientações pedagógicas",
                        example = "Realizar auditoria em até 20 minutos...")
                String orientacoesPedagogicas,
        @Schema(description = "Data de início", example = "2026-09-12T08:00:00")
                LocalDateTime dataInicio,
        @Schema(description = "Data de término", example = "2026-09-20T23:59:59")
                LocalDateTime dataFim,
        @Schema(description = "Tempo limite em minutos", example = "20") Integer tempoLimiteMinutos,
        @Schema(description = "Indica se a atividade está ativa", example = "true") Boolean ativa,
        @Schema(description = "Data de cadastro", example = "2026-09-12T08:00:00")
                LocalDateTime criadaEm,
        @Schema(description = "Total de alunos matriculados na turma", example = "30")
                int totalAlunos,
        @Schema(description = "Total de alunos matriculados na turma (alias)", example = "30")
                int totalAlunosTurma,
        @Schema(description = "Total de submissões entregues", example = "25") int totalSubmissoes,
        @Schema(description = "Total de submissões avaliadas pelo docente", example = "20")
                int totalAvaliadas) {

    /**
     * Converte entidade de domínio puro em DTO de resposta.
     *
     * @param a Entidade AtividadeEducacional.
     * @param totalAlunos Total de discentes na turma.
     * @param totalSubmissoes Total de submissões realizadas.
     * @param totalAvaliadas Total de submissões já corrigidas.
     * @return DTO preenchido.
     */
    public static AtividadeEducacionalDTO deEntidade(
            AtividadeEducacional a, int totalAlunos, int totalSubmissoes, int totalAvaliadas) {
        String codigoTurma = a.getTurma() != null ? a.getTurma().getCodigoDisciplina() : null;
        String nomeDisciplina = a.getTurma() != null ? a.getTurma().getNomeDisciplina() : null;
        String periodo = a.getTurma() != null ? a.getTurma().getPeriodoLetivo() : null;
        String professor = (a.getTurma() != null && a.getTurma().getProfessorResponsavel() != null)
                ? a.getTurma().getProfessorResponsavel().getNomeCompleto()
                : null;
        Long turmaId = a.getTurma() != null ? a.getTurma().getId() : null;

        return new AtividadeEducacionalDTO(
                a.getId(),
                turmaId,
                codigoTurma,
                codigoTurma,
                nomeDisciplina,
                periodo,
                periodo,
                professor,
                a.getCasoClinico() != null ? a.getCasoClinico().getId() : null,
                a.getCasoClinico() != null ? a.getCasoClinico().getTitulo() : null,
                a.getCasoClinico() != null && a.getCasoClinico().getUnidadeHospitalar() != null
                        ? a.getCasoClinico().getUnidadeHospitalar().getSigla()
                        : null,
                a.getTitulo(),
                a.getOrientacoesPedagogicas(),
                a.getDataInicio(),
                a.getDataFim(),
                a.getTempoLimiteMinutos(),
                a.getAtiva(),
                a.getCriadaEm(),
                totalAlunos,
                totalAlunos,
                totalSubmissoes,
                totalAvaliadas);
    }
}
