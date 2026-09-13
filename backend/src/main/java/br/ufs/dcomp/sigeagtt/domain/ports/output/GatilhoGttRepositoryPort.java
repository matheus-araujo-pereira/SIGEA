package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link GatilhoGtt}. */
public interface GatilhoGttRepositoryPort {

    /**
     * Lista todos os gatilhos cadastrados.
     *
     * @return Lista com todos os gatilhos.
     */
    List<GatilhoGtt> listarTodos();

    /**
     * Lista todos os gatilhos pertencentes a um módulo específico.
     *
     * @param moduloId Identificador do módulo GTT.
     * @return Lista de gatilhos do módulo.
     */
    List<GatilhoGtt> listarPorModuloId(Long moduloId);

    /**
     * Busca um gatilho pelo ID.
     *
     * @param id Identificador do gatilho.
     * @return {@link Optional} com o gatilho se localizado.
     */
    Optional<GatilhoGtt> buscarPorId(Long id);

    /**
     * Busca um gatilho pelo seu código único (ex: C1, M2).
     *
     * @param codigo Código do gatilho clínico.
     * @return {@link Optional} com o gatilho se localizado.
     */
    Optional<GatilhoGtt> buscarPorCodigo(String codigo);

    /**
     * Busca por código e ID diferente para validação em edições.
     *
     * @param codigo Código a ser verificado.
     * @param id ID do gatilho a excluir da checagem.
     * @return {@link Optional} com eventual conflito.
     */
    Optional<GatilhoGtt> buscarPorCodigoEIdDiferente(String codigo, Long id);

    /**
     * Salva ou atualiza um gatilho clínico.
     *
     * @param gatilho Gatilho a salvar.
     * @return Gatilho persistido.
     */
    GatilhoGtt salvar(GatilhoGtt gatilho);

    /**
     * Exclui um gatilho pelo ID.
     *
     * @param id Identificador do gatilho a excluir.
     */
    void excluir(Long id);
}
