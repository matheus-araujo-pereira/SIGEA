package br.ufs.sigea.gtt.trigger.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerCreateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerResponseDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerStatusUpdateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerUpdateDTO;
import br.ufs.sigea.gtt.trigger.service.GttTriggerService;
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
 * Controlador REST para Gatilhos (Triggers) do IHI-GTT.
 * Disponibiliza endpoints para gestão administrativa e consulta rápida educacional.
 */
@RestController
@RequestMapping("/api/gtt/triggers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gatilhos GTT", description = "Gestão e consulta dos 53 gatilhos clínicos do IHI Global Trigger Tool")
public class GttTriggerController {

    private final GttTriggerService triggerService;

    /**
     * Consulta educacional de gatilhos clínicos ativos.
     *
     * @param moduleId Filtro opcional por módulo específico
     * @return Lista de gatilhos ativos
     */
    @GetMapping("/catalog")
    @Operation(summary = "Guia educacional de gatilhos GTT", description = "Retorna lista de gatilhos clínicos para consulta e treinamento.")
    public ResponseEntity<ApiResponse<List<GttTriggerResponseDTO>>> getCatalog(
            @RequestParam(required = false) UUID moduleId) {

        List<GttTriggerResponseDTO> catalog = triggerService.getCatalog(moduleId);
        return ResponseEntity.ok(ApiResponse.ok(catalog, "Gatilhos clínicos recuperados com sucesso."));
    }

    /**
     * Lista gatilhos paginados para controle administrativo (exclusivo ADMIN).
     *
     * @param moduleId Filtro opcional por módulo
     * @param search   Termo de busca por código, nome ou descrição
     * @param isActive Filtro por status ativo
     * @param pageable Configuração de paginação (padrão: 10 itens por página)
     * @return Página de gatilhos
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar gatilhos GTT paginados (Admin)", description = "Retorna listagem de gatilhos com paginação padrão de 10 itens e filtros.")
    public ResponseEntity<ApiResponse<PageResponse<GttTriggerResponseDTO>>> listTriggers(
            @RequestParam(required = false) UUID moduleId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "code", direction = Sort.Direction.ASC) Pageable pageable) {

        PageResponse<GttTriggerResponseDTO> response = triggerService.listTriggers(moduleId, search, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Gatilhos listados com sucesso."));
    }

    /**
     * Busca os detalhes de um gatilho específico por ID.
     *
     * @param id ID do gatilho
     * @return Detalhes do gatilho
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar gatilho por ID", description = "Recupera dados detalhados de um gatilho clínico.")
    public ResponseEntity<ApiResponse<GttTriggerResponseDTO>> getTriggerById(@PathVariable UUID id) {
        GttTriggerResponseDTO trigger = triggerService.getTriggerById(id);
        return ResponseEntity.ok(ApiResponse.ok(trigger, "Gatilho recuperado com sucesso."));
    }

    /**
     * Cadastra um novo gatilho clínico (exclusivo ADMIN).
     *
     * @param request Dados do novo gatilho
     * @return Gatilho cadastrado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo gatilho GTT", description = "Cria um novo gatilho clínico vinculado a um módulo.")
    public ResponseEntity<ApiResponse<GttTriggerResponseDTO>> createTrigger(@Valid @RequestBody GttTriggerCreateDTO request) {
        GttTriggerResponseDTO response = triggerService.createTrigger(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Gatilho GTT cadastrado com sucesso."));
    }

    /**
     * Atualiza os dados de um gatilho existente (exclusivo ADMIN).
     *
     * @param id      ID do gatilho
     * @param request Novos dados
     * @return Gatilho atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar gatilho GTT", description = "Altera as informações e diretrizes de um gatilho clínico.")
    public ResponseEntity<ApiResponse<GttTriggerResponseDTO>> updateTrigger(
            @PathVariable UUID id,
            @Valid @RequestBody GttTriggerUpdateDTO request) {

        GttTriggerResponseDTO response = triggerService.updateTrigger(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Gatilho GTT atualizado com sucesso."));
    }

    /**
     * Altera o status (ativo/inativo) de um gatilho (exclusivo ADMIN).
     *
     * @param id      ID do gatilho
     * @param request Novo status
     * @return Gatilho atualizado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar status do gatilho GTT", description = "Ativa ou inativa um gatilho clínico.")
    public ResponseEntity<ApiResponse<GttTriggerResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody GttTriggerStatusUpdateDTO request) {

        GttTriggerResponseDTO response = triggerService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Status do gatilho GTT atualizado com sucesso."));
    }

    /**
     * Exclui permanentemente um gatilho clínico (exclusivo ADMIN).
     *
     * @param id ID do gatilho
     * @return Confirmação de exclusão
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir gatilho GTT", description = "Remove um gatilho clínico do sistema.")
    public ResponseEntity<ApiResponse<Void>> deleteTrigger(@PathVariable UUID id) {
        triggerService.deleteTrigger(id);
        return ResponseEntity.ok(ApiResponse.ok("Gatilho GTT excluído com sucesso."));
    }
}
