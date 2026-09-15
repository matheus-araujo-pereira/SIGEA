package br.ufs.sigea.auth.service;

import br.ufs.sigea.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Serviço responsável pela geração, validação e extração de informações de tokens JWT.
 */
@Slf4j
@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Construtor injetando as propriedades do JWT.
     *
     * @param secret       Chave secreta configurada no application.yml
     * @param expirationMs Tempo de validade do token em milissegundos
     */
    public JwtTokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Gera um token JWT assinado para o usuário informado.
     *
     * @param user Entidade do usuário autenticado
     * @return String contendo o token JWT
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("fullName", user.getFullName())
                .claim("role", user.getRole().name())
                .claim("mustChangePassword", user.getMustChangePassword())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Valida se a assinatura e o tempo de expiração do token são válidos.
     *
     * @param token Token JWT
     * @return true se o token for válido e não expirado
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT inválido ou expirado: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrai os claims (corpo) do token JWT.
     *
     * @param token Token JWT
     * @return Claims do token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrai o e-mail (subject) do token JWT.
     *
     * @param token Token JWT
     * @return Endereço de e-mail
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrai o ID do usuário do token JWT.
     *
     * @param token Token JWT
     * @return UUID do usuário
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractAllClaims(token).get("userId", String.class);
        return UUID.fromString(userIdStr);
    }
}
