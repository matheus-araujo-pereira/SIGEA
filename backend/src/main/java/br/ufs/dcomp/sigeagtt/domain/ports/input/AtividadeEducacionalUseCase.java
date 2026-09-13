package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Porta de entrada (Input Port / Use Case) para gerenciamento de atividades educacionais de
 * auditoria e painel docente no SIGEA-GTT.
 */
public interface AtividadeEducacionalUseCase {

    /**
     * Lista atividades visíveis para o usuário autenticado com estatísticas de submissões.
     *
     * @param usuarioLogado Usuário em sessão.
     * @return Lista de resumos de atividades educacionais.
     */
    List<ItemAtividadeResumo> listar(Usuario usuarioLogado);

    /**
     * Busca uma atividade pelo ID com as métricas consolidadas.
     *
     * @param id Identificador da atividade.
     * @return Resumo da atividade educacional.
     */
    ItemAtividadeResumo buscarPorId(Long id);

    /**
     * Obtém o painel de monitoramento e correção docente da atividade.
     *
     * @param atividadeId Identificador da atividade.
     * @param usuarioLogado Docente ou administrador solicitante.
     * @return Painel consolidado com o progresso de cada discente matriculado.
     */
    DadosPainelAtividade buscarPainelAtividade(Long atividadeId, Usuario usuarioLogado);

    /**
     * Cadastra uma nova atividade educacional vinculando turma e caso clínico.
     *
     * @param professorLogado Docente criador.
     * @param comando Dados da atividade a criar.
     * @return Resumo da atividade criada.
     */
    ItemAtividadeResumo salvar(Usuario professorLogado, DadosSalvarAtividade comando);

    /**
     * Atualiza dados de uma atividade educacional existente.
     *
     * @param id Identificador da atividade.
     * @param usuarioLogado Usuário solicitante.
     * @param comando Novos dados da atividade.
     * @return Resumo da atividade atualizada.
     */
    ItemAtividadeResumo atualizar(Long id, Usuario usuarioLogado, DadosSalvarAtividade comando);

    /**
     * Exclui uma atividade educacional do sistema.
     *
     * @param id Identificador da atividade a excluir.
     * @param usuarioLogado Usuário solicitante.
     */
    void excluir(Long id, Usuario usuarioLogado);

    /** Comando com dados para criação e edição de atividade educacional. */
    record DadosSalvarAtividade(
            Long turmaId,
            Long casoClinicoId,
            String titulo,
            String orientacoesPedagogicas,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer tempoLimiteMinutos,
            Boolean ativa) {}

    /** Resumo da atividade com contagens operacionais de alunos e submissões. */
    record ItemAtividadeResumo(
            AtividadeEducacional atividade,
            int totalAlunos,
            int totalSubmissoes,
            int totalAvaliadas) {}

    /** Progresso de um discente na resolução da atividade. */
    record ProgressoAluno(
            Long alunoId,
            String alunoNome,
            String alunoEmail,
            String alunoMatricula,
            Long submissaoId,
            StatusSubmissao status,
            Integer tempoGastoSegundos,
            LocalDateTime dataSubmissao,
            BigDecimal nota,
            String parecerDocente,
            LocalDateTime dataAvaliacao) {}

    /** Painel pedagógico completo da atividade. */
    record DadosPainelAtividade(
            ItemAtividadeResumo atividade,
            CasoClinico casoClinico,
            int totalAlunosTurma,
            int totalSubmissoes,
            int totalPendentesCorrecao,
            int totalAvaliadas,
            BigDecimal mediaNotas,
            List<ProgressoAluno> alunos) {}
}
