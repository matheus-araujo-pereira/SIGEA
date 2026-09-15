package br.ufs.sigea.gtt.severity.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityCreateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityResponseDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityStatusUpdateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityUpdateDTO;
import br.ufs.sigea.gtt.severity.service.HarmSeverityService;
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
 * Controlador REST para as Categorias de Gravidade de Dano segundo o Índice NCC MERP adaptado.
 */
@RestController
@RequestMapping("/api/gtt/severities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gravidades de Dano (NCC MERP)", description = "Gestão e consulta das categorias de gravidade de dano (A a I)")
public class HarmSeverityController {

    private final HarmSeverityService severityService;

    /**
     * Consulta educacional do guia interativo de gravidades ordenadas de A a I.
     *
     * @return Lista ordenada de gravidades ativas
     */
    @GetMapping("/guide")
    @Operation(summary = "Guia interativo de gravidades NCC MERP", description = "Retorna categorias ordenadas de A a I para treinamento e classificação de eventos.")
    public ResponseEntity<ApiResponse<List<HarmSeverityResponseDTO>>> getGuide() {
        List<HarmSeverityResponseDTO> guide = severityService.getGuide();
        return ResponseEntity.ok(ApiResponse.ok(guide, "Guia de gravidades recuperado com sucesso."));
    }

    /**
     * Lista categorias de gravidade paginadas para gestão administrativa (exclusivo ADMIN).
     *
     * @param search   Termo de pesquisa
     * @param isHarm   Filtro por gravidade com dano real
     * @param isActive Filtro por status ativo
     * @param pageable Configuração de paginação (padrão: 10 itens por página)
     * @return Página de gravidades
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar gravidades paginadas (Admin)", description = "Retorna listagem de gravidades com paginação padrão de 10 itens e filtros.")
    public ResponseEntity<ApiResponse<PageResponse<HarmSeverityResponseDTO>>> listSeverities(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isHarm,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "categoryLetter", direction = Sort.Direction.ASC) Pageable pageable) {

        PageResponse<HarmSeverityResponseDTO> response = severityService.listSeverities(search, isHarm, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Gravidades listadas com sucesso."));
    }

    /**
     * Busca os dados de uma gravidade específica por ID.
     *
     * @param id ID da gravidade
     * @return Dados da gravidade
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar gravidade por ID", description = "Recupera dados de uma categoria de gravidade.")
    public ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> getSeverityById(@PathVariable UUID id) {
        HarmSeverityResponseDTO severity = severityService.getSeverityById(id);
        return ResponseEntity.ok(ApiResponse.ok(severity, "Gravidade recuperada com sucesso."));
    }

    /**
     * Cadastra uma nova categoria de gravidade (exclusivo ADMIN).
     *
     * @param request Dados da nova categoria
     * @return Categoria cadastrada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar nova gravidade", description = "Cria uma nova categoria no índice de gravidade.")
    public ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> createSeverity(@Valid @RequestBody HarmSeverityCreateDTO request) {
        HarmSeverityResponseDTO response = severityService.createSeverity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Categoria de gravidade cadastrada com sucesso."));
    }

    /**
     * Atualiza os dados de uma categoria de gravidade existente (exclusivo ADMIN).
     *
     * @param id      ID da gravidade
     * @param request Novos dados
     * @return Gravidade atualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar gravidade", description = "Altera as informações e critérios de uma categoria de gravidade.")
    public ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> updateSeverity(
            @PathVariable UUID id,
            @Valid @RequestBody HarmSeverityUpdateDTO request) {

        HarmSeverityResponseDTO response = severityService.updateSeverity(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Categoria de gravidade atualizada com sucesso."));
    }

    /**
     * Altera o status (ativo/inativo) de uma gravidade (exclusivo ADMIN).
     *
     * @param id      ID da gravidade
     * @param request Novo status
     * @return Gravidade atualizada
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar status da gravidade", description = "Ativa ou inativa uma categoria de gravidade.")
    public ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody HarmSeverityStatusUpdateDTO request) {

        HarmSeverityResponseDTO response = severityService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Status da gravidade atualizado com sucesso."));
    }

    /**
     * Exclui permanentemente uma categoria de gravidade (exclusivo ADMIN).
     *
     * @param id ID da gravidade
     * @return Confirmação de exclusão
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir gravidade", description = "Remove uma categoria de gravidade do sistema.")
    public ResponseEntity<ApiResponse<Void>> deleteSeverity(@PathVariable UUID id) {
        severityService.deleteSeverity(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoria de gravidade excluída com sucesso."));
    }
}
