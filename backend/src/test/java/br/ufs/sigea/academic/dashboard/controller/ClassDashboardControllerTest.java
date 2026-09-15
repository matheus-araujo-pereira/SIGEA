package br.ufs.sigea.academic.dashboard.controller;

import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
import br.ufs.sigea.academic.dashboard.dto.GttMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.PedagogicalMetricsDTO;
import br.ufs.sigea.academic.dashboard.service.ClassDashboardService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassDashboardControllerTest {

    @Mock
    private ClassDashboardService dashboardService;

    @InjectMocks
    private ClassDashboardController dashboardController;

    private UUID classId;
    private User professor;
    private ClassDashboardDTO dashboardDTO;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        professor = User.builder().id(UUID.randomUUID()).role(UserRole.PROFESSOR).build();

        dashboardDTO = ClassDashboardDTO.builder()
                .classId(classId)
                .className("Enfermagem - T01 - 2026.2")
                .professorName("Prof. Waleska")
                .academicPeriod("2026.2")
                .isClosed(false)
                .gttMetrics(GttMetricsDTO.builder()
                        .adverseEventsPer1000PatientDays(12.5)
                        .harmDistribution(Collections.emptyMap())
                        .build())
                .pedagogicalMetrics(PedagogicalMetricsDTO.builder()
                        .classAverageGrade(8.75)
                        .topIdentifiedTriggers(Collections.emptyList())
                        .build())
                .build();
    }

    @Test
    @DisplayName("Deve retornar painel de indicadores da turma")
    void shouldReturnClassDashboard() {
        when(dashboardService.getClassDashboard(classId, professor)).thenReturn(dashboardDTO);

        ResponseEntity<ApiResponse<ClassDashboardDTO>> response =
                dashboardController.getClassDashboard(classId, professor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getClassId()).isEqualTo(classId);
        assertThat(response.getBody().getData().getGttMetrics().getAdverseEventsPer1000PatientDays()).isEqualTo(12.5);
    }
}
