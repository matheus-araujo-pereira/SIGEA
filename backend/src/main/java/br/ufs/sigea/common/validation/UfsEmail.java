package br.ufs.sigea.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação estrita de e-mails institucionais da UFS (@academico.ufs.br).
 */
@Documented
@Constraint(validatedBy = UfsEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UfsEmail {

    /**
     * Mensagem padrão retornada quando a validação falha.
     *
     * @return Mensagem de erro
     */
    String message() default "O e-mail deve pertencer obrigatoriamente ao domínio institucional @academico.ufs.br";

    /**
     * Grupos de validação aplicáveis.
     *
     * @return Classes dos grupos
     */
    Class<?>[] groups() default {};

    /**
     * Carga útil (payload) associada à validação.
     *
     * @return Classes de payload
     */
    Class<? extends Payload>[] payload() default {};
}
