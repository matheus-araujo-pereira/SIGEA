package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Ciclo de Melhoria Contínua PDCA (Plan, Do, Check, Act) / PDSA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdcaData implements Serializable {
    private String plan;       // Planejar: definição do problema e metas
    private String doPhase;    // Fazer / Executar: implementação da ação
    private String checkPhase; // Checar / Estudar: verificação de indicadores de resultado
    private String actPhase;   // Agir / Padronizar: consolidação do protocolo assistencial
}
