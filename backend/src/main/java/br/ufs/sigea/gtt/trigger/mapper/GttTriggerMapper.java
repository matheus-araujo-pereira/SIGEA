package br.ufs.sigea.gtt.trigger.mapper;

import br.ufs.sigea.gtt.trigger.domain.GttTrigger;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerCreateDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerResponseDTO;
import br.ufs.sigea.gtt.trigger.dto.GttTriggerUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper MapStruct para conversões de Gatilho IHI-GTT.
 */
@Mapper(componentModel = "spring")
public interface GttTriggerMapper {

    /**
     * Converte entidade GttTrigger para DTO de resposta preenchendo dados do módulo.
     *
     * @param trigger Entidade de domínio
     * @return DTO de resposta
     */
    @Mapping(target = "moduleId", source = "module.id")
    @Mapping(target = "moduleCode", source = "module.code")
    @Mapping(target = "moduleName", source = "module.name")
    GttTriggerResponseDTO toResponseDTO(GttTrigger trigger);

    /**
     * Converte DTO de criação para entidade GttTrigger (o módulo é atribuído pelo serviço).
     *
     * @param dto DTO com os dados de entrada
     * @return Entidade pronta para persistência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    GttTrigger toEntity(GttTriggerCreateDTO dto);

    /**
     * Atualiza uma entidade existente a partir do DTO de atualização.
     *
     * @param dto     DTO com novos dados
     * @param trigger Entidade de destino a ser atualizada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(GttTriggerUpdateDTO dto, @MappingTarget GttTrigger trigger);
}
