package br.ufs.dcomp.sigeagtt.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    @DisplayName("Deve gerar definição do OpenAPI 3.1 com metadados e esquema Bearer JWT")
    void deveGerarDefinicaoOpenApi() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.customOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("SIGEA-GTT API", api.getInfo().getTitle());
        assertEquals("1.0", api.getInfo().getVersion());
        assertTrue(api.getInfo().getDescription().contains("SIGEA-GTT"));
        assertEquals("sigeagtt@academico.ufs.br", api.getInfo().getContact().getEmail());
        assertTrue(api.getInfo().getLicense().getName().contains("UFS"));
        assertFalse(api.getSecurity().isEmpty());
        assertTrue(api.getSecurity().get(0).containsKey(OpenApiConfig.ESQUEMA_SEGURANCA_JWT));
        assertNotNull(
                api.getComponents().getSecuritySchemes().get(OpenApiConfig.ESQUEMA_SEGURANCA_JWT));
        assertEquals(
                "bearer",
                api.getComponents()
                        .getSecuritySchemes()
                        .get(OpenApiConfig.ESQUEMA_SEGURANCA_JWT)
                        .getScheme());
    }
}
