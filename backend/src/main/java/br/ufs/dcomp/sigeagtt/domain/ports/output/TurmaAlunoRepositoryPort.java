package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import java.util.List;

/** Porta de saída (Output Port) para operações de vínculo de matrícula em {@link TurmaAluno}. */
public interface TurmaAlunoRepositoryPort {

    /**
     * Conta o total de alunos matriculados em uma turma.
     *
     * @param turmaId Identificador da turma.
     * @return Quantidade de discentes matriculados.
     */
    long contarAlunosPorTurmaId(Long turmaId);

    /**
     * Lista todos os vínculos de matrícula de uma turma.
     *
     * @param turmaId Identificador da turma.
     * @return Lista de vínculos turma-aluno.
     */
    List<TurmaAluno> listarPorTurmaId(Long turmaId);

    /**
     * Verifica se determinado aluno já está matriculado na turma.
     *
     * @param turmaId Identificador da turma.
     * @param alunoId Identificador do aluno.
     * @return Verdadeiro caso o vínculo exista.
     */
    boolean existeMatricula(Long turmaId, Long alunoId);

    /**
     * Salva o vínculo de matrícula do aluno na turma.
     *
     * @param turmaAluno Vínculo a persistir.
     * @return Vínculo persistido.
     */
    TurmaAluno salvar(TurmaAluno turmaAluno);

    /**
     * Remove a matrícula de um discente na turma.
     *
     * @param turmaId Identificador da turma.
     * @param alunoId Identificador do aluno.
     */
    void desmatricular(Long turmaId, Long alunoId);
}
