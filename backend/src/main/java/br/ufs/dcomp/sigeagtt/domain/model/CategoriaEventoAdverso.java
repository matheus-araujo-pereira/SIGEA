package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa uma Categoria de Evento Adverso no SIGEA-GTT.
 *
 * <p>Mapeia a tipologia dos incidentes com dano identificados na auditoria clínica hospitalar, como
 * Infecção Relacionada à Assistência à Saúde (IRAS), Queda de Leito, Lesão por Pressão (LPP), Erro
 * de Medicação e Complicações Cirúrgicas.
 */
public class CategoriaEventoAdverso {

    private Long id;
    private String nome;
    private String definicaoOperacional;
    private Boolean ativa;

    /** Construtor padrão sem argumentos. */
    public CategoriaEventoAdverso() {
        this.ativa = true;
    }

    /**
     * Construtor completo da categoria de evento adverso.
     *
     * @param id Identificador único numérico da categoria.
     * @param nome Nome padronizado da categoria de evento adverso.
     * @param definicaoOperacional Critérios e definição operacional segundo os protocolos do MS /
     *     ANVISA / IHI.
     * @param ativa Indicador de disponibilidade da categoria.
     */
    public CategoriaEventoAdverso(
            Long id, String nome, String definicaoOperacional, Boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.definicaoOperacional = definicaoOperacional;
        this.ativa = ativa != null ? ativa : true;
    }

    /**
     * Valida os atributos essenciais da categoria de evento adverso.
     *
     * @throws RegraNegocioException Caso o nome ou a definição operacional estejam em branco.
     */
    public void validarInvariantes() {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome da categoria de evento adverso é obrigatório.");
        }
        if (definicaoOperacional == null || definicaoOperacional.isBlank()) {
            throw new RegraNegocioException("A definição operacional da categoria é obrigatória.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDefinicaoOperacional() {
        return definicaoOperacional;
    }

    public void setDefinicaoOperacional(String definicaoOperacional) {
        this.definicaoOperacional = definicaoOperacional;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaEventoAdverso that = (CategoriaEventoAdverso) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CategoriaEventoAdverso{"
                + "id="
                + id
                + ", nome='"
                + nome
                + '\''
                + ", ativa="
                + ativa
                + '}';
    }
}
