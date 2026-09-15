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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de regras de negócio para a gestão de Módulos IHI-GTT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GttModuleService {

    private final GttModuleRepository moduleRepository;
    private final GttTriggerRepository triggerRepository;
    private final GttModuleMapper moduleMapper;

    /**
     * Lista módulos paginados com suporte a pesquisa por texto e filtro de status.
     *
     * @param search   Termo de pesquisa (código ou nome)
     * @param isActive Filtro opcional por status ativo
     * @param pageable Configuração de paginação
     * @return Página de DTOs de módulos
     */
    @Transactional(readOnly = true)
    public PageResponse<GttModuleResponseDTO> listModules(String search, Boolean isActive, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<GttModule> page = moduleRepository.findWithFilters(query, isActive, pageable);

        Page<GttModuleResponseDTO> dtoPage = page.map(module -> {
            GttModuleResponseDTO dto = moduleMapper.toResponseDTO(module);
            dto.setTriggerCount(triggerRepository.countByModuleId(module.getId()));
            return dto;
        });

        return PageResponse.from(dtoPage);
    }

    /**
     * Retorna a lista de todos os módulos ativos para o catálogo educacional de consulta.
     *
     * @return Lista de módulos com a contagem de gatilhos vinculados
     */
    @Transactional(readOnly = true)
    public List<GttModuleResponseDTO> getCatalog() {
        return moduleRepository.findAllByIsActiveTrueOrderByCodeAsc().stream()
                .map(module -> {
                    GttModuleResponseDTO dto = moduleMapper.toResponseDTO(module);
                    dto.setTriggerCount(triggerRepository.countByModuleId(module.getId()));
                    return dto;
                })
                .toList();
    }

    /**
     * Recupera os detalhes de um módulo pelo identificador único.
     *
     * @param id ID do módulo
     * @return DTO com os detalhes do módulo
     * @throws ResourceNotFoundException se o módulo não for encontrado
     */
    @Transactional(readOnly = true)
    public GttModuleResponseDTO getModuleById(UUID id) {
        GttModule module = findModuleOrThrow(id);
        GttModuleResponseDTO dto = moduleMapper.toResponseDTO(module);
        dto.setTriggerCount(triggerRepository.countByModuleId(module.getId()));
        return dto;
    }

    /**
     * Cadastra um novo módulo GTT validando a unicidade do código.
     *
     * @param dto Dados para cadastro
     * @return DTO do módulo criado
     * @throws BusinessException se o código já estiver cadastrado
     */
    @Transactional
    public GttModuleResponseDTO createModule(GttModuleCreateDTO dto) {
        String normalizedCode = dto.getCode().trim().toUpperCase();

        if (moduleRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BusinessException("Já existe um módulo cadastrado com o código '" + normalizedCode + "'.");
        }

        GttModule module = moduleMapper.toEntity(dto);
        module.setCode(normalizedCode);
        module.setName(dto.getName().trim());
        if (dto.getDescription() != null) {
            module.setDescription(dto.getDescription().trim());
        }

        GttModule saved = moduleRepository.save(module);
        log.info("Módulo GTT criado com sucesso: id={}, code={}", saved.getId(), saved.getCode());

        GttModuleResponseDTO responseDTO = moduleMapper.toResponseDTO(saved);
        responseDTO.setTriggerCount(0L);
        return responseDTO;
    }

    /**
     * Atualiza os dados cadastrais de um módulo existente.
     *
     * @param id  ID do módulo
     * @param dto Novos dados
     * @return DTO atualizado
     * @throws ResourceNotFoundException se o módulo não existir
     * @throws BusinessException         se o novo código colidir com outro módulo
     */
    @Transactional
    public GttModuleResponseDTO updateModule(UUID id, GttModuleUpdateDTO dto) {
        GttModule module = findModuleOrThrow(id);
        String normalizedCode = dto.getCode().trim().toUpperCase();

        if (moduleRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, id)) {
            throw new BusinessException("Já existe outro módulo com o código '" + normalizedCode + "'.");
        }

        moduleMapper.updateEntityFromDTO(dto, module);
        module.setCode(normalizedCode);
        module.setName(dto.getName().trim());
        if (dto.getDescription() != null) {
            module.setDescription(dto.getDescription().trim());
        }

        GttModule updated = moduleRepository.save(module);
        log.info("Módulo GTT atualizado: id={}, code={}", updated.getId(), updated.getCode());

        GttModuleResponseDTO responseDTO = moduleMapper.toResponseDTO(updated);
        responseDTO.setTriggerCount(triggerRepository.countByModuleId(updated.getId()));
        return responseDTO;
    }

    /**
     * Altera o status ativo/inativo de um módulo.
     *
     * @param id  ID do módulo
     * @param dto DTO com o novo status
     * @return DTO atualizado
     */
    @Transactional
    public GttModuleResponseDTO updateStatus(UUID id, GttModuleStatusUpdateDTO dto) {
        GttModule module = findModuleOrThrow(id);
        module.setIsActive(dto.getIsActive());

        GttModule updated = moduleRepository.save(module);
        log.info("Status do módulo GTT id={} alterado para isActive={}", updated.getId(), updated.getIsActive());

        GttModuleResponseDTO responseDTO = moduleMapper.toResponseDTO(updated);
        responseDTO.setTriggerCount(triggerRepository.countByModuleId(updated.getId()));
        return responseDTO;
    }

    /**
     * Exclui um módulo GTT garantindo integridade referencial com os gatilhos associados.
     *
     * @param id ID do módulo a ser excluído
     * @throws ResourceNotFoundException se o módulo não existir
     * @throws BusinessException         se houver gatilhos vinculados ao módulo
     */
    @Transactional
    public void deleteModule(UUID id) {
        GttModule module = findModuleOrThrow(id);

        if (triggerRepository.existsByModuleId(id)) {
            throw new BusinessException("Não é possível excluir o módulo '" + module.getName() +
                    "' pois existem gatilhos associados a ele. Remova ou transfira os gatilhos antes.");
        }

        moduleRepository.delete(module);
        log.info("Módulo GTT id={} excluído com sucesso", id);
    }

    /**
     * Busca o módulo pelo ID ou lança exceção caso não exista.
     *
     * @param id ID do módulo
     * @return Entidade GttModule
     */
    private GttModule findModuleOrThrow(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo GTT não encontrado com o ID informado: " + id));
    }
}
