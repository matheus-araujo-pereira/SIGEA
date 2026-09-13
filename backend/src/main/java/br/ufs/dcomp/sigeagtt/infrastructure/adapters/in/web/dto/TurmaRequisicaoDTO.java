package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para criação ou edição de turmas acadêmicas no SIGEA-GTT.
 *
 * @param professorResponsavelId Identificador do docente responsável pela turma.
 * @param codigoDisciplina Código da disciplina no SIGAA (ex: MED001).
 * @param periodoLetivo Período acadêmico de oferta (ex: 2026.1).
 * @param anoSemestre Identificador do ano/semestre cronológico (ex: 2026/1).
 */
@Schema(description = "Dados para cadastro ou atualização de turma acadêmica.")
public record TurmaRequisicaoDTO(
        @Schema(
                        description = "ID do professor responsável",
                        example = "2",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O ID do professor responsável é obrigatório")
                Long professorResponsavelId,
        @Schema(
                        description = "Código da disciplina",
                        example = "MED001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O código da disciplina é obrigatório")
                @Size(max = 30, message = "O código da disciplina não pode exceder 30 caracteres")
                String codigoDisciplina,
        @Schema(
                        description = "Período letivo acadêmico",
                        example = "2026.1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O período letivo é obrigatório")
                @Size(max = 20, message = "O período letivo não pode exceder 20 caracteres")
                String periodoLetivo,
        @Schema(
                        description = "Ano e semestre",
                        example = "2026/1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O ano/semestre é obrigatório")
                @Size(max = 10, message = "O ano/semestre não pode exceder 10 caracteres")
                String anoSemestre) {}
