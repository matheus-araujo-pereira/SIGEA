package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubmissaoPdcaTest {

    @Test
    @DisplayName("Deve construir com construtor completo e construtor sem argumentos")
    void deveConstruirComConstrutores() {
        SubmissaoPdca pdca0 = new SubmissaoPdca();
        assertNull(pdca0.getId());

        SubmissaoAtividade sub = new SubmissaoAtividade();
        SubmissaoPdca pdca1 =
                new SubmissaoPdca(
                        1L,
                        sub,
                        "Meta 0% de infecção",
                        "Treinar enfermeiros",
                        "Auditar checklists",
                        "Padronizar protocolo");

        assertEquals(1L, pdca1.getId());
        assertEquals(sub, pdca1.getSubmissao());
        assertEquals("Meta 0% de infecção", pdca1.getPlanejar());
        assertEquals("Treinar enfermeiros", pdca1.getFazer());
        assertEquals("Auditar checklists", pdca1.getChecar());
        assertEquals("Padronizar protocolo", pdca1.getAgir());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals e hashCode")
    void deveTestarGettersSettersEqualsHashCode() {
        SubmissaoPdca p1 = new SubmissaoPdca();
        p1.setId(5L);
        SubmissaoAtividade sub = new SubmissaoAtividade();
        p1.setSubmissao(sub);
        p1.setPlanejar("Plan");
        p1.setFazer("Do");
        p1.setChecar("Check");
        p1.setAgir("Act");

        assertEquals(5L, p1.getId());
        assertEquals(sub, p1.getSubmissao());
        assertEquals("Plan", p1.getPlanejar());
        assertEquals("Do", p1.getFazer());
        assertEquals("Check", p1.getChecar());
        assertEquals("Act", p1.getAgir());

        SubmissaoPdca p2 = new SubmissaoPdca();
        p2.setId(5L);
        SubmissaoPdca p3 = new SubmissaoPdca();
        p3.setId(10L);

        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "outro");
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
