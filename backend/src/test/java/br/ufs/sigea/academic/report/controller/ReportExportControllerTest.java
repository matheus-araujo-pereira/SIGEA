package br.ufs.sigea.academic.report.controller;

import br.ufs.sigea.academic.report.service.ReportExportService;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportExportControllerTest {

    @Mock
    private ReportExportService reportExportService;

    @InjectMocks
    private ReportExportController controller;

    private User professorUser;
    private User studentUser;
    private UUID submissionId;
    private UUID classId;

    @BeforeEach
    void setUp() {
        submissionId = UUID.randomUUID();
        classId = UUID.randomUUID();

        professorUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Profª. Dra. Ana Waleska")
                .role(UserRole.PROFESSOR)
                .build();

        studentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Matheus Araujo Pereira")
                .role(UserRole.STUDENT)
                .build();
    }

    @Test
    @DisplayName("Deve exportar relatório de auditoria clínica em PDF com headers corretos")
    void shouldExportSubmissionAuditPdf() {
        byte[] mockPdf = "%PDF-1.4 Mock Content".getBytes(StandardCharsets.US_ASCII);
        when(reportExportService.generateSubmissionAuditPdf(submissionId, studentUser)).thenReturn(mockPdf);

        ResponseEntity<byte[]> response = controller.exportSubmissionAuditPdf(submissionId, studentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"relatorio_auditoria_" + submissionId + ".pdf\"");
        assertThat(response.getBody()).isEqualTo(mockPdf);
        verify(reportExportService).generateSubmissionAuditPdf(submissionId, studentUser);
    }

    @Test
    @DisplayName("Deve exportar boletim epidemiológico da turma em PDF com headers corretos")
    void shouldExportClassEpidemiologicalBulletinPdf() {
        byte[] mockPdf = "%PDF-1.4 Mock Bulletin".getBytes(StandardCharsets.US_ASCII);
        when(reportExportService.generateClassEpidemiologicalBulletinPdf(classId, professorUser)).thenReturn(mockPdf);

        ResponseEntity<byte[]> response = controller.exportClassEpidemiologicalBulletinPdf(classId, professorUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"boletim_epidemiologico_" + classId + ".pdf\"");
        assertThat(response.getBody()).isEqualTo(mockPdf);
        verify(reportExportService).generateClassEpidemiologicalBulletinPdf(classId, professorUser);
    }

    @Test
    @DisplayName("Deve exportar dados brutos para pesquisa em CSV com headers corretos")
    void shouldExportClassResearchCsv() {
        byte[] mockCsv = "\uFEFFsubmission_id,turma\n".getBytes(StandardCharsets.UTF_8);
        when(reportExportService.generateClassResearchCsv(classId, professorUser)).thenReturn(mockCsv);

        ResponseEntity<byte[]> response = controller.exportClassResearchCsv(classId, professorUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv; charset=UTF-8");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"dados_pesquisa_" + classId + ".csv\"");
        assertThat(response.getBody()).isEqualTo(mockCsv);
        verify(reportExportService).generateClassResearchCsv(classId, professorUser);
    }
}
