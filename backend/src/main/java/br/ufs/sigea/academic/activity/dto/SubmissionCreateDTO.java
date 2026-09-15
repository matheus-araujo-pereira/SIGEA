package br.ufs.sigea.academic.activity.dto;

import br.ufs.sigea.academic.activity.domain.data.IdentifiedTriggerData;
import br.ufs.sigea.academic.activity.domain.data.QualityToolsData;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload para envio da resolução da atividade pelo estudante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionCreateDTO {

    @Builder.Default
    @NotNull(message = "A lista de gatilhos identificados não pode ser nula")
    private List<IdentifiedTriggerData> identifiedTriggers = new ArrayList<>();

    @NotNull(message = "Os dados das ferramentas da qualidade são obrigatórios")
    private QualityToolsData qualityToolsData;
}
