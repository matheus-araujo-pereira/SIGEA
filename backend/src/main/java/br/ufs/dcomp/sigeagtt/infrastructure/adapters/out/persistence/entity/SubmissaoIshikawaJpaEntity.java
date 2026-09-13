package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de Diagrama de Ishikawa 6M na submissão discente. */
@Entity
@Table(name = "submissao_ishikawa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmissaoIshikawaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submissao_id", nullable = false, unique = true)
    private SubmissaoAtividadeJpaEntity submissao;

    @Column(name = "efeito_principal", nullable = false, columnDefinition = "TEXT")
    private String efeitoPrincipal;

    @Column(columnDefinition = "TEXT")
    private String metodo;

    @Column(name = "mao_de_obra", columnDefinition = "TEXT")
    private String maoDeObra;

    @Column(columnDefinition = "TEXT")
    private String material;

    @Column(columnDefinition = "TEXT")
    private String medida;

    @Column(name = "meio_ambiente", columnDefinition = "TEXT")
    private String meioAmbiente;

    @Column(columnDefinition = "TEXT")
    private String maquina;
}
