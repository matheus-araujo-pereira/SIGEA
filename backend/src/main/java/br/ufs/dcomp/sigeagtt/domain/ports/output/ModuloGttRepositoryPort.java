package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para operações de persistência de {@link ModuloGtt}. */
public interface ModuloGttRepositoryPort {

    /**
     * Lista todos os módulos GTT cadastrados.
     *
     * @return Lista de módulos GTT.
     */
    List<ModuloGtt> listarTodos();

    /**
     * Busca um módulo GTT pelo ID.
     *
     * @param id Identificador do módulo.
     * @return {@link Optional} com o módulo se existente.
     */
    Optional<ModuloGtt> buscarPorId(Long id);

    /**
     * Busca um módulo GTT pelo código identificador.
     *
     * @param codigo Código do módulo (ex: CUIDADOS).
     * @return {@link Optional} com o módulo se existente.
     */
    Optional<ModuloGtt> buscarPorCodigo(String codigo);

    /**
     * Busca por código e ID diferente para validação em edições.
     *
     * @param codigo Código a ser verificado.
     * @param id ID do módulo a excluir da checagem.
     * @return {@link Optional} com conflito de código se houver.
     */
    Optional<ModuloGtt> buscarPorCodigoEIdDiferente(String codigo, Long id);

    /**
     * Salva ou atualiza um módulo GTT.
     *
     * @param modulo Módulo a ser salvo.
     * @return Módulo salvo.
     */
    ModuloGtt salvar(ModuloGtt modulo);

    /**
     * Remove um módulo GTT pelo ID.
     *
     * @param id Identificador do módulo a excluir.
     */
    void excluir(Long id);
}
