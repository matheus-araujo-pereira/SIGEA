package br.ufs.sigea.gtt.trigger.service;

import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.gtt.module.domain.GttModule;
import br.ufs.sigea.gtt.module.repository.GttModuleRepository;
import br.ufs.sigea.gtt.trigger.domain.GttTrigger;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerCreateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerResponseDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerStatusUpdateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerUpdateDTO;
import br.ufs.sigea.gtt.trigger.mapper.GttTriggerMapper;
import br.ufs.sigea.gtt.trigger.repository.GttTriggerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GttTriggerServiceTest {

    @Mock
    private GttTriggerRepository triggerRepository;

    @Mock
    private GttModuleRepository moduleRepository;

    @Mock
    private GttTriggerMapper triggerMapper;

    @InjectMocks
    private GttTriggerService triggerService;

    private UUID triggerId;
    private UUID moduleId;
    private GttModule sampleModule;
    private GttTrigger sampleTrigger;
    private GttTriggerResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        triggerId = UUID.randomUUID();
        moduleId = UUID.randomUUID();

        sampleModule = GttModule.builder()
                .id(moduleId)
                .code("C")
                .name("Cuidados")
                .build();

        sampleTrigger = GttTrigger.builder()
                .id(triggerId)
                .module(sampleModule)
                .code("C1")
                .name("Transfusão de sangue")
                .description("Descrição do gatilho C1")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build();

        sampleResponseDTO = GttTriggerResponseDTO.builder()
                .id(triggerId)
                .moduleId(moduleId)
                .moduleCode("C")
                .moduleName("Cuidados")
                .code("C1")
                .name("Transfusão de sangue")
                .description("Descrição do gatilho C1")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Deve listar gatilhos com filtros e busca textual")
    void shouldListTriggersWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GttTrigger> page = new PageImpl<>(List.of(sampleTrigger), pageable, 1);

        when(triggerRepository.findWithFilters(eq(moduleId), eq("transfusão"), eq(true), eq(pageable))).thenReturn(page);
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        PageResponse<GttTriggerResponseDTO> result = triggerService.listTriggers(moduleId, " transfusão ", true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("C1");
    }

    @Test
    @DisplayName("Deve listar gatilhos com busca nula ou vazia")
    void shouldListTriggersWithNullOrEmptySearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GttTrigger> page = new PageImpl<>(List.of(sampleTrigger), pageable, 1);

        when(triggerRepository.findWithFilters(eq(null), eq(null), eq(null), eq(pageable))).thenReturn(page);
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        PageResponse<GttTriggerResponseDTO> result1 = triggerService.listTriggers(null, null, null, pageable);
        assertThat(result1.getContent()).hasSize(1);

        PageResponse<GttTriggerResponseDTO> result2 = triggerService.listTriggers(null, "   ", null, pageable);
        assertThat(result2.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar catálogo educacional com ou sem filtro de módulo")
    void shouldGetCatalog() {
        when(triggerRepository.findAllActiveWithModule()).thenReturn(List.of(sampleTrigger));
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        List<GttTriggerResponseDTO> all = triggerService.getCatalog(null);
        assertThat(all).hasSize(1);

        when(triggerRepository.findAllByModuleIdAndIsActiveTrue(moduleId)).thenReturn(List.of(sampleTrigger));
        List<GttTriggerResponseDTO> byModule = triggerService.getCatalog(moduleId);
        assertThat(byModule).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar gatilho por ID com sucesso")
    void shouldGetTriggerByIdSuccess() {
        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.of(sampleTrigger));
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        GttTriggerResponseDTO result = triggerService.getTriggerById(triggerId);

        assertThat(result.getId()).isEqualTo(triggerId);
        assertThat(result.getCode()).isEqualTo("C1");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar gatilho com ID inexistente")
    void shouldThrowWhenTriggerNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(triggerRepository.findByIdWithModule(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.getTriggerById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Gatilho GTT não encontrado");
    }

    @Test
    @DisplayName("Deve criar gatilho clínico com sucesso")
    void shouldCreateTriggerSuccess() {
        GttTriggerCreateDTO dto = GttTriggerCreateDTO.builder()
                .moduleId(moduleId)
                .code("c1")
                .name("Transfusão de sangue")
                .description("Descrição do gatilho")
                .build();

        GttTrigger entity = GttTrigger.builder().build();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByCodeIgnoreCase("C1")).thenReturn(false);
        when(triggerMapper.toEntity(dto)).thenReturn(entity);
        when(triggerRepository.save(entity)).thenReturn(sampleTrigger);
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        GttTriggerResponseDTO result = triggerService.createTrigger(dto);

        assertThat(result.getCode()).isEqualTo("C1");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar gatilho com módulo inexistente")
    void shouldThrowWhenModuleNotFoundOnCreateTrigger() {
        GttTriggerCreateDTO dto = GttTriggerCreateDTO.builder()
                .moduleId(moduleId)
                .code("C1")
                .name("Gatilho")
                .description("Desc")
                .build();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.createTrigger(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Módulo GTT não encontrado");
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar gatilho com código já cadastrado")
    void shouldThrowWhenTriggerCodeAlreadyExistsOnCreate() {
        GttTriggerCreateDTO dto = GttTriggerCreateDTO.builder()
                .moduleId(moduleId)
                .code("C1")
                .name("Gatilho")
                .description("Desc")
                .build();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByCodeIgnoreCase("C1")).thenReturn(true);

        assertThatThrownBy(() -> triggerService.createTrigger(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um gatilho cadastrado com o código 'C1'");
    }

    @Test
    @DisplayName("Deve atualizar gatilho existente com sucesso")
    void shouldUpdateTriggerSuccess() {
        GttTriggerUpdateDTO dto = GttTriggerUpdateDTO.builder()
                .moduleId(moduleId)
                .code("c1")
                .name("Nome Atualizado")
                .description("Descrição Atualizada")
                .build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.of(sampleTrigger));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByCodeIgnoreCaseAndIdNot("C1", triggerId)).thenReturn(false);
        when(triggerRepository.save(sampleTrigger)).thenReturn(sampleTrigger);
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        GttTriggerResponseDTO result = triggerService.updateTrigger(triggerId, dto);

        assertThat(result).isNotNull();
        verify(triggerMapper).updateEntityFromDTO(dto, sampleTrigger);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar gatilho inexistente")
    void shouldThrowWhenTriggerNotFoundOnUpdate() {
        GttTriggerUpdateDTO dto = GttTriggerUpdateDTO.builder()
                .moduleId(moduleId)
                .code("C1")
                .name("Nome")
                .description("Desc")
                .build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.updateTrigger(triggerId, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Gatilho GTT não encontrado");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar gatilho informando módulo inexistente")
    void shouldThrowWhenModuleNotFoundOnUpdateTrigger() {
        GttTriggerUpdateDTO dto = GttTriggerUpdateDTO.builder()
                .moduleId(moduleId)
                .code("C1")
                .name("Nome")
                .description("Desc")
                .build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.of(sampleTrigger));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.updateTrigger(triggerId, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Módulo GTT não encontrado");
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar gatilho com código conflitante")
    void shouldThrowWhenConflictingCodeOnUpdateTrigger() {
        GttTriggerUpdateDTO dto = GttTriggerUpdateDTO.builder()
                .moduleId(moduleId)
                .code("C2")
                .name("Nome")
                .description("Desc")
                .build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.of(sampleTrigger));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByCodeIgnoreCaseAndIdNot("C2", triggerId)).thenReturn(true);

        assertThatThrownBy(() -> triggerService.updateTrigger(triggerId, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe outro gatilho cadastrado com o código 'C2'");
    }

    @Test
    @DisplayName("Deve atualizar status do gatilho com sucesso")
    void shouldUpdateStatusTrigger() {
        GttTriggerStatusUpdateDTO dto = GttTriggerStatusUpdateDTO.builder().isActive(false).build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.of(sampleTrigger));
        when(triggerRepository.save(sampleTrigger)).thenReturn(sampleTrigger);
        when(triggerMapper.toResponseDTO(sampleTrigger)).thenReturn(sampleResponseDTO);

        GttTriggerResponseDTO result = triggerService.updateStatus(triggerId, dto);

        assertThat(result).isNotNull();
        assertThat(sampleTrigger.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar status de gatilho inexistente")
    void shouldThrowWhenTriggerNotFoundOnUpdateStatus() {
        GttTriggerStatusUpdateDTO dto = GttTriggerStatusUpdateDTO.builder().isActive(false).build();

        when(triggerRepository.findByIdWithModule(triggerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.updateStatus(triggerId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve excluir gatilho com sucesso")
    void shouldDeleteTriggerSuccess() {
        when(triggerRepository.findById(triggerId)).thenReturn(Optional.of(sampleTrigger));

        triggerService.deleteTrigger(triggerId);

        verify(triggerRepository).delete(sampleTrigger);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar excluir gatilho inexistente")
    void shouldThrowWhenDeleteTriggerNotFound() {
        when(triggerRepository.findById(triggerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> triggerService.deleteTrigger(triggerId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve testar prePersist da entidade GttTrigger")
    void shouldTestPrePersistTrigger() {
        GttTrigger trigger = new GttTrigger();
        trigger.setIsActive(null);
        trigger.setCreatedAt(null);

        trigger.prePersist();

        assertThat(trigger.getIsActive()).isTrue();
        assertThat(trigger.getCreatedAt()).isNotNull();

        OffsetDateTime date = OffsetDateTime.now().minusDays(1);
        trigger.setIsActive(false);
        trigger.setCreatedAt(date);
        trigger.prePersist();

        assertThat(trigger.getIsActive()).isFalse();
        assertThat(trigger.getCreatedAt()).isEqualTo(date);
    }
}
