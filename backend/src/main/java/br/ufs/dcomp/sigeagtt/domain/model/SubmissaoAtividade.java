package br.ufs.dcomp.sigeagtt.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa a Submissão / Resolução de uma Atividade de Auditoria
 * Clínica por um discente no SIGEA-GTT.
 *
 * <p>Agrupa a revisão retrospectiva com os gatilhos achados (IHI-GTT), a análise de causa raiz pelo
 * Diagrama de Ishikawa (6M), o Plano de Ação 5W3H, o Ciclo PDCA e a avaliação docente com parecer
 * formativo e nota numérica.
 */
public class SubmissaoAtividade {

    private Long id;
    private AtividadeEducacional atividade;
    private Usuario aluno;
    private StatusSubmissao status;
    private Integer tempoGastoSegundos;
    private LocalDateTime dataInicio;
    private LocalDateTime dataSubmissao;
    private Usuario professorCorretor;
    private BigDecimal nota;
    private String parecerDocente;
    private LocalDateTime dataAvaliacao;
    private List<SubmissaoGatilho> achadosGatilhos = new ArrayList<>();
    private SubmissaoIshikawa ishikawa;
    private List<SubmissaoPlano5w3h> planos5w3h = new ArrayList<>();
    private SubmissaoPdca pdca;

    /** Construtor padrão sem argumentos. */
    public SubmissaoAtividade() {
        this.status = StatusSubmissao.EM_ANDAMENTO;
        this.tempoGastoSegundos = 0;
        this.dataInicio = LocalDateTime.now();
    }

    /**
     * Construtor básico para inicialização de submissão por um discente.
     *
     * @param atividade Atividade educacional vinculada.
     * @param aluno Discente responsável pela resolução.
     */
    public SubmissaoAtividade(AtividadeEducacional atividade, Usuario aluno) {
        this.atividade = atividade;
        this.aluno = aluno;
        this.status = StatusSubmissao.EM_ANDAMENTO;
        this.tempoGastoSegundos = 0;
        this.dataInicio = LocalDateTime.now();
    }

    /**
     * Valida os atributos e regras de transição de estado da submissão.
     *
     * @throws RegraNegocioException Caso regras de notas, perfis ou vínculos sejam violados.
     */
    public void validarInvariantes() {
        if (atividade == null) {
            throw new RegraNegocioException("A atividade educacional vinculada é obrigatória.");
        }
        if (aluno == null) {
            throw new RegraNegocioException("O discente vinculado é obrigatório.");
        }
        if (nota != null
                && (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(BigDecimal.TEN) > 0)) {
            throw new RegraNegocioException(
                    "A nota de avaliação docente deve situar-se estritamente entre 0.00 e 10.00.");
        }
    }

    /**
     * Finaliza a submissão pelo aluno, alterando o status para SUBMETIDA.
     *
     * @param tempoGastoSegundos Tempo total despendido na resolução em segundos.
     */
    public void submeter(Integer tempoGastoSegundos) {
        this.status = StatusSubmissao.SUBMETIDA;
        this.tempoGastoSegundos =
                tempoGastoSegundos != null ? tempoGastoSegundos : this.tempoGastoSegundos;
        this.dataSubmissao = LocalDateTime.now();
    }

    /**
     * Registra a correção e homologação formativa docente.
     *
     * @param professor Docente avaliador.
     * @param nota Nota atribuída (0.00 a 10.00).
     * @param parecer Parecer pedagógico detalhado.
     */
    public void avaliar(Usuario professor, BigDecimal nota, String parecer) {
        if (professor == null
                || (professor.getPerfil() != PerfilUsuario.PROFESSOR
                        && professor.getPerfil() != PerfilUsuario.ADMINISTRADOR)) {
            throw new RegraNegocioException(
                    "Apenas docentes ou administradores podem avaliar submissões.");
        }
        if (nota != null
                && (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(BigDecimal.TEN) > 0)) {
            throw new RegraNegocioException("A nota deve estar entre 0.00 e 10.00.");
        }
        this.professorCorretor = professor;
        this.nota = nota;
        this.parecerDocente = parecer;
        this.dataAvaliacao = LocalDateTime.now();
        this.status = StatusSubmissao.AVALIADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtividadeEducacional getAtividade() {
        return atividade;
    }

    public void setAtividade(AtividadeEducacional atividade) {
        this.atividade = atividade;
    }

    public Usuario getAluno() {
        return aluno;
    }

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
    }

    public StatusSubmissao getStatus() {
        return status;
    }

    public void setStatus(StatusSubmissao status) {
        this.status = status;
    }

    public Integer getTempoGastoSegundos() {
        return tempoGastoSegundos;
    }

    public void setTempoGastoSegundos(Integer tempoGastoSegundos) {
        this.tempoGastoSegundos = tempoGastoSegundos;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataSubmissao() {
        return dataSubmissao;
    }

    public void setDataSubmissao(LocalDateTime dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public Usuario getProfessorCorretor() {
        return professorCorretor;
    }

    public void setProfessorCorretor(Usuario professorCorretor) {
        this.professorCorretor = professorCorretor;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public String getParecerDocente() {
        return parecerDocente;
    }

    public void setParecerDocente(String parecerDocente) {
        this.parecerDocente = parecerDocente;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public List<SubmissaoGatilho> getAchadosGatilhos() {
        return achadosGatilhos;
    }

    public void setAchadosGatilhos(List<SubmissaoGatilho> achadosGatilhos) {
        this.achadosGatilhos = achadosGatilhos;
    }

    public SubmissaoIshikawa getIshikawa() {
        return ishikawa;
    }

    public void setIshikawa(SubmissaoIshikawa ishikawa) {
        this.ishikawa = ishikawa;
    }

    public List<SubmissaoPlano5w3h> getPlanos5w3h() {
        return planos5w3h;
    }

    public void setPlanos5w3h(List<SubmissaoPlano5w3h> planos5w3h) {
        this.planos5w3h = planos5w3h;
    }

    public SubmissaoPdca getPdca() {
        return pdca;
    }

    public void setPdca(SubmissaoPdca pdca) {
        this.pdca = pdca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissaoAtividade that = (SubmissaoAtividade) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
