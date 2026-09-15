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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de regras de negócio para a gestão de Categorias de Gravidade de Dano NCC MERP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HarmSeverityService {

    private final HarmSeverityRepository severityRepository;
    private final HarmSeverityMapper severityMapper;

    /**
     * Lista categorias de gravidade paginadas com filtros por busca textual, flag de dano e status.
     *
     * @param search   Termo de pesquisa por letra, nome ou descrição
     * @param isHarm   Filtro opcional por flag de dano real
     * @param isActive Filtro opcional por status ativo
     * @param pageable Configuração de paginação
     * @return Página de categorias de gravidade
     */
    @Transactional(readOnly = true)
    public PageResponse<HarmSeverityResponseDTO> listSeverities(String search, Boolean isHarm, Boolean isActive, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<HarmSeverity> page = severityRepository.findWithFilters(query, isHarm, isActive, pageable);
        return PageResponse.from(page.map(severityMapper::toResponseDTO));
    }

    /**
     * Retorna todas as categorias ativas ordenadas de A a I para o guia interativo educacional.
     *
     * @return Lista ordenada de categorias
     */
    @Transactional(readOnly = true)
    public List<HarmSeverityResponseDTO> getGuide() {
        return severityRepository.findAllByIsActiveTrueOrderByCategoryLetterAsc().stream()
                .map(severityMapper::toResponseDTO)
                .toList();
    }

    /**
     * Recupera os dados de uma gravidade específica por ID.
     *
     * @param id ID da gravidade
     * @return DTO da gravidade
     * @throws ResourceNotFoundException se não for encontrada
     */
    @Transactional(readOnly = true)
    public HarmSeverityResponseDTO getSeverityById(UUID id) {
        HarmSeverity severity = findSeverityOrThrow(id);
        return severityMapper.toResponseDTO(severity);
    }

    /**
     * Cadastra uma nova categoria de gravidade.
     *
     * @param dto Dados para cadastro
     * @return DTO da categoria criada
     * @throws BusinessException se a letra da categoria já estiver cadastrada
     */
    @Transactional
    public HarmSeverityResponseDTO createSeverity(HarmSeverityCreateDTO dto) {
        String normalizedLetter = dto.getCategoryLetter().trim().toUpperCase();

        if (severityRepository.existsByCategoryLetterIgnoreCase(normalizedLetter)) {
            throw new BusinessException("Já existe uma categoria de gravidade cadastrada com a letra '" + normalizedLetter + "'.");
        }

        HarmSeverity severity = severityMapper.toEntity(dto);
        severity.setCategoryLetter(normalizedLetter);
        severity.setName(dto.getName().trim());
        severity.setDescription(dto.getDescription().trim());
        severity.setIsHarm(dto.getIsHarm());

        HarmSeverity saved = severityRepository.save(severity);
        log.info("Categoria de gravidade criada: id={}, letter={}", saved.getId(), saved.getCategoryLetter());

        return severityMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza uma categoria de gravidade existente.
     *
     * @param id  ID da categoria
     * @param dto Novos dados
     * @return DTO atualizado
     * @throws ResourceNotFoundException se não existir
     * @throws BusinessException         se houver colisão na letra identificadora
     */
    @Transactional
    public HarmSeverityResponseDTO updateSeverity(UUID id, HarmSeverityUpdateDTO dto) {
        HarmSeverity severity = findSeverityOrThrow(id);
        String normalizedLetter = dto.getCategoryLetter().trim().toUpperCase();

        if (severityRepository.existsByCategoryLetterIgnoreCaseAndIdNot(normalizedLetter, id)) {
            throw new BusinessException("Já existe outra categoria de gravidade com a letra '" + normalizedLetter + "'.");
        }

        severityMapper.updateEntityFromDTO(dto, severity);
        severity.setCategoryLetter(normalizedLetter);
        severity.setName(dto.getName().trim());
        severity.setDescription(dto.getDescription().trim());
        severity.setIsHarm(dto.getIsHarm());

        HarmSeverity updated = severityRepository.save(severity);
        log.info("Categoria de gravidade atualizada: id={}, letter={}", updated.getId(), updated.getCategoryLetter());

        return severityMapper.toResponseDTO(updated);
    }

    /**
     * Altera o status ativo/inativo de uma categoria de gravidade.
     *
     * @param id  ID da gravidade
     * @param dto Novo status
     * @return DTO atualizado
     */
    @Transactional
    public HarmSeverityResponseDTO updateStatus(UUID id, HarmSeverityStatusUpdateDTO dto) {
        HarmSeverity severity = findSeverityOrThrow(id);
        severity.setIsActive(dto.getIsActive());

        HarmSeverity updated = severityRepository.save(severity);
        log.info("Status da gravidade id={} alterado para isActive={}", updated.getId(), updated.getIsActive());

        return severityMapper.toResponseDTO(updated);
    }

    /**
     * Exclui uma categoria de gravidade do sistema.
     *
     * @param id ID da gravidade
     */
    @Transactional
    public void deleteSeverity(UUID id) {
        HarmSeverity severity = findSeverityOrThrow(id);
        severityRepository.delete(severity);
        log.info("Categoria de gravidade id={} excluída com sucesso", id);
    }

    /**
     * Busca uma gravidade por ID ou lança ResourceNotFoundException.
     *
     * @param id ID da gravidade
     * @return Entidade HarmSeverity
     */
    private HarmSeverity findSeverityOrThrow(UUID id) {
        return severityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de gravidade não encontrada com o ID: " + id));
    }
}
