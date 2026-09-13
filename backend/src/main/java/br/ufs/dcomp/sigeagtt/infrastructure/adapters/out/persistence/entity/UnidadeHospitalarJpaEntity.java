package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de unidades hospitalares. */
@Entity
@Table(name = "unidades_hospitalares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UnidadeHospitalarJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String sigla;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;
}
