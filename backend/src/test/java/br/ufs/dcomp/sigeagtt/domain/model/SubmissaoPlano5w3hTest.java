package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubmissaoPlano5w3hTest {

    @Test
    @DisplayName("Deve construir com construtor completo e construtor padrao")
    void deveConstruirComConstrutores() {
        SubmissaoPlano5w3h p0 = new SubmissaoPlano5w3h();
        assertNull(p0.getId());

        SubmissaoAtividade sub = new SubmissaoAtividade();
        BigDecimal custo = new BigDecimal("1500.00");
        SubmissaoPlano5w3h p1 =
                new SubmissaoPlano5w3h(
                        1L,
                        sub,
                        "Treinamento",
                        "Reduzir erros",
                        "Enfermeira Chefe",
                        "UTI",
                        "30 dias",
                        "Workshop presencial",
                        custo,
                        "Taxa de adesão");

        assertEquals(1L, p1.getId());
        assertEquals(sub, p1.getSubmissao());
        assertEquals("Treinamento", p1.getOQue());
        assertEquals("Reduzir erros", p1.getPorQue());
        assertEquals("Enfermeira Chefe", p1.getQuem());
        assertEquals("UTI", p1.getOnde());
        assertEquals("30 dias", p1.getQuando());
        assertEquals("Workshop presencial", p1.getComo());
        assertEquals(custo, p1.getQuantoCusta());
        assertEquals("Taxa de adesão", p1.getComoMedir());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals e hashCode")
    void deveTestarGettersSettersEqualsHashCode() {
        SubmissaoPlano5w3h p1 = new SubmissaoPlano5w3h();
        p1.setId(5L);
        SubmissaoAtividade sub = new SubmissaoAtividade();
        BigDecimal custo = new BigDecimal("200.00");

        p1.setSubmissao(sub);
        p1.setOQue("Ação");
        p1.setPorQue("Motivo");
        p1.setQuem("Responsável");
        p1.setOnde("Local");
        p1.setQuando("Prazo");
        p1.setComo("Método");
        p1.setQuantoCusta(custo);
        p1.setComoMedir("Indicador");

        assertEquals(5L, p1.getId());
        assertEquals(sub, p1.getSubmissao());
        assertEquals("Ação", p1.getOQue());
        assertEquals("Motivo", p1.getPorQue());
        assertEquals("Responsável", p1.getQuem());
        assertEquals("Local", p1.getOnde());
        assertEquals("Prazo", p1.getQuando());
        assertEquals("Método", p1.getComo());
        assertEquals(custo, p1.getQuantoCusta());
        assertEquals("Indicador", p1.getComoMedir());

        SubmissaoPlano5w3h p2 = new SubmissaoPlano5w3h();
        p2.setId(5L);
        SubmissaoPlano5w3h p3 = new SubmissaoPlano5w3h();
        p3.setId(10L);

        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "outro");
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
