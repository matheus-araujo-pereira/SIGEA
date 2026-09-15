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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de regras de negócio para a gestão de Gatilhos (Triggers) do IHI-GTT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GttTriggerService {

    private final GttTriggerRepository triggerRepository;
    private final GttModuleRepository moduleRepository;
    private final GttTriggerMapper triggerMapper;

    /**
     * Lista gatilhos com paginação padrão de 10 itens e filtros por módulo, busca textual e status.
     *
     * @param moduleId Filtro opcional por ID do módulo
     * @param search   Termo de busca por código, nome ou descrição
     * @param isActive Filtro opcional por status ativo
     * @param pageable Configuração de paginação
     * @return Página de gatilhos
     */
    @Transactional(readOnly = true)
    public PageResponse<GttTriggerResponseDTO> listTriggers(UUID moduleId, String search, Boolean isActive, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<GttTrigger> page = triggerRepository.findWithFilters(moduleId, query, isActive, pageable);
        return PageResponse.from(page.map(triggerMapper::toResponseDTO));
    }

    /**
     * Retorna lista de gatilhos ativos para o guia educacional de consulta rápida.
     *
     * @param moduleId Filtro opcional por módulo
     * @return Lista de gatilhos ativos
     */
    @Transactional(readOnly = true)
    public List<GttTriggerResponseDTO> getCatalog(UUID moduleId) {
        List<GttTrigger> triggers = (moduleId != null)
                ? triggerRepository.findAllByModuleIdAndIsActiveTrue(moduleId)
                : triggerRepository.findAllActiveWithModule();

        return triggers.stream().map(triggerMapper::toResponseDTO).toList();
    }

    /**
     * Recupera um gatilho específico pelo seu ID.
     *
     * @param id ID do gatilho
     * @return DTO com os dados do gatilho
     * @throws ResourceNotFoundException se não for encontrado
     */
    @Transactional(readOnly = true)
    public GttTriggerResponseDTO getTriggerById(UUID id) {
        GttTrigger trigger = triggerRepository.findByIdWithModule(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gatilho GTT não encontrado com o ID: " + id));
        return triggerMapper.toResponseDTO(trigger);
    }

    /**
     * Cadastra um novo gatilho clínico vinculado a um módulo ativo.
     *
     * @param dto Dados do novo gatilho
     * @return DTO do gatilho cadastrado
     * @throws ResourceNotFoundException se o módulo não existir
     * @throws BusinessException         se o código do gatilho já estiver em uso
     */
    @Transactional
    public GttTriggerResponseDTO createTrigger(GttTriggerCreateDTO dto) {
        GttModule module = moduleRepository.findById(dto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Módulo GTT não encontrado com o ID: " + dto.getModuleId()));

        String normalizedCode = dto.getCode().trim().toUpperCase();

        if (triggerRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BusinessException("Já existe um gatilho cadastrado com o código '" + normalizedCode + "'.");
        }

        GttTrigger trigger = triggerMapper.toEntity(dto);
        trigger.setModule(module);
        trigger.setCode(normalizedCode);
        trigger.setName(dto.getName().trim());
        trigger.setDescription(dto.getDescription().trim());

        GttTrigger saved = triggerRepository.save(trigger);
        log.info("Gatilho GTT cadastrado com sucesso: id={}, code={}, module={}", saved.getId(), saved.getCode(), module.getCode());

        return triggerMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza os dados de um gatilho existente.
     *
     * @param id  ID do gatilho
     * @param dto Novos dados
     * @return DTO atualizado
     * @throws ResourceNotFoundException se o gatilho ou o módulo informado não existir
     * @throws BusinessException         se houver colisão de código
     */
    @Transactional
    public GttTriggerResponseDTO updateTrigger(UUID id, GttTriggerUpdateDTO dto) {
        GttTrigger trigger = triggerRepository.findByIdWithModule(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gatilho GTT não encontrado com o ID: " + id));

        GttModule module = moduleRepository.findById(dto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Módulo GTT não encontrado com o ID: " + dto.getModuleId()));

        String normalizedCode = dto.getCode().trim().toUpperCase();

        if (triggerRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, id)) {
            throw new BusinessException("Já existe outro gatilho cadastrado com o código '" + normalizedCode + "'.");
        }

        triggerMapper.updateEntityFromDTO(dto, trigger);
        trigger.setModule(module);
        trigger.setCode(normalizedCode);
        trigger.setName(dto.getName().trim());
        trigger.setDescription(dto.getDescription().trim());

        GttTrigger updated = triggerRepository.save(trigger);
        log.info("Gatilho GTT atualizado: id={}, code={}", updated.getId(), updated.getCode());

        return triggerMapper.toResponseDTO(updated);
    }

    /**
     * Altera o status ativo/inativo de um gatilho.
     *
     * @param id  ID do gatilho
     * @param dto DTO com o novo status
     * @return DTO atualizado
     */
    @Transactional
    public GttTriggerResponseDTO updateStatus(UUID id, GttTriggerStatusUpdateDTO dto) {
        GttTrigger trigger = triggerRepository.findByIdWithModule(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gatilho GTT não encontrado com o ID: " + id));

        trigger.setIsActive(dto.getIsActive());
        GttTrigger updated = triggerRepository.save(trigger);
        log.info("Status do gatilho GTT id={} alterado para isActive={}", updated.getId(), updated.getIsActive());

        return triggerMapper.toResponseDTO(updated);
    }

    /**
     * Exclui permanentemente um gatilho clínico.
     *
     * @param id ID do gatilho
     * @throws ResourceNotFoundException se o gatilho não existir
     */
    @Transactional
    public void deleteTrigger(UUID id) {
        GttTrigger trigger = triggerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gatilho GTT não encontrado com o ID: " + id));

        triggerRepository.delete(trigger);
        log.info("Gatilho GTT id={} excluído com sucesso", id);
    }
}
