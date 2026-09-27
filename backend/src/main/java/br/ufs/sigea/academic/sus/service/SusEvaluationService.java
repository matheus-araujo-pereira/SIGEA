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
import br.ufs.sigea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço responsável pela gestão de avaliações da Escala SUS (System Usability Scale),
 * cálculo psicométrico de escores e extração de dados estatísticos para pesquisa acadêmica.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SusEvaluationService {

    private final SusEvaluationRepository susEvaluationRepository;
    private final UserRepository userRepository;
    private final AcademicClassRepository academicClassRepository;

    /**
     * Submete uma nova avaliação da Escala SUS para o estudante autenticado.
     *
     * @param dto       Dados das 10 respostas da escala Likert e comentários
     * @param studentId Identificador único do estudante
     * @return DTO com o escore calculado e classificações
     */
    @Transactional
    public SusEvaluationResponseDTO createEvaluation(SusEvaluationCreateDTO dto, UUID studentId) {
        log.info("Processando avaliação SUS para o estudante ID: {}", studentId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com ID: " + studentId));

        AcademicClass academicClass = null;
        if (dto.getAcademicClassId() != null) {
            academicClass = academicClassRepository.findById(dto.getAcademicClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + dto.getAcademicClassId()));

            if (susEvaluationRepository.existsByStudentIdAndAcademicClassId(studentId, dto.getAcademicClassId())) {
                throw new BusinessException("Você já enviou a avaliação de usabilidade desta turma.");
            }
        } else {
            if (susEvaluationRepository.existsByStudentIdAndAcademicClassIsNull(studentId)) {
                throw new BusinessException("Você já enviou a avaliação geral de usabilidade do sistema.");
            }
        }

        double score = SusEvaluation.calculateScore(
                dto.getQ1(), dto.getQ2(), dto.getQ3(), dto.getQ4(), dto.getQ5(),
                dto.getQ6(), dto.getQ7(), dto.getQ8(), dto.getQ9(), dto.getQ10()
        );

        String adjective = SusEvaluation.calculateAdjectiveRating(score);
        String acceptability = SusEvaluation.calculateAcceptability(score);
        String gradeLevel = SusEvaluation.calculateGradeLevel(score);

        SusEvaluation evaluation = SusEvaluation.builder()
                .student(student)
                .academicClass(academicClass)
                .q1(dto.getQ1())
                .q2(dto.getQ2())
                .q3(dto.getQ3())
                .q4(dto.getQ4())
                .q5(dto.getQ5())
                .q6(dto.getQ6())
                .q7(dto.getQ7())
                .q8(dto.getQ8())
                .q9(dto.getQ9())
                .q10(dto.getQ10())
                .score(score)
                .adjectiveRating(adjective)
                .acceptability(acceptability)
                .gradeLevel(gradeLevel)
                .suggestions(dto.getSuggestions())
                .build();

        SusEvaluation saved = susEvaluationRepository.save(evaluation);
        log.info("Avaliação SUS registrada com sucesso. ID: {}, Escore: {}", saved.getId(), score);

        return toResponseDTO(saved);
    }

    /**
     * Recupera a avaliação SUS do estudante para uma turma ou para o sistema geral.
     *
     * @param studentId       Identificador do estudante
     * @param academicClassId Identificador da turma (opcional)
     * @return Optional contendo a avaliação, caso já realizada
     */
    @Transactional(readOnly = true)
    public Optional<SusEvaluationResponseDTO> getMyEvaluation(UUID studentId, UUID academicClassId) {
        if (academicClassId != null) {
            return susEvaluationRepository.findByStudentIdAndAcademicClassId(studentId, academicClassId)
                    .map(this::toResponseDTO);
        }
        return susEvaluationRepository.findByStudentIdAndAcademicClassIsNull(studentId)
                .map(this::toResponseDTO);
    }

    /**
     * Lista todas as avaliações SUS submetidas pelo estudante.
     *
     * @param studentId Identificador do estudante
     * @return Lista de avaliações do estudante
     */
    @Transactional(readOnly = true)
    public List<SusEvaluationResponseDTO> getMyEvaluations(UUID studentId) {
        return susEvaluationRepository.findByStudentIdWithDetails(studentId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Calcula o sumário psicométrico consolidado da Escala SUS para uma turma específica.
     *
     * @param classId Identificador da turma acadêmica
     * @return DTO com média, taxa de adesão, distribuição e médias por pergunta
     */
    @Transactional(readOnly = true)
    public SusClassSummaryDTO getClassSummary(UUID classId) {
        AcademicClass academicClass = academicClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        List<SusEvaluation> evaluations = susEvaluationRepository.findByAcademicClassIdWithDetails(classId);
        long totalEvaluations = evaluations.size();
        long enrolledCount = academicClass.getStudents() != null ? academicClass.getStudents().size() : 0;
        double responseRate = enrolledCount > 0
                ? Math.min(100.0, (totalEvaluations * 100.0) / enrolledCount)
                : 0.0;

        if (evaluations.isEmpty()) {
            return SusClassSummaryDTO.builder()
                    .classId(classId)
                    .className(academicClass.getFormattedName())
                    .totalEvaluations(0)
                    .enrolledStudentsCount(enrolledCount)
                    .responseRatePercentage(responseRate)
                    .averageScore(0.0)
                    .adjectiveRating("Sem Respostas")
                    .acceptability("Sem Respostas")
                    .gradeLevel("N/A")
                    .adjectiveDistribution(initializeAdjectiveMap())
                    .questionAverages(createZeroAverages())
                    .evaluations(List.of())
                    .build();
        }

        double averageScore = evaluations.stream().mapToDouble(SusEvaluation::getScore).average().orElse(0.0);
        averageScore = Math.round(averageScore * 100.0) / 100.0;

        String adjective = SusEvaluation.calculateAdjectiveRating(averageScore);
        String acceptability = SusEvaluation.calculateAcceptability(averageScore);
        String gradeLevel = SusEvaluation.calculateGradeLevel(averageScore);

        Map<String, Long> distribution = calculateDistribution(evaluations);
        List<Double> questionAverages = calculateQuestionAverages(evaluations);

        List<SusEvaluationResponseDTO> responseList = evaluations.stream()
                .map(this::toResponseDTO)
                .toList();

        return SusClassSummaryDTO.builder()
                .classId(classId)
                .className(academicClass.getFormattedName())
                .totalEvaluations(totalEvaluations)
                .enrolledStudentsCount(enrolledCount)
                .responseRatePercentage(Math.round(responseRate * 100.0) / 100.0)
                .averageScore(averageScore)
                .adjectiveRating(adjective)
                .acceptability(acceptability)
                .gradeLevel(gradeLevel)
                .adjectiveDistribution(distribution)
                .questionAverages(questionAverages)
                .evaluations(responseList)
                .build();
    }

    /**
     * Calcula o sumário psicométrico consolidado global de todo o sistema para a administração.
     *
     * @return DTO com média global, distribuição e médias de questões
     */
    @Transactional(readOnly = true)
    public SusGeneralSummaryDTO getGeneralSummary() {
        List<SusEvaluation> evaluations = susEvaluationRepository.findAllWithDetails();
        long totalEvaluations = evaluations.size();

        if (evaluations.isEmpty()) {
            return SusGeneralSummaryDTO.builder()
                    .totalEvaluations(0)
                    .averageScore(0.0)
                    .adjectiveRating("Sem Respostas")
                    .acceptability("Sem Respostas")
                    .gradeLevel("N/A")
                    .adjectiveDistribution(initializeAdjectiveMap())
                    .questionAverages(createZeroAverages())
                    .build();
        }

        double averageScore = evaluations.stream().mapToDouble(SusEvaluation::getScore).average().orElse(0.0);
        averageScore = Math.round(averageScore * 100.0) / 100.0;

        String adjective = SusEvaluation.calculateAdjectiveRating(averageScore);
        String acceptability = SusEvaluation.calculateAcceptability(averageScore);
        String gradeLevel = SusEvaluation.calculateGradeLevel(averageScore);

        Map<String, Long> distribution = calculateDistribution(evaluations);
        List<Double> questionAverages = calculateQuestionAverages(evaluations);

        return SusGeneralSummaryDTO.builder()
                .totalEvaluations(totalEvaluations)
                .averageScore(averageScore)
                .adjectiveRating(adjective)
                .acceptability(acceptability)
                .gradeLevel(gradeLevel)
                .adjectiveDistribution(distribution)
                .questionAverages(questionAverages)
                .build();
    }

    /**
     * Exporta os dados brutos psicométricos do questionário SUS de uma turma em formato CSV (RFC 4180).
     *
     * @param classId Identificador da turma
     * @return Array de bytes com codificação UTF-8 e Byte Order Mark (\uFEFF)
     */
    @Transactional(readOnly = true)
    public byte[] exportClassSusCsv(UUID classId) {
        AcademicClass academicClass = academicClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + classId));

        List<SusEvaluation> evaluations = susEvaluationRepository.findByAcademicClassIdWithDetails(classId);
        return buildCsvBytes(evaluations, academicClass.getFormattedName());
    }

    /**
     * Exporta os dados brutos psicométricos de todas as avaliações SUS do sistema em formato CSV.
     *
     * @return Array de bytes com codificação UTF-8 e Byte Order Mark (\uFEFF)
     */
    @Transactional(readOnly = true)
    public byte[] exportGlobalSusCsv() {
        List<SusEvaluation> evaluations = susEvaluationRepository.findAllWithDetails();
        return buildCsvBytes(evaluations, "Geral_SIGEA");
    }

    private byte[] buildCsvBytes(List<SusEvaluation> evaluations, String contextName) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF'); // UTF-8 BOM para compatibilidade com Microsoft Excel
        csv.append("id,data_envio,turma,estudante_anonimizado,matricula,q1,q2,q3,q4,q5,q6,q7,q8,q9,q10,escore_sus,classificacao_adjetiva,aceitabilidade,conceito_escolar,sugestoes\n");

        DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;

        for (SusEvaluation e : evaluations) {
            String className = e.getAcademicClass() != null ? e.getAcademicClass().getFormattedName() : contextName;
            String studentAnon = "DISCENTE-" + e.getStudent().getId().toString().substring(0, 8);
            String reg = e.getStudent().getRegistrationNumber() != null ? e.getStudent().getRegistrationNumber() : "";

            csv.append(escapeCsv(e.getId().toString())).append(',')
                    .append(escapeCsv(dtf.format(e.getCreatedAt()))).append(',')
                    .append(escapeCsv(className)).append(',')
                    .append(escapeCsv(studentAnon)).append(',')
                    .append(escapeCsv(reg)).append(',')
                    .append(e.getQ1()).append(',')
                    .append(e.getQ2()).append(',')
                    .append(e.getQ3()).append(',')
                    .append(e.getQ4()).append(',')
                    .append(e.getQ5()).append(',')
                    .append(e.getQ6()).append(',')
                    .append(e.getQ7()).append(',')
                    .append(e.getQ8()).append(',')
                    .append(e.getQ9()).append(',')
                    .append(e.getQ10()).append(',')
                    .append(e.getScore()).append(',')
                    .append(escapeCsv(e.getAdjectiveRating())).append(',')
                    .append(escapeCsv(e.getAcceptability())).append(',')
                    .append(escapeCsv(e.getGradeLevel())).append(',')
                    .append(escapeCsv(e.getSuggestions()))
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private Map<String, Long> calculateDistribution(List<SusEvaluation> evaluations) {
        Map<String, Long> map = initializeAdjectiveMap();
        for (SusEvaluation e : evaluations) {
            String rating = e.getAdjectiveRating();
            map.put(rating, map.getOrDefault(rating, 0L) + 1L);
        }
        return map;
    }

    private Map<String, Long> initializeAdjectiveMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("Melhor Imaginável", 0L);
        map.put("Bom", 0L);
        map.put("Regular", 0L);
        map.put("Pobre", 0L);
        return map;
    }

    private List<Double> calculateQuestionAverages(List<SusEvaluation> evaluations) {
        List<Double> averages = new ArrayList<>(10);
        int total = evaluations.size();
        for (int i = 1; i <= 10; i++) {
            final int questionIndex = i;
            double avg = evaluations.stream()
                    .mapToInt(e -> getQuestionValue(e, questionIndex))
                    .average()
                    .orElse(0.0);
            averages.add(Math.round(avg * 100.0) / 100.0);
        }
        return averages;
    }

    private int getQuestionValue(SusEvaluation e, int index) {
        int[] answers = {e.getQ1(), e.getQ2(), e.getQ3(), e.getQ4(), e.getQ5(),
                e.getQ6(), e.getQ7(), e.getQ8(), e.getQ9(), e.getQ10()};
        return answers[index - 1];
    }

    private List<Double> createZeroAverages() {
        List<Double> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(0.0);
        }
        return list;
    }

    private SusEvaluationResponseDTO toResponseDTO(SusEvaluation e) {
        return SusEvaluationResponseDTO.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getFullName())
                .studentEmail(e.getStudent().getEmail())
                .studentRegistration(e.getStudent().getRegistrationNumber())
                .academicClassId(e.getAcademicClass() != null ? e.getAcademicClass().getId() : null)
                .className(e.getAcademicClass() != null ? e.getAcademicClass().getFormattedName() : null)
                .q1(e.getQ1())
                .q2(e.getQ2())
                .q3(e.getQ3())
                .q4(e.getQ4())
                .q5(e.getQ5())
                .q6(e.getQ6())
                .q7(e.getQ7())
                .q8(e.getQ8())
                .q9(e.getQ9())
                .q10(e.getQ10())
                .score(e.getScore())
                .adjectiveRating(e.getAdjectiveRating())
                .acceptability(e.getAcceptability())
                .gradeLevel(e.getGradeLevel())
                .suggestions(e.getSuggestions())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
