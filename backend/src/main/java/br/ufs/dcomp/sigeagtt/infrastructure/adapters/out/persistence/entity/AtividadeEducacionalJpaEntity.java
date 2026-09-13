package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de atividades pedagógicas de auditoria clínica. */
@Entity
@Table(name = "atividades_educacionais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AtividadeEducacionalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private TurmaJpaEntity turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caso_clinico_id", nullable = false)
    private CasoClinicoJpaEntity casoClinico;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "orientacoes_pedagogicas", columnDefinition = "TEXT")
    private String orientacoesPedagogicas;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Column(name = "tempo_limite_minutos", nullable = false)
    @Builder.Default
    private Integer tempoLimiteMinutos = 20;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    protected void aoCriar() {
        if (this.criadaEm == null) this.criadaEm = LocalDateTime.now();
        if (this.ativa == null) this.ativa = true;
        if (this.tempoLimiteMinutos == null) this.tempoLimiteMinutos = 20;
    }
}
