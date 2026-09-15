package br.ufs.sigea.gtt.trigger.repository;

import br.ufs.sigea.gtt.trigger.domain.GttTrigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de acesso a dados para a entidade GttTrigger.
 */
@Repository
public interface GttTriggerRepository extends JpaRepository<GttTrigger, UUID> {

    /**
     * Conta quantos gatilhos estão vinculados a um determinado módulo.
     *
     * @param moduleId ID do módulo
     * @return Quantidade de gatilhos associados
     */
    long countByModuleId(UUID moduleId);

    /**
     * Verifica se existem gatilhos associados a um determinado módulo.
     *
     * @param moduleId ID do módulo
     * @return true se houver ao menos um gatilho associado
     */
    boolean existsByModuleId(UUID moduleId);

    /**
     * Busca um gatilho pelo código único com fetch no módulo associado.
     *
     * @param code Código do gatilho (ex: C1, M4, S10)
     * @return Optional contendo o gatilho se encontrado
     */
    @Query("SELECT t FROM GttTrigger t JOIN FETCH t.module WHERE LOWER(t.code) = LOWER(:code)")
    Optional<GttTrigger> findByCodeIgnoreCase(@Param("code") String code);

    /**
     * Verifica se já existe um gatilho cadastrado com o código informado.
     *
     * @param code Código do gatilho
     * @return true se já existir
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Verifica se já existe outro gatilho com o mesmo código, desconsiderando um ID.
     *
     * @param code Código do gatilho
     * @param id   ID a ser desconsiderado
     * @return true se houver conflito
     */
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    /**
     * Busca um gatilho por ID carregando o módulo associado.
     *
     * @param id Identificador do gatilho
     * @return Optional com o gatilho carregado
     */
    @Query("SELECT t FROM GttTrigger t JOIN FETCH t.module WHERE t.id = :id")
    Optional<GttTrigger> findByIdWithModule(@Param("id") UUID id);

    /**
     * Consulta paginada de gatilhos aplicando filtros opcionais por módulo, busca textual e status.
     *
     * @param moduleId Filtro opcional por ID do módulo
     * @param search   Termo de pesquisa por código, nome ou descrição
     * @param isActive Filtro opcional por status ativo
     * @param pageable Configuração de paginação
     * @return Página de gatilhos
     */
    @Query(value = "SELECT t FROM GttTrigger t JOIN FETCH t.module m WHERE " +
                   "(:moduleId IS NULL OR m.id = :moduleId) AND " +
                   "(:search IS NULL OR TRIM(:search) = '' OR " +
                   " LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                   " LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                   " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                   "(:isActive IS NULL OR t.isActive = :isActive)",
           countQuery = "SELECT COUNT(t) FROM GttTrigger t WHERE " +
                        "(:moduleId IS NULL OR t.module.id = :moduleId) AND " +
                        "(:search IS NULL OR TRIM(:search) = '' OR " +
                        " LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        " LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                        "(:isActive IS NULL OR t.isActive = :isActive)")
    Page<GttTrigger> findWithFilters(@Param("moduleId") UUID moduleId,
                                     @Param("search") String search,
                                     @Param("isActive") Boolean isActive,
                                     Pageable pageable);

    /**
     * Retorna todos os gatilhos ativos ordenados por código.
     *
     * @return Lista de gatilhos ativos
     */
    @Query("SELECT t FROM GttTrigger t JOIN FETCH t.module WHERE t.isActive = true ORDER BY t.code ASC")
    List<GttTrigger> findAllActiveWithModule();

    /**
     * Retorna todos os gatilhos ordenados por código com o módulo carregado.
     *
     * @return Lista de todos os gatilhos
     */
    @Query("SELECT t FROM GttTrigger t JOIN FETCH t.module ORDER BY t.code ASC")
    List<GttTrigger> findAllWithModule();

    /**
     * Retorna gatilhos de um determinado módulo ordenados por código.
     *
     * @param moduleId ID do módulo
     * @return Lista de gatilhos ativos do módulo
     */
    @Query("SELECT t FROM GttTrigger t JOIN FETCH t.module WHERE t.module.id = :moduleId AND t.isActive = true ORDER BY t.code ASC")
    List<GttTrigger> findAllByModuleIdAndIsActiveTrue(@Param("moduleId") UUID moduleId);
}
