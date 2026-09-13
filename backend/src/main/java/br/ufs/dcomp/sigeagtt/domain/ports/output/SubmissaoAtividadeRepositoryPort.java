package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link SubmissaoAtividade}. */
public interface SubmissaoAtividadeRepositoryPort {

    /**
     * Lista todas as submissões cadastradas no sistema.
     *
     * @return Lista com todas as submissões.
     */
    List<SubmissaoAtividade> listarTodas();

    /**
     * Lista todas as submissões vinculadas a uma atividade educacional.
     *
     * @param atividadeId Identificador da atividade.
     * @return Lista de submissões da atividade.
     */
    List<SubmissaoAtividade> listarPorAtividadeId(Long atividadeId);

    /**
     * Lista submissões de determinado aluno ordenadas por data de início decrescente.
     *
     * @param alunoId Identificador do aluno.
     * @return Lista de submissões do aluno.
     */
    List<SubmissaoAtividade> listarPorAlunoId(Long alunoId);

    /**
     * Busca uma submissão pelo ID.
     *
     * @param id Identificador da submissão.
     * @return {@link Optional} com a submissão se encontrada.
     */
    Optional<SubmissaoAtividade> buscarPorId(Long id);

    /**
     * Busca a submissão específica de um aluno em determinada atividade.
     *
     * @param atividadeId Identificador da atividade.
     * @param alunoId Identificador do aluno.
     * @return {@link Optional} com a submissão se existente.
     */
    Optional<SubmissaoAtividade> buscarPorAtividadeEAluno(Long atividadeId, Long alunoId);

    /**
     * Salva ou atualiza a submissão.
     *
     * @param submissao Submissão a salvar.
     * @return Submissão persistida.
     */
    SubmissaoAtividade salvar(SubmissaoAtividade submissao);
}
