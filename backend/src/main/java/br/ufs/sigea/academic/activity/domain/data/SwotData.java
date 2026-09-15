package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Matriz SWOT (FOFA - Forças, Oportunidades, Fraquezas e Ameaças) aplicada à segurança do paciente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwotData implements Serializable {
    @Builder.Default
    private List<String> strengths = new ArrayList<>();     // Forças

    @Builder.Default
    private List<String> opportunities = new ArrayList<>(); // Oportunidades

    @Builder.Default
    private List<String> weaknesses = new ArrayList<>();    // Fraquezas

    @Builder.Default
    private List<String> threats = new ArrayList<>();       // Ameaças
}
