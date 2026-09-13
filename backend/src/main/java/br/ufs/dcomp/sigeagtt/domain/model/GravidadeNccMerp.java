package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Classificação de gravidade do dano ao paciente conforme o índice do National Coordinating Council
 * for Medication Error Reporting and Prevention (NCC MERP).
 *
 * <p>Na metodologia IHI Global Trigger Tool (IHI-GTT), apenas eventos adversos com dano
 * correspondente às Categorias E até I são considerados verdadeiros eventos adversos mensuráveis na
 * auditoria clínica hospitalar.
 */
public enum GravidadeNccMerp {
    /** Categoria E: Dano temporário ao paciente com necessidade de intervenção clínica. */
    CATEGORIA_E("Categoria E", "Dano temporário com necessidade de intervenção"),

    /**
     * Categoria F: Dano temporário ao paciente resultando em prolongamento da internação
     * hospitalar.
     */
    CATEGORIA_F("Categoria F", "Dano temporário com prolongamento de internação"),

    /** Categoria G: Dano permanente ao paciente. */
    CATEGORIA_G("Categoria G", "Dano permanente"),

    /**
     * Categoria H: Intervenção imediata necessária para suporte de vida a fim de evitar a morte do
     * paciente.
     */
    CATEGORIA_H("Categoria H", "Intervenção necessária para suporte à vida"),

    /** Categoria I: Óbito do paciente com contribuição ou decorrente do evento adverso. */
    CATEGORIA_I("Categoria I", "Óbito do paciente relacionado ao evento");

    private final String rotulo;
    private final String descricao;

    GravidadeNccMerp(String rotulo, String descricao) {
        this.rotulo = rotulo;
        this.descricao = descricao;
    }

    /**
     * Retorna o rótulo amigável da categoria NCC MERP.
     *
     * @return Nome legível da categoria.
     */
    public String getRotulo() {
        return rotulo;
    }

    /**
     * Retorna a descrição clínica do dano ao paciente.
     *
     * @return Descrição conceitual do dano.
     */
    public String getDescricao() {
        return descricao;
    }
}
