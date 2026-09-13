package br.ufs.dcomp.sigeagtt.domain.model;

import java.util.Objects;

/**
 * Entidade de domínio puro que representa uma Unidade Hospitalar no SIGEA-GTT.
 *
 * <p>Mapeia enfermarias, clínicas e UTIs do Hospital Universitário (ex: UTI-Adulto, Clínica Médica,
 * Pediatria) onde ocorrem as internações dos prontuários clínicos simulados.
 */
public class UnidadeHospitalar {

    private Long id;
    private String nome;
    private String sigla;
    private Boolean ativa;

    /** Construtor padrão sem argumentos. */
    public UnidadeHospitalar() {
        this.ativa = true;
    }

    /**
     * Construtor completo da entidade UnidadeHospitalar.
     *
     * @param id Identificador único numérico da unidade.
     * @param nome Nome por extenso da unidade ou setor hospitalar.
     * @param sigla Código mnemônico ou sigla da unidade (ex: UTI-A, CMED).
     * @param ativa Indicador de disponibilidade da unidade para novos casos.
     */
    public UnidadeHospitalar(Long id, String nome, String sigla, Boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.ativa = ativa != null ? ativa : true;
    }

    /**
     * Valida os atributos essenciais da unidade hospitalar.
     *
     * @throws RegraNegocioException Caso nome ou sigla estejam ausentes.
     */
    public void validarInvariantes() {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome da unidade hospitalar é obrigatório.");
        }
        if (sigla == null || sigla.isBlank()) {
            throw new RegraNegocioException("A sigla da unidade hospitalar é obrigatória.");
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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
        UnidadeHospitalar that = (UnidadeHospitalar) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UnidadeHospitalar{"
                + "id="
                + id
                + ", nome='"
                + nome
                + '\''
                + ", sigla='"
                + sigla
                + '\''
                + ", ativa="
                + ativa
                + '}';
    }
}
