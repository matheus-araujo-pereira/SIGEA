package br.ufs.sigea.academic.sus.controller;

import br.ufs.sigea.academic.sus.dto.SusClassSummaryDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationCreateDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationResponseDTO;
import br.ufs.sigea.academic.sus.dto.SusGeneralSummaryDTO;
import br.ufs.sigea.academic.sus.service.SusEvaluationService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para submissão e análise psicométrica de avaliações da Escala de Usabilidade do Sistema (SUS).
 */
@RestController
@RequestMapping("/api/sus")
@RequiredArgsConstructor
@Tag(name = "Escala SUS", description = "Endpoints para questionário de usabilidade SUS e extração de métricas psicométricas")
public class SusEvaluationController {

    private final SusEvaluationService susEvaluationService;

    @Operation(summary = "Submeter avaliação da Escala SUS",
            description = "Recebe as respostas dos 10 itens Likert (1 a 5), calcula o escore padronizado (0 a 100) e classifica a usabilidade.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Avaliação registrada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou avaliação já enviada para a turma"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Estudante ou turma não encontrados")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SusEvaluationResponseDTO>> submitEvaluation(
            @Valid @RequestBody SusEvaluationCreateDTO dto,
            @AuthenticationPrincipal User currentUser) {

        SusEvaluationResponseDTO response = susEvaluationService.createEvaluation(dto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Avaliação SUS registrada com sucesso."));
    }

    @Operation(summary = "Consultar avaliação SUS do usuário logado",
            description = "Retorna a avaliação previamente submetida pelo discente para a turma informada ou geral.")
    @GetMapping("/my-evaluation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SusEvaluationResponseDTO>> getMyEvaluation(
            @RequestParam(name = "classId", required = false) UUID classId,
            @AuthenticationPrincipal User currentUser) {

        return susEvaluationService.getMyEvaluation(currentUser.getId(), classId)
                .map(eval -> ResponseEntity.ok(ApiResponse.ok(eval, "Avaliação SUS recuperada com sucesso.")))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.<SusEvaluationResponseDTO>ok(null, "Nenhuma avaliação encontrada.")));
    }

    @Operation(summary = "Listar histórico de avaliações SUS do discente",
            description = "Retorna todas as avaliações de usabilidade enviadas pelo discente autenticado.")
    @GetMapping("/my-evaluations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SusEvaluationResponseDTO>>> getMyEvaluations(
            @AuthenticationPrincipal User currentUser) {

        List<SusEvaluationResponseDTO> list = susEvaluationService.getMyEvaluations(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(list, "Histórico de avaliações SUS recuperado com sucesso."));
    }

    @Operation(summary = "Obter sumário psicométrico do SUS da turma",
            description = "Retorna métricas consolidadas (média, taxa de resposta, classificação adjetiva e médias por pergunta) para docentes.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sumário obtido com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/classes/{classId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<ApiResponse<SusClassSummaryDTO>> getClassSummary(
            @PathVariable("classId") UUID classId) {

        SusClassSummaryDTO summary = susEvaluationService.getClassSummary(classId);
        return ResponseEntity.ok(ApiResponse.ok(summary, "Sumário psicométrico da turma recuperado com sucesso."));
    }

    @Operation(summary = "Exportar dados brutos do questionário SUS da turma (CSV)",
            description = "Gera planilha tabular em formato RFC 4180 / UTF-8 com BOM para importação em ferramentas estatísticas (SPSS, R, Python).")
    @GetMapping("/classes/{classId}/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<byte[]> exportClassSusCsv(
            @PathVariable("classId") UUID classId) {

        byte[] csvBytes = susEvaluationService.exportClassSusCsv(classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pesquisa_sus_turma_" + classId + ".csv\"")
                .body(csvBytes);
    }

    @Operation(summary = "Obter sumário psicométrico global institucional da Escala SUS",
            description = "Apresenta a consolidação analítica de todo o sistema SIGEA para o perfil Administrador.")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SusGeneralSummaryDTO>> getGeneralSummary() {
        SusGeneralSummaryDTO summary = susEvaluationService.getGeneralSummary();
        return ResponseEntity.ok(ApiResponse.ok(summary, "Sumário psicométrico global recuperado com sucesso."));
    }

    @Operation(summary = "Exportar dados brutos globais do SUS (CSV)",
            description = "Gera planilha de todas as avaliações do sistema para pesquisa institucional.")
    @GetMapping("/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportGlobalSusCsv() {
        byte[] csvBytes = susEvaluationService.exportGlobalSusCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pesquisa_sus_global.csv\"")
                .body(csvBytes);
    }
}
