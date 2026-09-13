package br.ufs.dcomp.sigeagtt;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SigeaGttBackendApplicationTest {

    @Test
    @DisplayName("Deve instanciar a classe da aplicacao")
    void deveInstanciarAplicacao() {
        SigeaGttBackendApplication app = new SigeaGttBackendApplication();
        assertNotNull(app);
    }
}
