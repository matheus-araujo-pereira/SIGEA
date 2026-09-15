package br.ufs.sigea.academic.clazz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Payload para criação de uma Turma Acadêmica.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClassCreateDTO {

    @NotBlank(message = "O nome da disciplina/matéria é obrigatório")
    @Size(max = 120, message = "O nome da matéria não pode exceder 120 caracteres")
    private String subjectName;

    @NotBlank(message = "O código da turma é obrigatório (ex: T01, T06)")
    @Size(max = 20, message = "O código da turma não pode exceder 20 caracteres")
    private String classCode;

    @NotBlank(message = "O período acadêmico é obrigatório (ex: 2026.1, 2026.2)")
    @Size(max = 10, message = "O período acadêmico não pode exceder 10 caracteres")
    private String academicPeriod;

    @NotNull(message = "O professor titular responsável pela turma é obrigatório")
    private UUID professorId;

    @Builder.Default
    private Set<UUID> studentIds = new HashSet<>();
}
