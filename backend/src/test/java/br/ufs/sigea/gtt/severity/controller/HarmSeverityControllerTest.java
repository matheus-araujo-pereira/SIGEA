package br.ufs.sigea.gtt.severity.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityCreateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityResponseDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityStatusUpdateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityUpdateDTO;
import br.ufs.sigea.gtt.severity.service.HarmSeverityService;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarmSeverityControllerTest {

    @Mock
    private HarmSeverityService severityService;

    @InjectMocks
    private HarmSeverityController severityController;

    private UUID severityId;
    private HarmSeverityResponseDTO sampleDTO;

    @BeforeEach
    void setUp() {
        severityId = UUID.randomUUID();
        sampleDTO = HarmSeverityResponseDTO.builder()
                .id(severityId)
                .categoryLetter("E")
                .name("Dano temporário")
                .description("Descrição")
                .isHarm(true)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Deve retornar guia interativo de gravidades")
    void shouldReturnGuide() {
        when(severityService.getGuide()).thenReturn(List.of(sampleDTO));

        ResponseEntity<ApiResponse<List<HarmSeverityResponseDTO>>> response = severityController.getGuide();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar gravidades com paginação")
    void shouldListSeverities() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponse<HarmSeverityResponseDTO> pageResponse = PageResponse.from(new PageImpl<>(List.of(sampleDTO), pageable, 1));
        when(severityService.listSeverities("E", true, true, pageable)).thenReturn(pageResponse);

        ResponseEntity<ApiResponse<PageResponse<HarmSeverityResponseDTO>>> response =
                severityController.listSeverities("E", true, true, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar gravidade por ID")
    void shouldGetSeverityById() {
        when(severityService.getSeverityById(severityId)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> response = severityController.getSeverityById(severityId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo(severityId);
    }

    @Test
    @DisplayName("Deve criar gravidade com status 201 CREATED")
    void shouldCreateSeverity() {
        HarmSeverityCreateDTO request = HarmSeverityCreateDTO.builder().categoryLetter("E").name("Dano").description("Desc").isHarm(true).build();
        when(severityService.createSeverity(request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> response = severityController.createSeverity(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar gravidade existente")
    void shouldUpdateSeverity() {
        HarmSeverityUpdateDTO request = HarmSeverityUpdateDTO.builder().categoryLetter("E").name("Dano").description("Desc").isHarm(true).build();
        when(severityService.updateSeverity(severityId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> response = severityController.updateSeverity(severityId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar status da gravidade")
    void shouldUpdateStatus() {
        HarmSeverityStatusUpdateDTO request = HarmSeverityStatusUpdateDTO.builder().isActive(false).build();
        when(severityService.updateStatus(severityId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<HarmSeverityResponseDTO>> response = severityController.updateStatus(severityId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve excluir gravidade")
    void shouldDeleteSeverity() {
        ResponseEntity<ApiResponse<Void>> response = severityController.deleteSeverity(severityId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(severityService).deleteSeverity(severityId);
    }
}
