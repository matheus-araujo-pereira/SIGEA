package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import java.util.List;

/** Porta de entrada (Input Port / Use Case) para gestão de módulos da metodologia IHI-GTT. */
public interface ModuloGttUseCase {

    /**
     * Lista todos os módulos GTT cadastrados.
     *
     * @return Lista de módulos.
     */
    List<ModuloGtt> listarTodos();

    /**
     * Busca um módulo GTT pelo ID.
     *
     * @param id Identificador do módulo.
     * @return Módulo GTT localizado.
     */
    ModuloGtt buscarPorId(Long id);

    /**
     * Cadastra um novo módulo GTT.
     *
     * @param codigo Código mnemônico único do módulo.
     * @param nome Nome de exibição.
     * @param descricao Descrição clínica do escopo.
     * @return Módulo cadastrado.
     */
    ModuloGtt cadastrar(String codigo, String nome, String descricao);

    /**
     * Atualiza dados de um módulo GTT existente.
     *
     * @param id Identificador do módulo a editar.
     * @param codigo Novo código.
     * @param nome Novo nome.
     * @param descricao Nova descrição.
     * @return Módulo atualizado.
     */
    ModuloGtt editar(Long id, String codigo, String nome, String descricao);

    /**
     * Exclui um módulo GTT do sistema.
     *
     * @param id Identificador do módulo.
     */
    void excluir(Long id);

    /**
     * Alterna o estado ativo/inativo do módulo GTT.
     *
     * @param id Identificador do módulo.
     * @return Módulo com status alternado.
     */
    ModuloGtt alternarStatus(Long id);
}
