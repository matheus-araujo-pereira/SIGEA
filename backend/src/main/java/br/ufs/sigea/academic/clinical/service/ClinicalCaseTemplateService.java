package br.ufs.sigea.academic.clinical.service;

import br.ufs.sigea.academic.clinical.domain.ClinicalCaseTemplate;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateCreateDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateResponseDTO;
import br.ufs.sigea.academic.clinical.dto.ClinicalCaseTemplateUpdateDTO;
import br.ufs.sigea.academic.clinical.repository.ClinicalCaseTemplateRepository;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de regras de negócio para gestão de Modelos de Casos Clínicos Simulados.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalCaseTemplateService {

    private final ClinicalCaseTemplateRepository repository;

    /**
     * Lista todos os modelos de casos clínicos cadastrados, com filtro opcional por módulo GTT.
     *
     * @param moduleCode Código do módulo GTT (opcional)
     * @return Lista de modelos encontrados
     */
    @Transactional(readOnly = true)
    public List<ClinicalCaseTemplateResponseDTO> listTemplates(String moduleCode) {
        List<ClinicalCaseTemplate> templates;
        if (moduleCode != null && !moduleCode.isBlank()) {
            templates = repository.findByModuleCodeOrderByCreatedAtDesc(moduleCode.trim().toUpperCase());
        } else {
            templates = repository.findAllByOrderByCreatedAtDesc();
        }
        return templates.stream().map(this::mapToDTO).toList();
    }

    /**
     * Busca um modelo de caso clínico específico por identificador UUID.
     *
     * @param id Identificador do modelo
     * @return Dados detalhados do modelo
     */
    @Transactional(readOnly = true)
    public ClinicalCaseTemplateResponseDTO getTemplateById(UUID id) {
        ClinicalCaseTemplate template = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de caso clínico não encontrado: " + id));
        return mapToDTO(template);
    }

    /**
     * Cria um novo modelo de caso clínico customizado associado ao usuário logado.
     *
     * @param dto         Dados do novo modelo
     * @param currentUser Usuário docente ou administrador criador
     * @return Modelo cadastrado
     */
    @Transactional
    public ClinicalCaseTemplateResponseDTO createTemplate(ClinicalCaseTemplateCreateDTO dto, User currentUser) {
        ClinicalCaseTemplate template = ClinicalCaseTemplate.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .moduleCode(dto.getModuleCode().trim().toUpperCase())
                .primaryTriggerCode(dto.getPrimaryTriggerCode().trim().toUpperCase())
                .expectedSeverity(dto.getExpectedSeverity().trim().toUpperCase())
                .clinicalCaseData(dto.getClinicalCaseData())
                .isSystemTemplate(false)
                .createdBy(currentUser)
                .build();

        ClinicalCaseTemplate saved = repository.save(template);
        log.info("Modelo de caso clínico criado com sucesso: id={}, title='{}', user='{}'",
                saved.getId(), saved.getTitle(), currentUser.getEmail());
        return mapToDTO(saved);
    }

    /**
     * Atualiza um modelo de caso clínico existente.
     *
     * @param id          Identificador do modelo
     * @param dto         Novos dados
     * @param currentUser Usuário realizando a atualização
     * @return Modelo atualizado
     */
    @Transactional
    public ClinicalCaseTemplateResponseDTO updateTemplate(UUID id, ClinicalCaseTemplateUpdateDTO dto, User currentUser) {
        ClinicalCaseTemplate template = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de caso clínico não encontrado: " + id));

        if (template.isSystemTemplate() && currentUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Apenas administradores podem modificar modelos canônicos do sistema.");
        }

        template.setTitle(dto.getTitle().trim());
        template.setDescription(dto.getDescription().trim());
        template.setModuleCode(dto.getModuleCode().trim().toUpperCase());
        template.setPrimaryTriggerCode(dto.getPrimaryTriggerCode().trim().toUpperCase());
        template.setExpectedSeverity(dto.getExpectedSeverity().trim().toUpperCase());
        template.setClinicalCaseData(dto.getClinicalCaseData());

        ClinicalCaseTemplate updated = repository.save(template);
        log.info("Modelo de caso clínico atualizado: id={}, title='{}'", updated.getId(), updated.getTitle());
        return mapToDTO(updated);
    }

    /**
     * Remove um modelo de caso clínico existente.
     *
     * @param id          Identificador do modelo
     * @param currentUser Usuário realizando a exclusão
     */
    @Transactional
    public void deleteTemplate(UUID id, User currentUser) {
        ClinicalCaseTemplate template = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de caso clínico não encontrado: " + id));

        if (template.isSystemTemplate() && currentUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Apenas administradores podem excluir modelos canônicos do sistema.");
        }

        repository.delete(template);
        log.info("Modelo de caso clínico excluído com sucesso: id={}", id);
    }

    /**
     * Mapeia a entidade ClinicalCaseTemplate para seu respectivo DTO de resposta.
     */
    private ClinicalCaseTemplateResponseDTO mapToDTO(ClinicalCaseTemplate template) {
        String createdByName = template.getCreatedBy() != null
                ? template.getCreatedBy().getFullName()
                : (template.isSystemTemplate() ? "Sistema SIGEA (Canônico)" : "Não informado");

        return ClinicalCaseTemplateResponseDTO.builder()
                .id(template.getId())
                .title(template.getTitle())
                .description(template.getDescription())
                .moduleCode(template.getModuleCode())
                .primaryTriggerCode(template.getPrimaryTriggerCode())
                .expectedSeverity(template.getExpectedSeverity())
                .clinicalCaseData(template.getClinicalCaseData())
                .isSystemTemplate(template.isSystemTemplate())
                .createdByName(createdByName)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
