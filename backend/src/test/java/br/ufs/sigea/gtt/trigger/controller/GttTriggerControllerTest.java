package br.ufs.sigea.gtt.trigger.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerCreateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerResponseDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerStatusUpdateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerUpdateDTO;
import br.ufs.sigea.gtt.trigger.service.GttTriggerService;
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
class GttTriggerControllerTest {

    @Mock
    private GttTriggerService triggerService;

    @InjectMocks
    private GttTriggerController triggerController;

    private UUID triggerId;
    private UUID moduleId;
    private GttTriggerResponseDTO sampleDTO;

    @BeforeEach
    void setUp() {
        triggerId = UUID.randomUUID();
        moduleId = UUID.randomUUID();

        sampleDTO = GttTriggerResponseDTO.builder()
                .id(triggerId)
                .moduleId(moduleId)
                .moduleCode("C")
                .moduleName("Cuidados")
                .code("C1")
                .name("Transfusão")
                .description("Descrição")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Deve retornar catálogo educacional de gatilhos")
    void shouldReturnCatalog() {
        when(triggerService.getCatalog(moduleId)).thenReturn(List.of(sampleDTO));

        ResponseEntity<ApiResponse<List<GttTriggerResponseDTO>>> response =
                triggerController.getCatalog(moduleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar gatilhos com paginação")
    void shouldListTriggers() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponse<GttTriggerResponseDTO> pageResponse = PageResponse.from(new PageImpl<>(List.of(sampleDTO), pageable, 1));
        when(triggerService.listTriggers(moduleId, "C1", true, pageable)).thenReturn(pageResponse);

        ResponseEntity<ApiResponse<PageResponse<GttTriggerResponseDTO>>> response =
                triggerController.listTriggers(moduleId, "C1", true, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar gatilho por ID")
    void shouldGetTriggerById() {
        when(triggerService.getTriggerById(triggerId)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttTriggerResponseDTO>> response = triggerController.getTriggerById(triggerId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo(triggerId);
    }

    @Test
    @DisplayName("Deve cadastrar novo gatilho com status 201 CREATED")
    void shouldCreateTrigger() {
        GttTriggerCreateDTO request = GttTriggerCreateDTO.builder().moduleId(moduleId).code("C1").name("Transfusão").description("Desc").build();
        when(triggerService.createTrigger(request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttTriggerResponseDTO>> response = triggerController.createTrigger(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar gatilho existente")
    void shouldUpdateTrigger() {
        GttTriggerUpdateDTO request = GttTriggerUpdateDTO.builder().moduleId(moduleId).code("C1").name("Transfusão").description("Desc").build();
        when(triggerService.updateTrigger(triggerId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttTriggerResponseDTO>> response = triggerController.updateTrigger(triggerId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar status do gatilho")
    void shouldUpdateStatus() {
        GttTriggerStatusUpdateDTO request = GttTriggerStatusUpdateDTO.builder().isActive(false).build();
        when(triggerService.updateStatus(triggerId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttTriggerResponseDTO>> response = triggerController.updateStatus(triggerId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve excluir gatilho")
    void shouldDeleteTrigger() {
        ResponseEntity<ApiResponse<Void>> response = triggerController.deleteTrigger(triggerId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(triggerService).deleteTrigger(triggerId);
    }
}
