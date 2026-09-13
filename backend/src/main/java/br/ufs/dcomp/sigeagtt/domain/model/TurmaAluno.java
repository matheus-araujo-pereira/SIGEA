package br.ufs.dcomp.sigeagtt.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa o vínculo de matrícula de um discente em uma Turma.
 *
 * <p>Controla a participação dos discentes nas turmas para execução das auditorias retrospectivas
 * pedagógicas.
 */
public class TurmaAluno implements Serializable {

    private Turma turma;
    private Usuario aluno;
    private LocalDateTime matriculadoEm;

    /** Construtor padrão sem argumentos. */
    public TurmaAluno() {
        this.matriculadoEm = LocalDateTime.now();
    }

    /**
     * Construtor com as entidades de vínculo turma-aluno.
     *
     * @param turma Turma à qual o discente está associado.
     * @param aluno Discente matriculado na turma.
     */
    public TurmaAluno(Turma turma, Usuario aluno) {
        this.turma = turma;
        this.aluno = aluno;
        this.matriculadoEm = LocalDateTime.now();
    }

    /**
     * Construtor completo do vínculo com timestamp de matrícula.
     *
     * @param turma Turma à qual o discente está associado.
     * @param aluno Discente matriculado na turma.
     * @param matriculadoEm Data e hora da efetivação da matrícula.
     */
    public TurmaAluno(Turma turma, Usuario aluno, LocalDateTime matriculadoEm) {
        this.turma = turma;
        this.aluno = aluno;
        this.matriculadoEm = matriculadoEm != null ? matriculadoEm : LocalDateTime.now();
    }

    /**
     * Valida o perfil do discente matriculado.
     *
     * @throws RegraNegocioException Caso o usuário não possua perfil de discente.
     */
    public void validarInvariantes() {
        if (turma == null) {
            throw new RegraNegocioException("A turma é obrigatória para a matrícula.");
        }
        if (aluno == null) {
            throw new RegraNegocioException("O discente é obrigatório para a matrícula.");
        }
        if (aluno.getPerfil() != PerfilUsuario.ALUNO) {
            throw new RegraNegocioException(
                    "Apenas usuários com perfil de ALUNO podem ser matriculados em turmas.");
        }
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Usuario getAluno() {
        return aluno;
    }

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
    }

    public LocalDateTime getMatriculadoEm() {
        return matriculadoEm;
    }

    public void setMatriculadoEm(LocalDateTime matriculadoEm) {
        this.matriculadoEm = matriculadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurmaAluno that = (TurmaAluno) o;
        return Objects.equals(turma, that.turma) && Objects.equals(aluno, that.aluno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turma, aluno);
    }
}
