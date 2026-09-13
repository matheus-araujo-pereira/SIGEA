package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Output Port) para operações de persistência de {@link CategoriaEventoAdverso}.
 */
public interface CategoriaEventoAdversoRepositoryPort {

    /**
     * Lista todas as categorias de eventos adversos cadastradas.
     *
     * @return Lista com todas as categorias.
     */
    List<CategoriaEventoAdverso> listarTodas();

    /**
     * Busca uma categoria pelo ID.
     *
     * @param id Identificador da categoria.
     * @return {@link Optional} com a categoria se localizada.
     */
    Optional<CategoriaEventoAdverso> buscarPorId(Long id);

    /**
     * Salva ou atualiza uma categoria de evento adverso.
     *
     * @param categoria Categoria a salvar.
     * @return Categoria persistida.
     */
    CategoriaEventoAdverso salvar(CategoriaEventoAdverso categoria);
}
