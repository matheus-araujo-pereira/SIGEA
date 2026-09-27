package br.ufs.sigea.academic.report.controller;

import br.ufs.sigea.academic.report.service.ReportExportService;
import br.ufs.sigea.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para download de laudos clínicos em PDF e extração de dados brutos para pesquisa (CSV).
 */
@RestController
@RequestMapping({"/api/academic", "/api/reports"})
@RequiredArgsConstructor
@Tag(name = "Relatórios e Exportação", description = "Endpoints para emissão de relatórios oficiais em PDF e dados em CSV")
public class ReportExportController {

    private final ReportExportService reportExportService;

    @Operation(summary = "Exportar Relatório de Auditoria Clínica Individual (PDF)",
            description = "Gera o laudo oficial em PDF contendo prontuário, gatilhos IHI, Ishikawa/GUT e parecer pedagógico docente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF gerado e transmitido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para submissão de outro discente/turma"),
            @ApiResponse(responseCode = "404", description = "Submissão não encontrada")
    })
    @GetMapping({"/submissions/{id}/export/pdf", "/submissions/{id}/pdf"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportSubmissionAuditPdf(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal User currentUser) {

        byte[] pdfBytes = reportExportService.generateSubmissionAuditPdf(id, currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio_auditoria_" + id + ".pdf\"")
                .body(pdfBytes);
    }

    @Operation(summary = "Exportar Boletim Epidemiológico da Turma (PDF)",
            description = "Emite o boletim oficial IHI-GTT com taxas por 1.000 dias-paciente, gravidade NCC MERP e ranking de gatilhos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boletim em PDF gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao docente da turma ou administrador"),
            @ApiResponse(responseCode = "404", description = "Turma acadêmica não encontrada")
    })
    @GetMapping({"/classes/{id}/export/pdf", "/classes/{id}/bulletin/pdf"})
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<byte[]> exportClassEpidemiologicalBulletinPdf(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal User currentUser) {

        byte[] pdfBytes = reportExportService.generateClassEpidemiologicalBulletinPdf(id, currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"boletim_epidemiologico_" + id + ".pdf\"")
                .body(pdfBytes);
    }

    @Operation(summary = "Exportar Dados Brutos da Turma para Pesquisa (CSV)",
            description = "Exporta os dados anonimizados das submissões e auditorias em formato RFC 4180 / UTF-8 com BOM para Excel, SPSS e R.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao docente da turma ou administrador"),
            @ApiResponse(responseCode = "404", description = "Turma acadêmica não encontrada")
    })
    @GetMapping({"/classes/{id}/export/csv", "/classes/{id}/research/csv"})
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<byte[]> exportClassResearchCsv(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal User currentUser) {

        byte[] csvBytes = reportExportService.generateClassResearchCsv(id, currentUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dados_pesquisa_" + id + ".csv\"")
                .body(csvBytes);
    }
}
