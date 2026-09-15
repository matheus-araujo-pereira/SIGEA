package br.ufs.sigea.gtt.module.service;

import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.gtt.module.domain.GttModule;
import br.ufs.sigea.gtt.module.dto.GttModuleCreateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleResponseDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleStatusUpdateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleUpdateDTO;
import br.ufs.sigea.gtt.module.mapper.GttModuleMapper;
import br.ufs.sigea.gtt.module.repository.GttModuleRepository;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GttModuleServiceTest {

    @Mock
    private GttModuleRepository moduleRepository;

    @Mock
    private GttTriggerRepository triggerRepository;

    @Mock
    private GttModuleMapper moduleMapper;

    @InjectMocks
    private GttModuleService moduleService;

    private GttModule sampleModule;
    private GttModuleResponseDTO sampleResponseDTO;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleModule = GttModule.builder()
                .id(sampleId)
                .code("C")
                .name("Cuidados")
                .description("Gatilhos relacionados a cuidados gerais")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build();

        sampleResponseDTO = GttModuleResponseDTO.builder()
                .id(sampleId)
                .code("C")
                .name("Cuidados")
                .description("Gatilhos relacionados a cuidados gerais")
                .isActive(true)
                .createdAt(sampleModule.getCreatedAt())
                .triggerCount(15L)
                .build();
    }

    @Test
    @DisplayName("Deve listar módulos paginados com busca e contagem de gatilhos")
    void shouldListModulesWithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GttModule> page = new PageImpl<>(List.of(sampleModule), pageable, 1);

        when(moduleRepository.findWithFilters(eq("cuidados"), eq(true), eq(pageable))).thenReturn(page);
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(15L);

        PageResponse<GttModuleResponseDTO> result = moduleService.listModules(" cuidados ", true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("C");
        assertThat(result.getContent().get(0).getTriggerCount()).isEqualTo(15L);
    }

    @Test
    @DisplayName("Deve listar módulos paginados quando busca for nula ou vazia")
    void shouldListModulesWithNullOrEmptySearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GttModule> page = new PageImpl<>(List.of(sampleModule), pageable, 1);

        when(moduleRepository.findWithFilters(eq(null), eq(null), eq(pageable))).thenReturn(page);
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(5L);

        PageResponse<GttModuleResponseDTO> result1 = moduleService.listModules(null, null, pageable);
        assertThat(result1.getContent()).hasSize(1);

        PageResponse<GttModuleResponseDTO> result2 = moduleService.listModules("   ", null, pageable);
        assertThat(result2.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar catálogo de módulos ativos")
    void shouldGetCatalog() {
        when(moduleRepository.findAllByIsActiveTrueOrderByCodeAsc()).thenReturn(List.of(sampleModule));
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(15L);

        List<GttModuleResponseDTO> catalog = moduleService.getCatalog();

        assertThat(catalog).hasSize(1);
        assertThat(catalog.get(0).getCode()).isEqualTo("C");
    }

    @Test
    @DisplayName("Deve buscar módulo por ID com sucesso")
    void shouldGetModuleByIdSuccess() {
        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(15L);

        GttModuleResponseDTO result = moduleService.getModuleById(sampleId);

        assertThat(result.getId()).isEqualTo(sampleId);
        assertThat(result.getName()).isEqualTo("Cuidados");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void shouldThrowWhenModuleNotFoundById() {
        UUID unknownId = UUID.randomUUID();
        when(moduleRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.getModuleById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Módulo GTT não encontrado");
    }

    @Test
    @DisplayName("Deve criar novo módulo com sucesso")
    void shouldCreateModuleSuccess() {
        GttModuleCreateDTO dto = GttModuleCreateDTO.builder()
                .code("m")
                .name("Medicação")
                .description("Gatilhos farmacológicos")
                .build();

        GttModule mapped = GttModule.builder().build();
        GttModule saved = GttModule.builder().id(UUID.randomUUID()).code("M").name("Medicação").build();
        GttModuleResponseDTO resp = GttModuleResponseDTO.builder().id(saved.getId()).code("M").name("Medicação").build();

        when(moduleRepository.existsByCodeIgnoreCase("M")).thenReturn(false);
        when(moduleMapper.toEntity(dto)).thenReturn(mapped);
        when(moduleRepository.save(any(GttModule.class))).thenReturn(saved);
        when(moduleMapper.toResponseDTO(saved)).thenReturn(resp);

        GttModuleResponseDTO result = moduleService.createModule(dto);

        assertThat(result.getCode()).isEqualTo("M");
        assertThat(result.getTriggerCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Deve criar novo módulo mesmo com descrição nula")
    void shouldCreateModuleWithNullDescription() {
        GttModuleCreateDTO dto = GttModuleCreateDTO.builder()
                .code("S")
                .name("Cirúrgico")
                .description(null)
                .build();

        GttModule mapped = GttModule.builder().build();
        GttModule saved = GttModule.builder().id(UUID.randomUUID()).code("S").name("Cirúrgico").build();
        GttModuleResponseDTO resp = GttModuleResponseDTO.builder().id(saved.getId()).code("S").name("Cirúrgico").build();

        when(moduleRepository.existsByCodeIgnoreCase("S")).thenReturn(false);
        when(moduleMapper.toEntity(dto)).thenReturn(mapped);
        when(moduleRepository.save(any(GttModule.class))).thenReturn(saved);
        when(moduleMapper.toResponseDTO(saved)).thenReturn(resp);

        GttModuleResponseDTO result = moduleService.createModule(dto);

        assertThat(result.getCode()).isEqualTo("S");
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar módulo com código já existente")
    void shouldThrowWhenCreateModuleWithDuplicateCode() {
        GttModuleCreateDTO dto = GttModuleCreateDTO.builder()
                .code("C")
                .name("Cuidados")
                .build();

        when(moduleRepository.existsByCodeIgnoreCase("C")).thenReturn(true);

        assertThatThrownBy(() -> moduleService.createModule(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um módulo cadastrado com o código 'C'");
    }

    @Test
    @DisplayName("Deve atualizar módulo existente com sucesso")
    void shouldUpdateModuleSuccess() {
        GttModuleUpdateDTO dto = GttModuleUpdateDTO.builder()
                .code("c")
                .name("Cuidados Gerais")
                .description("Descrição atualizada")
                .build();

        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(moduleRepository.existsByCodeIgnoreCaseAndIdNot("C", sampleId)).thenReturn(false);
        when(moduleRepository.save(sampleModule)).thenReturn(sampleModule);
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(10L);

        GttModuleResponseDTO result = moduleService.updateModule(sampleId, dto);

        assertThat(result).isNotNull();
        verify(moduleMapper).updateEntityFromDTO(dto, sampleModule);
    }

    @Test
    @DisplayName("Deve atualizar módulo com descrição nula")
    void shouldUpdateModuleWithNullDescription() {
        GttModuleUpdateDTO dto = GttModuleUpdateDTO.builder()
                .code("C")
                .name("Cuidados Gerais")
                .description(null)
                .build();

        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(moduleRepository.existsByCodeIgnoreCaseAndIdNot("C", sampleId)).thenReturn(false);
        when(moduleRepository.save(sampleModule)).thenReturn(sampleModule);
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(10L);

        GttModuleResponseDTO result = moduleService.updateModule(sampleId, dto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar módulo com código em conflito")
    void shouldThrowWhenUpdateModuleWithConflictingCode() {
        GttModuleUpdateDTO dto = GttModuleUpdateDTO.builder()
                .code("M")
                .name("Medicação")
                .build();

        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(moduleRepository.existsByCodeIgnoreCaseAndIdNot("M", sampleId)).thenReturn(true);

        assertThatThrownBy(() -> moduleService.updateModule(sampleId, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe outro módulo com o código 'M'");
    }

    @Test
    @DisplayName("Deve alterar status do módulo com sucesso")
    void shouldUpdateStatusSuccess() {
        GttModuleStatusUpdateDTO dto = GttModuleStatusUpdateDTO.builder().isActive(false).build();

        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(moduleRepository.save(sampleModule)).thenReturn(sampleModule);
        when(moduleMapper.toResponseDTO(sampleModule)).thenReturn(sampleResponseDTO);
        when(triggerRepository.countByModuleId(sampleId)).thenReturn(15L);

        GttModuleResponseDTO result = moduleService.updateStatus(sampleId, dto);

        assertThat(result).isNotNull();
        assertThat(sampleModule.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Deve excluir módulo sem gatilhos associados")
    void shouldDeleteModuleSuccess() {
        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByModuleId(sampleId)).thenReturn(false);

        moduleService.deleteModule(sampleId);

        verify(moduleRepository).delete(sampleModule);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar excluir módulo com gatilhos associados")
    void shouldThrowWhenDeleteModuleWithTriggers() {
        when(moduleRepository.findById(sampleId)).thenReturn(Optional.of(sampleModule));
        when(triggerRepository.existsByModuleId(sampleId)).thenReturn(true);

        assertThatThrownBy(() -> moduleService.deleteModule(sampleId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pois existem gatilhos associados a ele");
    }

    @Test
    @DisplayName("Deve testar métodos de ciclo de vida prePersist da entidade GttModule")
    void shouldTestPrePersistEntity() {
        GttModule entity = new GttModule();
        entity.setIsActive(null);
        entity.setCreatedAt(null);

        entity.prePersist();

        assertThat(entity.getIsActive()).isTrue();
        assertThat(entity.getCreatedAt()).isNotNull();

        OffsetDateTime existingTime = OffsetDateTime.now().minusDays(1);
        entity.setIsActive(false);
        entity.setCreatedAt(existingTime);
        entity.prePersist();

        assertThat(entity.getIsActive()).isFalse();
        assertThat(entity.getCreatedAt()).isEqualTo(existingTime);
    }
}
