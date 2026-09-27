package br.ufs.sigea.academic.clinical.service;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.clinical.domain.ClinicalCaseTemplate;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateCreateDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateResponseDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateUpdateDTO;
import br.ufs.sigea.academic.clinical.repository.ClinicalCaseTemplateRepository;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicalCaseTemplateServiceTest {

    @Mock
    private ClinicalCaseTemplateRepository repository;

    @InjectMocks
    private ClinicalCaseTemplateService service;

    private User adminUser;
    private User professorUser;
    private ClinicalCaseTemplate systemTemplate;
    private ClinicalCaseTemplate customTemplate;
    private UUID templateId;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();

        adminUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Matheus Araujo Pereira")
                .email("matheusaraujopereira@academico.ufs.br")
                .role(UserRole.ADMIN)
                .build();

        professorUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Profª. Drª. Ana Waleska")
                .email("anawaleska@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        systemTemplate = ClinicalCaseTemplate.builder()
                .id(templateId)
                .title("Caso Clínico 01: Vancomicina")
                .description("Descrição do caso canônico")
                .moduleCode("M")
                .primaryTriggerCode("M5")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Givaldo").build())
                .isSystemTemplate(true)
                .createdBy(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        customTemplate = ClinicalCaseTemplate.builder()
                .id(UUID.randomUUID())
                .title("Caso Clínico Customizado: Sedação")
                .description("Descrição customizada")
                .moduleCode("M")
                .primaryTriggerCode("M8")
                .expectedSeverity("E")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Sebastião").build())
                .isSystemTemplate(false)
                .createdBy(professorUser)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os modelos quando nenhum módulo for informado")
    void listTemplates_withoutModule_shouldReturnAll() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(systemTemplate, customTemplate));

        List<ClinicalCaseTemplateResponseDTO> result = service.listTemplates(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Caso Clínico 01: Vancomicina");
        assertThat(result.get(0).getCreatedByName()).isEqualTo("Sistema SIGEA (Canônico)");
        assertThat(result.get(1).getCreatedByName()).isEqualTo("Profª. Drª. Ana Waleska");
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Deve listar todos os modelos quando módulo estiver vazio ou em branco")
    void listTemplates_withBlankModule_shouldReturnAll() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(systemTemplate));

        List<ClinicalCaseTemplateResponseDTO> result = service.listTemplates("   ");

        assertThat(result).hasSize(1);
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Deve filtrar modelos por código de módulo em maiúsculas")
    void listTemplates_withModule_shouldFilterByModule() {
        when(repository.findByModuleCodeOrderByCreatedAtDesc("M")).thenReturn(List.of(systemTemplate));

        List<ClinicalCaseTemplateResponseDTO> result = service.listTemplates("m ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModuleCode()).isEqualTo("M");
        verify(repository).findByModuleCodeOrderByCreatedAtDesc("M");
    }

    @Test
    @DisplayName("Deve buscar modelo por ID com sucesso")
    void getTemplateById_whenExists_shouldReturnDTO() {
        when(repository.findById(templateId)).thenReturn(Optional.of(systemTemplate));

        ClinicalCaseTemplateResponseDTO result = service.getTemplateById(templateId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(templateId);
        assertThat(result.getTitle()).isEqualTo("Caso Clínico 01: Vancomicina");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar modelo inexistente")
    void getTemplateById_whenNotFound_shouldThrow() {
        UUID unknownId = UUID.randomUUID();
        when(repository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTemplateById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Modelo de caso clínico não encontrado");
    }

    @Test
    @DisplayName("Deve cadastrar novo modelo customizado com sucesso")
    void createTemplate_shouldPersistAndReturnDTO() {
        ClinicalCaseTemplateCreateDTO dto = ClinicalCaseTemplateCreateDTO.builder()
                .title("Novo Caso Clínico")
                .description("Descrição do novo caso")
                .moduleCode("c")
                .primaryTriggerCode("c7")
                .expectedSeverity("f")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Maria").build())
                .build();

        when(repository.save(any(ClinicalCaseTemplate.class))).thenAnswer(invocation -> {
            ClinicalCaseTemplate entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        ClinicalCaseTemplateResponseDTO result = service.createTemplate(dto, professorUser);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Novo Caso Clínico");
        assertThat(result.getModuleCode()).isEqualTo("C");
        assertThat(result.getPrimaryTriggerCode()).isEqualTo("C7");
        assertThat(result.getExpectedSeverity()).isEqualTo("F");
        assertThat(result.isSystemTemplate()).isFalse();
        assertThat(result.getCreatedByName()).isEqualTo("Profª. Drª. Ana Waleska");
        verify(repository).save(any(ClinicalCaseTemplate.class));
    }

    @Test
    @DisplayName("Deve atualizar modelo customizado com sucesso")
    void updateTemplate_whenCustomTemplate_shouldUpdate() {
        ClinicalCaseTemplateUpdateDTO dto = ClinicalCaseTemplateUpdateDTO.builder()
                .title("Caso Clínico Atualizado")
                .description("Nova descrição")
                .moduleCode("m")
                .primaryTriggerCode("m1")
                .expectedSeverity("e")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Carlos").build())
                .build();

        when(repository.findById(customTemplate.getId())).thenReturn(Optional.of(customTemplate));
        when(repository.save(any(ClinicalCaseTemplate.class))).thenReturn(customTemplate);

        ClinicalCaseTemplateResponseDTO result = service.updateTemplate(customTemplate.getId(), dto, professorUser);

        assertThat(result).isNotNull();
        assertThat(customTemplate.getTitle()).isEqualTo("Caso Clínico Atualizado");
        assertThat(customTemplate.getModuleCode()).isEqualTo("M");
        assertThat(customTemplate.getPrimaryTriggerCode()).isEqualTo("M1");
        assertThat(customTemplate.getExpectedSeverity()).isEqualTo("E");
        verify(repository).save(customTemplate);
    }

    @Test
    @DisplayName("Deve permitir que administrador atualize modelo canônico do sistema")
    void updateTemplate_whenSystemTemplateAsAdmin_shouldAllow() {
        ClinicalCaseTemplateUpdateDTO dto = ClinicalCaseTemplateUpdateDTO.builder()
                .title("Vancomicina com Detalhes")
                .description("Nova descrição canônica")
                .moduleCode("M")
                .primaryTriggerCode("M5")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Givaldo").build())
                .build();

        when(repository.findById(templateId)).thenReturn(Optional.of(systemTemplate));
        when(repository.save(any(ClinicalCaseTemplate.class))).thenReturn(systemTemplate);

        ClinicalCaseTemplateResponseDTO result = service.updateTemplate(templateId, dto, adminUser);

        assertThat(result).isNotNull();
        assertThat(systemTemplate.getTitle()).isEqualTo("Vancomicina com Detalhes");
        verify(repository).save(systemTemplate);
    }

    @Test
    @DisplayName("Deve bloquear professor ao tentar alterar modelo canônico do sistema")
    void updateTemplate_whenSystemTemplateAsProfessor_shouldThrow() {
        ClinicalCaseTemplateUpdateDTO dto = ClinicalCaseTemplateUpdateDTO.builder()
                .title("Alteração Bloqueada")
                .description("Tentativa")
                .moduleCode("M")
                .primaryTriggerCode("M5")
                .expectedSeverity("F")
                .clinicalCaseData(ClinicalCaseData.builder().patientName("Givaldo").build())
                .build();

        when(repository.findById(templateId)).thenReturn(Optional.of(systemTemplate));

        assertThatThrownBy(() -> service.updateTemplate(templateId, dto, professorUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Apenas administradores podem modificar modelos canônicos");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar modelo inexistente")
    void updateTemplate_whenNotFound_shouldThrow() {
        UUID unknownId = UUID.randomUUID();
        when(repository.findById(unknownId)).thenReturn(Optional.empty());

        ClinicalCaseTemplateUpdateDTO dto = ClinicalCaseTemplateUpdateDTO.builder().build();

        assertThatThrownBy(() -> service.updateTemplate(unknownId, dto, adminUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve excluir modelo customizado com sucesso")
    void deleteTemplate_whenCustomTemplate_shouldDelete() {
        when(repository.findById(customTemplate.getId())).thenReturn(Optional.of(customTemplate));

        service.deleteTemplate(customTemplate.getId(), professorUser);

        verify(repository).delete(customTemplate);
    }

    @Test
    @DisplayName("Deve permitir que administrador exclua modelo canônico do sistema")
    void deleteTemplate_whenSystemTemplateAsAdmin_shouldAllow() {
        when(repository.findById(templateId)).thenReturn(Optional.of(systemTemplate));

        service.deleteTemplate(templateId, adminUser);

        verify(repository).delete(systemTemplate);
    }

    @Test
    @DisplayName("Deve bloquear professor ao tentar excluir modelo canônico do sistema")
    void deleteTemplate_whenSystemTemplateAsProfessor_shouldThrow() {
        when(repository.findById(templateId)).thenReturn(Optional.of(systemTemplate));

        assertThatThrownBy(() -> service.deleteTemplate(templateId, professorUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Apenas administradores podem excluir modelos canônicos");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar excluir modelo inexistente")
    void deleteTemplate_whenNotFound_shouldThrow() {
        UUID unknownId = UUID.randomUUID();
        when(repository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTemplate(unknownId, adminUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve formatar nome do criador como 'Não informado' quando não for do sistema e createdBy for nulo")
    void mapToDTO_whenNotSystemAndCreatedByNull_shouldReturnNotReported() {
        ClinicalCaseTemplate orphan = ClinicalCaseTemplate.builder()
                .id(UUID.randomUUID())
                .title("Órfão")
                .description("Desc")
                .moduleCode("M")
                .primaryTriggerCode("M1")
                .expectedSeverity("E")
                .isSystemTemplate(false)
                .createdBy(null)
                .build();

        when(repository.findById(orphan.getId())).thenReturn(Optional.of(orphan));

        ClinicalCaseTemplateResponseDTO result = service.getTemplateById(orphan.getId());

        assertThat(result.getCreatedByName()).isEqualTo("Não informado");
    }
}
