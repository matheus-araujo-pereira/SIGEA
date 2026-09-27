package br.ufs.sigea.academic.clinical.controller;

import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateCreateDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateResponseDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateUpdateDTO;
import br.ufs.sigea.academic.clinical.service.ClinicalCaseTemplateService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
 * Controlador REST para gestão do Catálogo de Modelos de Casos Clínicos Simulados.
 */
@RestController
@RequestMapping("/api/academic/clinical-cases/templates")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Casos Clínicos Simulados (Templates)", description = "Catálogo de prontuários canônicos IHI-GTT e templates customizados para criação rápida de atividades")
public class ClinicalCaseTemplateController {

    private final ClinicalCaseTemplateService service;

    /**
     * Lista todos os modelos de casos clínicos cadastrados.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Listar modelos de casos clínicos", description = "Retorna lista de modelos de casos clínicos com filtro opcional por módulo GTT.")
    public ResponseEntity<ApiResponse<List<ClinicalCaseTemplateResponseDTO>>> listTemplates(
            @Parameter(description = "Código do módulo GTT (C, M, S, I, P, E)")
            @RequestParam(name = "moduleCode", required = false) String moduleCode
    ) {
        List<ClinicalCaseTemplateResponseDTO> templates = service.listTemplates(moduleCode);
        return ResponseEntity.ok(ApiResponse.ok(templates, "Modelos de casos clínicos listados com sucesso."));
    }

    /**
     * Busca um modelo específico por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Buscar modelo por ID", description = "Retorna o prontuário simulado completo e metadados de um modelo específico.")
    public ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> getTemplateById(
            @PathVariable UUID id
    ) {
        ClinicalCaseTemplateResponseDTO dto = service.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Modelo de caso clínico recuperado com sucesso."));
    }

    /**
     * Cadastra um novo modelo de caso clínico no catálogo.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Cadastrar modelo de caso clínico", description = "Permite a docentes e administradores criar novos modelos de prontuários.")
    public ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> createTemplate(
            @Valid @RequestBody ClinicalCaseTemplateCreateDTO dto,
            @AuthenticationPrincipal User currentUser
    ) {
        ClinicalCaseTemplateResponseDTO created = service.createTemplate(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Modelo de caso clínico cadastrado com sucesso."));
    }

    /**
     * Atualiza um modelo existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Atualizar modelo de caso clínico", description = "Atualiza os dados de um modelo de caso clínico.")
    public ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody ClinicalCaseTemplateUpdateDTO dto,
            @AuthenticationPrincipal User currentUser
    ) {
        ClinicalCaseTemplateResponseDTO updated = service.updateTemplate(id, dto, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Modelo de caso clínico atualizado com sucesso."));
    }

    /**
     * Remove um modelo do catálogo.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Excluir modelo de caso clínico", description = "Remove um modelo de caso clínico.")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        service.deleteTemplate(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(null, "Modelo de caso clínico excluído com sucesso."));
    }
}
