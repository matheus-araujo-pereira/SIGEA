package br.ufs.sigea.academic;

import br.ufs.sigea.academic.activity.domain.Activity;
import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import br.ufs.sigea.academic.activity.domain.data.EvolutionNoteData;
import br.ufs.sigea.academic.activity.domain.data.FiveWTwoHItemData;
import br.ufs.sigea.academic.activity.domain.data.GutItemData;
import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.IshikawaData;
import br.ufs.sigea.academic.activity.domain.data.LabExamData;
import br.ufs.sigea.academic.activity.domain.data.PdcaData;
import br.ufs.sigea.academic.activity.domain.data.PrescriptionData;
import br.ufs.sigea.academic.activity.domain.data.ProcedureData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
import br.ufs.sigea.academic.activity.domain.data.SwotData;
import br.ufs.sigea.academic.activity.dto.ActivityCreateDTO;
import br.ufs.sigea.academic.activity.dto.ActivityDetailDTO;
import br.ufs.sigea.academic.activity.dto.ActivityResponseDTO;
import br.ufs.sigea.academic.activity.dto.ActivityUpdateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionCreateDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionGradeDTO;
import br.ufs.sigea.academic.activity.dto.SubmissionResponseDTO;
import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCloseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassCreateDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassDetailDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassResponseDTO;
import br.ufs.sigea.academic.clazz.dto.AcademicClassUpdateDTO;
import br.ufs.sigea.academic.clazz.dto.ClassStudentSummaryDTO;
import br.ufs.sigea.academic.dashboard.dto.ClassDashboardDTO;
import br.ufs.sigea.academic.dashboard.dto.GttMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.PedagogicalMetricsDTO;
import br.ufs.sigea.academic.dashboard.dto.TriggerOccurrenceDTO;
import br.ufs.sigea.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcademicDomainAndDtoTest {

    @Test
    @DisplayName("Deve testar métodos de AcademicClass e ciclo de vida")
    void testAcademicClassEntity() {
        AcademicClass ac = new AcademicClass();
        ac.setId(UUID.randomUUID());
        ac.setSubjectName("Farmacologia");
        ac.setClassCode("T01");
        ac.setAcademicPeriod("2026.2");
        ac.setProfessor(new User());
        ac.setStudents(Collections.emptySet());
        ac.setIsClosed(null);
        ac.setCreatedAt(null);

        ac.prePersist();
        assertThat(ac.getCreatedAt()).isNotNull();
        assertThat(ac.getIsClosed()).isFalse();
        assertThat(ac.getFormattedName()).isEqualTo("Farmacologia - T01 - 2026.2");

        // prePersist with existing values
        ac.setIsClosed(true);
        Instant fixedInstant = Instant.now();
        ac.setCreatedAt(fixedInstant);
        ac.prePersist();
        assertThat(ac.getIsClosed()).isTrue();
        assertThat(ac.getCreatedAt()).isEqualTo(fixedInstant);

        AcademicClass built = AcademicClass.builder()
                .subjectName("Bioquímica")
                .classCode("T03")
                .academicPeriod("2026.1")
                .build();
        assertThat(built.getSubjectName()).isEqualTo("Bioquímica");
    }

    @Test
    @DisplayName("Deve testar métodos de Activity e ciclo de vida")
    void testActivityEntity() {
        Activity a = new Activity();
        a.setId(UUID.randomUUID());
        a.setTitle("Atividade 1");
        a.setDescription("Desc");
        a.setDeadline(Instant.now());
        a.setCreatedAt(null);

        a.prePersist();
        assertThat(a.getCreatedAt()).isNotNull();

        Instant now = Instant.now();
        a.setCreatedAt(now);
        a.prePersist();
        assertThat(a.getCreatedAt()).isEqualTo(now);

        Activity built = Activity.builder().title("T").build();
        assertThat(built.getTitle()).isEqualTo("T");
    }

    @Test
    @DisplayName("Deve testar métodos de ActivitySubmission e ciclo de vida")
    void testActivitySubmissionEntity() {
        ActivitySubmission sub = new ActivitySubmission();
        sub.setId(UUID.randomUUID());
        sub.setSubmissionDate(null);
        sub.setGrade(null);

        sub.prePersist();
        assertThat(sub.getSubmissionDate()).isNotNull();
        assertThat(sub.isGraded()).isFalse();

        sub.setGrade(BigDecimal.valueOf(8.5));
        assertThat(sub.isGraded()).isTrue();

        Instant fixed = Instant.now();
        sub.setSubmissionDate(fixed);
        sub.prePersist();
        assertThat(sub.getSubmissionDate()).isEqualTo(fixed);

        ActivitySubmission built = ActivitySubmission.builder()
                .grade(BigDecimal.TEN)
                .build();
        assertThat(built.getGrade()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Deve testar cálculo e branches do GutItemData")
    void testGutItemData() {
        GutItemData item1 = GutItemData.builder()
                .problem("Risco de infecção")
                .gravity(5)
                .urgency(4)
                .trend(3)
                .build();
        assertThat(item1.getScore()).isEqualTo(60);

        // Test with nulls (defaults to 1)
        GutItemData item2 = new GutItemData();
        assertThat(item2.getScore()).isEqualTo(1);

        // Test with values < 1 (defaults to 1)
        GutItemData item3 = GutItemData.builder()
                .gravity(0)
                .urgency(-1)
                .trend(0)
                .build();
        assertThat(item3.getScore()).isEqualTo(1);

        GutItemData item4 = new GutItemData("P", 3, 3, 3);
        item4.setScore(27);
        assertThat(item4.getScore()).isEqualTo(27);
    }

    @Test
    @DisplayName("Deve testar data classes do caso clínico e ferramentas da qualidade")
    void testDataClasses() {
        ClinicalCaseData caseData = ClinicalCaseData.builder()
                .patientName("João")
                .age(45)
                .gender("M")
                .bed("Leito 12")
                .admissionDate("2026-09-14")
                .patientDays(4)
                .admissionNotes("Apendicite aguda")
                .evolutionNotes(List.of(EvolutionNoteData.builder()
                        .dateTime("2026-09-14 10:00")
                        .professionalRole("Médico")
                        .note("Evolução estável")
                        .build()))
                .prescriptions(List.of(PrescriptionData.builder()
                        .medication("Dipirona")
                        .dosage("1g")
                        .route("EV")
                        .frequency("6/6h")
                        .administrationCheck("Administrado às 08:00")
                        .build()))
                .labExams(List.of(LabExamData.builder()
                        .examName("Hemograma")
                        .result("12.000")
                        .referenceValue("4.000 - 10.000")
                        .date("2026-09-14")
                        .build()))
                .procedures(List.of(ProcedureData.builder()
                        .procedureName("Apendicectomia")
                        .description("Sem intercorrências")
                        .date("2026-09-14")
                        .build()))
                .build();

        assertThat(caseData.getPatientName()).isEqualTo("João");
        assertThat(caseData.getEvolutionNotes()).hasSize(1);
        assertThat(caseData.getPrescriptions()).hasSize(1);
        assertThat(caseData.getLabExams()).hasSize(1);
        assertThat(caseData.getProcedures()).hasSize(1);

        IshikawaData ishikawa = IshikawaData.builder()
                .centralProblem("Queda")
                .methodCauses(List.of("Falha de protocolo"))
                .machineCauses(List.of("Bomba com defeito"))
                .materialCauses(List.of("Seringa"))
                .manpowerCauses(List.of("Fadiga"))
                .measurementCauses(List.of("Sem calibração"))
                .environmentCauses(List.of("Ruído"))
                .build();
        assertThat(ishikawa.getMethodCauses()).contains("Falha de protocolo");
        assertThat(ishikawa.getCentralProblem()).isEqualTo("Queda");

        FiveWTwoHItemData fiveW = FiveWTwoHItemData.builder()
                .what("Checklist cirúrgico")
                .why("Evitar eventos adversos")
                .where("Centro Cirúrgico")
                .when("Imediato")
                .who("Equipe de Enfermagem")
                .how("Aplicando formulário")
                .howMuch("R$ 0,00")
                .build();
        assertThat(fiveW.getWhat()).isEqualTo("Checklist cirúrgico");

        PdcaData pdca = PdcaData.builder()
                .plan("Planejamento de capacitação")
                .doPhase("Realização de treinamentos")
                .checkPhase("Auditoria de prontuários")
                .actPhase("Padronização de conduta")
                .build();
        assertThat(pdca.getPlan()).isEqualTo("Planejamento de capacitação");
        assertThat(pdca.getDoPhase()).isEqualTo("Realização de treinamentos");

        SwotData swot = SwotData.builder()
                .strengths(List.of("Equipe qualificada"))
                .weaknesses(List.of("Sobrecarga"))
                .opportunities(List.of("Novo protocolo IHI"))
                .threats(List.of("Alta rotatividade"))
                .build();
        assertThat(swot.getStrengths()).hasSize(1);

        QualityToolsData qt = QualityToolsData.builder()
                .ishikawa(ishikawa)
                .gutItems(List.of(new GutItemData("Problema", 5, 5, 5)))
                .fiveWTwoHItems(List.of(fiveW))
                .pdca(pdca)
                .swot(swot)
                .brainstormingNotes(List.of("Idéia 1", "Idéia 2"))
                .build();
        assertThat(qt.getBrainstormingNotes()).hasSize(2);
    }

    @Test
    @DisplayName("Deve testar todos os DTOs do módulo academic e dashboard")
    void testAllAcademicAndDashboardDTOs() {
        AcademicClassCloseDTO closeDTO = new AcademicClassCloseDTO();
        closeDTO.setIsClosed(true);
        assertThat(closeDTO.getIsClosed()).isTrue();

        ClassStudentSummaryDTO studentDTO = ClassStudentSummaryDTO.builder()
                .id(UUID.randomUUID())
                .fullName("Aluno")
                .email("aluno@academico.ufs.br")
                .registrationNumber("2026")
                .build();
        assertThat(studentDTO.getFullName()).isEqualTo("Aluno");

        ActivityCreateDTO acCreate = ActivityCreateDTO.builder()
                .classId(UUID.randomUUID())
                .title("T")
                .description("D")
                .deadline(Instant.now())
                .build();
        assertThat(acCreate.getTitle()).isEqualTo("T");

        ActivityUpdateDTO acUpdate = ActivityUpdateDTO.builder()
                .title("T2")
                .build();
        assertThat(acUpdate.getTitle()).isEqualTo("T2");

        TriggerOccurrenceDTO triggerOcc = TriggerOccurrenceDTO.builder()
                .triggerCode("C1")
                .triggerName("Queda")
                .count(5L)
                .build();
        assertThat(triggerOcc.getTriggerCode()).isEqualTo("C1");
    }
}
