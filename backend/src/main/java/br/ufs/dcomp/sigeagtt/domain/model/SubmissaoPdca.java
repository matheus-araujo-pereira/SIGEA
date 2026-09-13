package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa o Ciclo PDCA (Plan, Do, Check, Act) de melhoria contínua
 * no SIGEA-GTT.
 *
 * <p>Estrutura as 4 fases iterativas para intervenção clínica da qualidade em saúde: Planejar
 * (Plan), Fazer/Executar (Do), Checar/Verificar (Check) e Agir Corretivamente/Padronizar (Act).
 */
public class SubmissaoPdca {

    private Long id;
    private SubmissaoAtividade submissao;
    private String planejar;
    private String fazer;
    private String checar;
    private String agir;

    /** Construtor padrão sem argumentos. */
    public SubmissaoPdca() {}

    /**
     * Construtor completo do Ciclo PDCA.
     *
     * @param id Identificador único numérico do ciclo PDCA.
     * @param submissao Submissão de atividade associada.
     * @param planejar Fase Plan: metas de segurança do paciente e plano de ação.
     * @param fazer Fase Do: implementação em escala piloto e capacitação da equipe.
     * @param checar Fase Check: auditoria dos resultados e comparação com a meta.
     * @param agir Fase Act: padronização institucional ou correção de desvios.
     */
    public SubmissaoPdca(
            Long id,
            SubmissaoAtividade submissao,
            String planejar,
            String fazer,
            String checar,
            String agir) {
        this.id = id;
        this.submissao = submissao;
        this.planejar = planejar;
        this.fazer = fazer;
        this.checar = checar;
        this.agir = agir;
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

    public String getPlanejar() {
        return planejar;
    }

    public void setPlanejar(String planejar) {
        this.planejar = planejar;
    }

    public String getFazer() {
        return fazer;
    }

    public void setFazer(String fazer) {
        this.fazer = fazer;
    }

    public String getChecar() {
        return checar;
    }

    public void setChecar(String checar) {
        this.checar = checar;
    }

    public String getAgir() {
        return agir;
    }

    public void setAgir(String agir) {
        this.agir = agir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissaoPdca that = (SubmissaoPdca) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
