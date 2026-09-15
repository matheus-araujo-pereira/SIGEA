package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Ação estruturada na Matriz 5W2H para o plano de ação de prevenção.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiveWTwoHItemData implements Serializable {
    private String what;     // O quê
    private String why;      // Por quê
    private String where;    // Onde
    private String when;     // Quando
    private String who;      // Quem
    private String how;      // Como
    private String howMuch;  // Quanto custa
}
