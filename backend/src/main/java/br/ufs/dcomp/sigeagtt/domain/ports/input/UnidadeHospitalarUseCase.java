package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import java.util.List;

/** Porta de entrada (Input Port / Use Case) para gestão de unidades hospitalares no SIGEA-GTT. */
public interface UnidadeHospitalarUseCase {

    /**
     * Lista todas as unidades hospitalares cadastradas.
     *
     * @return Lista com todas as unidades.
     */
    List<UnidadeHospitalar> listarTodas();

    /**
     * Busca uma unidade hospitalar pelo ID.
     *
     * @param id Identificador da unidade.
     * @return Unidade hospitalar localizada.
     */
    UnidadeHospitalar buscarPorId(Long id);

    /**
     * Cadastra uma nova unidade hospitalar.
     *
     * @param sigla Sigla mnemônica única da unidade.
     * @param nome Nome por extenso do setor.
     * @return Unidade hospitalar cadastrada.
     */
    UnidadeHospitalar cadastrar(String sigla, String nome);

    /**
     * Atualiza dados de uma unidade hospitalar existente.
     *
     * @param id Identificador da unidade a editar.
     * @param sigla Nova sigla mnemônica.
     * @param nome Novo nome por extenso.
     * @return Unidade atualizada.
     */
    UnidadeHospitalar editar(Long id, String sigla, String nome);

    /**
     * Exclui uma unidade hospitalar do sistema.
     *
     * @param id Identificador da unidade.
     */
    void excluir(Long id);

    /**
     * Alterna o estado ativo/inativo de uma unidade hospitalar.
     *
     * @param id Identificador da unidade.
     * @return Unidade com o status alternado.
     */
    UnidadeHospitalar alternarStatus(Long id);
}
