package br.ufs.sigea.academic.report.service;

import br.ufs.sigea.academic.activity.domain.ActivitySubmission;
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
import br.ufs.sigea.common.util.CsvExportUtil;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo motor de exportação de relatórios clínicos em PDF e dados brutos em CSV.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportExportService {

    private final ActivitySubmissionRepository submissionRepository;
    private final AcademicClassRepository classRepository;
    private final ClassDashboardService classDashboardService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("America/Maceio"))
            .withLocale(Locale.of("pt", "BR"));

    // Cores do Design System SIGEA (Semiótica Clínica)
    private static final Color COLOR_PRIMARY = new Color(30, 58, 138);       // #1E3A8A (Azul Clínico Profundo)
    private static final Color COLOR_SUCCESS = new Color(5, 150, 105);       // #059669 (Verde Esmeralda Cirúrgico)
    private static final Color COLOR_DANGER = new Color(220, 38, 38);        // #DC2626 (Carmim de Alto Risco)
    private static final Color COLOR_BG_HEADER = new Color(241, 245, 249);   // #F1F5F9 (Cinza Cabeçalho)
    private static final Color COLOR_BG_ALT = new Color(248, 250, 252);      // #F8FAFC (Cinza Claro)
    private static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);      // #0F172A (Texto Principal)
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // #64748B (Texto Secundário)
    private static final Color COLOR_BORDER = new Color(226, 232, 240);      // #E2E8F0 (Borda Suave)

    // Tipografia
    private static final Font FONT_HEADER_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
    private static final Font FONT_HEADER_SUB = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(224, 231, 255));
    private static final Font FONT_DOC_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(254, 240, 138));
    private static final Font FONT_LEGAL = FontFactory.getFont(FontFactory.HELVETICA, 7, new Color(203, 213, 225));

    private static final Font FONT_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_PRIMARY);
    private static final Font FONT_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_TEXT_DARK);
    private static final Font FONT_VALUE = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_TEXT_DARK);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_PRIMARY);
    private static final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, COLOR_TEXT_MUTED);

    /**
     * Gera o Relatório de Auditoria Clínica Individual em PDF para uma submissão discente.
     */
    @Transactional(readOnly = true)
    public byte[] generateSubmissionAuditPdf(UUID submissionId, User currentUser) {
        ActivitySubmission sub = submissionRepository.findByIdWithDetails(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submissão não encontrada: " + submissionId));

        validateSubmissionAccess(sub, currentUser);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 32, 32, 32, 32);
            PdfWriter.getInstance(document, out);
            document.open();

            // Cabeçalho Institucional
            addInstitutionalHeader(document, "Relatório Oficial de Auditoria Clínica (IHI-GTT)");

            // 1. Identificação Geral
            addSectionTitle(document, "1. IDENTIFICAÇÃO DA ATIVIDADE E PRONTUÁRIO SIMULADO");
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{25, 25, 25, 25});
            infoTable.setSpacingAfter(8f);

            addKeyValuePair(infoTable, "Atividade:", sub.getActivity().getTitle(), 4);
            addKeyValuePair(infoTable, "Turma:", sub.getActivity().getAcademicClass().getFormattedName(), 2);
            addKeyValuePair(infoTable, "Docente Orientador:", sub.getActivity().getAcademicClass().getProfessor().getFullName(), 2);
            addKeyValuePair(infoTable, "Discente Auditor:", sub.getStudent().getFullName(), 2);
            String reg = sub.getStudent().getRegistrationNumber() != null ? sub.getStudent().getRegistrationNumber() : "—";
            addKeyValuePair(infoTable, "Matrícula / E-mail:", reg + " (" + sub.getStudent().getEmail() + ")", 2);
            addKeyValuePair(infoTable, "Data da Submissão:", formatInstant(sub.getSubmissionDate()), 2);
            addKeyValuePair(infoTable, "Prazo da Atividade:", formatInstant(sub.getActivity().getDeadline()), 2);

            var cc = sub.getActivity().getClinicalCaseData();
            if (cc != null) {
                addKeyValuePair(infoTable, "Paciente Simulado:", cc.getPatientName(), 2);
                String bed = cc.getBed() != null ? cc.getBed() : "—";
                int pDays = cc.getPatientDays() != null ? cc.getPatientDays() : 1;
                addKeyValuePair(infoTable, "Leito / Internação:", bed + " | " + pDays + " dias-paciente", 2);
                if (cc.getAdmissionNotes() != null && !cc.getAdmissionNotes().isBlank()) {
                    addKeyValuePair(infoTable, "Notas de Admissão:", cc.getAdmissionNotes(), 4);
                }
            }
            document.add(infoTable);

            // 2. Gatilhos Clínicos e Dano (Fase 1 - Revisão Primária)
            addSectionTitle(document, "2. GATILHOS CLÍNICOS E CLASSIFICAÇÃO DE DANO (FASE 1 - REVISÃO PRIMÁRIA)");
            List<IdentifiedTriggerData> triggers = sub.getIdentifiedTriggers() != null ? sub.getIdentifiedTriggers() : List.of();
            if (triggers.isEmpty()) {
                Paragraph p = new Paragraph("Nenhum gatilho clínico positivo foi apontado pelo discente auditor nesta revisão.", FONT_VALUE);
                p.setSpacingAfter(8f);
                document.add(p);
            } else {
                PdfPTable trigTable = new PdfPTable(6);
                trigTable.setWidthPercentage(100);
                trigTable.setWidths(new float[]{10, 8, 26, 12, 12, 32});
                trigTable.setSpacingAfter(8f);

                addTableHeaderCell(trigTable, "Código");
                addTableHeaderCell(trigTable, "Mód.");
                addTableHeaderCell(trigTable, "Gatilho Clínico");
                addTableHeaderCell(trigTable, "Dano Real?");
                addTableHeaderCell(trigTable, "NCC MERP");
                addTableHeaderCell(trigTable, "Justificativa / Nexo Causal");

                for (IdentifiedTriggerData t : triggers) {
                    addTableCell(trigTable, t.getTriggerCode(), Element.ALIGN_CENTER);
                    addTableCell(trigTable, t.getModuleCode(), Element.ALIGN_CENTER);
                    addTableCell(trigTable, t.getTriggerName(), Element.ALIGN_LEFT);
                    addTableCell(trigTable, Boolean.TRUE.equals(t.getIsHarm()) ? "SIM (EA)" : "NÃO", Element.ALIGN_CENTER,
                            Boolean.TRUE.equals(t.getIsHarm()) ? COLOR_DANGER : COLOR_SUCCESS);
                    addTableCell(trigTable, t.getHarmSeverityLetter() != null ? "Cat. " + t.getHarmSeverityLetter() : null, Element.ALIGN_CENTER);
                    addTableCell(trigTable, t.getClinicalJustification(), Element.ALIGN_LEFT);
                }
                document.add(trigTable);
            }

            // 3. Ferramentas da Qualidade & Análise Causal
            addSectionTitle(document, "3. FERRAMENTAS DA QUALIDADE & ANÁLISE CAUSAL");
            QualityToolsData qt = sub.getQualityToolsData();
            if (qt != null) {
                // Ishikawa 6M
                IshikawaData ishikawa = qt.getIshikawa();
                if (hasAnyIshikawaCause(ishikawa)) {
                    Paragraph ishiTitle = new Paragraph("A. Diagrama de Causa e Efeito (Ishikawa 6M):", FONT_LABEL);
                    ishiTitle.setSpacingBefore(4f);
                    document.add(ishiTitle);

                    PdfPTable ishiTable = new PdfPTable(2);
                    ishiTable.setWidthPercentage(100);
                    ishiTable.setWidths(new float[]{30, 70});
                    ishiTable.setSpacingAfter(6f);

                    addTableHeaderCell(ishiTable, "Dimensão 6M");
                    addTableHeaderCell(ishiTable, "Causas Identificadas");

                    addIshikawaRow(ishiTable, "Método", ishikawa.getMethodCauses());
                    addIshikawaRow(ishiTable, "Máquina / Equipamento", ishikawa.getMachineCauses());
                    addIshikawaRow(ishiTable, "Medida", ishikawa.getMeasurementCauses());
                    addIshikawaRow(ishiTable, "Meio Ambiente", ishikawa.getEnvironmentCauses());
                    addIshikawaRow(ishiTable, "Mão de Obra", ishikawa.getManpowerCauses());
                    addIshikawaRow(ishiTable, "Material", ishikawa.getMaterialCauses());
                    document.add(ishiTable);
                }

                // Matriz GUT
                if (countNonNull(qt.getGutItems()) > 0) {
                    Paragraph gutTitle = new Paragraph("B. Matriz de Priorização GUT (Gravidade, Urgência, Tendência):", FONT_LABEL);
                    gutTitle.setSpacingBefore(4f);
                    document.add(gutTitle);

                    PdfPTable gutTable = new PdfPTable(5);
                    gutTable.setWidthPercentage(100);
                    gutTable.setWidths(new float[]{46, 12, 12, 12, 18});
                    gutTable.setSpacingAfter(6f);

                    addTableHeaderCell(gutTable, "Problema / Ocorrência");
                    addTableHeaderCell(gutTable, "G (1-5)");
                    addTableHeaderCell(gutTable, "U (1-5)");
                    addTableHeaderCell(gutTable, "T (1-5)");
                    addTableHeaderCell(gutTable, "Total (GxUxT)");

                    for (GutItemData g : qt.getGutItems()) {
                        addTableCell(gutTable, g.getProblem(), Element.ALIGN_LEFT);
                        addTableCell(gutTable, String.valueOf(g.getGravity()), Element.ALIGN_CENTER);
                        addTableCell(gutTable, String.valueOf(g.getUrgency()), Element.ALIGN_CENTER);
                        addTableCell(gutTable, String.valueOf(g.getTrend()), Element.ALIGN_CENTER);
                        addTableCell(gutTable, String.valueOf(g.getScore()), Element.ALIGN_CENTER, COLOR_PRIMARY);
                    }
                    document.add(gutTable);
                }

                // Plano de Ação 5W2H
                if (countNonNull(qt.getFiveWTwoHItems()) > 0) {
                    Paragraph fwhTitle = new Paragraph("C. Plano de Ação 5W2H:", FONT_LABEL);
                    fwhTitle.setSpacingBefore(4f);
                    document.add(fwhTitle);

                    PdfPTable fwhTable = new PdfPTable(7);
                    fwhTable.setWidthPercentage(100);
                    fwhTable.setWidths(new float[]{18, 16, 14, 14, 12, 14, 12});
                    fwhTable.setSpacingAfter(6f);

                    addTableHeaderCell(fwhTable, "O quê (What)");
                    addTableHeaderCell(fwhTable, "Por quê (Why)");
                    addTableHeaderCell(fwhTable, "Onde (Where)");
                    addTableHeaderCell(fwhTable, "Quem (Who)");
                    addTableHeaderCell(fwhTable, "Quando (When)");
                    addTableHeaderCell(fwhTable, "Como (How)");
                    addTableHeaderCell(fwhTable, "Custo (Cost)");

                    for (FiveWTwoHItemData f : qt.getFiveWTwoHItems()) {
                        addTableCell(fwhTable, f.getWhat(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getWhy(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getWhere(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getWho(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getWhen(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getHow(), Element.ALIGN_LEFT);
                        addTableCell(fwhTable, f.getHowMuch(), Element.ALIGN_LEFT);
                    }
                    document.add(fwhTable);
                }
            }

            // 4. Avaliação Docente & Parecer Pedagógico (Fase 2 - Consenso)
            addSectionTitle(document, "4. AVALIAÇÃO DOCENTE & CONSENSO PEDAGÓGICO (FASE 2)");
            PdfPTable evalTable = new PdfPTable(2);
            evalTable.setWidthPercentage(100);
            evalTable.setWidths(new float[]{30, 70});
            evalTable.setSpacingAfter(10f);

            String notaFormatada = sub.isGraded()
                    ? String.format(Locale.of("pt", "BR"), "%.2f / 10,00", sub.getGrade())
                    : "Pendente de Avaliação pelo Docente";
            addKeyValuePair(evalTable, "Nota da Resolução:", notaFormatada, 2);
            addKeyValuePair(evalTable, "Data da Avaliação:", sub.getGradedAt() != null ? formatInstant(sub.getGradedAt()) : "—", 2);
            addKeyValuePair(evalTable, "Parecer Pedagógico:", sub.getProfessorFeedback() != null && !sub.getProfessorFeedback().isBlank()
                    ? sub.getProfessorFeedback() : "Nenhum parecer detalhado registrado pelo docente.", 2);
            document.add(evalTable);

            // Rodapé Institucional
            addInstitutionalFooter(document);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro na geração do relatório de auditoria clínica em PDF", e);
            throw new BusinessException("Falha ao gerar relatório de auditoria em PDF: " + e.getMessage());
        }
    }

    /**
     * Gera o Boletim Epidemiológico de Eventos Adversos da Turma Acadêmica em PDF.
     */
    @Transactional(readOnly = true)
    public byte[] generateClassEpidemiologicalBulletinPdf(UUID classId, User currentUser) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + classId));

        validateClassAccess(academicClass, currentUser);

        ClassDashboardDTO dashboard = classDashboardService.getClassDashboard(classId, currentUser);
        GttMetricsDTO gtt = dashboard.getGttMetrics();
        List<ActivitySubmission> submissions = submissionRepository.findByClassIdWithDetails(classId);

        // Agrupamentos adicionais para o Boletim Completo
        Map<String, Long> modDist = new HashMap<>();
        modDist.put("C", 0L);
        modDist.put("M", 0L);
        modDist.put("S", 0L);
        modDist.put("I", 0L);
        modDist.put("P", 0L);
        modDist.put("E", 0L);

        Map<String, TriggerOccurrence> triggerMap = new HashMap<>();
        for (ActivitySubmission s : submissions) {
            if (s.getIdentifiedTriggers() != null) {
                for (IdentifiedTriggerData t : s.getIdentifiedTriggers()) {
                    if (t.getModuleCode() != null) {
                        modDist.put(t.getModuleCode(), modDist.getOrDefault(t.getModuleCode(), 0L) + 1);
                    }
                    if (t.getTriggerCode() != null) {
                        triggerMap.computeIfAbsent(t.getTriggerCode(), k ->
                                new TriggerOccurrence(t.getTriggerCode(), t.getTriggerName(), t.getModuleCode(), 0))
                                .count++;
                    }
                }
            }
        }

        List<TriggerOccurrence> topTriggers = triggerMap.values().stream()
                .sorted(Comparator.comparingInt((TriggerOccurrence o) -> o.count).reversed())
                .limit(10)
                .toList();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 32, 32, 32, 32);
            PdfWriter.getInstance(document, out);
            document.open();

            // Cabeçalho Institucional
            addInstitutionalHeader(document, "Boletim Epidemiológico de Eventos Adversos (IHI-GTT)");

            // 1. Identificação da Turma e Amostra
            addSectionTitle(document, "1. IDENTIFICAÇÃO DA TURMA ACADÊMICA");
            PdfPTable classTable = new PdfPTable(4);
            classTable.setWidthPercentage(100);
            classTable.setWidths(new float[]{25, 25, 25, 25});
            classTable.setSpacingAfter(8f);

            PedagogicalMetricsDTO ped = dashboard.getPedagogicalMetrics();
            int enrolled = ped.getTotalEnrolledStudents() != null ? ped.getTotalEnrolledStudents() : 0;
            int totalActivities = ped.getTotalActivities() != null ? ped.getTotalActivities() : 0;
            int totalSubmissions = ped.getTotalSubmissions() != null ? ped.getTotalSubmissions() : 0;
            int expected = enrolled * totalActivities;
            double submissionRate = expected > 0 ? (totalSubmissions * 100.0) / expected : 0.0;

            addKeyValuePair(classTable, "Turma Acadêmica:", academicClass.getFormattedName(), 2);
            addKeyValuePair(classTable, "Semestre Letivo:", academicClass.getAcademicPeriod(), 2);
            addKeyValuePair(classTable, "Docente Responsável:", academicClass.getProfessor().getFullName(), 2);
            addKeyValuePair(classTable, "Status da Turma:", Boolean.TRUE.equals(academicClass.getIsClosed()) ? "Encerrada" : "Ativa", 2);
            addKeyValuePair(classTable, "Total de Alunos Matriculados:", String.valueOf(enrolled), 2);
            addKeyValuePair(classTable, "Total de Atividades Práticas:", String.valueOf(totalActivities), 2);
            addKeyValuePair(classTable, "Prontuários Auditados (Submissões):", String.valueOf(gtt.getTotalAdmissions()), 2);
            addKeyValuePair(classTable, "Taxa de Entrega de Atividades:", String.format(Locale.of("pt", "BR"), "%.1f%%", submissionRate), 2);
            document.add(classTable);

            // 2. Indicadores Epidemiológicos IHI-GTT Oficiais
            addSectionTitle(document, "2. INDICADORES EPIDEMIOLÓGICOS OFICIAIS (IHI GLOBAL TRIGGER TOOL)");
            PdfPTable metricCards = new PdfPTable(3);
            metricCards.setWidthPercentage(100);
            metricCards.setWidths(new float[]{33.3f, 33.3f, 33.3f});
            metricCards.setSpacingAfter(8f);

            addMetricCard(metricCards, "Eventos Adversos / 1.000 Dias",
                    String.format(Locale.of("pt", "BR"), "%.2f", gtt.getAdverseEventsPer1000PatientDays()),
                    "Total Dias-Paciente: " + gtt.getTotalPatientDays());

            addMetricCard(metricCards, "Eventos Adversos / 100 Admissões",
                    String.format(Locale.of("pt", "BR"), "%.2f", gtt.getAdverseEventsPer100Admissions()),
                    "Total de EA com Dano: " + gtt.getTotalAdverseEvents());

            addMetricCard(metricCards, "% Admissões com Dano Real",
                    String.format(Locale.of("pt", "BR"), "%.1f%%", gtt.getPercentAdmissionsWithAdverseEvents()),
                    "Prontuários c/ Dano: " + gtt.getAdmissionsWithAdverseEvents() + " de " + gtt.getTotalAdmissions());

            document.add(metricCards);

            // 3. Distribuição de Gravidade do Dano (NCC MERP)
            addSectionTitle(document, "3. DISTRIBUIÇÃO DE GRAVIDADE DE DANO (NCC MERP CATEGORIAS E A I)");
            PdfPTable harmTable = new PdfPTable(4);
            harmTable.setWidthPercentage(100);
            harmTable.setWidths(new float[]{15, 50, 15, 20});
            harmTable.setSpacingAfter(8f);

            addTableHeaderCell(harmTable, "Categoria");
            addTableHeaderCell(harmTable, "Classificação Clínica do Dano");
            addTableHeaderCell(harmTable, "Ocorrências");
            addTableHeaderCell(harmTable, "% do Total de EA");

            Map<String, Long> harmDist = gtt.getHarmDistribution();
            long totalEa = gtt.getTotalAdverseEvents() > 0 ? gtt.getTotalAdverseEvents() : 1;

            addHarmRow(harmTable, "E", "Dano temporário com necessidade de intervenção", harmDist.getOrDefault("E", 0L), totalEa);
            addHarmRow(harmTable, "F", "Dano temporário com prolongamento do tempo de internação", harmDist.getOrDefault("F", 0L), totalEa);
            addHarmRow(harmTable, "G", "Dano permanente ao paciente", harmDist.getOrDefault("G", 0L), totalEa);
            addHarmRow(harmTable, "H", "Intervenção necessária para manter a vida (suporte vital)", harmDist.getOrDefault("H", 0L), totalEa);
            addHarmRow(harmTable, "I", "Óbito do paciente relacionado ao evento adverso", harmDist.getOrDefault("I", 0L), totalEa);
            document.add(harmTable);

            // 4. Distribuição por Módulo Padronizado IHI-GTT
            addSectionTitle(document, "4. DISTRIBUIÇÃO DE GATILHOS POR MÓDULO IHI-GTT");
            PdfPTable modTable = new PdfPTable(3);
            modTable.setWidthPercentage(100);
            modTable.setWidths(new float[]{20, 55, 25});
            modTable.setSpacingAfter(8f);

            addTableHeaderCell(modTable, "Código Módulo");
            addTableHeaderCell(modTable, "Módulo Clínico Padronizado");
            addTableHeaderCell(modTable, "Gatilhos Identificados");

            addModuleRow(modTable, "C", "Cuidados Gerais (Care)", modDist.getOrDefault("C", 0L));
            addModuleRow(modTable, "M", "Medicamentos (Medication)", modDist.getOrDefault("M", 0L));
            addModuleRow(modTable, "S", "Cirúrgico (Surgical)", modDist.getOrDefault("S", 0L));
            addModuleRow(modTable, "I", "Terapia Intensiva (Intensive Care)", modDist.getOrDefault("I", 0L));
            addModuleRow(modTable, "P", "Perinatal", modDist.getOrDefault("P", 0L));
            addModuleRow(modTable, "E", "Pronto-Socorro / Emergência", modDist.getOrDefault("E", 0L));
            document.add(modTable);

            // 5. Ranking dos Gatilhos Mais Frequentes
            addSectionTitle(document, "5. RANKING DOS GATILHOS CLÍNICOS MAIS PREVALENTES");
            if (topTriggers.isEmpty()) {
                Paragraph p = new Paragraph("Nenhum gatilho clínico registrado na base de dados da turma.", FONT_VALUE);
                p.setSpacingAfter(8f);
                document.add(p);
            } else {
                PdfPTable topTable = new PdfPTable(4);
                topTable.setWidthPercentage(100);
                topTable.setWidths(new float[]{15, 12, 55, 18});
                topTable.setSpacingAfter(10f);

                addTableHeaderCell(topTable, "Gatilho");
                addTableHeaderCell(topTable, "Módulo");
                addTableHeaderCell(topTable, "Descrição do Gatilho");
                addTableHeaderCell(topTable, "Detecções");

                for (TriggerOccurrence t : topTriggers) {
                    addTableCell(topTable, t.code, Element.ALIGN_CENTER);
                    addTableCell(topTable, t.module != null ? t.module : "—", Element.ALIGN_CENTER);
                    addTableCell(topTable, t.name != null ? t.name : "—", Element.ALIGN_LEFT);
                    addTableCell(topTable, String.valueOf(t.count), Element.ALIGN_CENTER, COLOR_PRIMARY);
                }
                document.add(topTable);
            }

            // Rodapé Institucional
            addInstitutionalFooter(document);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro na geração do boletim epidemiológico em PDF", e);
            throw new BusinessException("Falha ao gerar boletim epidemiológico em PDF: " + e.getMessage());
        }
    }

    /**
     * Gera a base de dados brutos da turma em CSV para pesquisas científicas e bioestatística (SPSS / R / Python).
     */
    @Transactional(readOnly = true)
    public byte[] generateClassResearchCsv(UUID classId, User currentUser) {
        AcademicClass academicClass = classRepository.findByIdWithStudents(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma acadêmica não encontrada: " + classId));

        validateClassAccess(academicClass, currentUser);

        List<ActivitySubmission> submissions = submissionRepository.findByClassIdWithDetails(classId);

        CsvExportUtil csv = CsvExportUtil.create().header(
                "submission_id", "turma", "semestre", "docente_responsavel", "aluno_nome", "aluno_email", "aluno_matricula",
                "atividade_titulo", "data_submissao", "prazo_atividade", "nota", "avaliado", "data_avaliacao",
                "paciente_nome", "paciente_leito", "dias_paciente",
                "qtd_gatilhos_detectados", "codigos_gatilhos", "possui_evento_adverso", "maior_gravidade_ncc_merp",
                "qtd_causas_ishikawa", "qtd_itens_gut", "maior_escore_gut"
        );

        for (ActivitySubmission sub : submissions) {
            var cc = sub.getActivity().getClinicalCaseData();
            var triggers = sub.getIdentifiedTriggers() != null ? sub.getIdentifiedTriggers() : List.<IdentifiedTriggerData>of();

            boolean hasHarm = triggers.stream().anyMatch(t -> Boolean.TRUE.equals(t.getIsHarm()));
            String triggerCodes = triggers.stream()
                    .map(IdentifiedTriggerData::getTriggerCode)
                    .collect(Collectors.joining(";"));

            String highestSeverity = triggers.stream()
                    .map(IdentifiedTriggerData::getHarmSeverityLetter)
                    .filter(h -> h != null && !h.isBlank())
                    .map(h -> h.toUpperCase().trim())
                    .max(Comparator.naturalOrder())
                    .orElse("SEM_DANO");

            int ishikawaCount = 0;
            int gutCount = 0;
            int maxGutScore = 0;

            if (sub.getQualityToolsData() != null) {
                var ishi = sub.getQualityToolsData().getIshikawa();
                ishikawaCount = countIshikawaCauses(ishi);

                var gutList = sub.getQualityToolsData().getGutItems();
                if (gutList != null) {
                    gutCount = gutList.size();
                    for (GutItemData g : gutList) {
                        int s = g.getScore();
                        if (s > maxGutScore) {
                            maxGutScore = s;
                        }
                    }
                }
            }

            String patientName = cc != null && cc.getPatientName() != null ? cc.getPatientName() : "";
            String patientBed = cc != null && cc.getBed() != null ? cc.getBed() : "";
            int patientDays = cc != null && cc.getPatientDays() != null ? cc.getPatientDays() : 1;

            csv.addRow(
                    sub.getId().toString(),
                    academicClass.getFormattedName(),
                    academicClass.getAcademicPeriod(),
                    academicClass.getProfessor().getFullName(),
                    sub.getStudent().getFullName(),
                    sub.getStudent().getEmail(),
                    sub.getStudent().getRegistrationNumber() != null ? sub.getStudent().getRegistrationNumber() : "",
                    sub.getActivity().getTitle(),
                    formatInstant(sub.getSubmissionDate()),
                    formatInstant(sub.getActivity().getDeadline()),
                    sub.isGraded() ? String.format(Locale.US, "%.2f", sub.getGrade()) : "",
                    sub.isGraded() ? "SIM" : "NAO",
                    sub.getGradedAt() != null ? formatInstant(sub.getGradedAt()) : "",
                    patientName,
                    patientBed,
                    patientDays,
                    triggers.size(),
                    triggerCodes,
                    hasHarm ? "SIM" : "NAO",
                    highestSeverity,
                    ishikawaCount,
                    gutCount,
                    maxGutScore
            );
        }

        return csv.toByteArray();
    }

    // =========================================================================
    // Helpers de Validação de Acesso
    // =========================================================================

    private void validateSubmissionAccess(ActivitySubmission sub, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getRole() == UserRole.STUDENT) {
            if (!sub.getStudent().getId().equals(user.getId())) {
                throw new AccessDeniedException("Você só pode visualizar e exportar relatórios de suas próprias submissões.");
            }
            return;
        }
        // Perfil PROFESSOR
        User prof = sub.getActivity().getAcademicClass().getProfessor();
        if (prof == null || !prof.getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar submissões de turmas de outros docentes.");
        }
    }

    private void validateClassAccess(AcademicClass academicClass, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getRole() == UserRole.PROFESSOR) {
            if (academicClass.getProfessor() == null || !academicClass.getProfessor().getId().equals(user.getId())) {
                throw new AccessDeniedException("Você não tem permissão para exportar relatórios de turmas de outros docentes.");
            }
            return;
        }
        throw new AccessDeniedException("Estudantes não têm permissão para exportar dados consolidados ou boletins da turma.");
    }

    // =========================================================================
    // Helpers de Renderização PDF (OpenPDF)
    // =========================================================================

    private void addInstitutionalHeader(Document document, String documentTitle) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(8f);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(COLOR_PRIMARY);
        titleCell.setPadding(8f);
        titleCell.setBorder(PdfPCell.NO_BORDER);

        Paragraph inst = new Paragraph("UNIVERSIDADE FEDERAL DE SERGIPE — UFS", FONT_HEADER_TITLE);
        inst.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(inst);

        Paragraph dept = new Paragraph("DCOMP • Departamento de Enfermagem • SIGEA", FONT_HEADER_SUB);
        dept.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(dept);

        Paragraph mainTitle = new Paragraph(documentTitle.toUpperCase(), FONT_DOC_TITLE);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingBefore(3f);
        titleCell.addElement(mainTitle);

        Paragraph legal = new Paragraph("Comitê de Ética em Pesquisa (CEP/UFS) — CAAE nº 91836925.8.0000.5546 | Metodologia IHI-GTT", FONT_LEGAL);
        legal.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(legal);

        headerTable.addCell(titleCell);
        document.add(headerTable);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FONT_SECTION);
        p.setSpacingBefore(4f);
        p.setSpacingAfter(3f);
        document.add(p);
    }

    private void addKeyValuePair(PdfPTable table, String label, String value, int colSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(colSpan);
        cell.setPadding(3.5f);
        cell.setBorderColor(COLOR_BORDER);
        cell.setBackgroundColor(COLOR_BG_ALT);

        Paragraph p = new Paragraph();
        p.add(new Phrase(label + " ", FONT_LABEL));
        p.add(new Phrase(value != null ? value : "—", FONT_VALUE));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_TABLE_HEADER));
        cell.setBackgroundColor(COLOR_BG_HEADER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        cell.setBorderColor(COLOR_BORDER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, int align) {
        addTableCell(table, text, align, COLOR_TEXT_DARK);
    }

    private void addTableCell(PdfPTable table, String text, int align, Color textColor) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, textColor);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "—", font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3.5f);
        cell.setBorderColor(COLOR_BORDER);
        table.addCell(cell);
    }

    private void addIshikawaRow(PdfPTable table, String category, List<String> causes) {
        addTableCell(table, category, Element.ALIGN_LEFT, COLOR_PRIMARY);
        String causesStr = countNonNull(causes) > 0
                ? String.join("; ", causes)
                : "Nenhuma causa apontada.";
        addTableCell(table, causesStr, Element.ALIGN_LEFT);
    }

    private void addHarmRow(PdfPTable table, String category, String description, long count, long totalEa) {
        double pct = (count * 100.0) / totalEa;
        addTableCell(table, "Categoria " + category, Element.ALIGN_CENTER, COLOR_DANGER);
        addTableCell(table, description, Element.ALIGN_LEFT);
        addTableCell(table, String.valueOf(count), Element.ALIGN_CENTER);
        addTableCell(table, String.format(Locale.of("pt", "BR"), "%.1f%%", pct), Element.ALIGN_CENTER);
    }

    private void addModuleRow(PdfPTable table, String code, String name, long count) {
        addTableCell(table, "Módulo " + code, Element.ALIGN_CENTER, COLOR_PRIMARY);
        addTableCell(table, name, Element.ALIGN_LEFT);
        addTableCell(table, String.valueOf(count), Element.ALIGN_CENTER);
    }

    private void addMetricCard(PdfPTable table, String label, String value, String subtext) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_BG_HEADER);
        cell.setBorderColor(COLOR_PRIMARY);
        cell.setBorderWidth(1.5f);
        cell.setPadding(6f);

        Paragraph pLabel = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, COLOR_PRIMARY));
        pLabel.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pLabel);

        Paragraph pVal = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15f, COLOR_PRIMARY));
        pVal.setAlignment(Element.ALIGN_CENTER);
        pVal.setSpacingBefore(2f);
        pVal.setSpacingAfter(2f);
        cell.addElement(pVal);

        Paragraph pSub = new Paragraph(subtext, FontFactory.getFont(FontFactory.HELVETICA, 7f, COLOR_TEXT_MUTED));
        pSub.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pSub);

        table.addCell(cell);
    }

    private void addInstitutionalFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
                "SIGEA: Sistema Inteligente de Gestão de Eventos Adversos — Emissão gerada em " +
                formatInstant(Instant.now()) + " • Documento de uso acadêmico e pesquisa clínica.",
                FONT_FOOTER);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f);
        document.add(footer);
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return "—";
        }
        return DATE_TIME_FORMATTER.format(instant);
    }

    private boolean hasAnyIshikawaCause(IshikawaData ishi) {
        return countIshikawaCauses(ishi) > 0;
    }

    private int countIshikawaCauses(IshikawaData ishi) {
        if (ishi == null) {
            return 0;
        }
        return countNonNull(ishi.getMethodCauses())
                + countNonNull(ishi.getMachineCauses())
                + countNonNull(ishi.getMeasurementCauses())
                + countNonNull(ishi.getEnvironmentCauses())
                + countNonNull(ishi.getManpowerCauses())
                + countNonNull(ishi.getMaterialCauses());
    }

    private int countNonNull(List<?> list) {
        return list != null ? list.size() : 0;
    }

    private static class TriggerOccurrence {
        final String code;
        final String name;
        final String module;
        int count;

        TriggerOccurrence(String code, String name, String module, int count) {
            this.code = code;
            this.name = name;
            this.module = module;
            this.count = count;
        }
    }
}
