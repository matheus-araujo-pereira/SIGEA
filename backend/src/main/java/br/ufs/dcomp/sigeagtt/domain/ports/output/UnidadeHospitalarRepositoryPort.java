package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import java.util.List;
import java.util.Optional;

/** Porta de saída (Output Port) para persistência de {@link UnidadeHospitalar}. */
public interface UnidadeHospitalarRepositoryPort {

    /**
     * Recupera todas as unidades hospitalares cadastradas.
     *
     * @return Lista de unidades hospitalares.
     */
    List<UnidadeHospitalar> listarTodas();

    /**
     * Busca uma unidade hospitalar pelo ID.
     *
     * @param id Identificador da unidade.
     * @return {@link Optional} contendo a unidade se encontrada.
     */
    Optional<UnidadeHospitalar> buscarPorId(Long id);

    /**
     * Busca uma unidade hospitalar pela sigla.
     *
     * @param sigla Sigla mnemônica da unidade (ex: UTI-A).
     * @return {@link Optional} com a unidade caso encontrada.
     */
    Optional<UnidadeHospitalar> buscarPorSigla(String sigla);

    /**
     * Busca por sigla e ID diferente, para validação de unicidade na edição.
     *
     * @param sigla Sigla a ser verificada.
     * @param id Identificador da unidade a excluir da checagem.
     * @return {@link Optional} com eventual conflito.
     */
    Optional<UnidadeHospitalar> buscarPorSiglaEIdDiferente(String sigla, Long id);

    /**
     * Salva ou atualiza a unidade hospitalar.
     *
     * @param unidade Unidade hospitalar a salvar.
     * @return Unidade persistida.
     */
    UnidadeHospitalar salvar(UnidadeHospitalar unidade);

    /**
     * Remove uma unidade hospitalar pelo ID.
     *
     * @param id Identificador da unidade a remover.
     */
    void excluir(Long id);
}
