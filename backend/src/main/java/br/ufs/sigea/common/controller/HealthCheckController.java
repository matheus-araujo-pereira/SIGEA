package br.ufs.sigea.common.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST público de verificação de disponibilidade (heartbeat) e aquecimento de instância do SIGEA.
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Disponibilidade e Heartbeat", description = "Endpoints públicos de monitoramento e warmup da API")
public class HealthCheckController {

    /**
     * Endpoint ultraleve de verificação de pulso (ping) e aquecimento do backend em cold starts.
     *
     * @return Status operacional do sistema em formato padronizado ApiResponse
     */
    @GetMapping("/ping")
    @Operation(summary = "Verificar disponibilidade do backend", description = "Retorna status operacional imediato sem consultar banco de dados, permitindo keep-alive e aquecimento.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ping() {
        Map<String, Object> status = Map.of(
                "status", "UP",
                "system", "SIGEA",
                "environment", "production-ready"
        );
        return ResponseEntity.ok(ApiResponse.ok(status, "Sistema operacional."));
    }
}
