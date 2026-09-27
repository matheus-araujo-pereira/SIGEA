package br.ufs.sigea.academic.clinical.controller;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateCreateDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateResponseDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateUpdateDTO;
import br.ufs.sigea.academic.clinical.service.ClinicalCaseTemplateService;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicalCaseTemplateControllerTest {

    @Mock
    private ClinicalCaseTemplateService service;

    @InjectMocks
    private ClinicalCaseTemplateController controller;

    private User professorUser;
    private UUID templateId;
    private ClinicalCaseTemplateResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        professorUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Profª. Drª. Ana Waleska")
                .role(UserRole.PROFESSOR)
                .build();

        responseDTO = ClinicalCaseTemplateResponseDTO.builder()
                .id(templateId)
                .title("Caso Clínico 01: Vancomicina")
                .description("Descrição do modelo")
                .moduleCode("M")
                .primaryTriggerCode("M5")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Givaldo").build())
                .isSystemTemplate(true)
                .createdByName("Sistema SIGEA (Canônico)")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar modelos de casos clínicos com sucesso")
    void shouldListTemplates() {
        when(service.listTemplates(null)).thenReturn(List.of(responseDTO));

        ResponseEntity<ApiResponse<List<ClinicalCaseTemplateResponseDTO>>> response =
                controller.listTemplates(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getTitle()).isEqualTo("Caso Clínico 01: Vancomicina");
    }

    @Test
    @DisplayName("Deve listar modelos filtrando por módulo GTT com sucesso")
    void shouldListTemplatesWithModuleFilter() {
        when(service.listTemplates("M")).thenReturn(List.of(responseDTO));

        ResponseEntity<ApiResponse<List<ClinicalCaseTemplateResponseDTO>>> response =
                controller.listTemplates("M");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    @DisplayName("Deve obter modelo de caso clínico por ID")
    void shouldGetTemplateById() {
        when(service.getTemplateById(templateId)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> response =
                controller.getTemplateById(templateId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo(templateId);
    }

    @Test
    @DisplayName("Deve cadastrar novo modelo de caso clínico com sucesso")
    void shouldCreateTemplate() {
        ClinicalCaseTemplateCreateDTO createDTO = ClinicalCaseTemplateCreateDTO.builder()
                .title("Novo Caso Clínico")
                .description("Descrição")
                .moduleCode("C")
                .primaryTriggerCode("C7")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Severino").build())
                .build();

        when(service.createTemplate(createDTO, professorUser)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> response =
                controller.createTemplate(createDTO, professorUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Deve atualizar modelo de caso clínico com sucesso")
    void shouldUpdateTemplate() {
        ClinicalCaseTemplateUpdateDTO updateDTO = ClinicalCaseTemplateUpdateDTO.builder()
                .title("Caso Clínico Atualizado")
                .description("Nova descrição")
                .moduleCode("M")
                .primaryTriggerCode("M5")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Givaldo").build())
                .build();

        when(service.updateTemplate(templateId, updateDTO, professorUser)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<ClinicalCaseTemplateResponseDTO>> response =
                controller.updateTemplate(templateId, updateDTO, professorUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Deve excluir modelo de caso clínico com sucesso")
    void shouldDeleteTemplate() {
        ResponseEntity<ApiResponse<Void>> response =
                controller.deleteTemplate(templateId, professorUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("excluído com sucesso");
        verify(service).deleteTemplate(templateId, professorUser);
    }
}
