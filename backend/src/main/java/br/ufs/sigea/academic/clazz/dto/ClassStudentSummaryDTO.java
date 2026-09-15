package br.ufs.sigea.academic.clazz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Resumo dos dados de um estudante vinculado à turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentSummaryDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String registrationNumber;
}
