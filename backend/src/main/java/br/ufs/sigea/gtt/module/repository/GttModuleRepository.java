package br.ufs.sigea.gtt.module.repository;

import br.ufs.sigea.gtt.module.domain.GttModule;
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
 * Repositório de acesso a dados para a entidade GttModule.
 */
@Repository
public interface GttModuleRepository extends JpaRepository<GttModule, UUID> {

    /**
     * Busca um módulo pelo seu código único.
     *
     * @param code Código do módulo (ex: C, M, S)
     * @return Optional contendo o módulo se encontrado
     */
    Optional<GttModule> findByCodeIgnoreCase(String code);

    /**
     * Verifica se já existe um módulo cadastrado com o código informado.
     *
     * @param code Código do módulo
     * @return true se já existir
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Verifica se já existe outro módulo com o mesmo código, excluindo um ID específico.
     *
     * @param code Código do módulo
     * @param id   ID a ser desconsiderado na verificação
     * @return true se houver conflito
     */
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    /**
     * Recupera módulos de forma paginada aplicando filtros opcionais de busca textual e status.
     *
     * @param search   Termo de pesquisa por código ou nome
     * @param isActive Filtro opcional por status ativo/inativo
     * @param pageable Configuração de paginação
     * @return Página de módulos correspondentes
     */
    @Query("SELECT m FROM GttModule m WHERE " +
           "(CAST(:search AS string) IS NULL OR " +
           " LOWER(m.code) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
           " LOWER(m.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (CAST(:isActive AS boolean) IS NULL OR m.isActive = :isActive)")
    Page<GttModule> findWithFilters(@Param("search") String search,
                                    @Param("isActive") Boolean isActive,
                                    Pageable pageable);

    /**
     * Retorna todos os módulos ativos ordenados por código para exibição no catálogo.
     *
     * @return Lista ordenada de módulos ativos
     */
    List<GttModule> findAllByIsActiveTrueOrderByCodeAsc();

    /**
     * Retorna todos os módulos ordenados por código.
     *
     * @return Lista de módulos ordenados
     */
    List<GttModule> findAllByOrderByCodeAsc();
}
