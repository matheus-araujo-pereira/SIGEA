package br.ufs.dcomp.sigeagtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.flyway.enabled=false"})
class SigeaGttBackendApplicationTests {

    @MockitoBean private DataSource dataSource;

    @Value("${local.server.port}")
    private int port;

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("Deve exportar a especificação OpenAPI 3.1 para docs/sigea-openapi.json")
    void exportarEspecificacaoOpenApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/v3/api-docs"))
                        .GET()
                        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String json = response.body();
        assertNotNull(json);

        Path docsDir = Path.of("../docs");
        if (!Files.exists(docsDir)) {
            Files.createDirectories(docsDir);
        }
        Files.writeString(docsDir.resolve("sigea-openapi.json"), json);
    }
}
