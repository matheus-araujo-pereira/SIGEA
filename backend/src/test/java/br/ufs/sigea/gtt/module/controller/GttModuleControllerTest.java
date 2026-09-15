package br.ufs.sigea.gtt.module.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.gtt.module.dto.GttModuleCreateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleResponseDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleStatusUpdateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleUpdateDTO;
import br.ufs.sigea.gtt.module.service.GttModuleService;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GttModuleControllerTest {

    @Mock
    private GttModuleService moduleService;

    @InjectMocks
    private GttModuleController moduleController;

    private UUID sampleId;
    private GttModuleResponseDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleDTO = GttModuleResponseDTO.builder()
                .id(sampleId)
                .code("C")
                .name("Cuidados")
                .description("Cuidados gerais")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .triggerCount(15L)
                .build();
    }

    @Test
    @DisplayName("Deve retornar catálogo de módulos")
    void shouldReturnCatalog() {
        when(moduleService.getCatalog()).thenReturn(List.of(sampleDTO));

        ResponseEntity<ApiResponse<List<GttModuleResponseDTO>>> response = moduleController.getCatalog();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar módulos com paginação")
    void shouldListModules() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponse<GttModuleResponseDTO> pageResponse = PageResponse.from(new PageImpl<>(List.of(sampleDTO), pageable, 1));
        when(moduleService.listModules("C", true, pageable)).thenReturn(pageResponse);

        ResponseEntity<ApiResponse<PageResponse<GttModuleResponseDTO>>> response =
                moduleController.listModules("C", true, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar módulo por ID")
    void shouldGetModuleById() {
        when(moduleService.getModuleById(sampleId)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttModuleResponseDTO>> response = moduleController.getModuleById(sampleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo(sampleId);
    }

    @Test
    @DisplayName("Deve criar novo módulo com status 201 CREATED")
    void shouldCreateModule() {
        GttModuleCreateDTO request = GttModuleCreateDTO.builder().code("M").name("Medicação").build();
        when(moduleService.createModule(request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttModuleResponseDTO>> response = moduleController.createModule(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar módulo existente")
    void shouldUpdateModule() {
        GttModuleUpdateDTO request = GttModuleUpdateDTO.builder().code("C").name("Cuidados").build();
        when(moduleService.updateModule(sampleId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttModuleResponseDTO>> response = moduleController.updateModule(sampleId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve atualizar status do módulo")
    void shouldUpdateStatus() {
        GttModuleStatusUpdateDTO request = GttModuleStatusUpdateDTO.builder().isActive(false).build();
        when(moduleService.updateStatus(sampleId, request)).thenReturn(sampleDTO);

        ResponseEntity<ApiResponse<GttModuleResponseDTO>> response = moduleController.updateStatus(sampleId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(sampleDTO);
    }

    @Test
    @DisplayName("Deve excluir módulo com sucesso")
    void shouldDeleteModule() {
        ResponseEntity<ApiResponse<Void>> response = moduleController.deleteModule(sampleId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(moduleService).deleteModule(sampleId);
    }
}
