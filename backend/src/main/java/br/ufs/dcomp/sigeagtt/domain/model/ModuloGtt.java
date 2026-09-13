package br.ufs.dcomp.sigeagtt.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa um Módulo da metodologia Global Trigger Tool (IHI-GTT).
 *
 * <p>Mapeia os módulos canônicos do IHI: Cuidados Gerais (C), Cirúrgico (S), Medicamentoso (M),
 * Unidade de Terapia Intensiva (I) e Cuidados Perinatais (P).
 */
public class ModuloGtt {

    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private LocalDateTime criadoEm;

    /** Construtor padrão sem argumentos. */
    public ModuloGtt() {
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    /**
     * Construtor completo do módulo GTT.
     *
     * @param id Identificador único numérico do módulo.
     * @param codigo Código do módulo (ex: CUIDADOS, CIRURGICO, MEDICAMENTOSO).
     * @param nome Nome de exibição do módulo.
     * @param descricao Finalidade e escopo clínico do módulo.
     * @param ativo Indicador de ativação do módulo.
     * @param criadoEm Data e hora de criação.
     */
    public ModuloGtt(
            Long id,
            String codigo,
            String nome,
            String descricao,
            Boolean ativo,
            LocalDateTime criadoEm) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo != null ? ativo : true;
        this.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
    }

    /**
     * Valida os atributos essenciais do módulo.
     *
     * @throws RegraNegocioException Caso código ou nome estejam ausentes.
     */
    public void validarInvariantes() {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraNegocioException("O código do módulo GTT é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do módulo GTT é obrigatório.");
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuloGtt moduloGtt = (ModuloGtt) o;
        return Objects.equals(id, moduloGtt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ModuloGtt{"
                + "id="
                + id
                + ", codigo='"
                + codigo
                + '\''
                + ", nome='"
                + nome
                + '\''
                + ", ativo="
                + ativo
                + '}';
    }
}
