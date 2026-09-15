package br.ufs.sigea.academic.clazz.service;

import br.ufs.sigea.academic.activity.repository.ActivityRepository;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCreateDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassDetailDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassResponseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassUpdateDTO;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicClassServiceTest {

    @Mock
    private AcademicClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivitySubmissionRepository submissionRepository;

    @InjectMocks
    private AcademicClassService classService;

    private User professor;
    private User student;
    private AcademicClass sampleClass;
    private UUID classId;
    private UUID profId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        profId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        professor = User.builder()
                .id(profId)
                .fullName("Prof. Waleska")
                .email("waleska@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        student = User.builder()
                .id(studentId)
                .fullName("Aluno Silva")
                .email("aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("2026001")
                .build();

        sampleClass = AcademicClass.builder()
                .id(classId)
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professor(professor)
                .students(new HashSet<>(Set.of(student)))
                .isClosed(false)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar turmas com busca e filtros")
    void shouldListClassesWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByFilters(eq("Enfermagem"), eq("2026.2"), eq(false), eq(pageable))).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(3L);

        Page<AcademicClassResponseDTO> result = classService.listClasses(" Enfermagem ", " 2026.2 ", false, pageable);

        assertThat(result.getContent()).hasSize(1);
        AcademicClassResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getSubjectName()).isEqualTo("Enfermagem Hospitalar");
        assertThat(dto.getStudentCount()).isEqualTo(1);
        assertThat(dto.getActivityCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve listar turmas com busca e período nulos ou vazios")
    void shouldListClassesWithNullFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByFilters(eq(null), eq(null), eq(null), eq(pageable))).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(0L);

        Page<AcademicClassResponseDTO> result = classService.listClasses("   ", "", null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar turmas apenas com filtro de busca")
    void shouldListClassesWithOnlySearchFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByFilters(eq("Enfermagem"), eq(null), eq(null), eq(pageable))).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(0L);

        Page<AcademicClassResponseDTO> result = classService.listClasses("Enfermagem", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar turmas apenas com filtro de período")
    void shouldListClassesWithOnlyPeriodFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByFilters(eq(null), eq("2026.2"), eq(null), eq(pageable))).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(0L);

        Page<AcademicClassResponseDTO> result = classService.listClasses(null, "2026.2", null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar turmas por professor quando lista de estudantes for nula")
    void shouldListClassesByProfessorWithNullStudents() {
        sampleClass.setStudents(null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByProfessorId(profId, pageable)).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(0L);

        Page<AcademicClassResponseDTO> result = classService.listClassesByProfessor(profId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStudentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve retornar detalhes da turma por ID")
    void shouldGetClassById() {
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(2L);

        AcademicClassDetailDTO result = classService.getClassById(classId);

        assertThat(result.getId()).isEqualTo(classId);
        assertThat(result.getSubjectName()).isEqualTo("Enfermagem Hospitalar");
        assertThat(result.getStudents()).hasSize(1);
        assertThat(result.getStudents().get(0).getRegistrationNumber()).isEqualTo("2026001");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar turma inexistente")
    void shouldThrowWhenClassNotFound() {
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classService.getClassById(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma acadêmica não encontrada");
    }

    @Test
    @DisplayName("Deve listar turmas por professor")
    void shouldListClassesByProfessor() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByProfessorId(profId, pageable)).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(1L);

        Page<AcademicClassResponseDTO> result = classService.listClassesByProfessor(profId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProfessorName()).isEqualTo("Prof. Waleska");
    }

    @Test
    @DisplayName("Deve listar turmas por estudante")
    void shouldListClassesByStudent() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AcademicClass> page = new PageImpl<>(List.of(sampleClass), pageable, 1);

        when(classRepository.findByStudentId(studentId, pageable)).thenReturn(page);
        when(activityRepository.countByAcademicClassId(classId)).thenReturn(1L);

        Page<AcademicClassResponseDTO> result = classService.listClassesByStudent(studentId, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve criar turma com sucesso")
    void shouldCreateClassSuccessfully() {
        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Farmacologia Clínica")
                .classCode("t02")
                .academicPeriod("2026.2")
                .professorId(profId)
                .studentIds(Set.of(studentId))
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Farmacologia Clínica", "T02", "2026.2")).thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(userRepository.findAllById(dto.getStudentIds())).thenReturn(List.of(student));
        when(classRepository.save(any(AcademicClass.class))).thenAnswer(inv -> {
            AcademicClass c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        AcademicClassDetailDTO result = classService.createClass(dto);

        assertThat(result).isNotNull();
        assertThat(result.getSubjectName()).isEqualTo("Farmacologia Clínica");
        assertThat(result.getClassCode()).isEqualTo("T02");
        assertThat(result.getStudents()).hasSize(1);
    }

    @Test
    @DisplayName("Deve criar turma sem alunos (conjunto vazio)")
    void shouldCreateClassWithoutStudents() {
        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Farmacologia Clínica")
                .classCode("T02")
                .academicPeriod("2026.2")
                .professorId(profId)
                .studentIds(null)
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Farmacologia Clínica", "T02", "2026.2")).thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(classRepository.save(any(AcademicClass.class))).thenAnswer(inv -> {
            AcademicClass c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        AcademicClassDetailDTO result = classService.createClass(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStudents()).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar BusinessException se turma duplicada ao criar")
    void shouldThrowWhenDuplicateClassOnCreate() {
        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Enfermagem Hospitalar", "T01", "2026.2")).thenReturn(true);

        assertThatThrownBy(() -> classService.createClass(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe uma turma cadastrada");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se professor não for encontrado")
    void shouldThrowWhenProfessorNotFoundOnCreate() {
        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(any(), any(), any()))
                .thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classService.createClass(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Professor titular não encontrado");
    }

    @Test
    @DisplayName("Deve permitir ADMIN como docente da turma")
    void shouldAllowAdminAsProfessor() {
        User admin = User.builder()
                .id(profId)
                .fullName("Admin Geral")
                .email("admin@academico.ufs.br")
                .role(UserRole.ADMIN)
                .build();

        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Gestão Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(any(), any(), any()))
                .thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(admin));
        when(classRepository.save(any(AcademicClass.class))).thenAnswer(inv -> inv.getArgument(0));

        AcademicClassDetailDTO result = classService.createClass(dto);
        assertThat(result.getProfessorName()).isEqualTo("Admin Geral");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se docente tiver role STUDENT")
    void shouldThrowWhenProfessorRoleIsStudent() {
        User invalidUser = User.builder()
                .id(profId)
                .fullName("Aluno Fingindo")
                .email("aluno@academico.ufs.br")
                .role(UserRole.STUDENT)
                .build();

        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(any(), any(), any()))
                .thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(invalidUser));

        assertThatThrownBy(() -> classService.createClass(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("deve possuir perfil de PROFESSOR ou ADMIN");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se aluno matriculado não for STUDENT")
    void shouldThrowWhenEnrolledUserIsNotStudent() {
        User fakeStudent = User.builder()
                .id(studentId)
                .fullName("Docente Tentando Entrar")
                .email("docente@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        AcademicClassCreateDTO dto = AcademicClassCreateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .studentIds(Set.of(studentId))
                .build();

        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(any(), any(), any()))
                .thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(userRepository.findAllById(dto.getStudentIds())).thenReturn(List.of(fakeStudent));

        assertThatThrownBy(() -> classService.createClass(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não possui perfil de STUDENT");
    }

    @Test
    @DisplayName("Deve atualizar turma com alteração de identificadores e sem colisão")
    void shouldUpdateClassWithChangedIdentifiers() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Cirúrgica")
                .classCode("T05")
                .academicPeriod("2027.1")
                .professorId(profId)
                .studentIds(Set.of(studentId))
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Enfermagem Cirúrgica", "T05", "2027.1")).thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(userRepository.findAllById(dto.getStudentIds())).thenReturn(List.of(student));
        when(classRepository.save(any(AcademicClass.class))).thenReturn(sampleClass);

        AcademicClassDetailDTO result = classService.updateClass(classId, dto);

        assertThat(result).isNotNull();
        assertThat(sampleClass.getSubjectName()).isEqualTo("Enfermagem Cirúrgica");
        assertThat(sampleClass.getClassCode()).isEqualTo("T05");
    }

    @Test
    @DisplayName("Deve atualizar turma com mesmos identificadores sem checar colisão")
    void shouldUpdateClassWithSameIdentifiers() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("t01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .studentIds(Set.of(studentId))
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(userRepository.findAllById(dto.getStudentIds())).thenReturn(List.of(student));
        when(classRepository.save(any(AcademicClass.class))).thenReturn(sampleClass);

        AcademicClassDetailDTO result = classService.updateClass(classId, dto);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar turma quando apenas o código mudar")
    void shouldUpdateClassWhenOnlyClassCodeChanges() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T02")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Enfermagem Hospitalar", "T02", "2026.2")).thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(classRepository.save(any(AcademicClass.class))).thenReturn(sampleClass);

        AcademicClassDetailDTO result = classService.updateClass(classId, dto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar turma quando apenas o período mudar")
    void shouldUpdateClassWhenOnlyAcademicPeriodChanges() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2027.1")
                .professorId(profId)
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Enfermagem Hospitalar", "T01", "2027.1")).thenReturn(false);
        when(userRepository.findById(profId)).thenReturn(Optional.of(professor));
        when(classRepository.save(any(AcademicClass.class))).thenReturn(sampleClass);

        AcademicClassDetailDTO result = classService.updateClass(classId, dto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar se novo identificador colidir com outra turma")
    void shouldThrowWhenUpdateCollidesWithExistingClass() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Cirúrgica")
                .classCode("T05")
                .academicPeriod("2027.1")
                .professorId(profId)
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));
        when(classRepository.existsBySubjectNameIgnoreCaseAndClassCodeIgnoreCaseAndAcademicPeriodIgnoreCase(
                "Enfermagem Cirúrgica", "T05", "2027.1")).thenReturn(true);

        assertThatThrownBy(() -> classService.updateClass(classId, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe outra turma com esta disciplina");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar turma inexistente")
    void shouldThrowWhenUpdatingNonExistentClass() {
        AcademicClassUpdateDTO dto = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classService.updateClass(classId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve encerrar turma com sucesso quando não há correções pendentes")
    void shouldCloseClassWhenNoPendingGrading() {
        when(classRepository.findById(classId)).thenReturn(Optional.of(sampleClass));
        when(submissionRepository.countUngradedSubmissionsByClassId(classId)).thenReturn(0L);
        when(classRepository.save(sampleClass)).thenReturn(sampleClass);

        AcademicClassResponseDTO result = classService.updateClassClosedStatus(classId, true);

        assertThat(result.getIsClosed()).isTrue();
        verify(classRepository).save(sampleClass);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar encerrar turma com correções pendentes")
    void shouldThrowWhenClosingClassWithPendingGrading() {
        when(classRepository.findById(classId)).thenReturn(Optional.of(sampleClass));
        when(submissionRepository.countUngradedSubmissionsByClassId(classId)).thenReturn(4L);

        assertThatThrownBy(() -> classService.updateClassClosedStatus(classId, true))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("possui 4 atividade(s) submetida(s) com correção pendente");
    }

    @Test
    @DisplayName("Deve reabrir turma com sucesso sem checar correções pendentes")
    void shouldReopenClassWithoutPendingCheck() {
        sampleClass.setIsClosed(true);
        when(classRepository.findById(classId)).thenReturn(Optional.of(sampleClass));
        when(classRepository.save(sampleClass)).thenReturn(sampleClass);

        AcademicClassResponseDTO result = classService.updateClassClosedStatus(classId, false);

        assertThat(result.getIsClosed()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao alterar status de turma inexistente")
    void shouldThrowWhenStatusUpdateOnNonExistentClass() {
        when(classRepository.findById(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classService.updateClassClosedStatus(classId, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve excluir turma com sucesso")
    void shouldDeleteClassSuccessfully() {
        when(classRepository.findById(classId)).thenReturn(Optional.of(sampleClass));

        classService.deleteClass(classId);

        verify(classRepository).delete(sampleClass);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao excluir turma inexistente")
    void shouldThrowWhenDeletingNonExistentClass() {
        when(classRepository.findById(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classService.deleteClass(classId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lidar com alunos nulos no toResponseDTO e toDetailDTO")
    void shouldHandleNullStudents() {
        sampleClass.setStudents(null);
        when(classRepository.findByIdWithStudents(classId)).thenReturn(Optional.of(sampleClass));

        AcademicClassDetailDTO detail = classService.getClassById(classId);
        assertThat(detail.getStudents()).isEmpty();
        assertThat(detail.getStudentCount()).isEqualTo(0);
    }
}
