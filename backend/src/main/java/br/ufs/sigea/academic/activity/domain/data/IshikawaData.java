package br.ufs.sigea.academic.activity.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Diagrama de Causa e Efeito (Ishikawa / Espinha de Peixe) estruturado nos 6M.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IshikawaData implements Serializable {

    private String centralProblem; // Efeito / Problema principal (cabeça do peixe)

    @Builder.Default
    private List<String> methodCauses = new ArrayList<>(); // Método

    @Builder.Default
    private List<String> manpowerCauses = new ArrayList<>(); // Mão de Obra

    @Builder.Default
    private List<String> materialCauses = new ArrayList<>(); // Material

    @Builder.Default
    private List<String> machineCauses = new ArrayList<>(); // Máquina / Equipamentos

    @Builder.Default
    private List<String> environmentCauses = new ArrayList<>(); // Meio Ambiente

    @Builder.Default
    private List<String> measurementCauses = new ArrayList<>(); // Medida
}
