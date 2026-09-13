package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnidadeHospitalarTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        UnidadeHospitalar unidade = new UnidadeHospitalar();
        assertTrue(unidade.getAtiva());
        assertNull(unidade.getId());
        assertNull(unidade.getNome());
        assertNull(unidade.getSigla());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults quando nulo")
    void deveConstruirComConstrutorCompleto() {
        UnidadeHospitalar u1 = new UnidadeHospitalar(1L, "UTI Adulto", "UTI-A", true);
        assertEquals(1L, u1.getId());
        assertEquals("UTI Adulto", u1.getNome());
        assertEquals("UTI-A", u1.getSigla());
        assertTrue(u1.getAtiva());

        UnidadeHospitalar u2 = new UnidadeHospitalar(2L, "Clínica Médica", "CMED", null);
        assertTrue(u2.getAtiva());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando atributos obrigatorios estao presentes")
    void deveValidarComSucesso() {
        UnidadeHospitalar unidade = new UnidadeHospitalar(1L, "Pediatria", "PED", true);
        assertDoesNotThrow(unidade::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando nome for nulo ou em branco")
    void deveLancarExcecaoQuandoNomeInvalido() {
        UnidadeHospitalar u1 = new UnidadeHospitalar(1L, null, "PED", true);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, u1::validarInvariantes);
        assertEquals("O nome da unidade hospitalar é obrigatório.", ex1.getMessage());

        UnidadeHospitalar u2 = new UnidadeHospitalar(1L, "   ", "PED", true);
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, u2::validarInvariantes);
        assertEquals("O nome da unidade hospitalar é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando sigla for nula ou em branco")
    void deveLancarExcecaoQuandoSiglaInvalida() {
        UnidadeHospitalar u1 = new UnidadeHospitalar(1L, "Pediatria", null, true);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, u1::validarInvariantes);
        assertEquals("A sigla da unidade hospitalar é obrigatória.", ex1.getMessage());

        UnidadeHospitalar u2 = new UnidadeHospitalar(1L, "Pediatria", "  ", true);
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, u2::validarInvariantes);
        assertEquals("A sigla da unidade hospitalar é obrigatória.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarGettersSettersEqualsHashCodeToString() {
        UnidadeHospitalar u1 = new UnidadeHospitalar();
        u1.setId(10L);
        u1.setNome("Centro Cirúrgico");
        u1.setSigla("CCIR");
        u1.setAtiva(false);

        assertEquals(10L, u1.getId());
        assertEquals("Centro Cirúrgico", u1.getNome());
        assertEquals("CCIR", u1.getSigla());
        assertFalse(u1.getAtiva());

        UnidadeHospitalar u2 = new UnidadeHospitalar();
        u2.setId(10L);
        UnidadeHospitalar u3 = new UnidadeHospitalar();
        u3.setId(20L);

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u1, null);
        assertNotEquals(u1, "outro");
        assertEquals(u1.hashCode(), u2.hashCode());

        String str = u1.toString();
        assertTrue(str.contains("Centro Cirúrgico"));
        assertTrue(str.contains("CCIR"));
    }
}
