package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Prescrição medicamentosa e checagem de administração no prontuário simulado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionData implements Serializable {
    private String medication;
    private String dosage;
    private String route;
    private String frequency;
    private String administrationCheck; // Ex: "Administrado às 14:00 por Enf. Cláudia"
}
