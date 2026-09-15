package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Resultado de exame laboratorial ou laudo de imagem no prontuário simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabExamData implements Serializable {
    private String examName;
    private String result;
    private String referenceValue;
    private String date;
}
