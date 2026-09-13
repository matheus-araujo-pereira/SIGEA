package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de turmas acadêmicas. */
@Entity
@Table(name = "turmas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TurmaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private UsuarioJpaEntity professorResponsavel;

    @Column(name = "codigo_disciplina", nullable = false, length = 30)
    private String codigoDisciplina;

    @Column(name = "nome_disciplina", nullable = false, length = 150)
    private String nomeDisciplina;

    @Column(name = "periodo_letivo", nullable = false, length = 20)
    private String periodoLetivo;

    @Column(name = "ano_semestre", nullable = false, length = 10)
    private String anoSemestre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    protected void aoCriar() {
        if (this.criadaEm == null) this.criadaEm = LocalDateTime.now();
        if (this.ativa == null) this.ativa = true;
        if (this.nomeDisciplina == null || this.nomeDisciplina.isBlank()) {
            this.nomeDisciplina = "Segurança do Paciente e Auditoria Clínica";
        }
    }
}
