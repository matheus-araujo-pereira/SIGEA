package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Nota de evolução médica ou de enfermagem no prontuário simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionNoteData implements Serializable {
    private String dateTime;
    private String professionalRole; // Ex: "Médico Plantonista", "Enfermeiro Assistencial"
    private String note;
}
