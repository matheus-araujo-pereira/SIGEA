package br.ufs.sigea.academic.sus.service;

import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.academic.sus.domain.SusEvaluation;
import br.ufs.sigea.academic.sus.dto.SusClassSummaryDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationCreateDTO;
import br.ufs.sigea.academic.sus.dto.SusEvaluationResponseDTO;
import br.ufs.sigea.academic.sus.dto.SusGeneralSummaryDTO;
import br.ufs.sigea.academic.sus.repository.SusEvaluationRepository;
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

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusEvaluationServiceTest {

    @Mock
    private SusEvaluationRepository susEvaluationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AcademicClassRepository academicClassRepository;

    @InjectMocks
    private SusEvaluationService service;

    private User student;
    private AcademicClass academicClass;
    private UUID studentId;
    private UUID classId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        classId = UUID.randomUUID();

        student = User.builder()
                .id(studentId)
                .fullName("Matheus Araujo")
                .email("matheus@academico.ufs.br")
                .registrationNumber("20260001001")
                .role(UserRole.STUDENT)
                .build();

        academicClass = AcademicClass.builder()
                .id(classId)
                .subjectName("Enfermagem Cirúrgica")
                .classCode("T01")
                .academicPeriod("2026.1")
                .students(new HashSet<>(Set.of(student)))
                .build();
    }

    @Test
    @DisplayName("Deve submeter avaliação SUS vinculada a uma turma com sucesso")
    void shouldCreateEvaluationWithClass() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .academicClassId(classId)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .suggestions("Ótimo sistema")
                .build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.existsByStudentIdAndAcademicClassId(studentId, classId)).thenReturn(false);
        when(susEvaluationRepository.save(any(SusEvaluation.class))).thenAnswer(inv -> {
            SusEvaluation e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            e.setCreatedAt(Instant.now());
            return e;
        });

        SusEvaluationResponseDTO response = service.createEvaluation(dto, studentId);

        assertThat(response).isNotNull();
        assertThat(response.getScore()).isEqualTo(100.0);
        assertThat(response.getAdjectiveRating()).isEqualTo("Melhor Imaginável");
        assertThat(response.getAcceptability()).isEqualTo("Aceitável");
        assertThat(response.getGradeLevel()).isEqualTo("A");
        assertThat(response.getClassName()).isEqualTo("Enfermagem Cirúrgica - T01 - 2026.1");
        assertThat(response.getSuggestions()).isEqualTo("Ótimo sistema");
    }

    @Test
    @DisplayName("Deve submeter avaliação SUS geral (sem turma) com sucesso")
    void shouldCreateEvaluationWithoutClass() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .academicClassId(null)
                .q1(4).q2(2).q3(4).q4(2).q5(4).q6(2).q7(4).q8(2).q9(4).q10(2)
                .build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(susEvaluationRepository.existsByStudentIdAndAcademicClassIsNull(studentId)).thenReturn(false);
        when(susEvaluationRepository.save(any(SusEvaluation.class))).thenAnswer(inv -> {
            SusEvaluation e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            e.setCreatedAt(Instant.now());
            return e;
        });

        SusEvaluationResponseDTO response = service.createEvaluation(dto, studentId);

        assertThat(response).isNotNull();
        assertThat(response.getScore()).isEqualTo(75.0);
        assertThat(response.getAdjectiveRating()).isEqualTo("Bom");
        assertThat(response.getAcceptability()).isEqualTo("Aceitável");
        assertThat(response.getGradeLevel()).isEqualTo("C");
        assertThat(response.getAcademicClassId()).isNull();
        assertThat(response.getClassName()).isNull();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando estudante não for encontrado")
    void shouldThrowWhenStudentNotFound() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder().build();
        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createEvaluation(dto, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Estudante não encontrado");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando turma não for encontrada")
    void shouldThrowWhenAcademicClassNotFound() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .academicClassId(classId)
                .build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(academicClassRepository.findById(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createEvaluation(dto, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando estudante já avaliou a turma")
    void shouldThrowWhenAlreadyEvaluatedClass() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder()
                .academicClassId(classId)
                .build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.existsByStudentIdAndAcademicClassId(studentId, classId)).thenReturn(true);

        assertThatThrownBy(() -> service.createEvaluation(dto, studentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Você já enviou a avaliação de usabilidade desta turma");
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando estudante já avaliou o sistema geral")
    void shouldThrowWhenAlreadyEvaluatedSystemGeneral() {
        SusEvaluationCreateDTO dto = SusEvaluationCreateDTO.builder().build();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(susEvaluationRepository.existsByStudentIdAndAcademicClassIsNull(studentId)).thenReturn(true);

        assertThatThrownBy(() -> service.createEvaluation(dto, studentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Você já enviou a avaliação geral de usabilidade do sistema");
    }

    @Test
    @DisplayName("Deve buscar avaliação do usuário por turma ou geral")
    void shouldGetMyEvaluation() {
        SusEvaluation eval = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(academicClass)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .score(100.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("A")
                .createdAt(Instant.now())
                .build();

        when(susEvaluationRepository.findByStudentIdAndAcademicClassId(studentId, classId))
                .thenReturn(Optional.of(eval));

        Optional<SusEvaluationResponseDTO> res = service.getMyEvaluation(studentId, classId);
        assertThat(res).isPresent();
        assertThat(res.get().getScore()).isEqualTo(100.0);

        when(susEvaluationRepository.findByStudentIdAndAcademicClassIsNull(studentId))
                .thenReturn(Optional.empty());
        Optional<SusEvaluationResponseDTO> emptyRes = service.getMyEvaluation(studentId, null);
        assertThat(emptyRes).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todas as avaliações de um estudante")
    void shouldGetMyEvaluations() {
        SusEvaluation eval = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(academicClass)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .score(100.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("A")
                .createdAt(Instant.now())
                .build();

        when(susEvaluationRepository.findByStudentIdWithDetails(studentId)).thenReturn(List.of(eval));

        List<SusEvaluationResponseDTO> list = service.getMyEvaluations(studentId);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getStudentName()).isEqualTo("Matheus Araujo");
    }

    @Test
    @DisplayName("Deve gerar sumário da turma quando não houver avaliações")
    void shouldGetClassSummaryEmpty() {
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.findByAcademicClassIdWithDetails(classId)).thenReturn(List.of());

        SusClassSummaryDTO summary = service.getClassSummary(classId);

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalEvaluations()).isEqualTo(0);
        assertThat(summary.getAverageScore()).isEqualTo(0.0);
        assertThat(summary.getAdjectiveRating()).isEqualTo("Sem Respostas");
        assertThat(summary.getAcceptability()).isEqualTo("Sem Respostas");
        assertThat(summary.getGradeLevel()).isEqualTo("N/A");
        assertThat(summary.getEnrolledStudentsCount()).isEqualTo(1);
        assertThat(summary.getResponseRatePercentage()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve gerar sumário da turma quando turma não possuir estudantes matriculados")
    void shouldGetClassSummaryWithZeroEnrolledStudents() {
        academicClass.setStudents(null);
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.findByAcademicClassIdWithDetails(classId)).thenReturn(List.of());

        SusClassSummaryDTO summary = service.getClassSummary(classId);

        assertThat(summary).isNotNull();
        assertThat(summary.getEnrolledStudentsCount()).isEqualTo(0);
        assertThat(summary.getResponseRatePercentage()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao gerar sumário de turma inexistente")
    void shouldThrowWhenClassSummaryNotFound() {
        when(academicClassRepository.findById(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getClassSummary(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    @Test
    @DisplayName("Deve gerar sumário da turma com avaliações e métricas consolidadas")
    void shouldGetClassSummaryWithEvaluations() {
        SusEvaluation eval1 = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(academicClass)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .score(100.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("A")
                .createdAt(Instant.now())
                .build();

        SusEvaluation eval2 = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(academicClass)
                .q1(3).q2(3).q3(3).q4(3).q5(3).q6(3).q7(3).q8(3).q9(3).q10(3)
                .score(50.0)
                .adjectiveRating("Regular")
                .acceptability("Marginal")
                .gradeLevel("F")
                .createdAt(Instant.now())
                .build();

        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.findByAcademicClassIdWithDetails(classId)).thenReturn(List.of(eval1, eval2));

        SusClassSummaryDTO summary = service.getClassSummary(classId);

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalEvaluations()).isEqualTo(2);
        assertThat(summary.getAverageScore()).isEqualTo(75.0);
        assertThat(summary.getAdjectiveRating()).isEqualTo("Bom");
        assertThat(summary.getAcceptability()).isEqualTo("Aceitável");
        assertThat(summary.getGradeLevel()).isEqualTo("C");
        assertThat(summary.getAdjectiveDistribution()).containsEntry("Melhor Imaginável", 1L);
        assertThat(summary.getAdjectiveDistribution()).containsEntry("Regular", 1L);
        assertThat(summary.getQuestionAverages()).hasSize(10);
        assertThat(summary.getQuestionAverages().get(0)).isEqualTo(4.0); // (5 + 3) / 2
        assertThat(summary.getEvaluations()).hasSize(2);
    }

    @Test
    @DisplayName("Deve gerar sumário geral global com e sem avaliações")
    void shouldGetGeneralSummary() {
        when(susEvaluationRepository.findAllWithDetails()).thenReturn(List.of());
        SusGeneralSummaryDTO emptySummary = service.getGeneralSummary();
        assertThat(emptySummary.getTotalEvaluations()).isEqualTo(0);
        assertThat(emptySummary.getAverageScore()).isEqualTo(0.0);

        SusEvaluation eval = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .score(100.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("A")
                .createdAt(Instant.now())
                .build();

        when(susEvaluationRepository.findAllWithDetails()).thenReturn(List.of(eval));
        SusGeneralSummaryDTO summary = service.getGeneralSummary();
        assertThat(summary.getTotalEvaluations()).isEqualTo(1);
        assertThat(summary.getAverageScore()).isEqualTo(100.0);
        assertThat(summary.getAdjectiveRating()).isEqualTo("Melhor Imaginável");
    }

    @Test
    @DisplayName("Deve exportar CSV de avaliações da turma com headers RFC 4180 e UTF-8 BOM")
    void shouldExportClassSusCsv() {
        SusEvaluation eval = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(academicClass)
                .q1(5).q2(1).q3(5).q4(1).q5(5).q6(1).q7(5).q8(1).q9(5).q10(1)
                .score(100.0)
                .adjectiveRating("Melhor Imaginável")
                .acceptability("Aceitável")
                .gradeLevel("A")
                .suggestions("Interface com \"alta\" densidade e excelente usabilidade.")
                .createdAt(Instant.parse("2026-09-20T12:00:00Z"))
                .build();

        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(susEvaluationRepository.findByAcademicClassIdWithDetails(classId)).thenReturn(List.of(eval));

        byte[] csvBytes = service.exportClassSusCsv(classId);

        assertThat(csvBytes).isNotEmpty();
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csvContent.charAt(0)).isEqualTo('\uFEFF');
        assertThat(csvContent).contains("escore_sus,classificacao_adjetiva,aceitabilidade,conceito_escolar,sugestoes");
        assertThat(csvContent).contains("Enfermagem Cirúrgica - T01 - 2026.1");
        assertThat(csvContent).contains("Melhor Imaginável");
        assertThat(csvContent).contains("\"Interface com \"\"alta\"\" densidade e excelente usabilidade.\"");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao exportar CSV de turma inexistente")
    void shouldThrowWhenExportClassCsvClassMissing() {
        when(academicClassRepository.findById(classId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.exportClassSusCsv(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    @Test
    @DisplayName("Deve exportar CSV global de todas as avaliações com sucesso")
    void shouldExportGlobalSusCsv() {
        student.setRegistrationNumber(null);
        SusEvaluation eval = SusEvaluation.builder()
                .id(UUID.randomUUID())
                .student(student)
                .academicClass(null)
                .q1(1).q2(5).q3(1).q4(5).q5(1).q6(5).q7(1).q8(5).q9(1).q10(5)
                .score(0.0)
                .adjectiveRating("Pobre")
                .acceptability("Inaceitável")
                .gradeLevel("F")
                .suggestions(null)
                .createdAt(Instant.parse("2026-09-20T12:00:00Z"))
                .build();

        when(susEvaluationRepository.findAllWithDetails()).thenReturn(List.of(eval));

        byte[] csvBytes = service.exportGlobalSusCsv();

        assertThat(csvBytes).isNotEmpty();
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csvContent).contains("Geral_SIGEA");
        assertThat(csvContent).contains("Pobre");
        assertThat(csvContent).contains("Inaceitável");
    }

    @Test
    @DisplayName("Deve validar faixas do domínio SusEvaluation e métodos estáticos")
    void shouldValidateSusEvaluationDomainCalculations() {
        // Teste de cálculo de escores
        assertThat(SusEvaluation.calculateScore(1, 5, 1, 5, 1, 5, 1, 5, 1, 5)).isEqualTo(0.0);
        assertThat(SusEvaluation.calculateScore(5, 1, 5, 1, 5, 1, 5, 1, 5, 1)).isEqualTo(100.0);
        assertThat(SusEvaluation.calculateScore(3, 3, 3, 3, 3, 3, 3, 3, 3, 3)).isEqualTo(50.0);

        // Teste de classificações adjetivas
        assertThat(SusEvaluation.calculateAdjectiveRating(90.0)).isEqualTo("Melhor Imaginável");
        assertThat(SusEvaluation.calculateAdjectiveRating(75.0)).isEqualTo("Bom");
        assertThat(SusEvaluation.calculateAdjectiveRating(55.0)).isEqualTo("Regular");
        assertThat(SusEvaluation.calculateAdjectiveRating(45.0)).isEqualTo("Pobre");

        // Teste de aceitabilidade
        assertThat(SusEvaluation.calculateAcceptability(70.0)).isEqualTo("Aceitável");
        assertThat(SusEvaluation.calculateAcceptability(69.9)).isEqualTo("Marginal");
        assertThat(SusEvaluation.calculateAcceptability(49.9)).isEqualTo("Inaceitável");

        // Teste de conceitos escolares (grade scale)
        assertThat(SusEvaluation.calculateGradeLevel(95.0)).isEqualTo("A");
        assertThat(SusEvaluation.calculateGradeLevel(85.0)).isEqualTo("B");
        assertThat(SusEvaluation.calculateGradeLevel(75.0)).isEqualTo("C");
        assertThat(SusEvaluation.calculateGradeLevel(65.0)).isEqualTo("D");
        assertThat(SusEvaluation.calculateGradeLevel(55.0)).isEqualTo("F");

        // Teste de PrePersist
        SusEvaluation evaluation = new SusEvaluation();
        evaluation.prePersist();
        assertThat(evaluation.getCreatedAt()).isNotNull();

        Instant fixed = Instant.now().minusSeconds(1000);
        evaluation.setCreatedAt(fixed);
        evaluation.prePersist();
        assertThat(evaluation.getCreatedAt()).isEqualTo(fixed);
    }
}
