package br.ufs.sigea.academic.clazz.domain;

import br.ufs.sigea.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade que representa uma Turma Acadêmica na Universidade Federal de Sergipe (UFS).
 * Formato padrão: "Nome da Matéria - Turma - Período" (Ex: Enfermagem Hospitalar - T01 - 2026.2).
 */
@Entity
@Table(name = "academic_classes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "subject_name", nullable = false, length = 120)
    private String subjectName;

    @Column(name = "class_code", nullable = false, length = 20)
    private String classCode;

    @Column(name = "academic_period", nullable = false, length = 10)
    private String academicPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @Builder.Default
    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_students",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> students = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.isClosed == null) {
            this.isClosed = false;
        }
    }

    /**
     * Retorna a denominação padronizada da turma no padrão acadêmico UFS.
     */
    public String getFormattedName() {
        return String.format("%s - %s - %s", this.subjectName, this.classCode, this.academicPeriod);
    }
}
