package br.ufs.sigea.academic.clazz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload para alteração de encerramento da turma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClassCloseDTO {
    @NotNull(message = "O status de encerramento (isClosed) é obrigatório")
    private Boolean isClosed;
}
