package br.ufs.sigea.user.mapper;

import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.dto.UserCreateDTO;
import br.ufs.sigea.user.dto.UserCreateResponseDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper MapStruct para conversão entre entidade User e seus DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Converte entidade User em UserResponseDTO.
     *
     * @param user Entidade de usuário
     * @return DTO correspondente
     */
    UserResponseDTO toResponseDTO(User user);

    /**
     * Converte entidade User e a senha gerada em UserCreateResponseDTO.
     *
     * @param user                Entidade de usuário persistida
     * @param provisionalPassword Senha provisória em texto puro
     * @return DTO de resposta com senha provisória
     */
    @Mapping(target = "provisionalPassword", source = "provisionalPassword")
    UserCreateResponseDTO toCreateResponseDTO(User user, String provisionalPassword);

    /**
     * Converte UserCreateDTO em entidade User (sem criptografia de senha inicial).
     *
     * @param dto DTO de criação
     * @return Entidade User
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "mustChangePassword", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateDTO dto);
}
