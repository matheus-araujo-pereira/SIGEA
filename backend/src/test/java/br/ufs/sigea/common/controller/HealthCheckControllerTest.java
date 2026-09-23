package br.ufs.sigea.common.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes Unitários do HealthCheckController")
class HealthCheckControllerTest {

    private final HealthCheckController healthCheckController = new HealthCheckController();

    @Test
    @DisplayName("Deve responder 200 OK com status UP no endpoint de ping")
    void shouldReturnOkWithStatusUp() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response = healthCheckController.ping();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Sistema operacional.", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertNotNull(data);
        assertEquals("UP", data.get("status"));
        assertEquals("SIGEA", data.get("system"));
        assertEquals("production-ready", data.get("environment"));
    }
}
