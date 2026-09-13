package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Payload para criação ou edição de casos clínicos e prontuários simulados no SIGEA-GTT.
 *
 * @param unidadeHospitalarId Identificador da unidade hospitalar de internação.
 * @param titulo Título de referência pedagógica do caso clínico.
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
 * @param relatorioCirurgico Descrição do ato cirúrgico (opcional).
 * @param evolucoesMultiprofissionais Notas da equipe multiprofissional (médica, enfermagem,
 *     farmácia).
 */
@Schema(description = "Dados para cadastro ou atualização de prontuário simulado.")
public record SalvarCasoClinicoDTO(
        @Schema(
                        description = "ID da unidade hospitalar",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A unidade hospitalar é obrigatória")
                Long unidadeHospitalarId,
        @Schema(
                        description = "Título do caso clínico",
                        example = "Sepse Grave com Choque Séptico",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O título é obrigatório")
                @Size(max = 150, message = "O título não pode exceder 150 caracteres")
                String titulo,
        @Schema(
                        description = "Descrição e história clínica do paciente",
                        example = "Paciente admitido via pronto-socorro...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A descrição do caso é obrigatória")
                String descricaoCaso,
        @Schema(
                        description = "Objetivos de aprendizagem",
                        example = "Auditar gatilhos de medicação e cuidados gerais...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Os objetivos de aprendizagem são obrigatórios")
                String objetivosAprendizagem,
        @Schema(
                        description = "Número do prontuário fictício",
                        example = "PRT-2026-001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O número de atendimento é obrigatório")
                @Size(max = 50, message = "O número de atendimento não pode exceder 50 caracteres")
                String numeroAtendimento,
        @Schema(
                        description = "Idade do paciente",
                        example = "62",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A idade do paciente é obrigatória")
                Integer idadePaciente,
        @Schema(
                        description = "Data de admissão",
                        example = "2026-09-01",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A data de admissão é obrigatória")
                LocalDate dataAdmissao,
        @Schema(
                        description = "Data de alta",
                        example = "2026-09-07",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "A data de alta é obrigatória")
                LocalDate dataAlta,
        @Schema(
                        description = "Dias de permanência",
                        example = "6",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O tempo de permanência é obrigatório")
                Integer tempoPermanenciaDias,
        @Schema(
                        description = "Sumário de alta",
                        example = "Paciente com resolução do foco infeccioso...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O sumário de alta é obrigatório")
                String sumarioAlta,
        @Schema(
                        description = "Prescrições médicas",
                        example = "Vancomicina 1g IV de 12/12h...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "As prescrições médicas são obrigatórias")
                String prescricoesMedicas,
        @Schema(
                        description = "Exames laboratoriais",
                        example = "Hemocultura: Positiva para S. aureus...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Os exames laboratoriais são obrigatórios")
                String examesLaboratoriais,
        @Schema(
                        description = "Relatório cirúrgico",
                        example = "Desbridamento cirúrgico de ferida operatória",
                        nullable = true)
                String relatorioCirurgico,
        @Schema(
                        description = "Evoluções multiprofissionais",
                        example = "Enfermagem: Curativo realizado...",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "As evoluções multiprofissionais são obrigatórias")
                String evolucoesMultiprofissionais) {

    /**
     * Converte o DTO para o comando de domínio.
     *
     * @return DadosSalvarCasoClinico
     */
    public br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase.DadosSalvarCasoClinico
            paraComando() {
        return new br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase
                .DadosSalvarCasoClinico(
                unidadeHospitalarId,
                titulo,
                descricaoCaso,
                objetivosAprendizagem,
                numeroAtendimento,
                idadePaciente,
                dataAdmissao,
                dataAlta,
                tempoPermanenciaDias,
                sumarioAlta,
                prescricoesMedicas,
                examesLaboratoriais,
                relatorioCirurgico,
                evolucoesMultiprofissionais);
    }
}
