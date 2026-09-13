package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubmissaoGatilhoTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        SubmissaoGatilho sg = new SubmissaoGatilho();
        assertFalse(sg.getConfirmouDano());
        assertFalse(sg.getDanoPresenteAdmissao());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults quando nulos")
    void deveConstruirComConstrutorCompleto() {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        GatilhoGtt gat = new GatilhoGtt();
        CategoriaEventoAdverso cat = new CategoriaEventoAdverso();

        SubmissaoGatilho sg1 =
                new SubmissaoGatilho(
                        1L,
                        sub,
                        gat,
                        cat,
                        true,
                        "Paciente sangrou",
                        true,
                        GravidadeNccMerp.CATEGORIA_E);
        assertEquals(1L, sg1.getId());
        assertEquals(sub, sg1.getSubmissao());
        assertEquals(gat, sg1.getGatilho());
        assertEquals(cat, sg1.getCategoriaEventoAdverso());
        assertTrue(sg1.getConfirmouDano());
        assertEquals("Paciente sangrou", sg1.getJustificativaDano());
        assertTrue(sg1.getDanoPresenteAdmissao());
        assertEquals(GravidadeNccMerp.CATEGORIA_E, sg1.getGravidade());

        SubmissaoGatilho sg2 =
                new SubmissaoGatilho(
                        2L, sub, gat, cat, null, "Just", null, GravidadeNccMerp.CATEGORIA_F);
        assertFalse(sg2.getConfirmouDano());
        assertFalse(sg2.getDanoPresenteAdmissao());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals e hashCode")
    void deveTestarGettersSettersEqualsHashCode() {
        SubmissaoGatilho sg1 = new SubmissaoGatilho();
        sg1.setId(10L);
        SubmissaoAtividade sub = new SubmissaoAtividade();
        GatilhoGtt gat = new GatilhoGtt();
        CategoriaEventoAdverso cat = new CategoriaEventoAdverso();

        sg1.setSubmissao(sub);
        sg1.setGatilho(gat);
        sg1.setCategoriaEventoAdverso(cat);
        sg1.setConfirmouDano(true);
        sg1.setJustificativaDano("Dano verificado");
        sg1.setDanoPresenteAdmissao(false);
        sg1.setGravidade(GravidadeNccMerp.CATEGORIA_G);

        assertEquals(10L, sg1.getId());
        assertEquals(sub, sg1.getSubmissao());
        assertEquals(gat, sg1.getGatilho());
        assertEquals(cat, sg1.getCategoriaEventoAdverso());
        assertTrue(sg1.getConfirmouDano());
        assertEquals("Dano verificado", sg1.getJustificativaDano());
        assertFalse(sg1.getDanoPresenteAdmissao());
        assertEquals(GravidadeNccMerp.CATEGORIA_G, sg1.getGravidade());

        SubmissaoGatilho sg2 = new SubmissaoGatilho();
        sg2.setId(10L);
        SubmissaoGatilho sg3 = new SubmissaoGatilho();
        sg3.setId(20L);

        assertEquals(sg1, sg1);
        assertEquals(sg1, sg2);
        assertNotEquals(sg1, sg3);
        assertNotEquals(sg1, null);
        assertNotEquals(sg1, "outro");
        assertEquals(sg1.hashCode(), sg2.hashCode());
    }
}
