package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura de dados do prontuário simulado do caso clínico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalCaseData implements Serializable {

    private String patientName;
    private Integer age;
    private String gender;
    private String bed;
    private String admissionDate;
    
    /**
     * Quantidade de dias de internação (utilizado para o cálculo de EAs por 1.000 pacientes-dia).
     */
    @Builder.Default
    private Integer patientDays = 1;

    private String admissionNotes;

    @Builder.Default
    private List<EvolutionNoteData> evolutionNotes = new ArrayList<>();

    @Builder.Default
    private List<PrescriptionData> prescriptions = new ArrayList<>();

    @Builder.Default
    private List<LabExamData> labExams = new ArrayList<>();

    @Builder.Default
    private List<ProcedureData> procedures = new ArrayList<>();
}
