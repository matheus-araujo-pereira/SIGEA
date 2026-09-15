package br.ufs.sigea.auth.service;

import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private User testUser;
    private final String secret = "1234567890123456789012345678901234567890123456789012345678901234"; // 64 chars
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(secret, expirationMs);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Matheus Araujo")
                .email("matheus.araujo@academico.ufs.br")
                .role(UserRole.STUDENT)
                .registrationNumber("20260001")
                .isActive(true)
                .mustChangePassword(false)
                .build();
    }

    @Test
    @DisplayName("Deve gerar token JWT válido com claims correspondentes")
    void shouldGenerateValidToken() {
        String token = jwtTokenService.generateToken(testUser);

        assertNotNull(token);
        assertTrue(jwtTokenService.validateToken(token));
        assertEquals(testUser.getEmail(), jwtTokenService.extractEmail(token));
        assertEquals(testUser.getId(), jwtTokenService.extractUserId(token));

        Claims claims = jwtTokenService.extractAllClaims(token);
        assertEquals("Matheus Araujo", claims.get("fullName"));
        assertEquals("STUDENT", claims.get("role"));
        assertEquals(false, claims.get("mustChangePassword"));
    }

    @Test
    @DisplayName("Deve rejeitar token malformado ou adulterado")
    void shouldRejectMalformedToken() {
        assertFalse(jwtTokenService.validateToken("token.invalido.adulterado"));
        assertFalse(jwtTokenService.validateToken(""));
        assertFalse(jwtTokenService.validateToken(null));
    }

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void shouldRejectExpiredToken() {
        // Service com expiração negativa (já expirado)
        JwtTokenService expiredService = new JwtTokenService(secret, -1000);
        String expiredToken = expiredService.generateToken(testUser);

        assertFalse(jwtTokenService.validateToken(expiredToken));
    }
}
