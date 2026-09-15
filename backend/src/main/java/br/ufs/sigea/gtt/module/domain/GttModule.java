package br.ufs.sigea.gtt.module.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade que representa um Módulo do IHI Global Trigger Tool (IHI-GTT).
 * Exemplos: Cuidados (C), Medicação (M), Cirúrgico (S), Terapia Intensiva (I), Perinatal (P), Urgência (E).
 */
@Entity
@Table(name = "gtt_modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GttModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Define a data e hora de criação antes de persistir no banco.
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
