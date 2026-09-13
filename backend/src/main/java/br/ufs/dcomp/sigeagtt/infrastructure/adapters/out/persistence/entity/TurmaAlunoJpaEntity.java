package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/** Entidade JPA para mapeamento da associação entre turmas e alunos matriculados. */
@Entity
@Table(name = "turma_alunos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TurmaAlunoJpaEntity {

    @EmbeddedId @EqualsAndHashCode.Include private TurmaAlunoIdJpa id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("turmaId")
    @JoinColumn(name = "turma_id", insertable = false, updatable = false)
    private TurmaJpaEntity turma;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alunoId")
    @JoinColumn(name = "aluno_id", insertable = false, updatable = false)
    private UsuarioJpaEntity aluno;

    @Column(name = "matriculado_em", nullable = false, updatable = false)
    private LocalDateTime matriculadoEm;

    @PrePersist
    protected void aoMatricular() {
        if (this.matriculadoEm == null) this.matriculadoEm = LocalDateTime.now();
    }
}
