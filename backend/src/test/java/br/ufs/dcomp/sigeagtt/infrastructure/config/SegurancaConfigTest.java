package br.ufs.dcomp.sigeagtt.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class SegurancaConfigTest {

    private SegurancaConfig config;

    @BeforeEach
    void setUp() {
        config = new SegurancaConfig();
    }

    private void setFrontendOrigin(String origin) {
        try {
            Field field = SegurancaConfig.class.getDeclaredField("frontendOrigin");
            field.setAccessible(true);
            field.set(config, origin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve instanciar PasswordEncoder, UserDetailsService e SecurityContextRepository")
    void deveInstanciarBeansBasicos() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("123", encoder.encode("123")));

        assertNotNull(config.userDetailsService());
        assertNotNull(config.securityContextRepository());
    }

    @Test
    @DisplayName(
            "Deve configurar CorsConfigurationSource cobrindo todas as combinacoes de frontendOrigin")
    void deveConfigurarCors() {
        // 1. Origem customizada
        setFrontendOrigin("https://app.custom.com");
        CorsConfigurationSource source1 = config.corsConfigurationSource();
        CorsConfiguration c1 = source1.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(c1);
        assertTrue(c1.getAllowedOrigins().contains("https://app.custom.com"));
        assertTrue(c1.getAllowedOrigins().contains("http://localhost:4200"));
        assertTrue(c1.getAllowedOrigins().contains("https://sigea-gtt-frontend.onrender.com"));
        assertTrue(c1.getAllowCredentials());

        // 2. Origem padrão localhost (já inclusa)
        setFrontendOrigin("http://localhost:4200");
        CorsConfigurationSource source2 = config.corsConfigurationSource();
        CorsConfiguration c2 = source2.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(c2);

        // 3. Origem onrender (já inclusa)
        setFrontendOrigin("https://sigea-gtt-frontend.onrender.com");
        CorsConfigurationSource source3 = config.corsConfigurationSource();
        CorsConfiguration c3 = source3.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(c3);

        // 4. Origem nula
        setFrontendOrigin(null);
        CorsConfigurationSource source4 = config.corsConfigurationSource();
        CorsConfiguration c4 = source4.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(c4);

        // 5. Origem em branco
        setFrontendOrigin("   ");
        CorsConfigurationSource source5 = config.corsConfigurationSource();
        CorsConfiguration c5 = source5.getCorsConfiguration(new MockHttpServletRequest());
        assertNotNull(c5);
    }
}
