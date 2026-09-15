package br.ufs.sigea.academic.activity.domain;

import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa a submissão de uma atividade resolvida por um estudante e sua avaliação docente.
 */
@Entity
@Table(name = "activity_submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "identified_triggers", nullable = false, columnDefinition = "jsonb")
    private List<IdentifiedTriggerData> identifiedTriggers = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quality_tools_data", nullable = false, columnDefinition = "jsonb")
    private QualityToolsData qualityToolsData = new QualityToolsData();

    @Column(name = "submission_date", updatable = false)
    private Instant submissionDate;

    @Column(name = "grade", precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(name = "professor_feedback", columnDefinition = "TEXT")
    private String professorFeedback;

    @Column(name = "graded_at")
    private Instant gradedAt;

    @PrePersist
    public void prePersist() {
        if (this.submissionDate == null) {
            this.submissionDate = Instant.now();
        }
    }

    /**
     * Verifica se a submissão já recebeu avaliação e nota do professor.
     */
    public boolean isGraded() {
        return this.grade != null;
    }
}
