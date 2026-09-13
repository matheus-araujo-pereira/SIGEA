package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import java.util.List;

/**
 * Porta de entrada (Input Port / Use Case) para gestão de gatilhos clínicos da metodologia IHI-GTT.
 */
public interface GatilhoGttUseCase {

    /**
     * Lista gatilhos clínicos com ordenação natural, opcionalmente filtrados por módulo.
     *
     * @param moduloId Identificador do módulo (opcional).
     * @return Lista ordenada naturalmente de gatilhos.
     */
    List<GatilhoGtt> listar(Long moduloId);

    /**
     * Busca um gatilho clínico pelo ID.
     *
     * @param id Identificador do gatilho.
     * @return Gatilho clínico localizado.
     */
    GatilhoGtt buscarPorId(Long id);

    /**
     * Cadastra um novo gatilho clínico.
     *
     * @param moduloId Identificador do módulo associado.
     * @param codigo Código do gatilho (ex: C1, M3).
     * @param descricao Descrição clínica operacional.
     * @param limiarReferencia Parâmetros e valores de referência.
     * @return Gatilho cadastrado.
     */
    GatilhoGtt cadastrar(Long moduloId, String codigo, String descricao, String limiarReferencia);

    /**
     * Atualiza um gatilho existente.
     *
     * @param id Identificador do gatilho a editar.
     * @param moduloId Novo identificador de módulo.
     * @param codigo Novo código.
     * @param descricao Nova descrição.
     * @param limiarReferencia Novos limiares de referência.
     * @return Gatilho atualizado.
     */
    GatilhoGtt editar(
            Long id, Long moduloId, String codigo, String descricao, String limiarReferencia);

    /**
     * Remove um gatilho clínico pelo ID.
     *
     * @param id Identificador do gatilho a excluir.
     */
    void excluir(Long id);

    /**
     * Alterna o estado ativo/inativo do gatilho clínico.
     *
     * @param id Identificador do gatilho.
     * @return Gatilho com status alternado.
     */
    GatilhoGtt alternarStatus(Long id);
}
