package br.ufs.sigea.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador para garantir que o endereço eletrônico possui domínio estrito @academico.ufs.br.
 */
public class UfsEmailValidator implements ConstraintValidator<UfsEmail, String> {

    private static final String UFS_EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@academico\\.ufs\\.br$";
    private static final Pattern PATTERN = Pattern.compile(UFS_EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public void initialize(UfsEmail constraintAnnotation) {
        // Inicialização do validador se necessária
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return PATTERN.matcher(email.trim()).matches();
    }
}
