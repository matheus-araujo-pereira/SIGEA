package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload para criação ou edição de unidades hospitalares no SIGEA-GTT.
 *
 * @param sigla Código mnemônico ou sigla da unidade (ex: UTI-A, CMED).
 * @param nome Nome institucional completo da enfermaria ou setor.
 */
@Schema(description = "Dados para cadastro ou atualização de unidade hospitalar.")
public record UnidadeHospitalarRequisicaoDTO(
        @Schema(
                        description = "Sigla da unidade",
                        example = "UTI-A",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "A sigla é obrigatória")
                @Size(max = 20, message = "A sigla não pode exceder 20 caracteres")
                String sigla,
        @Schema(
                        description = "Nome da unidade hospitalar",
                        example = "Unidade de Terapia Intensiva Adulto",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O nome é obrigatório")
                @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
                String nome) {}
