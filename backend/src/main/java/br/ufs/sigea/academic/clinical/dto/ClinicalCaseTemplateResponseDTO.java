package br.ufs.sigea.academic.clinical.dto;

import br.ufs.sigea.academic.activity.domain.data.ClinicalCaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de resposta contendo os dados completos de um Modelo de Caso Clínico Simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de um Modelo de Caso Clínico Simulado")
public class ClinicalCaseTemplateResponseDTO {

    @Schema(description = "Identificador único do modelo", example = "c1000000-0000-0000-0000-000000000001")
    private UUID id;

    @Schema(description = "Título do caso clínico simulado", example = "Caso Clínico 01: Insuficiência Renal Aguda Nefrotóxica por Vancomicina")
    private String title;

    @Schema(description = "Descrição ou resumo do caso clínico", example = "Investigação de evento adverso associado ao uso de antimicrobiano nefrotóxico...")
    private String description;

    @Schema(description = "Código do módulo GTT relacionado", example = "M")
    private String moduleCode;

    @Schema(description = "Código do gatilho clínico primário esperado", example = "M5")
    private String primaryTriggerCode;

    @Schema(description = "Gravidade NCC MERP esperada no consenso", example = "F")
    private String expectedSeverity;

    @Schema(description = "Prontuário simulado completo estruturado")
    private ClinicalCaseData clinicalCaseData;

    @Schema(description = "Indica se é um modelo oficial canônico do sistema", example = "true")
    private boolean isSystemTemplate;

    @Schema(description = "Nome do criador do modelo (se customizado)", example = "Profª. Drª. Ana Waleska")
    private String createdByName;

    @Schema(description = "Data e hora de criação")
    private Instant createdAt;

    @Schema(description = "Data e hora da última atualização")
    private Instant updatedAt;
}
