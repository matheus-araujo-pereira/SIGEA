package br.ufs.sigea.academic.report.service;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.activity.domain.data.FiveWTwoHItemData;
import br.ufs.sigea.academic.activity.domain.data.GutItemData;
import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.IshikawaData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
import br.ufs.sigea.academic.activity.repository.ActivitySubmissionRepository;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.repository.AcademicClassRepository;
import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
import br.ufs.sigea.academic.dashboard.dto.GttMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.PedagogicalMetricsDTO;
import br.ufs.sigea.academic.dashboard.service.ClassDashboardService;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private ActivitySubmissionRepository submissionRepository;

    @Mock
    private AcademicClassRepository classRepository;

    @Mock
    private ClassDashboardService classDashboardService;

    @InjectMocks
    private ReportExportService reportExportService;

    private User adminUser;
    private User professorUser;
    private User otherProfessorUser;
    private User studentUser;
    private User otherStudentUser;

    private AcademicClass academicClass;
    private Activity activity;
    private ActivitySubmission submission;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Administrador SIGEA")
                .email("admin@academico.ufs.br")
                .role(UserRole.ADMIN)
                .build();

        professorUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Profª. Dra. Ana Waleska")
                .email("anawaleska@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        otherProfessorUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Prof. Dr. Gilton Ferreira")
                .email("gilton@academico.ufs.br")
                .role(UserRole.PROFESSOR)
                .build();

        studentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Matheus Araujo Pereira")
                .email("matheusaraujopereira@academico.ufs.br")
                .registrationNumber("20260001001")
                .role(UserRole.STUDENT)
                .build();

        otherStudentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Outro Estudante")
                .email("outro.aluno@academico.ufs.br")
                .registrationNumber("20260001002")
                .role(UserRole.STUDENT)
                .build();

        academicClass = AcademicClass.builder()
                .id(UUID.randomUUID())
                .subjectName("Enfermagem Hospitalar")
                .classCode("T01")
                .academicPeriod("2026.1")
                .professor(professorUser)
                .isClosed(false)
                .build();

        ClinicalCaseData cc = ClinicalCaseData.builder()
                .patientName("Maria José da Silva")
                .bed("Leito 14 - Clínica Médica")
                .patientDays(5)
                .admissionNotes("Paciente admitida com sepse de foco urinário.")
                .build();

        activity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Auditoria GTT - Caso 1")
                .academicClass(academicClass)
                .clinicalCaseData(cc)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        IdentifiedTriggerData trigger1 = IdentifiedTriggerData.builder()
                .triggerId(UUID.randomUUID())
                .triggerCode("M5")
                .triggerName("Aumento da Creatinina Sérica")
                .moduleCode("M")
                .isHarm(true)
                .harmSeverityLetter("F")
                .clinicalJustification("Elevação de creatinina > 3x após início de vancomicina com prolongamento do tempo de internação.")
                .build();

        IdentifiedTriggerData trigger2 = IdentifiedTriggerData.builder()
                .triggerId(UUID.randomUUID())
                .triggerCode("C7")
                .triggerName("Queda do Leito")
                .moduleCode("C")
                .isHarm(false)
                .harmSeverityLetter("C")
                .clinicalJustification("Queda sem dano físico após tentativa de mobilização.")
                .build();

        IshikawaData ishi = IshikawaData.builder()
                .methodCauses(List.of("Falta de protocolo de dosagem sérica"))
                .machineCauses(List.of("Bomba infusora descalibrada"))
                .measurementCauses(List.of("Atraso na coleta de creatinina"))
                .environmentCauses(List.of("Sobrecarga no plantão noturno"))
                .manpowerCauses(List.of("Subdimensionamento da equipe"))
                .materialCauses(List.of())
                .build();

        GutItemData gut = GutItemData.builder()
                .problem("Toxicidade renal por antimicrobiano")
                .gravity(5)
                .urgency(4)
                .trend(4)
                .build();

        FiveWTwoHItemData fwh = FiveWTwoHItemData.builder()
                .what("Criar protocolo de dosagem sérica")
                .why("Evitar nefrotoxicidade")
                .where("UTI e Enfermarias")
                .who("Comissão de Farmácia")
                .when("Imediato")
                .how("Revisão de POP")
                .howMuch("R$ 0,00")
                .build();

        QualityToolsData qt = QualityToolsData.builder()
                .ishikawa(ishi)
                .gutItems(List.of(gut))
                .fiveWTwoHItems(List.of(fwh))
                .build();

        submission = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(activity)
                .student(studentUser)
                .identifiedTriggers(List.of(trigger1, trigger2))
                .qualityToolsData(qt)
                .submissionDate(Instant.now().minusSeconds(3600))
                .grade(new BigDecimal("9.50"))
                .professorFeedback("Excelente correlação de causalidade e identificação precisa do gatilho M5.")
                .gradedAt(Instant.now().minusSeconds(1800))
                .build();
    }

    // =========================================================================
    // Testes de generateSubmissionAuditPdf
    // =========================================================================

    @Test
    @DisplayName("Deve gerar PDF de auditoria com sucesso quando o usuário for o aluno autor")
    void shouldGenerateSubmissionAuditPdfForStudentOwner() {
        when(submissionRepository.findByIdWithDetails(submission.getId())).thenReturn(Optional.of(submission));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(submission.getId(), studentUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Deve gerar PDF de auditoria com sucesso quando o usuário for o professor da turma")
    void shouldGenerateSubmissionAuditPdfForClassProfessor() {
        when(submissionRepository.findByIdWithDetails(submission.getId())).thenReturn(Optional.of(submission));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(submission.getId(), professorUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Deve gerar PDF de auditoria com sucesso quando o usuário for ADMIN")
    void shouldGenerateSubmissionAuditPdfForAdmin() {
        when(submissionRepository.findByIdWithDetails(submission.getId())).thenReturn(Optional.of(submission));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(submission.getId(), adminUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando aluno tentar baixar submissão de outro")
    void shouldThrowAccessDeniedWhenOtherStudentTriesToDownloadPdf() {
        when(submissionRepository.findByIdWithDetails(submission.getId())).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> reportExportService.generateSubmissionAuditPdf(submission.getId(), otherStudentUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você só pode visualizar e exportar relatórios de suas próprias submissões.");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando docente tentar baixar submissão de outra turma")
    void shouldThrowAccessDeniedWhenOtherProfessorTriesToDownloadPdf() {
        when(submissionRepository.findByIdWithDetails(submission.getId())).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> reportExportService.generateSubmissionAuditPdf(submission.getId(), otherProfessorUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não tem permissão para acessar submissões de turmas de outros docentes.");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando submissão não existir")
    void shouldThrowResourceNotFoundWhenSubmissionDoesNotExist() {
        UUID randomId = UUID.randomUUID();
        when(submissionRepository.findByIdWithDetails(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportExportService.generateSubmissionAuditPdf(randomId, adminUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Submissão não encontrada");
    }

    @Test
    @DisplayName("Deve gerar PDF de submissão sem gatilhos, sem ferramentas e ainda não avaliada")
    void shouldGeneratePdfForMinimalUngradedSubmission() {
        Activity minimalActivity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Atividade Básica")
                .academicClass(academicClass)
                .clinicalCaseData(null)
                .deadline(null)
                .build();

        User unregStudent = User.builder()
                .id(UUID.randomUUID())
                .fullName("Aluno Sem Matrícula")
                .email("alunosem@academico.ufs.br")
                .registrationNumber(null)
                .role(UserRole.STUDENT)
                .build();

        ActivitySubmission minimalSub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(minimalActivity)
                .student(unregStudent)
                .identifiedTriggers(null)
                .qualityToolsData(null)
                .submissionDate(null)
                .grade(null)
                .professorFeedback(null)
                .gradedAt(null)
                .build();

        when(submissionRepository.findByIdWithDetails(minimalSub.getId())).thenReturn(Optional.of(minimalSub));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(minimalSub.getId(), adminUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    // =========================================================================
    // Testes de generateClassEpidemiologicalBulletinPdf
    // =========================================================================

    @Test
    @DisplayName("Deve gerar Boletim Epidemiológico em PDF com sucesso para o professor da turma")
    void shouldGenerateClassEpidemiologicalBulletinPdfForProfessor() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));
        when(submissionRepository.findByClassIdWithDetails(academicClass.getId())).thenReturn(List.of(submission));

        Map<String, Long> harmMap = new HashMap<>();
        harmMap.put("E", 3L);
        harmMap.put("F", 1L);

        GttMetricsDTO gtt = GttMetricsDTO.builder()
                .totalAdmissions(10)
                .totalPatientDays(50)
                .totalAdverseEvents(4)
                .admissionsWithAdverseEvents(3)
                .adverseEventsPer1000PatientDays(80.0)
                .adverseEventsPer100Admissions(40.0)
                .percentAdmissionsWithAdverseEvents(30.0)
                .harmDistribution(harmMap)
                .build();

        PedagogicalMetricsDTO ped = PedagogicalMetricsDTO.builder()
                .totalActivities(2)
                .totalEnrolledStudents(10)
                .totalSubmissions(10)
                .gradedSubmissions(10)
                .classAverageGrade(8.75)
                .build();

        ClassDashboardDTO dash = ClassDashboardDTO.builder()
                .classId(academicClass.getId())
                .className(academicClass.getFormattedName())
                .professorName(professorUser.getFullName())
                .academicPeriod("2026.1")
                .isClosed(false)
                .gttMetrics(gtt)
                .pedagogicalMetrics(ped)
                .build();

        when(classDashboardService.getClassDashboard(eq(academicClass.getId()), any(User.class))).thenReturn(dash);

        byte[] pdf = reportExportService.generateClassEpidemiologicalBulletinPdf(academicClass.getId(), professorUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Deve gerar Boletim Epidemiológico em PDF com sucesso para ADMIN com turma vazia")
    void shouldGenerateClassEpidemiologicalBulletinPdfForAdminEmptyClass() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));
        when(submissionRepository.findByClassIdWithDetails(academicClass.getId())).thenReturn(List.of());

        GttMetricsDTO gtt = GttMetricsDTO.builder()
                .totalAdmissions(0)
                .totalPatientDays(0)
                .totalAdverseEvents(0)
                .admissionsWithAdverseEvents(0)
                .adverseEventsPer1000PatientDays(0.0)
                .adverseEventsPer100Admissions(0.0)
                .percentAdmissionsWithAdverseEvents(0.0)
                .harmDistribution(new HashMap<>())
                .build();

        PedagogicalMetricsDTO ped = PedagogicalMetricsDTO.builder()
                .totalActivities(0)
                .totalEnrolledStudents(0)
                .totalSubmissions(0)
                .gradedSubmissions(0)
                .classAverageGrade(0.0)
                .build();

        ClassDashboardDTO dash = ClassDashboardDTO.builder()
                .classId(academicClass.getId())
                .className(academicClass.getFormattedName())
                .professorName(professorUser.getFullName())
                .academicPeriod("2026.1")
                .isClosed(true)
                .gttMetrics(gtt)
                .pedagogicalMetrics(ped)
                .build();

        when(classDashboardService.getClassDashboard(eq(academicClass.getId()), any(User.class))).thenReturn(dash);

        byte[] pdf = reportExportService.generateClassEpidemiologicalBulletinPdf(academicClass.getId(), adminUser);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando aluno tentar gerar boletim da turma")
    void shouldThrowAccessDeniedWhenStudentTriesToGenerateBulletinPdf() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));

        assertThatThrownBy(() -> reportExportService.generateClassEpidemiologicalBulletinPdf(academicClass.getId(), studentUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Estudantes não têm permissão para exportar dados consolidados ou boletins da turma.");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando docente de outra turma tentar gerar boletim")
    void shouldThrowAccessDeniedWhenOtherProfessorTriesToGenerateBulletinPdf() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));

        assertThatThrownBy(() -> reportExportService.generateClassEpidemiologicalBulletinPdf(academicClass.getId(), otherProfessorUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não tem permissão para exportar relatórios de turmas de outros docentes.");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando turma não for encontrada")
    void shouldThrowResourceNotFoundWhenClassDoesNotExist() {
        UUID randomId = UUID.randomUUID();
        when(classRepository.findByIdWithStudents(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportExportService.generateClassEpidemiologicalBulletinPdf(randomId, adminUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma acadêmica não encontrada");
    }

    // =========================================================================
    // Testes de generateClassResearchCsv
    // =========================================================================

    @Test
    @DisplayName("Deve gerar base de dados brutos em CSV com sucesso para o professor da turma")
    void shouldGenerateClassResearchCsvForProfessor() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));
        when(submissionRepository.findByClassIdWithDetails(academicClass.getId())).thenReturn(List.of(submission));

        byte[] csvBytes = reportExportService.generateClassResearchCsv(academicClass.getId(), professorUser);

        assertThat(csvBytes).isNotNull().isNotEmpty();
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        // Deve conter o BOM UTF-8
        assertThat(csv.charAt(0)).isEqualTo('\uFEFF');
        // Deve conter cabeçalhos
        assertThat(csv).contains("submission_id,turma,semestre,docente_responsavel,aluno_nome");
        // Deve conter os dados da submissão
        assertThat(csv).contains("Matheus Araujo Pereira");
        assertThat(csv).contains("matheusaraujopereira@academico.ufs.br");
        assertThat(csv).contains("Profª. Dra. Ana Waleska");
        assertThat(csv).contains("M5;C7");
        assertThat(csv).contains("9.50");
        assertThat(csv).contains("SIM"); // possui_evento_adverso
    }

    @Test
    @DisplayName("Deve gerar CSV com turma vazia e submissão sem dados acessórios")
    void shouldGenerateCsvForEmptyOrSparseSubmissions() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));

        Activity emptyActivity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Atividade Sem Prontuário")
                .academicClass(academicClass)
                .clinicalCaseData(null)
                .build();

        User unreg = User.builder()
                .id(UUID.randomUUID())
                .fullName("Aluno Vazio")
                .email("aluno.vazio@academico.ufs.br")
                .role(UserRole.STUDENT)
                .build();

        ActivitySubmission sparseSub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(emptyActivity)
                .student(unreg)
                .identifiedTriggers(null)
                .qualityToolsData(null)
                .grade(null)
                .build();

        when(submissionRepository.findByClassIdWithDetails(academicClass.getId())).thenReturn(List.of(sparseSub));

        byte[] csvBytes = reportExportService.generateClassResearchCsv(academicClass.getId(), adminUser);

        assertThat(csvBytes).isNotNull().isNotEmpty();
        String csv = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csv).contains("Aluno Vazio");
        assertThat(csv).contains("NAO");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando estudante tentar baixar CSV")
    void shouldThrowAccessDeniedWhenStudentTriesToDownloadCsv() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));

        assertThatThrownBy(() -> reportExportService.generateClassResearchCsv(academicClass.getId(), studentUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Estudantes não têm permissão para exportar dados consolidados ou boletins da turma.");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando turma não for encontrada para CSV")
    void shouldThrowResourceNotFoundWhenClassDoesNotExistForCsv() {
        UUID randomId = UUID.randomUUID();
        when(classRepository.findByIdWithStudents(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportExportService.generateClassResearchCsv(randomId, adminUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma acadêmica não encontrada");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando ocorrer erro interno na geração de PDF de submissão")
    void shouldThrowBusinessExceptionWhenPdfGenerationFailsForSubmission() {
        ActivitySubmission mockSub = org.mockito.Mockito.mock(ActivitySubmission.class);
        when(mockSub.getId()).thenReturn(UUID.randomUUID());
        when(mockSub.getStudent()).thenReturn(studentUser);
        when(mockSub.getActivity()).thenThrow(new RuntimeException("Simulated error"));
        when(submissionRepository.findByIdWithDetails(mockSub.getId())).thenReturn(Optional.of(mockSub));

        assertThatThrownBy(() -> reportExportService.generateSubmissionAuditPdf(mockSub.getId(), studentUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Falha ao gerar relatório de auditoria em PDF");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando ocorrer erro interno na geração do boletim em PDF")
    void shouldThrowBusinessExceptionWhenPdfGenerationFailsForBulletin() {
        AcademicClass mockClass = org.mockito.Mockito.mock(AcademicClass.class);
        when(mockClass.getId()).thenReturn(UUID.randomUUID());
        when(mockClass.getProfessor()).thenReturn(professorUser);
        when(mockClass.getFormattedName()).thenThrow(new RuntimeException("Simulated error"));
        when(classRepository.findByIdWithStudents(mockClass.getId())).thenReturn(Optional.of(mockClass));

        ClassDashboardDTO dash = ClassDashboardDTO.builder()
                .gttMetrics(GttMetricsDTO.builder()
                        .totalAdmissions(1)
                        .totalPatientDays(1)
                        .totalAdverseEvents(0)
                        .harmDistribution(Map.of())
                        .build())
                .pedagogicalMetrics(PedagogicalMetricsDTO.builder().build())
                .build();
        when(classDashboardService.getClassDashboard(eq(mockClass.getId()), any(User.class))).thenReturn(dash);

        assertThatThrownBy(() -> reportExportService.generateClassEpidemiologicalBulletinPdf(mockClass.getId(), professorUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Falha ao gerar boletim epidemiológico em PDF");
    }

    @Test
    @DisplayName("Deve gerar PDF de submissão com caso clínico e gatilhos contendo campos nulos e feedback preenchido")
    void shouldGenerateSubmissionPdfWithNullClinicalCaseAndTriggerDetails() {
        ClinicalCaseData ccWithNulls = ClinicalCaseData.builder()
                .patientName(null)
                .bed(null)
                .patientDays(null)
                .admissionNotes(null)
                .build();

        Activity customActivity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Atividade com Detalhes Nulos")
                .academicClass(academicClass)
                .clinicalCaseData(ccWithNulls)
                .deadline(Instant.now())
                .build();

        IdentifiedTriggerData triggerWithoutHarm = IdentifiedTriggerData.builder()
                .triggerId(UUID.randomUUID())
                .triggerCode("I1")
                .triggerName("Reintubação Traqueal")
                .moduleCode("I")
                .isHarm(false)
                .harmSeverityLetter(null)
                .clinicalJustification(null)
                .build();

        IshikawaData emptyIshikawa = IshikawaData.builder()
                .methodCauses(null)
                .machineCauses(List.of())
                .measurementCauses(null)
                .environmentCauses(null)
                .manpowerCauses(null)
                .materialCauses(null)
                .build();

        GutItemData gutItem = GutItemData.builder()
                .problem("Falta de insumo")
                .gravity(4)
                .urgency(3)
                .trend(2)
                .build();

        FiveWTwoHItemData fwhItem = FiveWTwoHItemData.builder()
                .what("Repor estoque")
                .why("Evitar desassistência")
                .where("Farmácia Central")
                .who("Enfermeiro RT")
                .when("Imediato")
                .how("Requisição de urgência")
                .howMuch("R$ 0,00")
                .build();

        QualityToolsData qt = QualityToolsData.builder()
                .ishikawa(emptyIshikawa)
                .gutItems(List.of(gutItem))
                .fiveWTwoHItems(List.of(fwhItem))
                .build();

        ActivitySubmission customSub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(customActivity)
                .student(studentUser)
                .submissionDate(Instant.now())
                .identifiedTriggers(List.of(triggerWithoutHarm))
                .qualityToolsData(qt)
                .grade(BigDecimal.valueOf(8.5))
                .gradedAt(Instant.now())
                .professorFeedback("Excelente justificativa clínica e plano de ação.")
                .build();

        when(submissionRepository.findByIdWithDetails(customSub.getId())).thenReturn(Optional.of(customSub));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(customSub.getId(), studentUser);
        assertThat(pdf).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Deve gerar Boletim Epidemiológico em PDF com turma encerrada e gatilhos com campos nulos")
    void shouldGenerateBulletinPdfWithNullTriggerFieldsAndClosedClass() {
        AcademicClass closedClass = AcademicClass.builder()
                .id(UUID.randomUUID())
                .subjectName("Enfermagem Cirúrgica")
                .classCode("T02")
                .academicPeriod("2025.2")
                .professor(professorUser)
                .isClosed(true)
                .build();

        when(classRepository.findByIdWithStudents(closedClass.getId())).thenReturn(Optional.of(closedClass));

        IdentifiedTriggerData t1 = IdentifiedTriggerData.builder()
                .triggerCode(null)
                .moduleCode(null)
                .triggerName(null)
                .build();

        IdentifiedTriggerData t2 = IdentifiedTriggerData.builder()
                .triggerCode("S1")
                .moduleCode(null)
                .triggerName(null)
                .build();

        ActivitySubmission sub1 = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .identifiedTriggers(null)
                .build();

        ActivitySubmission sub2 = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .identifiedTriggers(List.of(t1, t2))
                .build();

        when(submissionRepository.findByClassIdWithDetails(closedClass.getId())).thenReturn(List.of(sub1, sub2));

        GttMetricsDTO gtt = GttMetricsDTO.builder()
                .totalAdmissions(2)
                .totalPatientDays(10)
                .totalAdverseEvents(0)
                .admissionsWithAdverseEvents(0)
                .adverseEventsPer1000PatientDays(0.0)
                .adverseEventsPer100Admissions(0.0)
                .percentAdmissionsWithAdverseEvents(0.0)
                .harmDistribution(Map.of())
                .build();

        PedagogicalMetricsDTO pedWithNulls = PedagogicalMetricsDTO.builder()
                .totalActivities(null)
                .totalEnrolledStudents(null)
                .totalSubmissions(null)
                .build();

        ClassDashboardDTO dash = ClassDashboardDTO.builder()
                .classId(closedClass.getId())
                .className(closedClass.getFormattedName())
                .professorName(professorUser.getFullName())
                .academicPeriod("2025.2")
                .isClosed(true)
                .gttMetrics(gtt)
                .pedagogicalMetrics(pedWithNulls)
                .build();

        when(classDashboardService.getClassDashboard(eq(closedClass.getId()), any(User.class))).thenReturn(dash);

        byte[] pdf = reportExportService.generateClassEpidemiologicalBulletinPdf(closedClass.getId(), professorUser);
        assertThat(pdf).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Deve gerar CSV cobrindo escape de aspas, múltiplos itens GUT e gravidade sem dano")
    void shouldCoverCsvEscapeAndMultipleGutItems() {
        when(classRepository.findByIdWithStudents(academicClass.getId())).thenReturn(Optional.of(academicClass));

        ClinicalCaseData ccWithNullFields = ClinicalCaseData.builder()
                .patientName(null)
                .bed(null)
                .patientDays(null)
                .build();

        Activity actWithQuotes = Activity.builder()
                .id(UUID.randomUUID())
                .title("Caso \"Crítico\" de UTI")
                .academicClass(academicClass)
                .clinicalCaseData(ccWithNullFields)
                .deadline(Instant.now())
                .build();

        GutItemData highGut = GutItemData.builder()
                .problem("Item Maior")
                .gravity(5)
                .urgency(5)
                .trend(5)
                .build();

        GutItemData lowGut = GutItemData.builder()
                .problem("Item Menor")
                .gravity(1)
                .urgency(1)
                .trend(1)
                .build();

        QualityToolsData qt = QualityToolsData.builder()
                .gutItems(List.of(highGut, lowGut))
                .build();

        IdentifiedTriggerData triggerNoHarm = IdentifiedTriggerData.builder()
                .triggerCode("C1")
                .triggerName("Gatilho Geral")
                .isHarm(false)
                .harmSeverityLetter(" ")
                .build();

        IdentifiedTriggerData triggerNullHarm = IdentifiedTriggerData.builder()
                .triggerCode("C2")
                .triggerName("Gatilho Null")
                .isHarm(null)
                .harmSeverityLetter(null)
                .build();

        ActivitySubmission sub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(actWithQuotes)
                .student(studentUser)
                .submissionDate(Instant.now())
                .identifiedTriggers(List.of(triggerNoHarm, triggerNullHarm))
                .qualityToolsData(qt)
                .grade(null)
                .build();

        ActivitySubmission subNullTools = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(actWithQuotes)
                .student(studentUser)
                .submissionDate(Instant.now())
                .identifiedTriggers(List.of())
                .qualityToolsData(QualityToolsData.builder().ishikawa(null).gutItems(null).build())
                .grade(null)
                .build();

        when(submissionRepository.findByClassIdWithDetails(academicClass.getId())).thenReturn(List.of(sub, subNullTools));

        byte[] csvBytes = reportExportService.generateClassResearchCsv(academicClass.getId(), adminUser);
        assertThat(csvBytes).isNotNull().isNotEmpty();
        String csv = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csv).contains("\"\"Crítico\"\"");
        assertThat(csv).contains("SEM_DANO");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando docente for nulo na validação de submissão")
    void shouldThrowAccessDeniedWhenProfessorIsNullInSubmissionValidation() {
        AcademicClass classWithoutProf = AcademicClass.builder()
                .id(UUID.randomUUID())
                .subjectName("Saúde Coletiva")
                .classCode("T03")
                .academicPeriod("2026.1")
                .professor(null)
                .build();

        Activity act = Activity.builder()
                .id(UUID.randomUUID())
                .academicClass(classWithoutProf)
                .build();

        ActivitySubmission sub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(act)
                .student(studentUser)
                .build();

        when(submissionRepository.findByIdWithDetails(sub.getId())).thenReturn(Optional.of(sub));

        assertThatThrownBy(() -> reportExportService.generateSubmissionAuditPdf(sub.getId(), professorUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não tem permissão para acessar submissões de turmas de outros docentes.");
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando docente for nulo na validação de turma")
    void shouldThrowAccessDeniedWhenProfessorIsNullInClassValidation() {
        AcademicClass classWithoutProf = AcademicClass.builder()
                .id(UUID.randomUUID())
                .subjectName("Saúde Coletiva")
                .classCode("T03")
                .academicPeriod("2026.1")
                .professor(null)
                .build();

        when(classRepository.findByIdWithStudents(classWithoutProf.getId())).thenReturn(Optional.of(classWithoutProf));

        assertThatThrownBy(() -> reportExportService.generateClassEpidemiologicalBulletinPdf(classWithoutProf.getId(), professorUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Você não tem permissão para exportar relatórios de turmas de outros docentes.");
    }

    @Test
    @DisplayName("Deve gerar PDF de submissão com notas e feedback contendo apenas espaços em branco e listas vazias")
    void shouldGenerateSubmissionPdfWithBlankNotesAndFeedback() {
        ClinicalCaseData ccBlank = ClinicalCaseData.builder()
                .patientName("Paciente Teste")
                .bed("10")
                .patientDays(2)
                .admissionNotes("   ")
                .build();

        Activity customActivity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Atividade com Espaços")
                .academicClass(academicClass)
                .clinicalCaseData(ccBlank)
                .deadline(Instant.now())
                .build();

        QualityToolsData qtEmptyLists = QualityToolsData.builder()
                .ishikawa(null)
                .gutItems(List.of())
                .fiveWTwoHItems(List.of())
                .build();

        ActivitySubmission blankSub = ActivitySubmission.builder()
                .id(UUID.randomUUID())
                .activity(customActivity)
                .student(studentUser)
                .submissionDate(Instant.now())
                .identifiedTriggers(List.of())
                .qualityToolsData(qtEmptyLists)
                .grade(new BigDecimal("7.00"))
                .gradedAt(Instant.now())
                .professorFeedback("   ")
                .build();

        when(submissionRepository.findByIdWithDetails(blankSub.getId())).thenReturn(Optional.of(blankSub));

        byte[] pdf = reportExportService.generateSubmissionAuditPdf(blankSub.getId(), studentUser);
        assertThat(pdf).isNotNull().isNotEmpty();
    }
}
