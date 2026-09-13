package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Entidade JPA para mapeamento da tabela de submissões das atividades educacionais. */
@Entity
@Table(
        name = "submissoes_atividades",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_submissao_aluno_atividade",
                    columnNames = {"atividade_id", "aluno_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmissaoAtividadeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atividade_id", nullable = false)
    private AtividadeEducacionalJpaEntity atividade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private UsuarioJpaEntity aluno;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "status_submissao_enum")
    @Builder.Default
    private StatusSubmissao status = StatusSubmissao.EM_ANDAMENTO;

    @Column(name = "tempo_gasto_segundos", nullable = false)
    @Builder.Default
    private Integer tempoGastoSegundos = 0;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_submissao")
    private LocalDateTime dataSubmissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_corretor_id")
    private UsuarioJpaEntity professorCorretor;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(name = "parecer_docente", columnDefinition = "TEXT")
    private String parecerDocente;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;

    @OneToMany(mappedBy = "submissao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmissaoGatilhoJpaEntity> achadosGatilhos = new ArrayList<>();

    @OneToOne(mappedBy = "submissao", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubmissaoIshikawaJpaEntity ishikawa;

    @OneToMany(mappedBy = "submissao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmissaoPlano5w3hJpaEntity> planos5w3h = new ArrayList<>();

    @OneToOne(mappedBy = "submissao", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubmissaoPdcaJpaEntity pdca;

    @PrePersist
    protected void aoCriar() {
        if (this.dataInicio == null) this.dataInicio = LocalDateTime.now();
        if (this.status == null) this.status = StatusSubmissao.EM_ANDAMENTO;
        if (this.tempoGastoSegundos == null) this.tempoGastoSegundos = 0;
    }
}
