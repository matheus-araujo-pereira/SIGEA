package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa um Gatilho Clínico Identificado (Achado GTT) na auditoria
 * de um prontuário.
 *
 * <p>Documenta a presença de gatilho, se houve dano confirmado ao paciente, justificativa clínica,
 * dano presente na admissão e classificação de gravidade NCC MERP (Categorias E a I).
 */
public class SubmissaoGatilho {

    private Long id;
    private SubmissaoAtividade submissao;
    private GatilhoGtt gatilho;
    private CategoriaEventoAdverso categoriaEventoAdverso;
    private Boolean confirmouDano;
    private String justificativaDano;
    private Boolean danoPresenteAdmissao;
    private GravidadeNccMerp gravidade;

    /** Construtor padrão sem argumentos. */
    public SubmissaoGatilho() {
        this.confirmouDano = false;
        this.danoPresenteAdmissao = false;
    }

    /**
     * Construtor completo de achado de gatilho.
     *
     * @param id Identificador único numérico do achado.
     * @param submissao Submissão de auditoria vinculada.
     * @param gatilho Gatilho IHI-GTT identificado.
     * @param categoriaEventoAdverso Categoria do evento adverso (se confirmado dano).
     * @param confirmouDano Se o gatilho resultou em dano real ao paciente.
     * @param justificativaDano Justificativa clínica da equipe auditora.
     * @param danoPresenteAdmissao Se o evento adverso ocorreu previamente à admissão hospitalar.
     * @param gravidade Classificação de gravidade pelo índice NCC MERP.
     */
    public SubmissaoGatilho(
            Long id,
            SubmissaoAtividade submissao,
            GatilhoGtt gatilho,
            CategoriaEventoAdverso categoriaEventoAdverso,
            Boolean confirmouDano,
            String justificativaDano,
            Boolean danoPresenteAdmissao,
            GravidadeNccMerp gravidade) {
        this.id = id;
        this.submissao = submissao;
        this.gatilho = gatilho;
        this.categoriaEventoAdverso = categoriaEventoAdverso;
        this.confirmouDano = confirmouDano != null ? confirmouDano : false;
        this.justificativaDano = justificativaDano;
        this.danoPresenteAdmissao = danoPresenteAdmissao != null ? danoPresenteAdmissao : false;
        this.gravidade = gravidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubmissaoAtividade getSubmissao() {
        return submissao;
    }

    public void setSubmissao(SubmissaoAtividade submissao) {
        this.submissao = submissao;
    }

    public GatilhoGtt getGatilho() {
        return gatilho;
    }

    public void setGatilho(GatilhoGtt gatilho) {
        this.gatilho = gatilho;
    }

    public CategoriaEventoAdverso getCategoriaEventoAdverso() {
        return categoriaEventoAdverso;
    }

    public void setCategoriaEventoAdverso(CategoriaEventoAdverso categoriaEventoAdverso) {
        this.categoriaEventoAdverso = categoriaEventoAdverso;
    }

    public Boolean getConfirmouDano() {
        return confirmouDano;
    }

    public void setConfirmouDano(Boolean confirmouDano) {
        this.confirmouDano = confirmouDano;
    }

    public String getJustificativaDano() {
        return justificativaDano;
    }

    public void setJustificativaDano(String justificativaDano) {
        this.justificativaDano = justificativaDano;
    }

    public Boolean getDanoPresenteAdmissao() {
        return danoPresenteAdmissao;
    }

    public void setDanoPresenteAdmissao(Boolean danoPresenteAdmissao) {
        this.danoPresenteAdmissao = danoPresenteAdmissao;
    }

    public GravidadeNccMerp getGravidade() {
        return gravidade;
    }

    public void setGravidade(GravidadeNccMerp gravidade) {
        this.gravidade = gravidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissaoGatilho that = (SubmissaoGatilho) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
