package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entidade JPA para mapeamento da tabela de gatilhos identificados na auditoria clínica discente.
 */
@Entity
@Table(name = "submissao_gatilhos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmissaoGatilhoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submissao_id", nullable = false)
    private SubmissaoAtividadeJpaEntity submissao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gatilho_id", nullable = false)
    private GatilhoGttJpaEntity gatilho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_ea_id")
    private CategoriaEventoAdversoJpaEntity categoriaEventoAdverso;

    @Column(name = "confirmou_dano", nullable = false)
    @Builder.Default
    private Boolean confirmouDano = false;

    @Column(name = "justificativa_dano", columnDefinition = "TEXT")
    private String justificativaDano;

    @Column(name = "dano_presente_admissao", nullable = false)
    @Builder.Default
    private Boolean danoPresenteAdmissao = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gravidade", columnDefinition = "gravidade_ncc_merp_enum")
    private GravidadeNccMerp gravidade;
}
