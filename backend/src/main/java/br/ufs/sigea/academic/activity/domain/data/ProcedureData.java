package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Procedimento cirúrgico ou invasivo no prontuário simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureData implements Serializable {
    private String procedureName;
    private String description;
    private String date;
}
