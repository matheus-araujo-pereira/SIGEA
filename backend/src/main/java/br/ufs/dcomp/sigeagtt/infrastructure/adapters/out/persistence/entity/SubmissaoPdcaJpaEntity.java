package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de ciclo PDCA na submissão discente. */
@Entity
@Table(name = "submissao_pdca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmissaoPdcaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submissao_id", nullable = false, unique = true)
    private SubmissaoAtividadeJpaEntity submissao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String planejar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fazer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String checar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String agir;
}
