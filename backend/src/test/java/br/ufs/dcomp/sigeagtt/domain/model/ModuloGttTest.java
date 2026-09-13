package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModuloGttTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        ModuloGtt m = new ModuloGtt();
        assertTrue(m.getAtivo());
        assertNotNull(m.getCriadoEm());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults")
    void deveConstruirComConstrutorCompleto() {
        LocalDateTime agora = LocalDateTime.now();
        ModuloGtt m1 =
                new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "Módulo geral", true, agora);
        assertEquals(1L, m1.getId());
        assertEquals("CUIDADOS", m1.getCodigo());
        assertEquals("Cuidados Gerais", m1.getNome());
        assertEquals("Módulo geral", m1.getDescricao());
        assertTrue(m1.getAtivo());
        assertEquals(agora, m1.getCriadoEm());

        ModuloGtt m2 = new ModuloGtt(2L, "CIR", "Cirúrgico", "Cirurgias", null, null);
        assertTrue(m2.getAtivo());
        assertNotNull(m2.getCriadoEm());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando codigo e nome estao preenchidos")
    void deveValidarComSucesso() {
        ModuloGtt m = new ModuloGtt();
        m.setCodigo("MED");
        m.setNome("Medicamentos");

        assertDoesNotThrow(m::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando codigo for nulo ou em branco")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        ModuloGtt m1 = new ModuloGtt();
        m1.setCodigo(null);
        m1.setNome("Medicamentos");
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, m1::validarInvariantes);
        assertEquals("O código do módulo GTT é obrigatório.", ex1.getMessage());

        ModuloGtt m2 = new ModuloGtt();
        m2.setCodigo("   ");
        m2.setNome("Medicamentos");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, m2::validarInvariantes);
        assertEquals("O código do módulo GTT é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando nome for nulo ou em branco")
    void deveLancarExcecaoQuandoNomeInvalido() {
        ModuloGtt m1 = new ModuloGtt();
        m1.setCodigo("MED");
        m1.setNome(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, m1::validarInvariantes);
        assertEquals("O nome do módulo GTT é obrigatório.", ex1.getMessage());

        ModuloGtt m2 = new ModuloGtt();
        m2.setCodigo("MED");
        m2.setNome("  ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, m2::validarInvariantes);
        assertEquals("O nome do módulo GTT é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        ModuloGtt m1 = new ModuloGtt();
        m1.setId(10L);
        m1.setCodigo("UTI");
        m1.setNome("Terapia Intensiva");
        m1.setDescricao("Desc");
        m1.setAtivo(false);
        LocalDateTime data = LocalDateTime.now();
        m1.setCriadoEm(data);

        assertEquals(10L, m1.getId());
        assertEquals("UTI", m1.getCodigo());
        assertEquals("Terapia Intensiva", m1.getNome());
        assertEquals("Desc", m1.getDescricao());
        assertFalse(m1.getAtivo());
        assertEquals(data, m1.getCriadoEm());

        ModuloGtt m2 = new ModuloGtt();
        m2.setId(10L);
        ModuloGtt m3 = new ModuloGtt();
        m3.setId(20L);

        assertEquals(m1, m1);
        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertNotEquals(m1, null);
        assertNotEquals(m1, "outro");
        assertEquals(m1.hashCode(), m2.hashCode());

        String str = m1.toString();
        assertTrue(str.contains("UTI"));
        assertTrue(str.contains("Terapia Intensiva"));
    }
}
