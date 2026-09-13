package br.ufs.dcomp.sigeagtt.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa uma Turma acadêmica no SIGEA-GTT.
 *
 * <p>Agrupa os discentes sob a supervisão de um professor responsável durante um período letivo da
 * graduação médica ou internato hospitalar.
 */
public class Turma {

    private Long id;
    private Usuario professorResponsavel;
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String periodoLetivo;
    private String anoSemestre;
    private Boolean ativa;
    private LocalDateTime criadaEm;

    /** Construtor padrão sem argumentos. */
    public Turma() {
        this.nomeDisciplina = "Segurança do Paciente e Auditoria Clínica";
        this.ativa = true;
        this.criadaEm = LocalDateTime.now();
    }

    /**
     * Construtor completo de Turma.
     *
     * @param id Identificador numérico único da turma.
     * @param professorResponsavel Docente responsável pela regência da turma.
     * @param codigoDisciplina Código da disciplina no SIGAA (ex: MED001).
     * @param periodoLetivo Período acadêmico de oferta (ex: 2026.1).
     * @param anoSemestre Identificador cronológico do semestre (ex: 2026/1).
     * @param ativa Indicador de vigência e atividade da turma.
     * @param criadaEm Data e hora do registro da turma no sistema.
     */
    public Turma(
            Long id,
            Usuario professorResponsavel,
            String codigoDisciplina,
            String periodoLetivo,
            String anoSemestre,
            Boolean ativa,
            LocalDateTime criadaEm) {
        this(
                id,
                professorResponsavel,
                codigoDisciplina,
                "Segurança do Paciente e Auditoria Clínica",
                periodoLetivo,
                anoSemestre,
                ativa,
                criadaEm);
    }

    /**
     * Construtor completo com nome da disciplina.
     *
     * @param id Identificador numérico único da turma.
     * @param professorResponsavel Docente responsável pela regência da turma.
     * @param codigoDisciplina Código da disciplina no SIGAA (ex: MED001).
     * @param nomeDisciplina Nome descritivo da disciplina acadêmica.
     * @param periodoLetivo Período acadêmico de oferta (ex: 2026.1).
     * @param anoSemestre Identificador cronológico do semestre (ex: 2026/1).
     * @param ativa Indicador de vigência e atividade da turma.
     * @param criadaEm Data e hora do registro da turma no sistema.
     */
    public Turma(
            Long id,
            Usuario professorResponsavel,
            String codigoDisciplina,
            String nomeDisciplina,
            String periodoLetivo,
            String anoSemestre,
            Boolean ativa,
            LocalDateTime criadaEm) {
        this.id = id;
        this.professorResponsavel = professorResponsavel;
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina =
                nomeDisciplina != null && !nomeDisciplina.isBlank()
                        ? nomeDisciplina
                        : "Segurança do Paciente e Auditoria Clínica";
        this.periodoLetivo = periodoLetivo;
        this.anoSemestre = anoSemestre;
        this.ativa = ativa != null ? ativa : true;
        this.criadaEm = criadaEm != null ? criadaEm : LocalDateTime.now();
    }

    /**
     * Valida os atributos essenciais e regras de negócio da turma.
     *
     * @throws RegraNegocioException Caso o docente não tenha perfil de professor ou dados estejam
     *     inválidos.
     */
    public void validarInvariantes() {
        if (professorResponsavel == null) {
            throw new RegraNegocioException("O professor responsável pela turma é obrigatório.");
        }
        if (professorResponsavel.getPerfil() != PerfilUsuario.PROFESSOR
                && professorResponsavel.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new RegraNegocioException(
                    "Apenas usuários com perfil docente podem ser responsáveis por turmas.");
        }
        if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
            throw new RegraNegocioException("O código da disciplina é obrigatório.");
        }
        if (periodoLetivo == null || periodoLetivo.isBlank()) {
            throw new RegraNegocioException("O período letivo é obrigatório.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getProfessorResponsavel() {
        return professorResponsavel;
    }

    public void setProfessorResponsavel(Usuario professorResponsavel) {
        this.professorResponsavel = professorResponsavel;
    }

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public String getPeriodoLetivo() {
        return periodoLetivo;
    }

    public void setPeriodoLetivo(String periodoLetivo) {
        this.periodoLetivo = periodoLetivo;
    }

    public String getAnoSemestre() {
        return anoSemestre;
    }

    public void setAnoSemestre(String anoSemestre) {
        this.anoSemestre = anoSemestre;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turma turma = (Turma) o;
        return Objects.equals(id, turma.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Turma{"
                + "id="
                + id
                + ", codigoDisciplina='"
                + codigoDisciplina
                + '\''
                + ", periodoLetivo='"
                + periodoLetivo
                + '\''
                + ", ativa="
                + ativa
                + '}';
    }
}
