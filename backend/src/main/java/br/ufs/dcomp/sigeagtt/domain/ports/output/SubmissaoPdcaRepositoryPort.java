package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import java.util.Optional;

/** Porta de saída (Output Port) para persistência do Ciclo PDCA {@link SubmissaoPdca}. */
public interface SubmissaoPdcaRepositoryPort {

    /**
     * Busca o ciclo PDCA associado a uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     * @return {@link Optional} com o ciclo PDCA se registrado.
     */
    Optional<SubmissaoPdca> buscarPorSubmissaoId(Long submissaoId);

    /**
     * Salva ou atualiza o ciclo PDCA.
     *
     * @param pdca Ciclo PDCA a persistir.
     * @return Ciclo PDCA persistido.
     */
    SubmissaoPdca salvar(SubmissaoPdca pdca);

    /**
     * Remove o ciclo PDCA de uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     */
    void excluirPorSubmissaoId(Long submissaoId);
}
