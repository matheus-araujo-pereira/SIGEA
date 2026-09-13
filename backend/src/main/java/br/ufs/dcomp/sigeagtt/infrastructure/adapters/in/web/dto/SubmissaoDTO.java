package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * DTO para representação completa de uma submissão de atividade educacional.
 *
 * @param id Identificador da submissão
 * @param atividadeId Identificador da atividade vinculada
 * @param atividadeTitulo Título da atividade educacional
 * @param disciplinaNome Nome da disciplina da turma
 * @param professorNome Nome do docente responsável
 * @param tempoLimiteMinutos Tempo máximo permitido para resolução
 * @param casoClinico Caso clínico integral disponibilizado para auditoria
 * @param alunoId Identificador do discente autor
 * @param alunoNome Nome completo do discente
 * @param alunoMatricula Matrícula acadêmica do discente
 * @param alunoEmail E-mail institucional do discente
 * @param status Status do ciclo de vida da submissão
 * @param tempoGastoSegundos Tempo despendido pelo discente em segundos
 * @param dataInicio Data e hora de abertura da submissão
 * @param dataSubmissao Data e hora de entrega da resolução
 * @param professorCorretorId Identificador do docente avaliador
 * @param professorCorretorNome Nome do docente avaliador
 * @param nota Nota formativa de 0.00 a 10.00
 * @param parecerDocente Parecer pedagógico qualitativo do docente
 * @param dataAvaliacao Data e hora da avaliação
 * @param achadosGatilhos Lista de gatilhos clínicos identificados
 * @param ishikawa Diagrama de Causa e Efeito (Ishikawa 6M)
 * @param planos5w3h Matriz do Plano de Ação 5W3H
 * @param pdca Ciclo de melhoria da qualidade PDCA
 */
@Schema(description = "Representação completa de submissão de atividade educacional")
public record SubmissaoDTO(
        @Schema(description = "Identificador da submissão", example = "1") Long id,
        @Schema(description = "ID da atividade", example = "10") Long atividadeId,
        @Schema(
                        description = "Título da atividade",
                        example = "Auditoria Farmacoterapêutica na UTI")
                String atividadeTitulo,
        @Schema(description = "Nome da disciplina", example = "Segurança do Paciente")
                String disciplinaNome,
        @Schema(description = "Nome do professor responsável", example = "Dr. Carlos Santos")
                String professorNome,
        @Schema(description = "Tempo limite em minutos", example = "45") Integer tempoLimiteMinutos,
        @Schema(description = "Caso clínico associado") CasoClinicoDTO casoClinico,
        @Schema(description = "ID do discente", example = "5") Long alunoId,
        @Schema(description = "Nome do discente", example = "Maria Silva") String alunoNome,
        @Schema(description = "Matrícula acadêmica", example = "2024001234") String alunoMatricula,
        @Schema(description = "E-mail do discente", example = "maria.silva@academico.ufs.br")
                String alunoEmail,
        @Schema(description = "Status da submissão", example = "SUBMETIDA") StatusSubmissao status,
        @Schema(description = "Tempo gasto em segundos", example = "1200")
                Integer tempoGastoSegundos,
        @Schema(description = "Data de início da resolução") LocalDateTime dataInicio,
        @Schema(description = "Data da entrega definitiva") LocalDateTime dataSubmissao,
        @Schema(description = "ID do professor corretor", example = "2") Long professorCorretorId,
        @Schema(description = "Nome do professor corretor", example = "Dr. Carlos Santos")
                String professorCorretorNome,
        @Schema(description = "Nota atribuída de 0.00 a 10.00", example = "9.50") BigDecimal nota,
        @Schema(
                        description = "Parecer pedagógico docente",
                        example = "Ótima identificação de gatilhos.")
                String parecerDocente,
        @Schema(description = "Data da avaliação formativa") LocalDateTime dataAvaliacao,
        @Schema(description = "Lista de achados de gatilhos")
                List<SubmissaoGatilhoDTO> achadosGatilhos,
        @Schema(description = "Análise de causa raiz Ishikawa 6M") SubmissaoIshikawaDTO ishikawa,
        @Schema(description = "Plano de ação 5W3H") List<SubmissaoPlano5w3hDTO> planos5w3h,
        @Schema(description = "Ciclo PDCA de melhoria") SubmissaoPdcaDTO pdca) {

    /**
     * Converte entidade de domínio para o DTO completo.
     *
     * @param s Entidade de domínio SubmissaoAtividade
     * @return DTO preenchido
     */
    public static SubmissaoDTO deEntidade(SubmissaoAtividade s) {
        if (s == null) return null;

        List<SubmissaoGatilhoDTO> gatilhos =
                s.getAchadosGatilhos() == null
                        ? Collections.emptyList()
                        : s.getAchadosGatilhos().stream()
                                .map(SubmissaoGatilhoDTO::deEntidade)
                                .toList();

        SubmissaoIshikawaDTO ish = SubmissaoIshikawaDTO.deEntidade(s.getIshikawa());

        List<SubmissaoPlano5w3hDTO> planos =
                s.getPlanos5w3h() == null
                        ? Collections.emptyList()
                        : s.getPlanos5w3h().stream()
                                .map(SubmissaoPlano5w3hDTO::deEntidade)
                                .toList();

        SubmissaoPdcaDTO cicloPdca = SubmissaoPdcaDTO.deEntidade(s.getPdca());

        return new SubmissaoDTO(
                s.getId(),
                s.getAtividade() != null ? s.getAtividade().getId() : null,
                s.getAtividade() != null ? s.getAtividade().getTitulo() : null,
                s.getAtividade() != null && s.getAtividade().getTurma() != null
                        ? s.getAtividade().getTurma().getNomeDisciplina()
                        : null,
                s.getAtividade() != null
                                && s.getAtividade().getTurma() != null
                                && s.getAtividade().getTurma().getProfessorResponsavel() != null
                        ? s.getAtividade().getTurma().getProfessorResponsavel().getNomeCompleto()
                        : null,
                s.getAtividade() != null ? s.getAtividade().getTempoLimiteMinutos() : 20,
                s.getAtividade() != null && s.getAtividade().getCasoClinico() != null
                        ? CasoClinicoDTO.deEntidade(s.getAtividade().getCasoClinico())
                        : null,
                s.getAluno() != null ? s.getAluno().getId() : null,
                s.getAluno() != null ? s.getAluno().getNomeCompleto() : null,
                s.getAluno() != null ? s.getAluno().getMatriculaSigaa() : null,
                s.getAluno() != null ? s.getAluno().getEmail() : null,
                s.getStatus(),
                s.getTempoGastoSegundos(),
                s.getDataInicio(),
                s.getDataSubmissao(),
                s.getProfessorCorretor() != null ? s.getProfessorCorretor().getId() : null,
                s.getProfessorCorretor() != null
                        ? s.getProfessorCorretor().getNomeCompleto()
                        : null,
                s.getNota(),
                s.getParecerDocente(),
                s.getDataAvaliacao(),
                gatilhos,
                ish,
                planos,
                cicloPdca);
    }
}
