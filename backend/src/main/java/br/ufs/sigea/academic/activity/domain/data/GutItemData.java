package br.ufs.sigea.academic.activity.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Item avaliado na Matriz GUT (Gravidade, Urgência e Tendência).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutItemData implements Serializable {
    private String problem;
    private Integer gravity;  // 1 a 5
    private Integer urgency;  // 1 a 5
    private Integer trend;    // 1 a 5

    public Integer getScore() {
        int g = (gravity != null && gravity >= 1) ? gravity : 1;
        int u = (urgency != null && urgency >= 1) ? urgency : 1;
        int t = (trend != null && trend >= 1) ? trend : 1;
        return g * u * t;
    }

    public void setScore(Integer score) {
        // Campo computado aceito na desserialização
    }
}
