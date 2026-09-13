package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.ports.input.IndicadoresEpidemiologicosUseCase;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class IndicadoresControladorTest {

    @Mock private IndicadoresEpidemiologicosUseCase servico;

    @InjectMocks private IndicadoresControlador controlador;

    @Test
    @DisplayName("Deve calcular indicadores epidemiologicos")
    void deveCalcularIndicadores() {
        when(servico.calcularIndicadoresIndividuais(
                        1L, "2026.1", null, null, null, null, null, null, null))
                .thenReturn(Map.of("totalEventosAdversos", 5));

        ResponseEntity<Map<String, Object>> resp =
                controlador.calcular(1L, "2026.1", null, null, null, null, null, null, null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(5, resp.getBody().get("totalEventosAdversos"));
    }

    @Test
    @DisplayName("Deve obter quadro resumo")
    void deveObterQuadroResumo() {
        when(servico.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, null, true, "busca", 0, 15))
                .thenReturn(Map.of("total", 10));

        ResponseEntity<Map<String, Object>> resp =
                controlador.obterQuadroResumo(
                        null, null, null, null, null, null, null, null, null, true, "busca", 0, 15);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10, resp.getBody().get("total"));
    }

    @Test
    @DisplayName("Deve obter desempenho de gatilhos")
    void deveObterDesempenhoGatilhos() {
        LocalDate ini = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);
        when(servico.obterDesempenhoGatilhos(1L, "2026.1", 100L, 2L, ini, fim))
                .thenReturn(Map.of("totalGatilhosIdentificados", 8));

        ResponseEntity<Map<String, Object>> resp =
                controlador.obterDesempenhoGatilhos(1L, "2026.1", 100L, 2L, ini, fim);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(8, resp.getBody().get("totalGatilhosIdentificados"));
    }
}
