package br.ufs.sigea.academic.clazz.controller;

import br.ufs.sigea.academic.clazz.dto.AcademicClassCloseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCreateDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassDetailDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassResponseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassUpdateDTO;
import br.ufs.sigea.academic.clazz.service.AcademicClassService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicClassControllerTest {

    @Mock
    private AcademicClassService classService;

    @InjectMocks
    private AcademicClassController classController;

    private UUID classId;
    private UUID profId;
    private UUID studentId;
    private AcademicClassResponseDTO responseDTO;
    private AcademicClassDetailDTO detailDTO;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        profId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        responseDTO = AcademicClassResponseDTO.builder()
                .id(classId)
                .subjectName("Enfermagem")
                .classCode("T01")
                .academicPeriod("2026.2")
                .formattedName("Enfermagem - T01 - 2026.2")
                .professorId(profId)
                .professorName("Prof. Waleska")
                .professorEmail("waleska@academico.ufs.br")
                .isClosed(false)
                .studentCount(1)
                .activityCount(2)
                .createdAt(Instant.now())
                .build();

        detailDTO = AcademicClassDetailDTO.builder()
                .id(classId)
                .subjectName("Enfermagem")
                .classCode("T01")
                .academicPeriod("2026.2")
                .formattedName("Enfermagem - T01 - 2026.2")
                .professorId(profId)
                .professorName("Prof. Waleska")
                .professorEmail("waleska@academico.ufs.br")
                .isClosed(false)
                .studentCount(1)
                .activityCount(2)
                .students(List.of())
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar turmas como admin")
    void shouldListClasses() {
        Pageable pageable = PageRequest.of(0, 10);
        when(classService.listClasses("enf", "2026.2", false, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> response =
                classController.listClasses("enf", "2026.2", false, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar turmas do estudante em getMyClasses")
    void shouldGetMyClassesForStudent() {
        User student = User.builder().id(studentId).role(UserRole.STUDENT).build();
        Pageable pageable = PageRequest.of(0, 10);
        when(classService.listClassesByStudent(studentId, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> response =
                classController.getMyClasses(student, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Minhas turmas");
    }

    @Test
    @DisplayName("Deve retornar turmas do professor em getMyClasses")
    void shouldGetMyClassesForProfessor() {
        User prof = User.builder().id(profId).role(UserRole.PROFESSOR).build();
        Pageable pageable = PageRequest.of(0, 10);
        when(classService.listClassesByProfessor(profId, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> response =
                classController.getMyClasses(prof, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Turmas do docente");
    }

    @Test
    @DisplayName("Deve retornar todas as turmas para ADMIN em getMyClasses")
    void shouldGetMyClassesForAdmin() {
        User admin = User.builder().id(UUID.randomUUID()).role(UserRole.ADMIN).build();
        Pageable pageable = PageRequest.of(0, 10);
        when(classService.listClasses(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(responseDTO), pageable, 1));

        ResponseEntity<ApiResponse<PageResponse<AcademicClassResponseDTO>>> response =
                classController.getMyClasses(admin, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).contains("Todas as turmas");
    }

    @Test
    @DisplayName("Deve buscar turma por ID")
    void shouldGetClassById() {
        when(classService.getClassById(classId)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<AcademicClassDetailDTO>> response = classController.getClassById(classId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getId()).isEqualTo(classId);
    }

    @Test
    @DisplayName("Deve criar turma com sucesso")
    void shouldCreateClass() {
        AcademicClassCreateDTO createDTO = AcademicClassCreateDTO.builder()
                .subjectName("Enfermagem")
                .classCode("T01")
                .academicPeriod("2026.2")
                .professorId(profId)
                .studentIds(Set.of(studentId))
                .build();

        when(classService.createClass(createDTO)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<AcademicClassDetailDTO>> response = classController.createClass(createDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar turma com sucesso")
    void shouldUpdateClass() {
        AcademicClassUpdateDTO updateDTO = AcademicClassUpdateDTO.builder()
                .subjectName("Enfermagem Cirúrgica")
                .classCode("T02")
                .academicPeriod("2026.2")
                .professorId(profId)
                .build();

        when(classService.updateClass(classId, updateDTO)).thenReturn(detailDTO);

        ResponseEntity<ApiResponse<AcademicClassDetailDTO>> response = classController.updateClass(classId, updateDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve encerrar turma com sucesso")
    void shouldCloseClass() {
        AcademicClassCloseDTO closeDTO = new AcademicClassCloseDTO(true);
        responseDTO.setIsClosed(true);
        when(classService.updateClassClosedStatus(classId, true)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<AcademicClassResponseDTO>> response = classController.updateStatus(classId, closeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Turma encerrada com sucesso.");
    }

    @Test
    @DisplayName("Deve reabrir turma com sucesso")
    void shouldReopenClass() {
        AcademicClassCloseDTO closeDTO = new AcademicClassCloseDTO(false);
        responseDTO.setIsClosed(false);
        when(classService.updateClassClosedStatus(classId, false)).thenReturn(responseDTO);

        ResponseEntity<ApiResponse<AcademicClassResponseDTO>> response = classController.updateStatus(classId, closeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Turma reaberta com sucesso.");
    }

    @Test
    @DisplayName("Deve excluir turma com sucesso")
    void shouldDeleteClass() {
        ResponseEntity<ApiResponse<Void>> response = classController.deleteClass(classId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(classService).deleteClass(classId);
    }
}
