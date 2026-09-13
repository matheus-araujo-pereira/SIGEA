package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de categorias de eventos adversos. */
@Entity
@Table(name = "categorias_eventos_adversos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoriaEventoAdversoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "definicao_operacional", nullable = false, columnDefinition = "TEXT")
    private String definicaoOperacional;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;
}
