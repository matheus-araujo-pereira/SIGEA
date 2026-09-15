package br.ufs.sigea.academic.activity.controller;

import br.ufs.sigea.academic.activity.dto.SubmissionCreateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionGradeDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionResponseDTO;
import br.ufs.sigea.academic.activity.service.ActivitySubmissionService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivitySubmissionControllerTest {

    @Mock
    private ActivitySubmissionService submissionService;

    @InjectMocks
    private ActivitySubmissionController submissionController;

    private UUID activityId;
    private UUID submissionId;
    private User student;
    private User professor;
    private SubmissionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();
        submissionId = UUID.randomUUID();

        student = User.builder().id(UUID.randomUUID()).role(UserRole.STUDENT).build();
        professor = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();

        responseDTO = SubmissionResponseDTO.builder()
                .id(submissionId)
                .activityId(activityId)
                .studentId(student.getId())
                .submissionDate(Instant.now())
                .grade(new BigDecimal("9.00"))
                .isGraded(true)
                .build();
    }

    @Test
    @DisplayName("Deve submeter atividade como estudante")
    void shouldSubmitActivity() {
        SubmissionCreateDTO dto = SubmissionCreateDTO.builder().build();
        when(submissionService.submitActivity(activityId, dto, student)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<SubmissionResponseDTO>> response =
                submissionController.submitActivity(activityId, dto, student);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getId()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("Deve listar submissões de uma atividade para docente")
    void shouldListSubmissions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(submissionService.listSubmissionsByActivity(activityId, professor, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<SubmissionResponseDTO>>> response =
                submissionController.listSubmissions(activityId, professor, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar submissão por ID")
    void shouldGetSubmissionById() {
        when(submissionService.getSubmissionById(submissionId, student)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<SubmissionResponseDTO>> response =
                submissionController.getSubmissionById(submissionId, student);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getId()).isEqualTo(submissionId);
    }

    @Test
    @DisplayName("Deve avaliar submissão com nota")
    void shouldGradeSubmission() {
        SubmissionGradeDTO dto = SubmissionGradeDTO.builder().grade(new BigDecimal("9.00")).build();
        when(submissionService.gradeSubmission(submissionId, dto, professor)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<SubmissionResponseDTO>> response =
                submissionController.gradeSubmission(submissionId, dto, professor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getGrade()).isEqualByComparingTo("9.00");
    }

    @Test
    @DisplayName("Deve listar submissões do estudante logado")
    void shouldGetMySubmissions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(submissionService.listSubmissionsByStudent(student.getId(), student, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<SubmissionResponseDTO>>> response =
                submissionController.getMySubmissions(student, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }
}
