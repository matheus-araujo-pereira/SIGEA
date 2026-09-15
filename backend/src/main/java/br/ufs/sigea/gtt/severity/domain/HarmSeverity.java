package br.ufs.sigea.gtt.severity.domain;

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

import java.util.UUID;

/**
 * Entidade que representa uma Categoria de Gravidade de Dano segundo o Índice NCC MERP adaptado pelo IHI.
 * Categorias A a I, com divisão epistemológica entre Sem Dano (A a D) e Com Dano (E a I).
 */
@Entity
@Table(name = "harm_severities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarmSeverity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "category_letter", nullable = false, unique = true, length = 1)
    private String categoryLetter;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_harm", nullable = false)
    private Boolean isHarm;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Define valores padrão antes da inserção no banco.
     */
    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.categoryLetter != null) {
            this.categoryLetter = this.categoryLetter.trim().toUpperCase();
        }
    }
}
