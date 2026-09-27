package br.ufs.sigea.academic.sus.domain;

import br.ufs.sigea.academic.clazz.domain.AcademicClass;
import br.ufs.sigea.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa uma avaliação de usabilidade preenchida segundo a Escala SUS (System Usability Scale).
 * Segue a metodologia canônica de Brooke (1996) e classificação de Bangor, Kortum & Miller (2008).
 */
@Entity
@Table(name = "sus_evaluations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SusEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_class_id")
    private AcademicClass academicClass;

    @Column(name = "q1", nullable = false)
    private Integer q1;

    @Column(name = "q2", nullable = false)
    private Integer q2;

    @Column(name = "q3", nullable = false)
    private Integer q3;

    @Column(name = "q4", nullable = false)
    private Integer q4;

    @Column(name = "q5", nullable = false)
    private Integer q5;

    @Column(name = "q6", nullable = false)
    private Integer q6;

    @Column(name = "q7", nullable = false)
    private Integer q7;

    @Column(name = "q8", nullable = false)
    private Integer q8;

    @Column(name = "q9", nullable = false)
    private Integer q9;

    @Column(name = "q10", nullable = false)
    private Integer q10;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "adjective_rating", nullable = false, length = 50)
    private String adjectiveRating;

    @Column(name = "acceptability", nullable = false, length = 50)
    private String acceptability;

    @Column(name = "grade_level", nullable = false, length = 10)
    private String gradeLevel;

    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    /**
     * Calcula o escore SUS padronizado (0 a 100) com base nas respostas de 1 a 5.
     *
     * @param q1  Item 1 (Positivo)
     * @param q2  Item 2 (Negativo)
     * @param q3  Item 3 (Positivo)
     * @param q4  Item 4 (Negativo)
     * @param q5  Item 5 (Positivo)
     * @param q6  Item 6 (Negativo)
     * @param q7  Item 7 (Positivo)
     * @param q8  Item 8 (Negativo)
     * @param q9  Item 9 (Positivo)
     * @param q10 Item 10 (Negativo)
     * @return Escore final entre 0.0 e 100.0
     */
    public static double calculateScore(int q1, int q2, int q3, int q4, int q5,
                                        int q6, int q7, int q8, int q9, int q10) {
        int sum = (q1 - 1) + (5 - q2) + (q3 - 1) + (5 - q4) + (q5 - 1)
                + (5 - q6) + (q7 - 1) + (5 - q8) + (q9 - 1) + (5 - q10);
        return sum * 2.5;
    }

    /**
     * Determina a classificação adjetiva de usabilidade (Bangor et al., 2008).
     *
     * @param score Escore SUS (0 a 100)
     * @return Classificação descritiva
     */
    public static String calculateAdjectiveRating(double score) {
        if (score >= 85.0) {
            return "Melhor Imaginável";
        } else if (score >= 70.0) {
            return "Bom";
        } else if (score >= 50.0) {
            return "Regular";
        } else {
            return "Pobre";
        }
    }

    /**
     * Determina a faixa de aceitabilidade da usabilidade.
     *
     * @param score Escore SUS (0 a 100)
     * @return Grau de aceitabilidade
     */
    public static String calculateAcceptability(double score) {
        if (score >= 70.0) {
            return "Aceitável";
        } else if (score >= 50.0) {
            return "Marginal";
        } else {
            return "Inaceitável";
        }
    }

    /**
     * Determina o conceito escolar equivalente (Grade Scale).
     *
     * @param score Escore SUS (0 a 100)
     * @return Conceito (A, B, C, D, F)
     */
    public static String calculateGradeLevel(double score) {
        if (score >= 90.0) {
            return "A";
        } else if (score >= 80.0) {
            return "B";
        } else if (score >= 70.0) {
            return "C";
        } else if (score >= 60.0) {
            return "D";
        } else {
            return "F";
        }
    }
}
