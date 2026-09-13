package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubmissaoIshikawaTest {

    @Test
    @DisplayName("Deve construir com construtor completo e construtor sem argumentos")
    void deveConstruirComConstrutorCompleto() {
        SubmissaoIshikawa si0 = new SubmissaoIshikawa();
        assertNull(si0.getId());

        SubmissaoAtividade sub = new SubmissaoAtividade();
        SubmissaoIshikawa si1 =
                new SubmissaoIshikawa(
                        1L,
                        sub,
                        "Hemorragia",
                        "Sem protocolo",
                        "Fadiga de plantão",
                        "Falta de insumo",
                        "Falha na medição do INR",
                        "Iluminação inadequada",
                        "Bomba de infusão com defeito");

        assertEquals(1L, si1.getId());
        assertEquals(sub, si1.getSubmissao());
        assertEquals("Hemorragia", si1.getEfeitoPrincipal());
        assertEquals("Sem protocolo", si1.getMetodo());
        assertEquals("Fadiga de plantão", si1.getMaoDeObra());
        assertEquals("Falta de insumo", si1.getMaterial());
        assertEquals("Falha na medição do INR", si1.getMedida());
        assertEquals("Iluminação inadequada", si1.getMeioAmbiente());
        assertEquals("Bomba de infusão com defeito", si1.getMaquina());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals e hashCode")
    void deveTestarGettersSettersEqualsHashCode() {
        SubmissaoIshikawa si1 = new SubmissaoIshikawa();
        si1.setId(10L);
        SubmissaoAtividade sub = new SubmissaoAtividade();
        si1.setSubmissao(sub);
        si1.setEfeitoPrincipal("Efeito");
        si1.setMetodo("Método");
        si1.setMaoDeObra("Mão");
        si1.setMaterial("Mat");
        si1.setMedida("Med");
        si1.setMeioAmbiente("Meio");
        si1.setMaquina("Maq");

        assertEquals(10L, si1.getId());
        assertEquals(sub, si1.getSubmissao());
        assertEquals("Efeito", si1.getEfeitoPrincipal());
        assertEquals("Método", si1.getMetodo());
        assertEquals("Mão", si1.getMaoDeObra());
        assertEquals("Mat", si1.getMaterial());
        assertEquals("Med", si1.getMedida());
        assertEquals("Meio", si1.getMeioAmbiente());
        assertEquals("Maq", si1.getMaquina());

        SubmissaoIshikawa si2 = new SubmissaoIshikawa();
        si2.setId(10L);
        SubmissaoIshikawa si3 = new SubmissaoIshikawa();
        si3.setId(20L);

        assertEquals(si1, si1);
        assertEquals(si1, si2);
        assertNotEquals(si1, si3);
        assertNotEquals(si1, null);
        assertNotEquals(si1, "outro");
        assertEquals(si1.hashCode(), si2.hashCode());
    }
}
