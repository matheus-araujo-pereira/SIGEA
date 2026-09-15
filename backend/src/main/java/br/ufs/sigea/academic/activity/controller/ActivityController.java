package br.ufs.sigea.academic.activity.controller;

import br.ufs.sigea.academic.activity.dto.ActivityCreateDTO;
import br.ufs.sigea.academic.activity.dto.ActivityDetailDTO;
import br.ufs.sigea.academic.activity.dto.ActivityResponseDTO;
import br.ufs.sigea.academic.activity.dto.ActivityUpdateDTO;
import br.ufs.sigea.academic.activity.service.ActivityService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
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

import java.util.UUID;

/**
 * Controlador REST para gestão de Atividades Avaliativas com Casos Clínicos Simulados.
 */
@RestController
@RequestMapping("/api/academic/activities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Atividades e Casos Clínicos", description = "Criação, edição e consulta de atividades com prontuários simulados")
public class ActivityController {

    private final ActivityService activityService;

    /**
     * Lista atividades vinculadas a uma turma acadêmica.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Listar atividades da turma", description = "Retorna lista paginada de atividades cadastradas em uma turma.")
    public ResponseEntity<ApiResponse<PageResponse<ActivityResponseDTO>>> listActivities(
            @RequestParam(name = "classId") UUID classId,
            @PageableDefault(size = 10, sort = "deadline", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(activityService.listActivitiesByClass(classId, pageable)),
                "Atividades listadas com sucesso."
        ));
    }

    /**
     * Consulta os detalhes de uma atividade por ID (incluindo prontuário simulado e resolução do estudante).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Detalhes da atividade", description = "Retorna os dados completos da atividade, prontuário simulado e resolução do aluno logado.")
    public ResponseEntity<ApiResponse<ActivityDetailDTO>> getActivityById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                activityService.getActivityById(id, currentUser),
                "Detalhes da atividade recuperados com sucesso."
        ));
    }

    /**
     * Cadastra uma nova atividade com caso clínico simulado.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Criar atividade (Docente/Admin)", description = "Cria uma nova atividade com prontuário fictício na turma do professor.")
    public ResponseEntity<ApiResponse<ActivityDetailDTO>> createActivity(
            @Valid @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        activityService.createActivity(dto, currentUser),
                        "Atividade avaliativa criada com sucesso."
                ));
    }

    /**
     * Atualiza os dados de uma atividade existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Atualizar atividade (Docente/Admin)", description = "Atualiza título, orientações, prontuário simulado e prazo de entrega.")
    public ResponseEntity<ApiResponse<ActivityDetailDTO>> updateActivity(
            @PathVariable UUID id,
            @Valid @RequestBody ActivityUpdateDTO dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                activityService.updateActivity(id, dto, currentUser),
                "Atividade atualizada com sucesso."
        ));
    }

    /**
     * Exclui uma atividade.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Excluir atividade (Docente/Admin)", description = "Exclui a atividade e suas submissões vinculadas.")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        activityService.deleteActivity(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(null, "Atividade excluída com sucesso."));
    }
}
