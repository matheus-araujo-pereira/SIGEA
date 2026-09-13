package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link SubmissaoGatilho}. */
public interface SubmissaoGatilhoRepositoryPort {

    /**
     * Lista todos os achados de gatilho vinculados a uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     * @return Lista de gatilhos achados.
     */
    List<SubmissaoGatilho> listarPorSubmissaoId(Long submissaoId);

    /**
     * Lista todos os achados de gatilhos no sistema (usado para cálculo de indicadores globais).
     *
     * @return Lista com todos os achados de gatilhos.
     */
    List<SubmissaoGatilho> listarTodos();

    /**
     * Busca um achado pelo ID.
     *
     * @param id Identificador do registro.
     * @return {@link Optional} com o achado se localizado.
     */
    Optional<SubmissaoGatilho> buscarPorId(Long id);

    /**
     * Salva ou atualiza um achado de gatilho.
     *
     * @param achado Entidade a salvar.
     * @return Achado persistido.
     */
    SubmissaoGatilho salvar(SubmissaoGatilho achado);

    /**
     * Exclui todos os achados de uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     */
    void excluirPorSubmissaoId(Long submissaoId);
}
