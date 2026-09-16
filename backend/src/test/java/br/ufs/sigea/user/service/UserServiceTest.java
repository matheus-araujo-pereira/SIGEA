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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserService userService;

    private User studentUser;
    private User adminUser;
    private UUID studentId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        studentUser = User.builder()
                .id(studentId)
                .fullName("Aluno Teste")
                .email("aluno@academico.ufs.br")
                .passwordHash("hashed-password")
                .role(UserRole.STUDENT)
                .registrationNumber("20261001")
                .isActive(true)
                .mustChangePassword(true)
                .build();

        adminUser = User.builder()
                .id(adminId)
                .fullName("Admin Teste")
                .email("admin@academico.ufs.br")
                .passwordHash("hashed-admin")
                .role(UserRole.ADMIN)
                .registrationNumber(null)
                .isActive(true)
                .mustChangePassword(false)
                .build();
    }

    @Test
    @DisplayName("Deve criar estudante com matrícula válida com sucesso")
    void shouldCreateStudentUserSuccessfully() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Novo Aluno")
                .email("novo.aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("20261234")
                .build();

        when(userRepository.existsByEmailIgnoreCase("novo.aluno@academico.ufs.br")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-pwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserCreateResponseDTO response = userService.createUser(dto);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getProvisionalPassword());
        assertEquals("novo.aluno@academico.ufs.br", response.getEmail());
        assertEquals("20261234", response.getRegistrationNumber());
        assertEquals(UserRole.STUDENT, response.getRole());
        assertTrue(response.getMustChangePassword());
    }

    @Test
    @DisplayName("Deve criar professor e garantir matrícula nula com sucesso")
    void shouldCreateProfessorUserSuccessfully() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Novo Professor")
                .email("professor@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .registrationNumber(null)
                .build();

        when(userRepository.existsByEmailIgnoreCase("professor@academico.ufs.br")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-pwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserCreateResponseDTO response = userService.createUser(dto);

        assertNotNull(response);
        assertNull(response.getRegistrationNumber());
        assertEquals(UserRole.PROFESSOR, response.getRole());
    }

    @Test
    @DisplayName("Deve permitir criação de professor com matrícula em branco/espaços normalizando para nulo")
    void shouldCreateProfessorWithWhitespaceRegistrationAsNull() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Professor Com Espaço")
                .email("professor.espaco@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .registrationNumber("   ")
                .build();

        when(userRepository.existsByEmailIgnoreCase("professor.espaco@academico.ufs.br")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-pwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserCreateResponseDTO response = userService.createUser(dto);
        assertNotNull(response);
        assertNull(response.getRegistrationNumber());
    }

    @Test
    @DisplayName("Deve rejeitar criação com e-mail fora do domínio @academico.ufs.br")
    void shouldRejectCreationWithInvalidDomain() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Aluno Inválido")
                .email("aluno@gmail.com")
                .role(UserRole.STUDENT)
                .registrationNumber("12345")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertTrue(ex.getMessage().contains("@academico.ufs.br"));
    }

    @Test
    @DisplayName("Deve rejeitar criação com e-mail nulo")
    void shouldRejectCreationWithNullEmail() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Aluno Inválido")
                .email(null)
                .role(UserRole.STUDENT)
                .registrationNumber("12345")
                .build();

        assertThrows(BusinessException.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName("Deve rejeitar criação com e-mail duplicado")
    void shouldRejectCreationWithDuplicateEmail() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Aluno Duplicado")
                .email("aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("12345")
                .build();

        when(userRepository.existsByEmailIgnoreCase("aluno@academico.ufs.br")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertTrue(ex.getMessage().contains("Já existe um usuário"));
    }

    @Test
    @DisplayName("Deve rejeitar criação de estudante sem matrícula")
    void shouldRejectStudentCreationWithoutRegistration() {
        UserCreateDTO dtoEmpty = UserCreateDTO.builder()
                .fullName("Aluno Sem Matrícula")
                .email("aluno.sem@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("")
                .build();

        assertThrows(BusinessException.class, () -> userService.createUser(dtoEmpty));

        UserCreateDTO dtoNull = UserCreateDTO.builder()
                .fullName("Aluno Sem Matrícula")
                .email("aluno.sem@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber(null)
                .build();

        assertThrows(BusinessException.class, () -> userService.createUser(dtoNull));
    }

    @Test
    @DisplayName("Deve rejeitar criação de admin ou professor com matrícula preenchida")
    void shouldRejectNonStudentWithRegistration() {
        UserCreateDTO dto = UserCreateDTO.builder()
                .fullName("Professor Com Matrícula")
                .email("prof@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .registrationNumber("99999")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertTrue(ex.getMessage().contains("matrícula deve ser nulo"));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .fullName("Aluno Nome Atualizado")
                .email("aluno.novo@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("20269999")
                .build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("aluno.novo@academico.ufs.br", studentId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        UserResponseDTO response = userService.updateUser(studentId, dto);

        assertNotNull(response);
        assertEquals("Aluno Nome Atualizado", studentUser.getFullName());
        assertEquals("aluno.novo@academico.ufs.br", studentUser.getEmail());
    }

    @Test
    @DisplayName("Deve atualizar professor garantindo matrícula nula")
    void shouldUpdateProfessorUserWithNullRegistration() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .fullName("Professor Atualizado")
                .email("prof@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .registrationNumber(null)
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("prof@academico.ufs.br", adminId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        UserResponseDTO response = userService.updateUser(adminId, dto);

        assertNotNull(response);
        assertNull(adminUser.getRegistrationNumber());
    }

    @Test
    @DisplayName("Deve permitir atualização de professor com matrícula em branco")
    void shouldUpdateProfessorWithBlankRegistration() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .fullName("Professor Atualizado")
                .email("prof@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .registrationNumber("   ")
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("prof@academico.ufs.br", adminId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        UserResponseDTO response = userService.updateUser(adminId, dto);
        assertNotNull(response);
        assertNull(adminUser.getRegistrationNumber());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar usuário inexistente")
    void shouldFailUpdatingNonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .fullName("Teste")
                .email("teste@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("123")
                .build();

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(nonExistentId, dto));
    }

    @Test
    @DisplayName("Deve atualizar status ativo/inativo com sucesso")
    void shouldUpdateStatusSuccessfully() {
        UserStatusUpdateDTO dto = new UserStatusUpdateDTO(false);
        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        UserResponseDTO response = userService.updateStatus(studentId, dto, adminId);

        assertNotNull(response);
        assertFalse(studentUser.getIsActive());
    }

    @Test
    @DisplayName("Deve impedir que o administrador inative a própria conta")
    void shouldPreventAdminFromInactivatingSelf() {
        UserStatusUpdateDTO dto = new UserStatusUpdateDTO(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userService.updateStatus(adminId, dto, adminId));

        assertTrue(ex.getMessage().contains("não pode inativar a própria conta"));
    }

    @Test
    @DisplayName("Deve permitir administrador alterar o próprio status se for true")
    void shouldAllowAdminToSetSelfActive() {
        UserStatusUpdateDTO dto = new UserStatusUpdateDTO(true);
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        UserResponseDTO response = userService.updateStatus(adminId, dto, adminId);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));

        userService.deleteUser(studentId, adminId);

        verify(userRepository).delete(studentUser);
    }

    @Test
    @DisplayName("Deve impedir administrador de excluir a própria conta")
    void shouldPreventAdminFromDeletingSelf() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                userService.deleteUser(adminId, adminId));

        assertTrue(ex.getMessage().contains("não pode excluir a própria conta"));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));

        UserResponseDTO response = userService.getUserById(studentId);

        assertNotNull(response);
        assertEquals(studentId, response.getId());
    }

    @Test
    @DisplayName("Deve listar usuários paginados com filtros")
    void shouldListUsersPagedWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(studentUser), pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<UserResponseDTO> response = userService.listUsers("aluno", UserRole.STUDENT, true, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
    }

    @Test
    @DisplayName("Deve listar usuários sem filtros")
    void shouldListUsersWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<UserResponseDTO> response = userService.listUsers(null, null, null, pageable);

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
    }

    @Test
    @DisplayName("Deve testar a execução direta de predicados da Specification cobrindo todos os ramos")
    @SuppressWarnings("unchecked")
    void shouldExecuteSpecificationPredicatesCoveringAllBranches() {
        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> path = mock(Path.class);
        Expression<String> expr = mock(Expression.class);
        Predicate pred = mock(Predicate.class);

        org.mockito.Mockito.lenient().when(root.get(anyString())).thenReturn(path);
        org.mockito.Mockito.lenient().when(cb.lower(any())).thenReturn(expr);
        org.mockito.Mockito.lenient().when(cb.like(any(), anyString())).thenReturn(pred);
        org.mockito.Mockito.lenient().when(cb.or(any(), any(), any())).thenReturn(pred);
        org.mockito.Mockito.lenient().when(cb.equal(any(), any())).thenReturn(pred);
        org.mockito.Mockito.lenient().when(cb.and(any(Predicate[].class))).thenReturn(pred);

        Pageable pageable = PageRequest.of(0, 10);
        ArgumentCaptor<Specification<User>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(userRepository.findAll(specCaptor.capture(), eq(pageable))).thenReturn(new PageImpl<>(List.of()));

        // 1. Search present + role present + isActive present
        userService.listUsers("termo", UserRole.STUDENT, true, pageable);
        Specification<User> spec1 = specCaptor.getValue();
        Predicate res1 = spec1.toPredicate(root, query, cb);
        assertNotNull(res1);

        // 2. Search is blank + role is null + isActive is null
        userService.listUsers("   ", null, null, pageable);
        Specification<User> spec2 = specCaptor.getValue();
        Predicate res2 = spec2.toPredicate(root, query, cb);
        assertNotNull(res2);

        // 3. Search is null + role is null + isActive is false
        userService.listUsers(null, null, false, pageable);
        Specification<User> spec3 = specCaptor.getValue();
        Predicate res3 = spec3.toPredicate(root, query, cb);
        assertNotNull(res3);
    }

    @Test
    @DisplayName("Deve atualizar o próprio perfil com sucesso")
    void shouldUpdateProfileSuccessfully() {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO("Novo Nome Próprio");
        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        UserResponseDTO response = userService.updateProfile(studentId, dto);

        assertNotNull(response);
        assertEquals("Novo Nome Próprio", studentUser.getFullName());
    }

    @Test
    @DisplayName("Deve redefinir a senha do usuário para a senha padrão com sucesso")
    void shouldResetPasswordSuccessfully() {
        when(userRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(passwordEncoder.encode(UserService.DEFAULT_PASSWORD)).thenReturn("encodedDefaultPassword");
        when(userRepository.save(any(User.class))).thenReturn(studentUser);
        when(userMapper.toCreateResponseDTO(studentUser, UserService.DEFAULT_PASSWORD))
                .thenReturn(new UserCreateResponseDTO(studentId, studentUser.getFullName(), studentUser.getEmail(),
                        studentUser.getRole(), studentUser.getRegistrationNumber(), studentUser.getIsActive(),
                        studentUser.getMustChangePassword(), UserService.DEFAULT_PASSWORD, null));

        UserCreateResponseDTO response = userService.resetPassword(studentId);

        assertNotNull(response);
        assertEquals(UserService.DEFAULT_PASSWORD, response.getProvisionalPassword());
        assertTrue(studentUser.getMustChangePassword());
        verify(userRepository).save(studentUser);
    }
}
