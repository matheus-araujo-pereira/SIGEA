package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import java.util.List;

/**
 * Porta de saída (Output Port) para persistência dos Planos de Ação 5W3H {@link
 * SubmissaoPlano5w3h}.
 */
public interface SubmissaoPlano5w3hRepositoryPort {

    /**
     * Lista as ações 5W3H vinculadas a uma submissão.
     *
     * @param submissaoId Identificador da submissão.
     * @return Lista de ações do plano.
     */
    List<SubmissaoPlano5w3h> listarPorSubmissaoId(Long submissaoId);

    /**
     * Salva uma ação 5W3H.
     *
     * @param plano Ação a persistir.
     * @return Ação persistida.
     */
    SubmissaoPlano5w3h salvar(SubmissaoPlano5w3h plano);

    /**
     * Exclui todas as ações 5W3H vinculadas à submissão.
     *
     * @param submissaoId Identificador da submissão.
     */
    void excluirPorSubmissaoId(Long submissaoId);
}
