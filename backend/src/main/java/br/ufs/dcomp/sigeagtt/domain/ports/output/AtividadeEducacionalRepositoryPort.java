package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para persistência de {@link AtividadeEducacional}. */
public interface AtividadeEducacionalRepositoryPort {

    /**
     * Lista todas as atividades educacionais ordenadas por data de criação decrescente.
     *
     * @return Lista de atividades.
     */
    List<AtividadeEducacional> listarTodas();

    /**
     * Lista atividades das turmas sob responsabilidade de um professor.
     *
     * @param professorId Identificador do docente.
     * @return Lista de atividades do docente.
     */
    List<AtividadeEducacional> listarPorProfessorId(Long professorId);

    /**
     * Lista atividades disponíveis para determinado discente (via turmas onde está matriculado).
     *
     * @param alunoId Identificador do aluno.
     * @return Lista de atividades liberadas para o discente.
     */
    List<AtividadeEducacional> listarParaAluno(Long alunoId);

    /**
     * Busca uma atividade educacional pelo ID.
     *
     * @param id Identificador da atividade.
     * @return {@link Optional} com a atividade caso localizada.
     */
    Optional<AtividadeEducacional> buscarPorId(Long id);

    /**
     * Salva ou atualiza uma atividade educacional.
     *
     * @param atividade Atividade a salvar.
     * @return Atividade persistida.
     */
    AtividadeEducacional salvar(AtividadeEducacional atividade);

    /**
     * Remove uma atividade educacional pelo ID.
     *
     * @param id Identificador da atividade a excluir.
     */
    void excluir(Long id);
}
