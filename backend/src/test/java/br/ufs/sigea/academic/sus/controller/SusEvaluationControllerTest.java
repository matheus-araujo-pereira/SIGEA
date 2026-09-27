package br.ufs.sigea.academic.sus.controller;

import br.ufs.sigea.academic.sus.dto.SusClassSummaryDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationCreateDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationResponseDTO;
import br.ufs.sigea.academic.sus.dto.SusGeneralSummaryDTO;
import br.ufs.sigea.academic.sus.service.SusEvaluationService;
import br.ufs.sigea.common.dto.ApiResponse;
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
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusEvaluationControllerTest {

    @Mock
    private SusEvaluationService susEvaluationService;

    @InjectMocks
    private SusEvaluationController controller;

    private User studentUser;
    private UUID classId;
    private SusEvaluationResponseDTO mockResponseDTO;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();

        studentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Matheus Araujo")
                .email("matheus@academico.ufs.br")
                .role(UserRole.STUDENT)
                .build();

        mockResponseDTO = SusEvaluationResponseDTO.builder()
                .id(UUID.randomUUID())
                .studentId(studentUser.getId())
                .studentName(studentUser.getFullName())
                .academicClassId(classId)
                .score(85.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("B")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve submeter avaliação SUS com sucesso e retornar 201 Created")
    void shouldSubmitEvaluation() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .academicClassId(classId)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .build();

        when(susEvaluationService.createEvaluation(dto, studentUser.getId())).thenReturn(mockResponseDTO);

        ResponseEntity<ApiResponse<SusEvaluationResponseDTO>> response = controller.submitEvaluation(dto, studentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(mockResponseDTO);
        verify(susEvaluationService).createEvaluation(dto, studentUser.getId());
    }

    @Test
    @DisplayName("Deve calcular pré-visualização SUS com sucesso e retornar 200 OK")
    void shouldCalculatePreview() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .build();

        br.ufs.sigea.academic.sus.dto.SusEvaluationPreviewDTO mockPreview =
                new br.ufs.sigea.academic.sus.dto.SusEvaluationPreviewDTO(100.0, "Melhor Imaginável", "Aceitável", "A");

        when(susEvaluationService.calculatePreview(dto)).thenReturn(mockPreview);

        ResponseEntity<ApiResponse<br.ufs.sigea.academic.sus.dto.SusEvaluationPreviewDTO>> response =
                controller.calculatePreview(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(mockPreview);
        verify(susEvaluationService).calculatePreview(dto);
    }

    @Test
    @DisplayName("Deve consultar avaliação do usuário por turma com dados presentes e ausentes")
    void shouldGetMyEvaluation() {
        when(susEvaluationService.getMyEvaluation(studentUser.getId(), classId)).thenReturn(Optional.of(mockResponseDTO));

        ResponseEntity<ApiResponse<SusEvaluationResponseDTO>> response = controller.getMyEvaluation(classId, studentUser);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(mockResponseDTO);

        when(susEvaluationService.getMyEvaluation(studentUser.getId(), null)).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse<SusEvaluationResponseDTO>> emptyResponse = controller.getMyEvaluation(null, studentUser);
        assertThat(emptyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(emptyResponse.getBody()).isNotNull();
        assertThat(emptyResponse.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("Deve listar histórico de avaliações do discente")
    void shouldGetMyEvaluations() {
        when(susEvaluationService.getMyEvaluations(studentUser.getId())).thenReturn(List.of(mockResponseDTO));

        ResponseEntity<ApiResponse<List<SusEvaluationResponseDTO>>> response = controller.getMyEvaluations(studentUser);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).containsExactly(mockResponseDTO);
    }

    @Test
    @DisplayName("Deve obter sumário psicométrico consolidado da turma")
    void shouldGetClassSummary() {
        SusClassSummaryDTO summary = SusClassSummaryDTO.builder()
                .classId(classId)
                .className("Turma Teste")
                .totalEvaluations(10)
                .averageScore(82.5)
                .adjectiveRating("Bom")
                .build();

        when(susEvaluationService.getClassSummary(classId)).thenReturn(summary);

        ResponseEntity<ApiResponse<SusClassSummaryDTO>> response = controller.getClassSummary(classId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(summary);
    }

    @Test
    @DisplayName("Deve exportar CSV do questionário SUS da turma")
    void shouldExportClassSusCsv() {
        byte[] csv = "id,escore_sus\n1,85.0".getBytes(StandardCharsets.UTF_8);
        when(susEvaluationService.exportClassSusCsv(classId)).thenReturn(csv);

        ResponseEntity<byte[]> response = controller.exportClassSusCsv(classId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv; charset=UTF-8");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("attachment; filename=\"pesquisa_sus_turma_");
        assertThat(response.getBody()).isEqualTo(csv);
    }

    @Test
    @DisplayName("Deve obter sumário psicométrico global institucional")
    void shouldGetGeneralSummary() {
        SusGeneralSummaryDTO summary = SusGeneralSummaryDTO.builder()
                .totalEvaluations(25)
                .averageScore(88.0)
                .build();

        when(susEvaluationService.getGeneralSummary()).thenReturn(summary);

        ResponseEntity<ApiResponse<SusGeneralSummaryDTO>> response = controller.getGeneralSummary();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(summary);
    }

    @Test
    @DisplayName("Deve exportar CSV global de avaliações SUS")
    void shouldExportGlobalSusCsv() {
        byte[] csv = "id,escore_sus\n".getBytes(StandardCharsets.UTF_8);
        when(susEvaluationService.exportGlobalSusCsv()).thenReturn(csv);

        ResponseEntity<byte[]> response = controller.exportGlobalSusCsv();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv; charset=UTF-8");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("pesquisa_sus_global.csv");
        assertThat(response.getBody()).isEqualTo(csv);
    }
}
