package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payload para a listagem das atividades pedagógicas do discente autenticado.
 *
 * @param atividadeId Identificador da atividade educacional.
 * @param titulo Título da atividade educacional.
 * @param turmaId Identificador da turma.
 * @param turmaCodigo Código da disciplina no SIGAA.
 * @param turmaNome Nome da disciplina.
 * @param professorResponsavel Nome do professor regente da turma.
 * @param casoClinicoId Identificador do caso clínico / prontuário.
 * @param casoClinicoTitulo Título do caso clínico.
 * @param unidadeHospitalarSigla Sigla da unidade de internação do caso.
 * @param dataInicio Data e hora de abertura da atividade.
 * @param dataFim Prazo final de entrega.
 * @param tempoLimiteMinutos Limite de tempo cronometrado (padrão 20 min).
 * @param submissaoId Identificador da submissão do aluno (se iniciada).
 * @param status Status atual da resolução (EM_ANDAMENTO, SUBMETIDA, AVALIADA).
 * @param nota Nota atribuída pelo docente (se avaliada).
 * @param tempoGastoSegundos Tempo cronometrado decorrido em segundos.
 * @param dataSubmissao Data e hora do envio final.
 * @param dataAvaliacao Data e hora da homologação da avaliação.
 */
@Schema(description = "Item da listagem de atividades disponíveis para o discente.")
public record MinhaAtividadeItemDTO(
        @Schema(description = "ID da atividade", example = "100") Long atividadeId,
        @Schema(
                        description = "Título da atividade",
                        example = "Auditoria Retrospectiva GTT - Sepse")
                String titulo,
        @Schema(description = "ID da turma", example = "10") Long turmaId,
        @Schema(description = "Código da disciplina", example = "MED001") String turmaCodigo,
        @Schema(description = "Nome da disciplina", example = "Semiologia Médica") String turmaNome,
        @Schema(description = "Nome do professor", example = "Prof. Dr. Marcos")
                String professorResponsavel,
        @Schema(description = "ID do caso clínico", example = "200") Long casoClinicoId,
        @Schema(description = "Título do caso clínico", example = "Sepse Grave com Choque Séptico")
                String casoClinicoTitulo,
        @Schema(description = "Sigla da unidade hospitalar", example = "UTI-A")
                String unidadeHospitalarSigla,
        @Schema(description = "Data de abertura", example = "2026-09-12T08:00:00")
                LocalDateTime dataInicio,
        @Schema(description = "Prazo limite", example = "2026-09-20T23:59:59")
                LocalDateTime dataFim,
        @Schema(description = "Tempo limite em minutos", example = "20") Integer tempoLimiteMinutos,
        @Schema(description = "ID da submissão", example = "300", nullable = true) Long submissaoId,
        @Schema(description = "Status da submissão", example = "EM_ANDAMENTO", nullable = true)
                StatusSubmissao status,
        @Schema(description = "Nota obtida (0.00 a 10.00)", example = "9.50", nullable = true)
                BigDecimal nota,
        @Schema(description = "Tempo cronometrado em segundos", example = "720")
                Integer tempoGastoSegundos,
        @Schema(description = "Data de envio", example = "2026-09-12T15:30:00", nullable = true)
                LocalDateTime dataSubmissao,
        @Schema(description = "Data de avaliação", example = "2026-09-12T17:00:00", nullable = true)
                LocalDateTime dataAvaliacao) {

    /**
     * Converte o registro de caso de uso em DTO.
     *
     * @param item Item do caso de uso
     * @return DTO preenchido
     */
    public static MinhaAtividadeItemDTO deItemMinhaAtividade(
            br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.ItemMinhaAtividade
                    item) {
        if (item == null) return null;
        return new MinhaAtividadeItemDTO(
                item.atividadeId(),
                item.titulo(),
                item.turmaId(),
                item.codigoDisciplina(),
                item.nomeDisciplina(),
                item.professorNome(),
                item.casoClinicoId(),
                item.casoClinicoTitulo(),
                item.unidadeSigla(),
                item.dataInicio(),
                item.dataFim(),
                item.tempoLimiteMinutos(),
                item.submissaoId(),
                item.status(),
                item.nota(),
                item.tempoGastoSegundos(),
                item.dataSubmissao(),
                item.dataAvaliacao());
    }
}
