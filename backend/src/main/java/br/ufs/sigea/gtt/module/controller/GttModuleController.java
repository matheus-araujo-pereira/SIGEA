package br.ufs.sigea.gtt.module.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.module.dto.GttModuleCreateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleResponseDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleStatusUpdateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleUpdateDTO;
import br.ufs.sigea.gtt.module.service.GttModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para Módulos do IHI-GTT.
 * Oferece gestão administrativa (ADMIN) e consulta para visualização educacional (todos os perfis).
 */
@RestController
@RequestMapping("/api/gtt/modules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Módulos GTT", description = "Gestão e consulta de módulos assistenciais do IHI Global Trigger Tool")
public class GttModuleController {

    private final GttModuleService moduleService;

    /**
     * Consulta pública/autenticada do catálogo de módulos ativos para fins educacionais.
     *
     * @return Lista de módulos ativos com contagem de gatilhos
     */
    @GetMapping("/catalog")
    @Operation(summary = "Catálogo educacional de módulos GTT", description = "Retorna lista de módulos ativos para estudo e consulta rápida.")
    public ResponseEntity<ApiResponse<List<GttModuleResponseDTO>>> getCatalog() {
        List<GttModuleResponseDTO> catalog = moduleService.getCatalog();
        return ResponseEntity.ok(ApiResponse.ok(catalog, "Catálogo de módulos recuperado com sucesso."));
    }

    /**
     * Lista módulos paginados com filtros (exclusivo para ADMIN).
     *
     * @param search   Termo de pesquisa por código ou nome
     * @param isActive Filtro por status ativo
     * @param pageable Configuração de paginação (padrão: 10 itens por página)
     * @return Página de módulos
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar módulos GTT paginados (Admin)", description = "Retorna listagem paginada (10 itens) de módulos para gestão administrativa.")
    public ResponseEntity<ApiResponse<PageResponse<GttModuleResponseDTO>>> listModules(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "code", direction = Sort.Direction.ASC) Pageable pageable) {

        PageResponse<GttModuleResponseDTO> response = moduleService.listModules(search, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Módulos listados com sucesso."));
    }

    /**
     * Busca os detalhes de um módulo específico por ID.
     *
     * @param id ID do módulo
     * @return Detalhes do módulo
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar módulo por ID", description = "Recupera os detalhes de um módulo GTT.")
    public ResponseEntity<ApiResponse<GttModuleResponseDTO>> getModuleById(@PathVariable UUID id) {
        GttModuleResponseDTO module = moduleService.getModuleById(id);
        return ResponseEntity.ok(ApiResponse.ok(module, "Módulo recuperado com sucesso."));
    }

    /**
     * Cadastra um novo módulo GTT no sistema (exclusivo ADMIN).
     *
     * @param request Dados do novo módulo
     * @return Módulo cadastrado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo módulo GTT", description = "Cria um novo módulo assistencial no sistema.")
    public ResponseEntity<ApiResponse<GttModuleResponseDTO>> createModule(@Valid @RequestBody GttModuleCreateDTO request) {
        GttModuleResponseDTO response = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Módulo GTT cadastrado com sucesso."));
    }

    /**
     * Atualiza os dados de um módulo GTT existente (exclusivo ADMIN).
     *
     * @param id      ID do módulo
     * @param request Novos dados
     * @return Módulo atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar módulo GTT", description = "Altera as informações cadastrais de um módulo.")
    public ResponseEntity<ApiResponse<GttModuleResponseDTO>> updateModule(
            @PathVariable UUID id,
            @Valid @RequestBody GttModuleUpdateDTO request) {

        GttModuleResponseDTO response = moduleService.updateModule(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Módulo GTT atualizado com sucesso."));
    }

    /**
     * Altera o status (ativo/inativo) de um módulo GTT (exclusivo ADMIN).
     *
     * @param id      ID do módulo
     * @param request Novo status
     * @return Módulo atualizado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar status do módulo GTT", description = "Ativa ou inativa um módulo assistencial.")
    public ResponseEntity<ApiResponse<GttModuleResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody GttModuleStatusUpdateDTO request) {

        GttModuleResponseDTO response = moduleService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Status do módulo GTT atualizado com sucesso."));
    }

    /**
     * Exclui permanentemente um módulo GTT (exclusivo ADMIN).
     *
     * @param id ID do módulo
     * @return Confirmação de exclusão
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir módulo GTT", description = "Remove um módulo caso não existam gatilhos vinculados.")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable UUID id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.ok("Módulo GTT excluído com sucesso."));
    }
}
