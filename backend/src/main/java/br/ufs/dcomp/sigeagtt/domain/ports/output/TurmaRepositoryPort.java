package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link Turma}. */
public interface TurmaRepositoryPort {

    /**
     * Lista todas as turmas cadastradas.
     *
     * @return Lista de turmas.
     */
    List<Turma> listarTodas();

    /**
     * Lista as turmas associadas a um professor responsável.
     *
     * @param professorId Identificador do docente.
     * @return Lista de turmas sob regência do docente.
     */
    List<Turma> listarPorProfessorId(Long professorId);

    /**
     * Busca uma turma pelo ID.
     *
     * @param id Identificador da turma.
     * @return {@link Optional} com a turma se localizada.
     */
    Optional<Turma> buscarPorId(Long id);

    /**
     * Salva ou atualiza uma turma.
     *
     * @param turma Turma a ser salva.
     * @return Turma persistida.
     */
    Turma salvar(Turma turma);

    /**
     * Exclui uma turma pelo ID.
     *
     * @param id Identificador da turma a excluir.
     */
    void excluir(Long id);
}
