package br.ufs.sigea.academic.clinical;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.clinical.domain.ClinicalCaseTemplate;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateCreateDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateResponseDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateUpdateDTO;
import br.ufs.sigea.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClinicalCaseTemplateDomainAndDtoTest {

    @Test
    @DisplayName("Deve testar a entidade ClinicalCaseTemplate e seus callbacks de ciclo de vida")
    void testClinicalCaseTemplateEntity() {
        ClinicalCaseTemplate template = new ClinicalCaseTemplate();
        UUID id = UUID.randomUUID();
        User user = new User();
        ClinicalCaseData data = new ClinicalCaseData();

        template.setId(id);
        template.setTitle("Título");
        template.setDescription("Descrição");
        template.setModuleCode("M");
        template.setPrimaryTriggerCode("M1");
        template.setExpectedSeverity("E");
        template.setClinicalCaseData(data);
        template.setSystemTemplate(true);
        template.setCreatedBy(user);
        template.setCreatedAt(Instant.now());
        template.setUpdatedAt(Instant.now());

        assertThat(template.getId()).isEqualTo(id);
        assertThat(template.getTitle()).isEqualTo("Título");
        assertThat(template.getDescription()).isEqualTo("Descrição");
        assertThat(template.getModuleCode()).isEqualTo("M");
        assertThat(template.getPrimaryTriggerCode()).isEqualTo("M1");
        assertThat(template.getExpectedSeverity()).isEqualTo("E");
        assertThat(template.getClinicalCaseData()).isEqualTo(data);
        assertThat(template.isSystemTemplate()).isTrue();
        assertThat(template.getCreatedBy()).isEqualTo(user);
        assertThat(template.getCreatedAt()).isNotNull();
        assertThat(template.getUpdatedAt()).isNotNull();

        // Teste callbacks quando nulos
        ClinicalCaseTemplate newTemplate = new ClinicalCaseTemplate();
        newTemplate.prePersist();
        assertThat(newTemplate.getCreatedAt()).isNotNull();
        assertThat(newTemplate.getUpdatedAt()).isNotNull();

        // Teste callbacks quando já inicializados (cobre branches != null)
        Instant existingTime = Instant.now().minusSeconds(3600);
        ClinicalCaseTemplate existingTemplate = new ClinicalCaseTemplate();
        existingTemplate.setCreatedAt(existingTime);
        existingTemplate.setUpdatedAt(existingTime);
        existingTemplate.prePersist();
        assertThat(existingTemplate.getCreatedAt()).isEqualTo(existingTime);
        assertThat(existingTemplate.getUpdatedAt()).isEqualTo(existingTime);

        newTemplate.preUpdate();
        assertThat(newTemplate.getUpdatedAt()).isNotNull();

        // Teste construtor e builder
        ClinicalCaseTemplate built = ClinicalCaseTemplate.builder()
                .id(id)
                .title("Título")
                .description("Desc")
                .moduleCode("C")
                .primaryTriggerCode("C1")
                .expectedSeverity("F")
                .clinicalCaseData(data)
                .isSystemTemplate(false)
                .createdBy(user)
                .build();
        assertThat(built.getTitle()).isEqualTo("Título");
    }

    @Test
    @DisplayName("Deve testar ClinicalCaseTemplateResponseDTO")
    void testResponseDTO() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        ClinicalCaseData data = new ClinicalCaseData();

        ClinicalCaseTemplateResponseDTO dto = ClinicalCaseTemplateResponseDTO.builder()
                .id(id)
                .title("Título")
                .description("Descrição")
                .moduleCode("S")
                .primaryTriggerCode("S1")
                .expectedSeverity("G")
                .clinicalCaseData(data)
                .isSystemTemplate(true)
                .createdByName("Admin")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getTitle()).isEqualTo("Título");
        assertThat(dto.getDescription()).isEqualTo("Descrição");
        assertThat(dto.getModuleCode()).isEqualTo("S");
        assertThat(dto.getPrimaryTriggerCode()).isEqualTo("S1");
        assertThat(dto.getExpectedSeverity()).isEqualTo("G");
        assertThat(dto.getClinicalCaseData()).isEqualTo(data);
        assertThat(dto.isSystemTemplate()).isTrue();
        assertThat(dto.getCreatedByName()).isEqualTo("Admin");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);

        ClinicalCaseTemplateResponseDTO noArgs = new ClinicalCaseTemplateResponseDTO();
        noArgs.setTitle("Teste");
        assertThat(noArgs.getTitle()).isEqualTo("Teste");
        assertThat(dto.toString()).contains("Título");
    }

    @Test
    @DisplayName("Deve testar ClinicalCaseTemplateCreateDTO e UpdateDTO")
    void testCreateAndUpdateDTO() {
        ClinicalCaseData data = new ClinicalCaseData();

        ClinicalCaseTemplateCreateDTO create = ClinicalCaseTemplateCreateDTO.builder()
                .title("Criar")
                .description("Desc")
                .moduleCode("I")
                .primaryTriggerCode("I1")
                .expectedSeverity("H")
                .clinicalCaseData(data)
                .build();

        assertThat(create.getTitle()).isEqualTo("Criar");
        assertThat(create.getDescription()).isEqualTo("Desc");
        assertThat(create.getModuleCode()).isEqualTo("I");
        assertThat(create.getPrimaryTriggerCode()).isEqualTo("I1");
        assertThat(create.getExpectedSeverity()).isEqualTo("H");
        assertThat(create.getClinicalCaseData()).isEqualTo(data);

        ClinicalCaseTemplateCreateDTO emptyCreate = new ClinicalCaseTemplateCreateDTO();
        emptyCreate.setTitle("Vazio");
        assertThat(emptyCreate.getTitle()).isEqualTo("Vazio");
        assertThat(create.toString()).contains("Criar");

        ClinicalCaseTemplateUpdateDTO update = ClinicalCaseTemplateUpdateDTO.builder()
                .title("Atualizar")
                .description("Desc Alt")
                .moduleCode("P")
                .primaryTriggerCode("P1")
                .expectedSeverity("I")
                .clinicalCaseData(data)
                .build();

        assertThat(update.getTitle()).isEqualTo("Atualizar");
        assertThat(update.getDescription()).isEqualTo("Desc Alt");
        assertThat(update.getModuleCode()).isEqualTo("P");
        assertThat(update.getPrimaryTriggerCode()).isEqualTo("P1");
        assertThat(update.getExpectedSeverity()).isEqualTo("I");
        assertThat(update.getClinicalCaseData()).isEqualTo(data);

        ClinicalCaseTemplateUpdateDTO emptyUpdate = new ClinicalCaseTemplateUpdateDTO();
        emptyUpdate.setTitle("Vazio Alt");
        assertThat(emptyUpdate.getTitle()).isEqualTo("Vazio Alt");
        assertThat(update.toString()).contains("Atualizar");
    }
}
