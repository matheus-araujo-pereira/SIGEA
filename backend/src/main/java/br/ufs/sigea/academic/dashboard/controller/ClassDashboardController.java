package br.ufs.sigea.academic.dashboard.controller;

import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
import br.ufs.sigea.academic.dashboard.service.ClassDashboardService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para fornecimento dos indicadores oficiais do IHI-GTT e métricas pedagógicas da turma.
 */
@RestController
@RequestMapping("/api/academic/classes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard Analítico da Turma", description = "Indicadores oficiais IHI-GTT e desempenho pedagógico")
public class ClassDashboardController {

    private final ClassDashboardService dashboardService;

    /**
     * Consulta os indicadores do painel analítico da turma.
     */
    @GetMapping("/{classId}/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(
            summary = "Painel de Indicadores GTT e Pedagógicos (Docente/Admin)",
            description = "Calcula e retorna taxas oficiais de EAs por 1.000 pacientes-dia, EAs por 100 admissões, " +
                          "% de casos com dano, distribuição por severidade (E a I) e média de notas da turma."
    )
    public ResponseEntity<ApiResponse<ClassDashboardDTO>> getClassDashboard(
            @PathVariable UUID classId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getClassDashboard(classId, currentUser), "Dashboard da turma recuperado com sucesso."));
    }
}
