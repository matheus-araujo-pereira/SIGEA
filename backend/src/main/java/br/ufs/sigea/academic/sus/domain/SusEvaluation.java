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
}
