package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Gatilho clínico identificado pelo aluno no prontuário simulado e sua respectiva classificação de dano.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifiedTriggerData implements Serializable {
    private UUID triggerId;
    private String triggerCode;
    private String triggerName;
    private String moduleCode;
    
    /**
     * Flag indicativa de se o gatilho detectou um Evento Adverso com Dano Real (Categorias E a I).
     */
    private Boolean isHarm;

    /**
     * Categoria de gravidade de dano conforme NCC MERP (ex: "E", "F", "G", "H", "I").
     */
    private String harmSeverityLetter;

    /**
     * Justificativa clínica da identificação do evento adverso e nexo causal no prontuário.
     */
    private String clinicalJustification;
}
