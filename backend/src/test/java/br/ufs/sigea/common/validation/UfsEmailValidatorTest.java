package br.ufs.sigea.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UfsEmailValidatorTest {

    private UfsEmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UfsEmailValidator();
        validator.initialize(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "matheus.araujo@academico.ufs.br",
            "ana.waleska@academico.ufs.br",
            "gilton@academico.ufs.br",
            "aluno123@academico.ufs.br",
            "USER.NAME+TAG@ACADEMICO.UFS.BR"
    })
    @DisplayName("Deve validar e-mails com domínio @academico.ufs.br com sucesso")
    void shouldValidateValidUfsEmails(String email) {
        assertTrue(validator.isValid(email, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "usuario@gmail.com",
            "usuario@ufs.br",
            "usuario@dcomp.ufs.br",
            "usuario@academico.ufs.br.com",
            "@academico.ufs.br",
            "usuario@",
            "usuario@academico@ufs.br"
    })
    @DisplayName("Deve rejeitar e-mails com domínios diferentes de @academico.ufs.br")
    void shouldRejectInvalidDomainEmails(String email) {
        assertFalse(validator.isValid(email, null));
    }

    @Test
    @DisplayName("Deve rejeitar e-mail nulo")
    void shouldRejectNullEmail() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    @DisplayName("Deve rejeitar e-mail em branco")
    void shouldRejectEmptyEmail() {
        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("   ", null));
    }
}
