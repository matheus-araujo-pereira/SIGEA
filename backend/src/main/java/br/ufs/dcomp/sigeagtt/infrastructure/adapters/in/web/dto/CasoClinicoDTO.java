package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payload de resposta com os dados completos do prontuário simulado.
 *
 * @param id Identificador do caso clínico.
 * @param professorCriadorId Identificador do docente autor.
 * @param professorCriadorNome Nome do docente autor.
 * @param unidadeHospitalarId Identificador da unidade de internação.
 * @param unidadeHospitalarNome Nome da unidade hospitalar.
 * @param unidadeHospitalarSigla Sigla da unidade hospitalar.
 * @param titulo Título de referência pedagógica.
 * @param descricaoCaso Resumo da história clínica do paciente.
 * @param objetivosAprendizagem Objetivos instrucionais de segurança do paciente.
 * @param numeroAtendimento Número fictício de atendimento hospitalar.
 * @param idadePaciente Idade do paciente simulado em anos.
 * @param dataAdmissao Data de internação hospitalar.
 * @param dataAlta Data de encerramento da internação.
 * @param tempoPermanenciaDias Tempo de permanência do paciente em dias.
 * @param sumarioAlta Resumo médico de alta hospitalar.
 * @param prescricoesMedicas Prescrições farmacológicas e terapêuticas.
 * @param examesLaboratoriais Resultados e laudos de exames complementares.
 * @param relatorioCirurgico Descrição do ato operatório (quando houver).
 * @param evolucoesMultiprofissionais Notas da equipe multiprofissional.
 * @param criadoEm Data e hora de criação do caso.
 */
@Schema(description = "Dados detalhados de caso clínico / prontuário simulado.")
public record CasoClinicoDTO(
        @Schema(description = "Identificador único do caso clínico", example = "200") Long id,
        @Schema(description = "ID do professor autor", example = "2") Long professorCriadorId,
        @Schema(description = "Nome do professor autor", example = "Prof. Dr. Marcos")
                String professorCriadorNome,
        @Schema(description = "ID da unidade hospitalar", example = "1") Long unidadeHospitalarId,
        @Schema(description = "Nome da unidade hospitalar", example = "UTI Adulto")
                String unidadeHospitalarNome,
        @Schema(description = "Sigla da unidade hospitalar", example = "UTI-A")
                String unidadeHospitalarSigla,
        @Schema(
                        description = "Título do caso clínico",
                        example = "Sepse Grave com Falência Múltipla de Órgãos")
                String titulo,
        @Schema(
                        description = "Descrição e história clínica do paciente",
                        example = "Paciente admitido com quadro febril agudo...")
                String descricaoCaso,
        @Schema(
                        description = "Objetivos pedagógicos da auditoria",
                        example = "Identificar atraso na administração de antimicrobianos...")
                String objetivosAprendizagem,
        @Schema(description = "Número do prontuário hospitalar fictício", example = "PRT-2026-001")
                String numeroAtendimento,
        @Schema(description = "Idade do paciente em anos", example = "58") Integer idadePaciente,
        @Schema(description = "Data de admissão", example = "2026-09-01") LocalDate dataAdmissao,
        @Schema(description = "Data de alta", example = "2026-09-06") LocalDate dataAlta,
        @Schema(description = "Tempo de internação em dias", example = "5")
                Integer tempoPermanenciaDias,
        @Schema(
                        description = "Sumário de alta médica",
                        example = "Paciente evoluiu favoravelmente após antibiótico...")
                String sumarioAlta,
        @Schema(description = "Prescrições médicas", example = "Ceftriaxona 1g IV de 12/12h...")
                String prescricoesMedicas,
        @Schema(
                        description = "Exames laboratoriais",
                        example = "Lactato: 3.8 mmol/L; Leucócitos: 18.000...")
                String examesLaboratoriais,
        @Schema(
                        description = "Relatório cirúrgico",
                        example = "Apendicectomia videolaparoscópica realizada sem intercorrências",
                        nullable = true)
                String relatorioCirurgico,
        @Schema(
                        description = "Evoluções da equipe multiprofissional",
                        example = "Enfermagem 08h: paciente taquipneico...")
                String evolucoesMultiprofissionais,
        @Schema(description = "Data de cadastro do caso", example = "2026-09-12T08:00:00")
                LocalDateTime criadoEm) {

    /**
     * Converte entidade de domínio puro em DTO de resposta.
     *
     * @param c Entidade CasoClinico.
     * @return DTO correspondente.
     */
    public static CasoClinicoDTO deEntidade(CasoClinico c) {
        if (c == null) return null;
        return new CasoClinicoDTO(
                c.getId(),
                c.getProfessorCriador() != null ? c.getProfessorCriador().getId() : null,
                c.getProfessorCriador() != null ? c.getProfessorCriador().getNomeCompleto() : null,
                c.getUnidadeHospitalar() != null ? c.getUnidadeHospitalar().getId() : null,
                c.getUnidadeHospitalar() != null ? c.getUnidadeHospitalar().getNome() : null,
                c.getUnidadeHospitalar() != null ? c.getUnidadeHospitalar().getSigla() : null,
                c.getTitulo(),
                c.getDescricaoCaso(),
                c.getObjetivosAprendizagem(),
                c.getNumeroAtendimento(),
                c.getIdadePaciente(),
                c.getDataAdmissao(),
                c.getDataAlta(),
                c.getTempoPermanenciaDias(),
                c.getSumarioAlta(),
                c.getPrescricoesMedicas(),
                c.getExamesLaboratoriais(),
                c.getRelatorioCirurgico(),
                c.getEvolucoesMultiprofissionais(),
                c.getCriadoEm());
    }
}
