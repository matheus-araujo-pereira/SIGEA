package br.ufs.sigea.academic.clinical.domain;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa um Modelo Canônico de Caso Clínico Simulado para atividades avaliativas.
 */
@Entity
@Table(name = "clinical_case_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalCaseTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "module_code", nullable = false, length = 20)
    private String moduleCode;

    @Column(name = "primary_trigger_code", nullable = false, length = 10)
    private String primaryTriggerCode;

    @Column(name = "expected_severity", nullable = false, length = 5)
    private String expectedSeverity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "clinical_case_data", nullable = false, columnDefinition = "jsonb")
    private ClinicalCaseData clinicalCaseData;

    @Column(name = "is_system_template", nullable = false)
    private boolean isSystemTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
