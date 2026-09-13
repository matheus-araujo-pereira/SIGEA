package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Payload para criação ou edição de atividade educacional de auditoria no SIGEA-GTT.
 *
 * @param turmaId Identificador da turma à qual a atividade será atribuída.
 * @param casoClinicoId Identificador do prontuário simulado a ser auditado.
 * @param titulo Título de referência da atividade.
 * @param orientacoesPedagogicas Diretrizes pedagógicas e critérios de pontuação.
 * @param dataInicio Data e hora de abertura da atividade para os alunos.
 * @param dataFim Data e hora limite para envio da submissão.
 * @param tempoLimiteMinutos Tempo cronometrado máximo em minutos (padrão 20 min).
 * @param ativa Status de atividade (visibilidade e permissão de resolução).
 */
@Schema(description = "Dados para cadastro ou atualização de atividade educacional.")
public record SalvarAtividadeDTO(
        @Schema(
                        description = "ID da turma",
                        example = "10",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A turma é obrigatória")
                Long turmaId,
        @Schema(
                        description = "ID do caso clínico",
                        example = "200",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O caso clínico é obrigatório")
                Long casoClinicoId,
        @Schema(
                        description = "Título da atividade",
                        example = "Auditoria Clínica - Módulo UTI",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O título é obrigatório")
                @Size(max = 200, message = "O título não pode exceder 200 caracteres")
                String titulo,
        @Schema(
                        description = "Orientações pedagógicas para os alunos",
                        example = "Identificar os gatilhos e preencher a ferramenta 5W3H...")
                String orientacoesPedagogicas,
        @Schema(
                        description = "Data e hora de início",
                        example = "2026-09-12T08:00:00",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A data de início é obrigatória")
                LocalDateTime dataInicio,
        @Schema(
                        description = "Data e hora de término",
                        example = "2026-09-20T23:59:59",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A data de término é obrigatória")
                LocalDateTime dataFim,
        @Schema(description = "Tempo limite em minutos", example = "20") Integer tempoLimiteMinutos,
        @Schema(description = "Status ativo da atividade", example = "true") Boolean ativa) {

    /**
     * Converte o DTO para o comando de domínio.
     *
     * @return DadosSalvarAtividade
     */
    public br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosSalvarAtividade
            paraComando() {
        return new br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase
                .DadosSalvarAtividade(
                turmaId,
                casoClinicoId,
                titulo,
                orientacoesPedagogicas,
                dataInicio,
                dataFim,
                tempoLimiteMinutos,
                ativa);
    }
}
