package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pacote consolidado das Ferramentas da Qualidade preenchidas na resolução da atividade.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityToolsData implements Serializable {

    @Builder.Default
    private IshikawaData ishikawa = new IshikawaData();

    @Builder.Default
    private List<GutItemData> gutItems = new ArrayList<>();

    @Builder.Default
    private List<FiveWTwoHItemData> fiveWTwoHItems = new ArrayList<>();

    @Builder.Default
    private PdcaData pdca = new PdcaData();

    @Builder.Default
    private SwotData swot = new SwotData();

    @Builder.Default
    private List<String> brainstormingNotes = new ArrayList<>();
}
