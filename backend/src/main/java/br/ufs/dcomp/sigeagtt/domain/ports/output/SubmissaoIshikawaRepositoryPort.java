package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import java.util.Optional;

/**
 * Porta de saída (Output Port) para persistência do Diagrama de Ishikawa {@link SubmissaoIshikawa}.
 */
public interface SubmissaoIshikawaRepositoryPort {

    /**
     * Busca o diagrama de Ishikawa associado a uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     * @return {@link Optional} com o diagrama se preenchido.
     */
    Optional<SubmissaoIshikawa> buscarPorSubmissaoId(Long submissaoId);

    /**
     * Salva ou atualiza o diagrama de Ishikawa.
     *
     * @param ishikawa Diagrama a salvar.
     * @return Diagrama salvo.
     */
    SubmissaoIshikawa salvar(SubmissaoIshikawa ishikawa);

    /**
     * Remove o diagrama de Ishikawa de uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     */
    void excluirPorSubmissaoId(Long submissaoId);
}
