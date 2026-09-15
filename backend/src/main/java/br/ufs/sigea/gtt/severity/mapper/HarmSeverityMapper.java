package br.ufs.sigea.gtt.severity.mapper;

import br.ufs.sigea.gtt.severity.domain.HarmSeverity;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityCreateDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityResponseDTO;
import br.ufs.sigea.gtt.severity.dto.HarmSeverityUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper MapStruct para conversões de Categoria de Gravidade NCC MERP.
 */
@Mapper(componentModel = "spring")
public interface HarmSeverityMapper {

    /**
     * Converte entidade HarmSeverity para DTO de resposta.
     *
     * @param severity Entidade de domínio
     * @return DTO de resposta
     */
    HarmSeverityResponseDTO toResponseDTO(HarmSeverity severity);

    /**
     * Converte DTO de criação para entidade HarmSeverity.
     *
     * @param dto DTO com os dados de entrada
     * @return Entidade pronta para persistência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    HarmSeverity toEntity(HarmSeverityCreateDTO dto);

    /**
     * Atualiza uma entidade existente a partir do DTO de atualização.
     *
     * @param dto      DTO com novos dados
     * @param severity Entidade de destino a ser atualizada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDTO(HarmSeverityUpdateDTO dto, @MappingTarget HarmSeverity severity);
}
