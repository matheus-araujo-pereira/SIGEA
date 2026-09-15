package br.ufs.sigea.user.service;

import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.dto.UserCreateDTO;
import br.ufs.sigea.user.dto.UserCreateResponseDTO;
import br.ufs.sigea.user.dto.UserProfileUpdateDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.dto.UserStatusUpdateDTO;
import br.ufs.sigea.user.dto.UserUpdateDTO;
import br.ufs.sigea.user.mapper.UserMapper;
import br.ufs.sigea.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de aplicação para gestão de usuários e regras de negócio de cadastro.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHAR = "!@#$%&*";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHAR;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Cria um novo usuário no sistema gerando senha provisória automática e marcando must_change_password.
     *
     * @param dto Dados para criação do usuário
     * @return DTO com informações do usuário criado e senha provisória em texto puro
     */
    @Transactional
    public UserCreateResponseDTO createUser(UserCreateDTO dto) {
        validateEmailDomain(dto.getEmail());
        validateEmailUniqueness(dto.getEmail(), null);
        validateRegistrationNumber(dto.getRole(), dto.getRegistrationNumber());

        String rawPassword = generateProvisionalPassword();
        String passwordHash = passwordEncoder.encode(rawPassword);

        String registrationNumber = dto.getRole() == UserRole.STUDENT 
                ? dto.getRegistrationNumber().trim() 
                : null;

        User user = User.builder()
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .passwordHash(passwordHash)
                .role(dto.getRole())
                .registrationNumber(registrationNumber)
                .isActive(true)
                .mustChangePassword(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuário cadastrado com sucesso: id={}, email={}, role={}", savedUser.getId(), savedUser.getEmail(), savedUser.getRole());

        return userMapper.toCreateResponseDTO(savedUser, rawPassword);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id  Identificador único do usuário
     * @param dto Dados de atualização
     * @return Dados atualizados do usuário
     */
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO dto) {
        User user = findUserById(id);

        validateEmailDomain(dto.getEmail());
        validateEmailUniqueness(dto.getEmail(), id);
        validateRegistrationNumber(dto.getRole(), dto.getRegistrationNumber());

        String registrationNumber = dto.getRole() == UserRole.STUDENT 
                ? dto.getRegistrationNumber().trim() 
                : null;

        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setRole(dto.getRole());
        user.setRegistrationNumber(registrationNumber);

        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado: id={}, email={}", updatedUser.getId(), updatedUser.getEmail());

        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Atualiza o status de ativação (ativo/inativo) do usuário.
     *
     * @param id             Identificador do usuário
     * @param dto            Novo status
     * @param currentAdminId ID do administrador que está executando a ação
     * @return Dados atualizados do usuário
     */
    @Transactional
    public UserResponseDTO updateStatus(UUID id, UserStatusUpdateDTO dto, UUID currentAdminId) {
        if (id.equals(currentAdminId) && Boolean.FALSE.equals(dto.getIsActive())) {
            throw new BusinessException("O administrador não pode inativar a própria conta.");
        }

        User user = findUserById(id);
        user.setIsActive(dto.getIsActive());
        User updatedUser = userRepository.save(user);

        log.info("Status do usuário id={} alterado para isActive={}", id, dto.getIsActive());
        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Exclui um usuário do sistema.
     *
     * @param id             Identificador do usuário
     * @param currentAdminId ID do administrador que está executando a ação
     */
    @Transactional
    public void deleteUser(UUID id, UUID currentAdminId) {
        if (id.equals(currentAdminId)) {
            throw new BusinessException("O administrador não pode excluir a própria conta.");
        }

        User user = findUserById(id);
        userRepository.delete(user);
        log.info("Usuário id={} excluído com sucesso pelo admin id={}", id, currentAdminId);
    }

    /**
     * Busca usuário pelo ID.
     *
     * @param id Identificador único
     * @return DTO com os dados do usuário
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        return userMapper.toResponseDTO(findUserById(id));
    }

    /**
     * Lista usuários de forma paginada com suporte a filtros de pesquisa.
     * Padrão de 10 itens por página.
     *
     * @param search   Termo de busca (nome, e-mail ou matrícula)
     * @param role     Filtro por perfil
     * @param isActive Filtro por status
     * @param pageable Configuração de paginação e ordenação
     * @return Página de usuários
     */
    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> listUsers(String search, UserRole role, Boolean isActive, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), searchPattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), searchPattern);
                Predicate regMatch = cb.like(cb.lower(root.get("registrationNumber")), searchPattern);
                predicates.add(cb.or(nameMatch, emailMatch, regMatch));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> usersPage = userRepository.findAll(spec, pageable);
        return PageResponse.from(usersPage.map(userMapper::toResponseDTO));
    }

    /**
     * Atualiza os dados básicos do perfil do usuário conectado.
     *
     * @param userId Identificador do usuário logado
     * @param dto    Dados de atualização do perfil
     * @return DTO com dados atualizados
     */
    @Transactional
    public UserResponseDTO updateProfile(UUID userId, UserProfileUpdateDTO dto) {
        User user = findUserById(userId);
        user.setFullName(dto.getFullName().trim());
        User saved = userRepository.save(user);
        return userMapper.toResponseDTO(saved);
    }

    /**
     * Obtém a entidade User por ID ou lança ResourceNotFoundException.
     *
     * @param id Identificador do usuário
     * @return Entidade User encontrada
     */
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    /**
     * Valida se o domínio de e-mail é estritamente @academico.ufs.br.
     */
    private void validateEmailDomain(String email) {
        if (email == null || !email.trim().toLowerCase().endsWith("@academico.ufs.br")) {
            throw new BusinessException("O e-mail deve pertencer obrigatoriamente ao domínio institucional @academico.ufs.br");
        }
    }

    /**
     * Valida a unicidade de e-mail cadastrado.
     */
    private void validateEmailUniqueness(String email, UUID currentUserId) {
        String cleanEmail = email.trim().toLowerCase();
        boolean exists = (currentUserId == null)
                ? userRepository.existsByEmailIgnoreCase(cleanEmail)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(cleanEmail, currentUserId);

        if (exists) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail institucional: " + cleanEmail);
        }
    }

    /**
     * Valida a obrigatoriedade da matrícula para STUDENT e proibição para ADMIN/PROFESSOR.
     */
    private void validateRegistrationNumber(UserRole role, String registrationNumber) {
        if (role == UserRole.STUDENT) {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new BusinessException("A matrícula é obrigatória para o perfil de Estudante (STUDENT).");
            }
        } else {
            if (registrationNumber != null && !registrationNumber.trim().isEmpty()) {
                throw new BusinessException("O campo matrícula deve ser nulo para os perfis ADMIN e PROFESSOR.");
            }
        }
    }

    /**
     * Gera uma senha provisória aleatória e segura de 10 caracteres.
     */
    public String generateProvisionalPassword() {
        StringBuilder password = new StringBuilder(10);
        // Garantir pelo menos um caractere de cada categoria
        password.append(CHAR_UPPER.charAt(RANDOM.nextInt(CHAR_UPPER.length())));
        password.append(CHAR_LOWER.charAt(RANDOM.nextInt(CHAR_LOWER.length())));
        password.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length())));
        password.append(SPECIAL_CHAR.charAt(RANDOM.nextInt(SPECIAL_CHAR.length())));

        for (int i = 4; i < 10; i++) {
            password.append(PASSWORD_ALLOW.charAt(RANDOM.nextInt(PASSWORD_ALLOW.length())));
        }

        // Embaralhar caracteres
        char[] array = password.toString().toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return new String(array);
    }
}
