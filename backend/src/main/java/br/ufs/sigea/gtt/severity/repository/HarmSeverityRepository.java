package br.ufs.sigea.gtt.severity.repository;

import br.ufs.sigea.gtt.severity.domain.HarmSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de acesso a dados para a entidade HarmSeverity.
 */
@Repository
public interface HarmSeverityRepository extends JpaRepository<HarmSeverity, UUID> {

    /**
     * Busca uma categoria pela letra identificadora (A a I).
     *
     * @param categoryLetter Letra da categoria (ex: E)
     * @return Optional contendo a gravidade se encontrada
     */
    Optional<HarmSeverity> findByCategoryLetterIgnoreCase(String categoryLetter);

    /**
     * Verifica se já existe uma categoria cadastrada com a letra informada.
     *
     * @param categoryLetter Letra da categoria
     * @return true se já existir
     */
    boolean existsByCategoryLetterIgnoreCase(String categoryLetter);

    /**
     * Verifica se já existe outra categoria com a mesma letra, desconsiderando um ID.
     *
     * @param categoryLetter Letra da categoria
     * @param id             ID a ser desconsiderado
     * @return true se houver conflito
     */
    boolean existsByCategoryLetterIgnoreCaseAndIdNot(String categoryLetter, UUID id);

    /**
     * Retorna todas as gravidades ativas ordenadas pela letra da categoria (A a I).
     *
     * @return Lista ordenada de gravidades ativas
     */
    List<HarmSeverity> findAllByIsActiveTrueOrderByCategoryLetterAsc();

    /**
     * Retorna todas as gravidades cadastradas ordenadas pela letra da categoria (A a I).
     *
     * @return Lista de gravidades
     */
    List<HarmSeverity> findAllByOrderByCategoryLetterAsc();

    /**
     * Consulta paginada com filtros por termo de busca, flag de dano (isHarm) e status ativo.
     *
     * @param search   Termo de pesquisa por letra, nome ou descrição
     * @param isHarm   Filtro opcional por flag de dano real
     * @param isActive Filtro opcional por status ativo
     * @param pageable Configuração de paginação
     * @return Página de gravidades
     */
    @Query("SELECT s FROM HarmSeverity s WHERE " +
           "(:search IS NULL OR TRIM(:search) = '' OR " +
           " LOWER(s.categoryLetter) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isHarm IS NULL OR s.isHarm = :isHarm) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<HarmSeverity> findWithFilters(@Param("search") String search,
                                       @Param("isHarm") Boolean isHarm,
                                       @Param("isActive") Boolean isActive,
                                       Pageable pageable);
}
