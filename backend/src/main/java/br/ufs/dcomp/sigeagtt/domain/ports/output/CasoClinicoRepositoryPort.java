package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link CasoClinico}. */
public interface CasoClinicoRepositoryPort {

    /**
     * Lista todos os casos clínicos ordenados pela data de criação decrescente.
     *
     * @return Lista de casos clínicos.
     */
    List<CasoClinico> listarTodos();

    /**
     * Lista casos clínicos criados por determinado docente.
     *
     * @param professorId Identificador do professor criador.
     * @return Lista de casos do docente.
     */
    List<CasoClinico> listarPorProfessorCriadorId(Long professorId);

    /**
     * Busca um caso clínico pelo ID.
     *
     * @param id Identificador do caso clínico.
     * @return {@link Optional} com o caso se localizado.
     */
    Optional<CasoClinico> buscarPorId(Long id);

    /**
     * Salva ou atualiza um caso clínico.
     *
     * @param caso Caso a persistir.
     * @return Caso persistido.
     */
    CasoClinico salvar(CasoClinico caso);

    /**
     * Exclui um caso clínico pelo ID.
     *
     * @param id Identificador do caso a excluir.
     */
    void excluir(Long id);
}
