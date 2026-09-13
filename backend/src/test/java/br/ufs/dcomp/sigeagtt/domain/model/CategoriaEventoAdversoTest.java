package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoriaEventoAdversoTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        CategoriaEventoAdverso cat = new CategoriaEventoAdverso();
        assertTrue(cat.getAtiva());
        assertNull(cat.getId());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults")
    void deveConstruirComConstrutorCompleto() {
        CategoriaEventoAdverso c1 =
                new CategoriaEventoAdverso(1L, "IRAS", "Infecção hospitalar", true);
        assertEquals(1L, c1.getId());
        assertEquals("IRAS", c1.getNome());
        assertEquals("Infecção hospitalar", c1.getDefinicaoOperacional());
        assertTrue(c1.getAtiva());

        CategoriaEventoAdverso c2 = new CategoriaEventoAdverso(2L, "Queda", "Queda de leito", null);
        assertTrue(c2.getAtiva());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando nome e definicao estao presentes")
    void deveValidarComSucesso() {
        CategoriaEventoAdverso cat =
                new CategoriaEventoAdverso(1L, "LPP", "Lesão por Pressão", true);
        assertDoesNotThrow(cat::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando nome for nulo ou em branco")
    void deveLancarExcecaoQuandoNomeInvalido() {
        CategoriaEventoAdverso c1 = new CategoriaEventoAdverso(1L, null, "Definicao", true);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, c1::validarInvariantes);
        assertEquals("O nome da categoria de evento adverso é obrigatório.", ex1.getMessage());

        CategoriaEventoAdverso c2 = new CategoriaEventoAdverso(1L, "   ", "Definicao", true);
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, c2::validarInvariantes);
        assertEquals("O nome da categoria de evento adverso é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando definicao operacional for nula ou em branco")
    void deveLancarExcecaoQuandoDefinicaoInvalida() {
        CategoriaEventoAdverso c1 = new CategoriaEventoAdverso(1L, "Nome", null, true);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, c1::validarInvariantes);
        assertEquals("A definição operacional da categoria é obrigatória.", ex1.getMessage());

        CategoriaEventoAdverso c2 = new CategoriaEventoAdverso(1L, "Nome", "   ", true);
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, c2::validarInvariantes);
        assertEquals("A definição operacional da categoria é obrigatória.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        CategoriaEventoAdverso c1 = new CategoriaEventoAdverso();
        c1.setId(5L);
        c1.setNome("Erro de Medicação");
        c1.setDefinicaoOperacional("Dose errada");
        c1.setAtiva(false);

        assertEquals(5L, c1.getId());
        assertEquals("Erro de Medicação", c1.getNome());
        assertEquals("Dose errada", c1.getDefinicaoOperacional());
        assertFalse(c1.getAtiva());

        CategoriaEventoAdverso c2 = new CategoriaEventoAdverso();
        c2.setId(5L);
        CategoriaEventoAdverso c3 = new CategoriaEventoAdverso();
        c3.setId(10L);

        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "outro");
        assertEquals(c1.hashCode(), c2.hashCode());

        String str = c1.toString();
        assertTrue(str.contains("Erro de Medicação"));
    }
}
