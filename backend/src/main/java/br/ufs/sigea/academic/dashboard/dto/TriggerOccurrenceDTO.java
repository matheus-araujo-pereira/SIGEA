package br.ufs.sigea.academic.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Frequência de identificação de um gatilho pelos alunos da turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerOccurrenceDTO {
    private String triggerCode;
    private String triggerName;
    private Long count;
}
