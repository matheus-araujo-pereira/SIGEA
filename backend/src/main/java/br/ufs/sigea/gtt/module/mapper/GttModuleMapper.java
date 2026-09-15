package br.ufs.sigea.gtt.module.mapper;

import br.ufs.sigea.gtt.module.domain.GttModule;
import br.ufs.sigea.gtt.module.dto.GttModuleCreateDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleResponseDTO;
import br.ufs.sigea.gtt.module.dto.GttModuleUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper MapStruct para conversões de Módulo GTT.
 */
@Mapper(componentModel = "spring")
public interface GttModuleMapper {

    /**
     * Converte entidade GttModule para DTO de resposta.
     *
     * @param module Entidade de domínio
     * @return DTO de resposta
     */
    @Mapping(target = "triggerCount", ignore = true)
    GttModuleResponseDTO toResponseDTO(GttModule module);

    /**
     * Converte DTO de criação para entidade GttModule.
     *
     * @param dto DTO com os dados de entrada
     * @return Entidade pronta para persistência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    GttModule toEntity(GttModuleCreateDTO dto);

    /**
     * Atualiza uma entidade existente a partir do DTO de atualização.
     *
     * @param dto    DTO com novos dados
     * @param module Entidade de destino a ser atualizada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(GttModuleUpdateDTO dto, @MappingTarget GttModule module);
}
