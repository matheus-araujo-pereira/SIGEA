package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort.DadosToken;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class TokenServiceAdapterTest {

    private TokenServiceAdapter tokenService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        tokenService =
                new TokenServiceAdapter("chave-secreta-de-testes-1234567890-abcdef", objectMapper);
    }

    @Test
    @DisplayName("Deve gerar e validar token JWT com sucesso")
    void deveGerarEValidarToken() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("usuario@ufs.br");
        usuario.setPerfil(PerfilUsuario.PROFESSOR);

        String token = tokenService.gerarToken(usuario);
        assertNotNull(token);
        assertTrue(token.contains("."));

        DadosToken dados = tokenService.validarToken(token);
        assertNotNull(dados);
        assertEquals(10L, dados.id());
        assertEquals("usuario@ufs.br", dados.email());
        assertEquals("PROFESSOR", dados.perfil());
    }

    @Test
    @DisplayName("Deve gerar token para usuario com perfil nulo")
    void deveGerarTokenPerfilNulo() {
        Usuario usuario = new Usuario();
        usuario.setId(15L);
        usuario.setEmail("semperfil@ufs.br");
        usuario.setPerfil(null);

        String token = tokenService.gerarToken(usuario);
        assertNotNull(token);

        // Perfil fica vazio (""), mas validarToken requer email != null e perfil !=
        // null.
        // Dado que perfil é "", não é nulo.
        DadosToken dados = tokenService.validarToken(token);
        assertNotNull(dados);
        assertEquals("", dados.perfil());
    }

    @Test
    @DisplayName("Deve retornar nulo para tokens invalidos ou corrompidos")
    void deveRetornarNuloParaTokensInvalidos() {
        assertNull(tokenService.validarToken(null));
        assertNull(tokenService.validarToken(""));
        assertNull(tokenService.validarToken("   "));
        assertNull(tokenService.validarToken("invalido"));
        assertNull(tokenService.validarToken("header.payload")); // menos de 3 partes
        assertNull(tokenService.validarToken("header.payload.signature.extra")); // mais de 3 partes

        // Token com assinatura adulterada
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@ufs.br");
        usuario.setPerfil(PerfilUsuario.ALUNO);
        String token = tokenService.gerarToken(usuario);
        String[] partes = token.split("\\.");
        String tokenAdulterado = partes[0] + "." + partes[1] + ".assinaturaFalsa";
        assertNull(tokenService.validarToken(tokenAdulterado));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException se ocorrer erro na geração do token")
    void deveLancarExcecaoAoGerarTokenComErro() {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        when(mockMapper.createObjectNode()).thenThrow(new RuntimeException("Simulated error"));
        TokenServiceAdapter failingService = new TokenServiceAdapter("chave", mockMapper);

        Usuario u = new Usuario();
        u.setEmail("teste@ufs.br");
        assertThrows(RuntimeException.class, () -> failingService.gerarToken(u));
    }

    @Test
    @DisplayName("Deve validar token sem ID e retornar DadosToken com id nulo")
    void deveValidarTokenSemId() throws Exception {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payloadJson = "{\"sub\":\"user@ufs.br\",\"perfil\":\"ALUNO\",\"exp\":9999999999}";
        String payload =
                Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String conteudo = header + "." + payload;
        String assinatura = assinar(conteudo, "chave-secreta-de-testes-1234567890-abcdef");
        String token = conteudo + "." + assinatura;

        DadosToken dados = tokenService.validarToken(token);
        assertNotNull(dados);
        assertNull(dados.id());
        assertEquals("user@ufs.br", dados.email());
        assertEquals("ALUNO", dados.perfil());
    }

    @Test
    @DisplayName("Deve retornar nulo ao validar token sem claim exp")
    void deveRetornarNuloSemExp() throws Exception {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payloadJson = "{\"sub\":\"user@ufs.br\",\"perfil\":\"ALUNO\"}";
        String payload =
                Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String conteudo = header + "." + payload;
        String assinatura = assinar(conteudo, "chave-secreta-de-testes-1234567890-abcdef");
        String token = conteudo + "." + assinatura;

        assertNull(tokenService.validarToken(token));
    }

    @Test
    @DisplayName("Deve retornar nulo ao validar token expirado")
    void deveRetornarNuloTokenExpirado() throws Exception {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payloadJson = "{\"sub\":\"user@ufs.br\",\"perfil\":\"ALUNO\",\"exp\":1000}";
        String payload =
                Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String conteudo = header + "." + payload;
        String assinatura = assinar(conteudo, "chave-secreta-de-testes-1234567890-abcdef");
        String token = conteudo + "." + assinatura;

        assertNull(tokenService.validarToken(token));
    }

    @Test
    @DisplayName("Deve retornar nulo quando sub ou perfil estiverem ausentes")
    void deveRetornarNuloSemSubOuPerfil() throws Exception {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

        // Sem sub
        String p1 =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"perfil\":\"ALUNO\",\"exp\":9999999999}".getBytes());
        String c1 = header + "." + p1;
        String t1 = c1 + "." + assinar(c1, "chave-secreta-de-testes-1234567890-abcdef");
        assertNull(tokenService.validarToken(t1));

        // Sem perfil
        String p2 =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"sub\":\"user@ufs.br\",\"exp\":9999999999}".getBytes());
        String c2 = header + "." + p2;
        String t2 = c2 + "." + assinar(c2, "chave-secreta-de-testes-1234567890-abcdef");
        assertNull(tokenService.validarToken(t2));
    }

    @Test
    @DisplayName("Deve retornar nulo quando ocorrer exceção ao decodificar token")
    void deveRetornarNuloExcecaoAoDecodificar() throws Exception {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String invalidPayload = "@@@invalidBase64Payload@@@";
        String conteudo = header + "." + invalidPayload;
        String assinatura = assinar(conteudo, "chave-secreta-de-testes-1234567890-abcdef");
        String token = conteudo + "." + assinatura;
        assertNull(tokenService.validarToken(token));
    }

    private String assinar(String conteudo, String segredo) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKey =
                new javax.crypto.spec.SecretKeySpec(
                        segredo.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hmacBytes = mac.doFinal(conteudo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }
}
