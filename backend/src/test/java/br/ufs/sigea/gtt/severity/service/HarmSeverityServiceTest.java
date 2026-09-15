package br.ufs.sigea.gtt.severity.service;

import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.gtt.severity.domain.HarmSeverity;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityCreateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityResponseDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityStatusUpdateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityUpdateDTO;
import br.ufs.sigea.gtt.severity.mapper.HarmSeverityMapper;
import br.ufs.sigea.gtt.severity.repository.HarmSeverityRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarmSeverityServiceTest {

    @Mock
    private HarmSeverityRepository severityRepository;

    @Mock
    private HarmSeverityMapper severityMapper;

    @InjectMocks
    private HarmSeverityService severityService;

    private UUID severityId;
    private HarmSeverity sampleSeverity;
    private HarmSeverityResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        severityId = UUID.randomUUID();

        sampleSeverity = HarmSeverity.builder()
                .id(severityId)
                .categoryLetter("E")
                .name("Dano temporário com intervenção")
                .description("Exigiu intervenção")
                .isHarm(true)
                .isActive(true)
                .build();

        sampleResponseDTO = HarmSeverityResponseDTO.builder()
                .id(severityId)
                .categoryLetter("E")
                .name("Dano temporário com intervenção")
                .description("Exigiu intervenção")
                .isHarm(true)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Deve listar gravidades com filtros e busca")
    void shouldListSeveritiesWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<HarmSeverity> page = new PageImpl<>(List.of(sampleSeverity), pageable, 1);

        when(severityRepository.findWithFilters(eq("dano"), eq(true), eq(true), eq(pageable))).thenReturn(page);
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        PageResponse<HarmSeverityResponseDTO> result = severityService.listSeverities(" dano ", true, true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategoryLetter()).isEqualTo("E");
    }

    @Test
    @DisplayName("Deve listar gravidades com busca nula ou vazia")
    void shouldListSeveritiesWithNullOrEmptySearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<HarmSeverity> page = new PageImpl<>(List.of(sampleSeverity), pageable, 1);

        when(severityRepository.findWithFilters(eq(null), eq(null), eq(null), eq(pageable))).thenReturn(page);
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        PageResponse<HarmSeverityResponseDTO> result1 = severityService.listSeverities(null, null, null, pageable);
        assertThat(result1.getContent()).hasSize(1);

        PageResponse<HarmSeverityResponseDTO> result2 = severityService.listSeverities("   ", null, null, pageable);
        assertThat(result2.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar guia interativo de gravidades ativas")
    void shouldGetGuide() {
        when(severityRepository.findAllByIsActiveTrueOrderByCategoryLetterAsc()).thenReturn(List.of(sampleSeverity));
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        List<HarmSeverityResponseDTO> guide = severityService.getGuide();

        assertThat(guide).hasSize(1);
        assertThat(guide.get(0).getCategoryLetter()).isEqualTo("E");
    }

    @Test
    @DisplayName("Deve buscar gravidade por ID com sucesso")
    void shouldGetSeverityByIdSuccess() {
        when(severityRepository.findById(severityId)).thenReturn(Optional.of(sampleSeverity));
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        HarmSeverityResponseDTO result = severityService.getSeverityById(severityId);

        assertThat(result.getId()).isEqualTo(severityId);
        assertThat(result.getCategoryLetter()).isEqualTo("E");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void shouldThrowWhenSeverityNotFoundById() {
        UUID unknownId = UUID.randomUUID();
        when(severityRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> severityService.getSeverityById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoria de gravidade não encontrada");
    }

    @Test
    @DisplayName("Deve criar categoria de gravidade com sucesso")
    void shouldCreateSeveritySuccess() {
        HarmSeverityCreateDTO dto = HarmSeverityCreateDTO.builder()
                .categoryLetter("f")
                .name("Prolongamento de hospitalização")
                .description("Descrição F")
                .isHarm(true)
                .build();

        HarmSeverity mapped = HarmSeverity.builder().build();

        when(severityRepository.existsByCategoryLetterIgnoreCase("F")).thenReturn(false);
        when(severityMapper.toEntity(dto)).thenReturn(mapped);
        when(severityRepository.save(mapped)).thenReturn(sampleSeverity);
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        HarmSeverityResponseDTO result = severityService.createSeverity(dto);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar gravidade com letra já existente")
    void shouldThrowWhenCategoryLetterAlreadyExistsOnCreate() {
        HarmSeverityCreateDTO dto = HarmSeverityCreateDTO.builder()
                .categoryLetter("E")
                .name("Dano")
                .description("Desc")
                .isHarm(true)
                .build();

        when(severityRepository.existsByCategoryLetterIgnoreCase("E")).thenReturn(true);

        assertThatThrownBy(() -> severityService.createSeverity(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe uma categoria de gravidade cadastrada com a letra 'E'");
    }

    @Test
    @DisplayName("Deve atualizar gravidade existente com sucesso")
    void shouldUpdateSeveritySuccess() {
        HarmSeverityUpdateDTO dto = HarmSeverityUpdateDTO.builder()
                .categoryLetter("e")
                .name("Novo Nome")
                .description("Nova Descrição")
                .isHarm(true)
                .build();

        when(severityRepository.findById(severityId)).thenReturn(Optional.of(sampleSeverity));
        when(severityRepository.existsByCategoryLetterIgnoreCaseAndIdNot("E", severityId)).thenReturn(false);
        when(severityRepository.save(sampleSeverity)).thenReturn(sampleSeverity);
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        HarmSeverityResponseDTO result = severityService.updateSeverity(severityId, dto);

        assertThat(result).isNotNull();
        verify(severityMapper).updateEntityFromDTO(dto, sampleSeverity);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar gravidade inexistente")
    void shouldThrowWhenSeverityNotFoundOnUpdate() {
        HarmSeverityUpdateDTO dto = HarmSeverityUpdateDTO.builder().categoryLetter("E").name("N").description("D").isHarm(true).build();
        when(severityRepository.findById(severityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> severityService.updateSeverity(severityId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar gravidade com letra conflitante")
    void shouldThrowWhenConflictingLetterOnUpdateSeverity() {
        HarmSeverityUpdateDTO dto = HarmSeverityUpdateDTO.builder()
                .categoryLetter("F")
                .name("Nome")
                .description("Desc")
                .isHarm(true)
                .build();

        when(severityRepository.findById(severityId)).thenReturn(Optional.of(sampleSeverity));
        when(severityRepository.existsByCategoryLetterIgnoreCaseAndIdNot("F", severityId)).thenReturn(true);

        assertThatThrownBy(() -> severityService.updateSeverity(severityId, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe outra categoria de gravidade com a letra 'F'");
    }

    @Test
    @DisplayName("Deve alterar status de gravidade com sucesso")
    void shouldUpdateStatusSuccess() {
        HarmSeverityStatusUpdateDTO dto = HarmSeverityStatusUpdateDTO.builder().isActive(false).build();

        when(severityRepository.findById(severityId)).thenReturn(Optional.of(sampleSeverity));
        when(severityRepository.save(sampleSeverity)).thenReturn(sampleSeverity);
        when(severityMapper.toResponseDTO(sampleSeverity)).thenReturn(sampleResponseDTO);

        HarmSeverityResponseDTO result = severityService.updateStatus(severityId, dto);

        assertThat(result).isNotNull();
        assertThat(sampleSeverity.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao alterar status de gravidade inexistente")
    void shouldThrowWhenSeverityNotFoundOnUpdateStatus() {
        HarmSeverityStatusUpdateDTO dto = HarmSeverityStatusUpdateDTO.builder().isActive(false).build();
        when(severityRepository.findById(severityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> severityService.updateStatus(severityId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve excluir categoria de gravidade com sucesso")
    void shouldDeleteSeveritySuccess() {
        when(severityRepository.findById(severityId)).thenReturn(Optional.of(sampleSeverity));

        severityService.deleteSeverity(severityId);

        verify(severityRepository).delete(sampleSeverity);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao excluir gravidade inexistente")
    void shouldThrowWhenDeleteSeverityNotFound() {
        when(severityRepository.findById(severityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> severityService.deleteSeverity(severityId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve testar prePersist da entidade HarmSeverity")
    void shouldTestPrePersistHarmSeverity() {
        HarmSeverity severity = new HarmSeverity();
        severity.setIsActive(null);
        severity.setCategoryLetter(null);

        severity.prePersist();

        assertThat(severity.getIsActive()).isTrue();

        severity.setIsActive(false);
        severity.setCategoryLetter("  e  ");
        severity.prePersist();

        assertThat(severity.getIsActive()).isFalse();
        assertThat(severity.getCategoryLetter()).isEqualTo("E");
    }
}
