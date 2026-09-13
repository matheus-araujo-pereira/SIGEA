package br.ufs.dcomp.sigeagtt.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa um item do Plano de Ação 5W3H no SIGEA-GTT.
 *
 * <p>Ferramenta gerencial para planejamento operacional das intervenções de melhoria em resposta
 * aos eventos adversos: What (O que), Why (Por que), Who (Quem), Where (Onde), When (Quando), How
 * (Como), How Much (Quanto custa) e How to Measure (Como medir).
 */
public class SubmissaoPlano5w3h {

    private Long id;
    private SubmissaoAtividade submissao;
    private String oQue;
    private String porQue;
    private String quem;
    private String onde;
    private String quando;
    private String como;
    private BigDecimal quantoCusta;
    private String comoMedir;

    /** Construtor padrão sem argumentos. */
    public SubmissaoPlano5w3h() {}

    /**
     * Construtor completo do item do Plano de Ação 5W3H.
     *
     * @param id Identificador único numérico do item.
     * @param submissao Submissão de atividade à qual o plano pertence.
     * @param oQue Descrição da ação corretiva ou preventiva proposta.
     * @param porQue Justificativa baseada na causa raiz identificada.
     * @param quem Cargo, função ou responsável pela execução.
     * @param onde Setor ou unidade hospitalar de aplicação.
     * @param quando Cronograma ou prazo de implantação.
     * @param como Método e etapas operacionais de execução.
     * @param quantoCusta Estimativa de custo financeiro orçado.
     * @param comoMedir Indicador de processo ou resultado para monitorar a eficácia.
     */
    public SubmissaoPlano5w3h(
            Long id,
            SubmissaoAtividade submissao,
            String oQue,
            String porQue,
            String quem,
            String onde,
            String quando,
            String como,
            BigDecimal quantoCusta,
            String comoMedir) {
        this.id = id;
        this.submissao = submissao;
        this.oQue = oQue;
        this.porQue = porQue;
        this.quem = quem;
        this.onde = onde;
        this.quando = quando;
        this.como = como;
        this.quantoCusta = quantoCusta;
        this.comoMedir = comoMedir;
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

    public String getOQue() {
        return oQue;
    }

    public void setOQue(String oQue) {
        this.oQue = oQue;
    }

    public String getPorQue() {
        return porQue;
    }

    public void setPorQue(String porQue) {
        this.porQue = porQue;
    }

    public String getQuem() {
        return quem;
    }

    public void setQuem(String quem) {
        this.quem = quem;
    }

    public String getOnde() {
        return onde;
    }

    public void setOnde(String onde) {
        this.onde = onde;
    }

    public String getQuando() {
        return quando;
    }

    public void setQuando(String quando) {
        this.quando = quando;
    }

    public String getComo() {
        return como;
    }

    public void setComo(String como) {
        this.como = como;
    }

    public BigDecimal getQuantoCusta() {
        return quantoCusta;
    }

    public void setQuantoCusta(BigDecimal quantoCusta) {
        this.quantoCusta = quantoCusta;
    }

    public String getComoMedir() {
        return comoMedir;
    }

    public void setComoMedir(String comoMedir) {
        this.comoMedir = comoMedir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissaoPlano5w3h that = (SubmissaoPlano5w3h) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
