package br.ufs.dcomp.sigeagtt.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa uma Atividade Educacional de Auditoria Clínica no
 * SIGEA-GTT.
 *
 * <p>Vincula uma {@link Turma} a um {@link CasoClinico} com prazos estipulados e tempo limite de
 * execução em conformidade com a regra dos 20 minutos por prontuário preconizada pelo Institute for
 * Healthcare Improvement (IHI).
 */
public class AtividadeEducacional {

    private Long id;
    private Turma turma;
    private CasoClinico casoClinico;
    private String titulo;
    private String orientacoesPedagogicas;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer tempoLimiteMinutos;
    private Boolean ativa;
    private LocalDateTime criadaEm;

    /** Construtor padrão sem argumentos. */
    public AtividadeEducacional() {
        this.tempoLimiteMinutos = 20;
        this.ativa = true;
        this.criadaEm = LocalDateTime.now();
    }

    /**
     * Construtor completo da atividade educacional.
     *
     * @param id Identificador único numérico da atividade.
     * @param turma Turma acadêmica participante da auditoria.
     * @param casoClinico Prontuário simulado a ser auditado retrospectivamente.
     * @param titulo Título da atividade educacional.
     * @param orientacoesPedagogicas Instruções pedagógicas e critérios de avaliação do docente.
     * @param dataInicio Início do período de liberação da atividade para os discentes.
     * @param dataFim Término do prazo para entrega da submissão.
     * @param tempoLimiteMinutos Limite de tempo cronometrado para auditoria (padrão 20 min).
     * @param ativa Indicador de disponibilidade da atividade.
     * @param criadaEm Data de criação.
     */
    public AtividadeEducacional(
            Long id,
            Turma turma,
            CasoClinico casoClinico,
            String titulo,
            String orientacoesPedagogicas,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer tempoLimiteMinutos,
            Boolean ativa,
            LocalDateTime criadaEm) {
        this.id = id;
        this.turma = turma;
        this.casoClinico = casoClinico;
        this.titulo = titulo;
        this.orientacoesPedagogicas = orientacoesPedagogicas;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tempoLimiteMinutos = tempoLimiteMinutos != null ? tempoLimiteMinutos : 20;
        this.ativa = ativa != null ? ativa : true;
        this.criadaEm = criadaEm != null ? criadaEm : LocalDateTime.now();
    }

    /**
     * Valida os atributos e prazos cronológicos da atividade educacional.
     *
     * @throws RegraNegocioException Caso prazos ou vínculos obrigatórios estejam inconsistentes.
     */
    public void validarInvariantes() {
        if (titulo == null || titulo.isBlank()) {
            throw new RegraNegocioException("O título da atividade educacional é obrigatório.");
        }
        if (turma == null) {
            throw new RegraNegocioException("A turma vinculada à atividade é obrigatória.");
        }
        if (casoClinico == null) {
            throw new RegraNegocioException("O caso clínico associado é obrigatório.");
        }
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new RegraNegocioException(
                    "A data de término não pode ser anterior à data de início da atividade.");
        }
        if (tempoLimiteMinutos != null && tempoLimiteMinutos <= 0) {
            throw new RegraNegocioException("O tempo limite em minutos deve ser maior que zero.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public CasoClinico getCasoClinico() {
        return casoClinico;
    }

    public void setCasoClinico(CasoClinico casoClinico) {
        this.casoClinico = casoClinico;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOrientacoesPedagogicas() {
        return orientacoesPedagogicas;
    }

    public void setOrientacoesPedagogicas(String orientacoesPedagogicas) {
        this.orientacoesPedagogicas = orientacoesPedagogicas;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getTempoLimiteMinutos() {
        return tempoLimiteMinutos;
    }

    public void setTempoLimiteMinutos(Integer tempoLimiteMinutos) {
        this.tempoLimiteMinutos = tempoLimiteMinutos;
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
        AtividadeEducacional that = (AtividadeEducacional) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AtividadeEducacional{"
                + "id="
                + id
                + ", titulo='"
                + titulo
                + '\''
                + ", ativa="
                + ativa
                + '}';
    }
}
