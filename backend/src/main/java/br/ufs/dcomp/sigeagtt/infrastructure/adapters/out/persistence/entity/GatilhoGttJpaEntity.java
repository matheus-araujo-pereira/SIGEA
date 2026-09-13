package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de gatilhos do método IHI-GTT. */
@Entity
@Table(name = "gatilhos_gtt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GatilhoGttJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private ModuloGttJpaEntity modulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "limiar_referencia", length = 150)
    private String limiarReferencia;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
