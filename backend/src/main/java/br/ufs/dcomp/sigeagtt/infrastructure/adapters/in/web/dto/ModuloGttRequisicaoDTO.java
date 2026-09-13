package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload para criação ou edição de módulos da metodologia IHI-GTT.
 *
 * @param codigo Código identificador único do módulo (ex: CUIDADOS, CIRURGICO).
 * @param nome Nome de exibição do módulo.
 * @param descricao Descrição do escopo clínico de atuação.
 */
@Schema(description = "Dados para cadastro ou atualização de módulo GTT.")
public record ModuloGttRequisicaoDTO(
        @Schema(
                        description = "Código identificador do módulo",
                        example = "CUIDADOS",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O código do módulo é obrigatório")
                @Size(max = 30, message = "O código do módulo não pode exceder 30 caracteres")
                String codigo,
        @Schema(
                        description = "Nome do módulo",
                        example = "Cuidados Gerais",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O nome do módulo é obrigatório")
                @Size(max = 100, message = "O nome do módulo não pode exceder 100 caracteres")
                String nome,
        @Schema(
                        description = "Descrição conceitual do módulo",
                        example = "Gatilhos relacionados a cuidados hospitalares gerais")
                String descricao) {}
