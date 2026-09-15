package br.ufs.sigea.academic.clazz.controller;

import br.ufs.sigea.academic.clazz.dto.AcademicClassCloseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCreateDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassDetailDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassResponseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassUpdateDTO;
import br.ufs.sigea.academic.clazz.service.AcademicClassService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para gestão administrativa e pedagógica de Turmas Acadêmicas.
 */
@RestController
@RequestMapping("/api/academic/classes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Turmas Acadêmicas", description = "CRUD de turmas, controle de matrículas e encerramento de período")
public class AcademicClassController {

    private final AcademicClassService classService;

    /**
     * Lista turmas do sistema de forma paginada com filtros administrativos.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar turmas (Admin)", description = "Retorna lista paginada de turmas com filtros por disciplina, turma, período e status.")
    public ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> listClasses(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "academicPeriod", required = false) String academicPeriod,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @PageableDefault(size = 10, sort = "subjectName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(classService.listClasses(search, academicPeriod, isClosed, pageable)),
                "Turmas listadas com sucesso."
        ));
    }

    /**
     * Consulta as turmas do usuário autenticado (como docente titular ou estudante matriculado).
     */
    @GetMapping("/my-classes")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Minhas Turmas", description = "Lista as turmas vinculadas ao docente ou estudante autenticado.")
    public ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> getMyClasses(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "subjectName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        if (currentUser.getRole() == UserRole.STUDENT) {
            return ResponseEntity.ok(ApiResponse.ok(
                    PageResponse.from(classService.listClassesByStudent(currentUser.getId(), pageable)),
                    "Minhas turmas recuperadas com sucesso."
            ));
        } else if (currentUser.getRole() == UserRole.PROFESSOR) {
            return ResponseEntity.ok(ApiResponse.ok(
                    PageResponse.from(classService.listClassesByProfessor(currentUser.getId(), pageable)),
                    "Turmas do docente recuperadas com sucesso."
            ));
        } else {
            // ADMIN consulta todas
            return ResponseEntity.ok(ApiResponse.ok(
                    PageResponse.from(classService.listClasses(null, null, null, pageable)),
                    "Todas as turmas recuperadas com sucesso."
            ));
        }
    }

    /**
     * Obtém os detalhes completos de uma turma por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @Operation(summary = "Detalhes da turma", description = "Retorna dados completos da turma, docente e discentes matriculados.")
    public ResponseEntity<ApiResponse<AcademicClassDetailDTO>> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(classService.getClassById(id), "Detalhes da turma recuperados com sucesso."));
    }

    /**
     * Cadastra uma nova turma acadêmica.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar turma (Admin)", description = "Cria uma nova turma vinculando obrigatoriamente um professor titular e lista de estudantes.")
    public ResponseEntity<ApiResponse<AcademicClassDetailDTO>> createClass(
            @Valid @RequestBody AcademicClassCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(classService.createClass(dto), "Turma acadêmica criada com sucesso."));
    }

    /**
     * Atualiza os dados de uma turma acadêmica existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar turma (Admin)", description = "Atualiza disciplina, período, professor responsável e alunos da turma.")
    public ResponseEntity<ApiResponse<AcademicClassDetailDTO>> updateClass(
            @PathVariable UUID id,
            @Valid @RequestBody AcademicClassUpdateDTO dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                classService.updateClass(id, dto),
                "Turma acadêmica atualizada com sucesso."
        ));
    }

    /**
     * Altera o status de encerramento da turma (apenas permitido se não houver correções pendentes).
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Encerrar ou reabrir turma (Admin)", description = "Altera o status de encerramento da turma com checagem de pendências de correção.")
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AcademicClassCloseDTO dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                classService.updateClassClosedStatus(id, dto.getIsClosed()),
                Boolean.TRUE.equals(dto.getIsClosed()) ? "Turma encerrada com sucesso." : "Turma reaberta com sucesso."
        ));
    }

    /**
     * Exclui uma turma acadêmica.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir turma (Admin)", description = "Remove permanentemente uma turma acadêmica.")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable UUID id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Turma acadêmica excluída com sucesso."));
    }
}
