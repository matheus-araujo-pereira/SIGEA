package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de planos de ação 5W3H na submissão discente. */
@Entity
@Table(name = "submissao_planos_5w3h")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmissaoPlano5w3hJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submissao_id", nullable = false)
    private SubmissaoAtividadeJpaEntity submissao;

    @Column(name = "o_que", nullable = false, columnDefinition = "TEXT")
    private String oQue;

    @Column(name = "por_que", nullable = false, columnDefinition = "TEXT")
    private String porQue;

    @Column(nullable = false, length = 100)
    private String quem;

    @Column(nullable = false, length = 100)
    private String onde;

    @Column(nullable = false, length = 100)
    private String quando;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String como;

    @Column(name = "quanto_custa", precision = 12, scale = 2)
    private BigDecimal quantoCusta;

    @Column(name = "como_medir", length = 150)
    private String comoMedir;
}
