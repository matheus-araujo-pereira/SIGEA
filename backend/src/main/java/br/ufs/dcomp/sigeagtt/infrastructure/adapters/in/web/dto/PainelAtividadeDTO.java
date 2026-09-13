package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * Payload para o painel de acompanhamento, métricas de turma e correção docente de uma atividade.
 *
 * @param atividade Resumo institucional da atividade educacional.
 * @param casoClinico Prontuário simulado associado à auditoria.
 * @param totalAlunosTurma Total de discentes matriculados na turma.
 * @param totalSubmissoes Total de discentes que enviaram a resolução.
 * @param totalPendentesCorrecao Total de submissões pendentes de nota/parecer docente.
 * @param totalAvaliadas Total de submissões já homologadas.
 * @param mediaNotas Média aritmética das notas atribuídas às submissões avaliadas.
 * @param alunos Lista com o status e progresso individual de cada aluno da turma.
 */
@Schema(description = "Painel consolidado docente para monitoramento e avaliação da atividade.")
public record PainelAtividadeDTO(
        @Schema(description = "Dados da atividade") AtividadeEducacionalDTO atividade,
        @Schema(description = "Dados do caso clínico auditado") CasoClinicoDTO casoClinico,
        @Schema(description = "Total de discentes da turma", example = "40") int totalAlunosTurma,
        @Schema(description = "Total de submissões recebidas", example = "35") int totalSubmissoes,
        @Schema(description = "Total aguardando correção", example = "5")
                int totalPendentesCorrecao,
        @Schema(description = "Total de submissões avaliadas", example = "30") int totalAvaliadas,
        @Schema(description = "Média geral de notas da turma", example = "8.75", nullable = true)
                BigDecimal mediaNotas,
        @Schema(description = "Listagem do progresso individual de cada discente")
                List<AlunoProgressoDTO> alunos) {

    /**
     * Converte o registro de caso de uso de domínio no DTO consolidado do painel.
     *
     * @param dados Dados consolidados do caso de uso
     * @return DTO do painel docente preenchido
     */
    public static PainelAtividadeDTO deDadosPainel(
            br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase
                            .DadosPainelAtividade
                    dados) {
        if (dados == null) return null;
        AtividadeEducacionalDTO ativDto =
                AtividadeEducacionalDTO.deEntidade(
                        dados.atividade().atividade(),
                        dados.atividade().totalAlunos(),
                        dados.atividade().totalSubmissoes(),
                        dados.atividade().totalAvaliadas());
        CasoClinicoDTO casoDto = CasoClinicoDTO.deEntidade(dados.casoClinico());
        List<AlunoProgressoDTO> alunos =
                dados.alunos() == null
                        ? java.util.List.of()
                        : dados.alunos().stream()
                                .map(
                                        p ->
                                                new AlunoProgressoDTO(
                                                        p.alunoId(),
                                                        p.alunoNome(),
                                                        p.alunoEmail(),
                                                        p.alunoMatricula(),
                                                        p.submissaoId(),
                                                        p.status(),
                                                        p.tempoGastoSegundos(),
                                                        p.dataSubmissao(),
                                                        p.nota(),
                                                        p.parecerDocente(),
                                                        p.dataAvaliacao()))
                                .toList();

        return new PainelAtividadeDTO(
                ativDto,
                casoDto,
                dados.totalAlunosTurma(),
                dados.totalSubmissoes(),
                dados.totalPendentesCorrecao(),
                dados.totalAvaliadas(),
                dados.mediaNotas(),
                alunos);
    }
}
