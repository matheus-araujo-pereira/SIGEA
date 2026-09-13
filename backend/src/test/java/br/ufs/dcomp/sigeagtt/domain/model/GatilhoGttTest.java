package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GatilhoGttTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        GatilhoGtt g = new GatilhoGtt();
        assertTrue(g.getAtivo());
        assertNull(g.getId());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults")
    void deveConstruirComConstrutorCompleto() {
        ModuloGtt modulo = new ModuloGtt();
        GatilhoGtt g1 = new GatilhoGtt(1L, "C1", modulo, "PCR", "Início das manobras", true);
        assertEquals(1L, g1.getId());
        assertEquals("C1", g1.getCodigo());
        assertEquals(modulo, g1.getModulo());
        assertEquals("PCR", g1.getDescricao());
        assertEquals("Início das manobras", g1.getLimiarReferencia());
        assertTrue(g1.getAtivo());

        GatilhoGtt g2 = new GatilhoGtt(2L, "M1", modulo, "Vitamina K", "INR > 5", null);
        assertTrue(g2.getAtivo());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando todos os dados obrigatorios estao preenchidos")
    void deveValidarComSucesso() {
        GatilhoGtt g = new GatilhoGtt();
        g.setCodigo("C1");
        g.setModulo(new ModuloGtt());
        g.setDescricao("Parada cardiorrespiratória");

        assertDoesNotThrow(g::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando codigo for nulo ou em branco")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        GatilhoGtt g = new GatilhoGtt();
        g.setModulo(new ModuloGtt());
        g.setDescricao("Descricao");

        g.setCodigo(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, g::validarInvariantes);
        assertEquals("O código do gatilho clínico é obrigatório.", ex1.getMessage());

        g.setCodigo("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, g::validarInvariantes);
        assertEquals("O código do gatilho clínico é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando modulo associado for nulo")
    void deveLancarExcecaoQuandoModuloNulo() {
        GatilhoGtt g = new GatilhoGtt();
        g.setCodigo("C1");
        g.setModulo(null);
        g.setDescricao("Descricao");

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, g::validarInvariantes);
        assertEquals("O módulo GTT associado é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando descricao for nula ou em branco")
    void deveLancarExcecaoQuandoDescricaoInvalida() {
        GatilhoGtt g = new GatilhoGtt();
        g.setCodigo("C1");
        g.setModulo(new ModuloGtt());

        g.setDescricao(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, g::validarInvariantes);
        assertEquals("A descrição clínica do gatilho é obrigatória.", ex1.getMessage());

        g.setDescricao("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, g::validarInvariantes);
        assertEquals("A descrição clínica do gatilho é obrigatória.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        GatilhoGtt g1 = new GatilhoGtt();
        g1.setId(10L);
        g1.setCodigo("S1");
        ModuloGtt mod = new ModuloGtt();
        g1.setModulo(mod);
        g1.setDescricao("Retorno ao CCIR");
        g1.setLimiarReferencia("< 48h");
        g1.setAtivo(false);

        assertEquals(10L, g1.getId());
        assertEquals("S1", g1.getCodigo());
        assertEquals(mod, g1.getModulo());
        assertEquals("Retorno ao CCIR", g1.getDescricao());
        assertEquals("< 48h", g1.getLimiarReferencia());
        assertFalse(g1.getAtivo());

        GatilhoGtt g2 = new GatilhoGtt();
        g2.setId(10L);
        GatilhoGtt g3 = new GatilhoGtt();
        g3.setId(20L);

        assertEquals(g1, g1);
        assertEquals(g1, g2);
        assertNotEquals(g1, g3);
        assertNotEquals(g1, null);
        assertNotEquals(g1, "outro");
        assertEquals(g1.hashCode(), g2.hashCode());

        String str = g1.toString();
        assertTrue(str.contains("S1"));
        assertTrue(str.contains("Retorno ao CCIR"));
    }
}
