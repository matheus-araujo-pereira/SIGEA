package br.ufs.sigea.academic.activity.controller;

import br.ufs.sigea.academic.activity.dto.ActivityCreateDTO;
import br.ufs.sigea.academic.activity.dto.ActivityDetailDTO;
import br.ufs.sigea.academic.activity.dto.ActivityResponseDTO;
import br.ufs.sigea.academic.activity.dto.ActivityUpdateDTO;
import br.ufs.sigea.academic.activity.service.ActivityService;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    private UUID classId;
    private UUID activityId;
    private User user;
    private ActivityResponseDTO responseDTO;
    private ActivityDetailDTO detailDTO;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        activityId = UUID.randomUUID();
        user = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();

        responseDTO = ActivityResponseDTO.builder()
                .id(activityId)
                .classId(classId)
                .className("Enfermagem - T01 - 2026.2")
                .title("Estudo de Caso 1")
                .description("Descrição")
                .deadline(Instant.now())
                .isExpired(false)
                .submissionCount(3)
                .createdAt(Instant.now())
                .build();

        detailDTO = ActivityDetailDTO.builder()
                .id(activityId)
                .classId(classId)
                .className("Enfermagem - T01 - 2026.2")
                .title("Estudo de Caso 1")
                .description("Descrição")
                .deadline(Instant.now())
                .isExpired(false)
                .submissionCount(3)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar atividades de uma turma")
    void shouldListActivities() {
        Pageable pageable = PageRequest.of(0, 10);
        when(activityService.listActivitiesByClass(classId, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<ActivityResponseDTO>>> response =
                activityController.listActivities(classId, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve obter detalhes da atividade por ID")
    void shouldGetActivityById() {
        when(activityService.getActivityById(activityId, user)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<ActivityDetailDTO>> response = activityController.getActivityById(activityId, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getId()).isEqualTo(activityId);
    }

    @Test
    @DisplayName("Deve criar atividade com sucesso")
    void shouldCreateActivity() {
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .classId(classId)
                .title("Estudo 1")
                .description("Desc")
                .deadline(Instant.now())
                .build();

        when(activityService.createActivity(createDTO, user)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<ActivityDetailDTO>> response = activityController.createActivity(createDTO, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar atividade com sucesso")
    void shouldUpdateActivity() {
        ActivityUpdateDTO updateDTO = ActivityUpdateDTO.builder()
                .title("Estudo Atualizado")
                .description("Desc")
                .deadline(Instant.now())
                .build();

        when(activityService.updateActivity(activityId, updateDTO, user)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<ActivityDetailDTO>> response = activityController.updateActivity(activityId, updateDTO, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve excluir atividade com sucesso")
    void shouldDeleteActivity() {
        ResponseEntity<ApiResponse<Void>> response = activityController.deleteActivity(activityId, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(activityService).deleteActivity(activityId, user);
    }
}
