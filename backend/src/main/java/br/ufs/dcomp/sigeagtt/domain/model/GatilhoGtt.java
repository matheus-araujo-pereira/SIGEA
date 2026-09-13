package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa um Gatilho Clínico da metodologia IHI Global Trigger
 * Tool.
 *
 * <p>Identifica pistas diagnósticas, laboratoriais ou prescricionais que sinalizam a possível
 * ocorrência de um evento adverso no paciente (ex: C1 - Parada Cardiorrespiratória, M1 - Vitamina K
 * para reversão de anticoagulação).
 */
public class GatilhoGtt {

    private Long id;
    private String codigo;
    private ModuloGtt modulo;
    private String descricao;
    private String limiarReferencia;
    private Boolean ativo;

    /** Construtor padrão sem argumentos. */
    public GatilhoGtt() {
        this.ativo = true;
    }

    /**
     * Construtor completo do gatilho clínico GTT.
     *
     * @param id Identificador único numérico do gatilho.
     * @param codigo Código identificador do gatilho (ex: C1, M1, S2, I3).
     * @param modulo Módulo ao qual o gatilho pertence.
     * @param descricao Descrição clínica operacional do gatilho.
     * @param limiarReferencia Valores de referência laboratoriais ou critérios diagnósticos.
     * @param ativo Indicador de ativação do gatilho.
     */
    public GatilhoGtt(
            Long id,
            String codigo,
            ModuloGtt modulo,
            String descricao,
            String limiarReferencia,
            Boolean ativo) {
        this.id = id;
        this.codigo = codigo;
        this.modulo = modulo;
        this.descricao = descricao;
        this.limiarReferencia = limiarReferencia;
        this.ativo = ativo != null ? ativo : true;
    }

    /**
     * Valida os atributos essenciais do gatilho clínico.
     *
     * @throws RegraNegocioException Caso código, módulo ou descrição estejam ausentes.
     */
    public void validarInvariantes() {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraNegocioException("O código do gatilho clínico é obrigatório.");
        }
        if (modulo == null) {
            throw new RegraNegocioException("O módulo GTT associado é obrigatório.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraNegocioException("A descrição clínica do gatilho é obrigatória.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ModuloGtt getModulo() {
        return modulo;
    }

    public void setModulo(ModuloGtt modulo) {
        this.modulo = modulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLimiarReferencia() {
        return limiarReferencia;
    }

    public void setLimiarReferencia(String limiarReferencia) {
        this.limiarReferencia = limiarReferencia;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatilhoGtt that = (GatilhoGtt) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GatilhoGtt{"
                + "id="
                + id
                + ", codigo='"
                + codigo
                + '\''
                + ", descricao='"
                + descricao
                + '\''
                + ", ativo="
                + ativo
                + '}';
    }
}
