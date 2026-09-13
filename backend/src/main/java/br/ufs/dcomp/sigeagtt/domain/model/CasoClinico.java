package br.ufs.dcomp.sigeagtt.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio puro que representa um Caso Clínico / Prontuário Simulado no SIGEA-GTT.
 *
 * <p>Armazena o prontuário hospitalar completo estruturado (admissão, alta, exames laboratoriais,
 * prescrições, evoluções multiprofissionais e sumário de alta) criado por docentes para treinamento
 * de auditoria clínica.
 */
public class CasoClinico {

    private Long id;
    private Usuario professorCriador;
    private UnidadeHospitalar unidadeHospitalar;
    private String titulo;
    private String descricaoCaso;
    private String objetivosAprendizagem;
    private String numeroAtendimento;
    private Integer idadePaciente;
    private LocalDate dataAdmissao;
    private LocalDate dataAlta;
    private Integer tempoPermanenciaDias;
    private String sumarioAlta;
    private String prescricoesMedicas;
    private String examesLaboratoriais;
    private String relatorioCirurgico;
    private String evolucoesMultiprofissionais;
    private LocalDateTime criadoEm;

    /** Construtor padrão sem argumentos. */
    public CasoClinico() {
        this.criadoEm = LocalDateTime.now();
    }

    /**
     * Construtor completo do Caso Clínico.
     *
     * @param id Identificador único numérico do caso.
     * @param professorCriador Docente autor do prontuário simulado.
     * @param unidadeHospitalar Unidade de internação hospitalar.
     * @param titulo Título de referência pedagógica do caso.
     * @param descricaoCaso Resumo da história clínica do paciente.
     * @param objetivosAprendizagem Objetivos instrucionais de segurança do paciente.
     * @param numeroAtendimento Número do prontuário / atendimento fictício.
     * @param idadePaciente Idade do paciente simulado em anos.
     * @param dataAdmissao Data de internação hospitalar.
     * @param dataAlta Data de encerramento da internação.
     * @param tempoPermanenciaDias Tempo de permanência do paciente em dias.
     * @param sumarioAlta Resumo médico de alta hospitalar.
     * @param prescricoesMedicas Prescrições farmacológicas e terapêuticas.
     * @param examesLaboratoriais Resultados e laudos de exames complementares.
     * @param relatorioCirurgico Descrição do ato operatório (quando aplicável).
     * @param evolucoesMultiprofissionais Notas da equipe multidisciplinar (médica, enfermagem,
     *     farmácia).
     * @param criadoEm Data e hora de cadastro.
     */
    public CasoClinico(
            Long id,
            Usuario professorCriador,
            UnidadeHospitalar unidadeHospitalar,
            String titulo,
            String descricaoCaso,
            String objetivosAprendizagem,
            String numeroAtendimento,
            Integer idadePaciente,
            LocalDate dataAdmissao,
            LocalDate dataAlta,
            Integer tempoPermanenciaDias,
            String sumarioAlta,
            String prescricoesMedicas,
            String examesLaboratoriais,
            String relatorioCirurgico,
            String evolucoesMultiprofissionais,
            LocalDateTime criadoEm) {
        this.id = id;
        this.professorCriador = professorCriador;
        this.unidadeHospitalar = unidadeHospitalar;
        this.titulo = titulo;
        this.descricaoCaso = descricaoCaso;
        this.objetivosAprendizagem = objetivosAprendizagem;
        this.numeroAtendimento = numeroAtendimento;
        this.idadePaciente = idadePaciente;
        this.dataAdmissao = dataAdmissao;
        this.dataAlta = dataAlta;
        this.tempoPermanenciaDias = tempoPermanenciaDias;
        this.sumarioAlta = sumarioAlta;
        this.prescricoesMedicas = prescricoesMedicas;
        this.examesLaboratoriais = examesLaboratoriais;
        this.relatorioCirurgico = relatorioCirurgico;
        this.evolucoesMultiprofissionais = evolucoesMultiprofissionais;
        this.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
    }

    /**
     * Valida as invariantes do caso clínico.
     *
     * @throws RegraNegocioException Caso atributos obrigatórios estejam ausentes ou datas sejam
     *     inconsistentes.
     */
    public void validarInvariantes() {
        if (titulo == null || titulo.isBlank()) {
            throw new RegraNegocioException("O título do caso clínico é obrigatório.");
        }
        if (numeroAtendimento == null || numeroAtendimento.isBlank()) {
            throw new RegraNegocioException("O número de atendimento do prontuário é obrigatório.");
        }
        if (dataAdmissao != null && dataAlta != null && dataAlta.isBefore(dataAdmissao)) {
            throw new RegraNegocioException(
                    "A data de alta não pode ser anterior à data de admissão.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getProfessorCriador() {
        return professorCriador;
    }

    public void setProfessorCriador(Usuario professorCriador) {
        this.professorCriador = professorCriador;
    }

    public UnidadeHospitalar getUnidadeHospitalar() {
        return unidadeHospitalar;
    }

    public void setUnidadeHospitalar(UnidadeHospitalar unidadeHospitalar) {
        this.unidadeHospitalar = unidadeHospitalar;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricaoCaso() {
        return descricaoCaso;
    }

    public void setDescricaoCaso(String descricaoCaso) {
        this.descricaoCaso = descricaoCaso;
    }

    public String getObjetivosAprendizagem() {
        return objetivosAprendizagem;
    }

    public void setObjetivosAprendizagem(String objetivosAprendizagem) {
        this.objetivosAprendizagem = objetivosAprendizagem;
    }

    public String getNumeroAtendimento() {
        return numeroAtendimento;
    }

    public void setNumeroAtendimento(String numeroAtendimento) {
        this.numeroAtendimento = numeroAtendimento;
    }

    public Integer getIdadePaciente() {
        return idadePaciente;
    }

    public void setIdadePaciente(Integer idadePaciente) {
        this.idadePaciente = idadePaciente;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public LocalDate getDataAlta() {
        return dataAlta;
    }

    public void setDataAlta(LocalDate dataAlta) {
        this.dataAlta = dataAlta;
    }

    public Integer getTempoPermanenciaDias() {
        return tempoPermanenciaDias;
    }

    public void setTempoPermanenciaDias(Integer tempoPermanenciaDias) {
        this.tempoPermanenciaDias = tempoPermanenciaDias;
    }

    public String getSumarioAlta() {
        return sumarioAlta;
    }

    public void setSumarioAlta(String sumarioAlta) {
        this.sumarioAlta = sumarioAlta;
    }

    public String getPrescricoesMedicas() {
        return prescricoesMedicas;
    }

    public void setPrescricoesMedicas(String prescricoesMedicas) {
        this.prescricoesMedicas = prescricoesMedicas;
    }

    public String getExamesLaboratoriais() {
        return examesLaboratoriais;
    }

    public void setExamesLaboratoriais(String examesLaboratoriais) {
        this.examesLaboratoriais = examesLaboratoriais;
    }

    public String getRelatorioCirurgico() {
        return relatorioCirurgico;
    }

    public void setRelatorioCirurgico(String relatorioCirurgico) {
        this.relatorioCirurgico = relatorioCirurgico;
    }

    public String getEvolucoesMultiprofissionais() {
        return evolucoesMultiprofissionais;
    }

    public void setEvolucoesMultiprofissionais(String evolucoesMultiprofissionais) {
        this.evolucoesMultiprofissionais = evolucoesMultiprofissionais;
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
        CasoClinico that = (CasoClinico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CasoClinico{"
                + "id="
                + id
                + ", titulo='"
                + titulo
                + '\''
                + ", numeroAtendimento='"
                + numeroAtendimento
                + '\''
                + '}';
    }
}
