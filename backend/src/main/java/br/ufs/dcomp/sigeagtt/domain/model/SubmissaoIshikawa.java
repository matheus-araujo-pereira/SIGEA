package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa o Diagrama de Causa e Efeito (Ishikawa 6M) de uma
 * submissão.
 *
 * <p>Ferramenta da qualidade para investigação de causa raiz dos eventos adversos identificados no
 * prontuário, analisando as 6 dimensões: Método, Mão de Obra, Material, Medida, Meio Ambiente e
 * Máquina.
 */
public class SubmissaoIshikawa {

    private Long id;
    private SubmissaoAtividade submissao;
    private String efeitoPrincipal;
    private String metodo;
    private String maoDeObra;
    private String material;
    private String medida;
    private String meioAmbiente;
    private String maquina;

    /** Construtor padrão sem argumentos. */
    public SubmissaoIshikawa() {}

    /**
     * Construtor completo do Diagrama de Ishikawa.
     *
     * @param id Identificador numérico do registro.
     * @param submissao Submissão de atividade associada.
     * @param efeitoPrincipal Efeito ou evento adverso principal a ser investigado.
     * @param metodo Causas associadas a processos, protocolos clínicos e fluxos de trabalho.
     * @param maoDeObra Causas associadas à capacitação, estresse ou comunicação da equipe.
     * @param material Causas associadas a insumos, medicamentos ou prontuários.
     * @param medida Causas associadas a monitoramento, exames e indicadores.
     * @param meioAmbiente Causas associadas à infraestrutura hospitalar, ruído ou iluminação.
     * @param maquina Causas associadas a equipamentos médicos, bombas de infusão ou monitores.
     */
    public SubmissaoIshikawa(
            Long id,
            SubmissaoAtividade submissao,
            String efeitoPrincipal,
            String metodo,
            String maoDeObra,
            String material,
            String medida,
            String meioAmbiente,
            String maquina) {
        this.id = id;
        this.submissao = submissao;
        this.efeitoPrincipal = efeitoPrincipal;
        this.metodo = metodo;
        this.maoDeObra = maoDeObra;
        this.material = material;
        this.medida = medida;
        this.meioAmbiente = meioAmbiente;
        this.maquina = maquina;
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

    public String getEfeitoPrincipal() {
        return efeitoPrincipal;
    }

    public void setEfeitoPrincipal(String efeitoPrincipal) {
        this.efeitoPrincipal = efeitoPrincipal;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getMaoDeObra() {
        return maoDeObra;
    }

    public void setMaoDeObra(String maoDeObra) {
        this.maoDeObra = maoDeObra;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getMeioAmbiente() {
        return meioAmbiente;
    }

    public void setMeioAmbiente(String meioAmbiente) {
        this.meioAmbiente = meioAmbiente;
    }

    public String getMaquina() {
        return maquina;
    }

    public void setMaquina(String maquina) {
        this.maquina = maquina;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissaoIshikawa that = (SubmissaoIshikawa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
