package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para cadastro ou atualização de gatilhos clínicos da metodologia IHI-GTT.
 *
 * @param moduloId Identificador do módulo pai.
 * @param codigo Código alfanumérico do gatilho (ex: C1, M2).
 * @param descricao Descrição clínica do gatilho.
 * @param limiarReferencia Parâmetros e valores de referência laboratoriais ou clínicos.
 */
@Schema(description = "Dados para cadastro ou atualização de gatilho clínico GTT.")
public record GatilhoGttRequisicaoDTO(
        @Schema(
                        description = "ID do módulo GTT associado",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "O ID do módulo é obrigatório")
                Long moduloId,
        @Schema(
                        description = "Código do gatilho clínico",
                        example = "C1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O código do gatilho é obrigatório")
                @Size(max = 10, message = "O código do gatilho não pode exceder 10 caracteres")
                String codigo,
        @Schema(
                        description = "Descrição clínica do gatilho",
                        example = "Parada cardiorrespiratória",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A descrição do gatilho é obrigatória")
                String descricao,
        @Schema(
                        description = "Limiar de referência laboratorial ou clínico",
                        example = "Evento súbito ou necessidade de manobras de RCP")
                @Size(max = 150, message = "O limiar de referência não pode exceder 150 caracteres")
                String limiarReferencia) {}
