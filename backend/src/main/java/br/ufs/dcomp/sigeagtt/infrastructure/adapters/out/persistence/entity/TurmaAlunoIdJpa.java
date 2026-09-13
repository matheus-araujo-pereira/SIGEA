package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;

/** Chave composta incorporável para a relação turma-aluno em JPA. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurmaAlunoIdJpa implements Serializable {

    @Column(name = "turma_id")
    private Long turmaId;

    @Column(name = "aluno_id")
    private Long alunoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurmaAlunoIdJpa that = (TurmaAlunoIdJpa) o;
        return Objects.equals(turmaId, that.turmaId) && Objects.equals(alunoId, that.alunoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turmaId, alunoId);
    }
}
