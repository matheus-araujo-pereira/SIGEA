package br.ufs.sigea.academic.activity.controller;

import br.ufs.sigea.academic.activity.dto.SubmissionCreateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionGradeDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionResponseDTO;
import br.ufs.sigea.academic.activity.service.ActivitySubmissionService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para submissão de resoluções de atividades e avaliação pedagógica docente.
 */
@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Submissões e Avaliações", description = "Envio de respostas de alunos, avaliação com nota e parecer pedagógico")
public class ActivitySubmissionController {

    private final ActivitySubmissionService submissionService;

    /**
     * Submete a resolução da atividade pelo estudante matriculado.
     */
    @PostMapping("/activities/{activityId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enviar resolução da atividade (Estudante)", description = "Grava gatilhos identificados e ferramentas da qualidade da atividade.")
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> submitActivity(
            @PathVariable UUID activityId,
            @Valid @RequestBody SubmissionCreateDTO dto,
            @AuthenticationPrincipal User currentStudent
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        submissionService.submitActivity(activityId, dto, currentStudent),
                        "Atividade submetida com sucesso."
                ));
    }

    /**
     * Lista todas as submissões recebidas para uma atividade (visão docente).
     */
    @GetMapping("/activities/{activityId}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Listar submissões da atividade (Docente/Admin)", description = "Lista resoluções enviadas pelos alunos para correção.")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponseDTO>>> listSubmissions(
            @PathVariable UUID activityId,
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(submissionService.listSubmissionsByActivity(activityId, currentUser, pageable)),
                "Submissões da atividade listadas com sucesso."
        ));
    }

    /**
     * Consulta uma submissão por ID.
     */
    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Detalhes da submissão", description = "Retorna a resolução do aluno, nota e parecer pedagógico.")
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> getSubmissionById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                submissionService.getSubmissionById(id, currentUser),
                "Detalhes da submissão recuperados com sucesso."
        ));
    }

    /**
     * Atribui nota e parecer pedagógico a uma submissão de estudante.
     */
    @PatchMapping("/submissions/{id}/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Avaliar submissão (Docente/Admin)", description = "Atribui nota (0.0 a 10.0) e parecer pedagógico detalhado ao aluno.")
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> gradeSubmission(
            @PathVariable UUID id,
            @Valid @RequestBody SubmissionGradeDTO dto,
            @AuthenticationPrincipal User currentProfessor
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                submissionService.gradeSubmission(id, dto, currentProfessor),
                "Avaliação e parecer pedagógico registrados com sucesso."
        ));
    }

    /**
     * Lista as submissões do estudante logado.
     */
    @GetMapping("/submissions/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Minhas submissões (Estudante)", description = "Lista as submissões enviadas pelo estudante autenticado.")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponseDTO>>> getMySubmissions(
            @AuthenticationPrincipal User currentStudent,
            @PageableDefault(size = 10, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(submissionService.listSubmissionsByStudent(currentStudent.getId(), currentStudent, pageable)),
                "Minhas submissões recuperadas com sucesso."
        ));
    }
}
