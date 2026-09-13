package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AtividadeEducacionalTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        AtividadeEducacional ativ = new AtividadeEducacional();
        assertEquals(20, ativ.getTempoLimiteMinutos());
        assertTrue(ativ.getAtiva());
        assertNotNull(ativ.getCriadaEm());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults")
    void deveConstruirComConstrutorCompleto() {
        Turma turma = new Turma();
        CasoClinico caso = new CasoClinico();
        LocalDateTime ini = LocalDateTime.of(2026, 3, 1, 8, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 3, 15, 18, 0);
        LocalDateTime agora = LocalDateTime.now();

        AtividadeEducacional a1 =
                new AtividadeEducacional(
                        1L, turma, caso, "Auditoria 1", "Instruções", ini, fim, 30, true, agora);

        assertEquals(1L, a1.getId());
        assertEquals(turma, a1.getTurma());
        assertEquals(caso, a1.getCasoClinico());
        assertEquals("Auditoria 1", a1.getTitulo());
        assertEquals("Instruções", a1.getOrientacoesPedagogicas());
        assertEquals(ini, a1.getDataInicio());
        assertEquals(fim, a1.getDataFim());
        assertEquals(30, a1.getTempoLimiteMinutos());
        assertTrue(a1.getAtiva());
        assertEquals(agora, a1.getCriadaEm());

        AtividadeEducacional a2 =
                new AtividadeEducacional(
                        2L, turma, caso, "Auditoria 2", "Inst", ini, fim, null, null, null);
        assertEquals(20, a2.getTempoLimiteMinutos());
        assertTrue(a2.getAtiva());
        assertNotNull(a2.getCriadaEm());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando dados e datas consistentes")
    void deveValidarComSucesso() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade GTT");
        a.setTurma(new Turma());
        a.setCasoClinico(new CasoClinico());
        a.setDataInicio(LocalDateTime.of(2026, 3, 1, 8, 0));
        a.setDataFim(LocalDateTime.of(2026, 3, 5, 18, 0));
        a.setTempoLimiteMinutos(20);

        assertDoesNotThrow(a::validarInvariantes);

        // Também deve validar se datas forem nulas
        a.setDataInicio(null);
        a.setDataFim(null);
        assertDoesNotThrow(a::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando titulo for nulo ou em branco")
    void deveLancarExcecaoQuandoTituloInvalido() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTurma(new Turma());
        a.setCasoClinico(new CasoClinico());

        a.setTitulo(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("O título da atividade educacional é obrigatório.", ex1.getMessage());

        a.setTitulo("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("O título da atividade educacional é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando turma vinculada for nula")
    void deveLancarExcecaoQuandoTurmaNula() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade");
        a.setTurma(null);
        a.setCasoClinico(new CasoClinico());

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("A turma vinculada à atividade é obrigatória.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando caso clinico associado for nulo")
    void deveLancarExcecaoQuandoCasoClinicoNulo() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade");
        a.setTurma(new Turma());
        a.setCasoClinico(null);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("O caso clínico associado é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando data de fim for anterior a data de inicio")
    void deveLancarExcecaoQuandoDataFimAnteriorDataInicio() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade");
        a.setTurma(new Turma());
        a.setCasoClinico(new CasoClinico());
        a.setDataInicio(LocalDateTime.of(2026, 3, 10, 8, 0));
        a.setDataFim(LocalDateTime.of(2026, 3, 5, 8, 0));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals(
                "A data de término não pode ser anterior à data de início da atividade.",
                ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando tempo limite for menor ou igual a zero")
    void deveLancarExcecaoQuandoTempoLimiteInvalido() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade");
        a.setTurma(new Turma());
        a.setCasoClinico(new CasoClinico());

        a.setTempoLimiteMinutos(0);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("O tempo limite em minutos deve ser maior que zero.", ex1.getMessage());

        a.setTempoLimiteMinutos(-5);
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, a::validarInvariantes);
        assertEquals("O tempo limite em minutos deve ser maior que zero.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        AtividadeEducacional a1 = new AtividadeEducacional();
        a1.setId(50L);
        Turma t = new Turma();
        CasoClinico c = new CasoClinico();
        LocalDateTime d1 = LocalDateTime.now();
        LocalDateTime d2 = d1.plusDays(7);
        LocalDateTime agora = LocalDateTime.now();

        a1.setTurma(t);
        a1.setCasoClinico(c);
        a1.setTitulo("Auditoria Teste");
        a1.setOrientacoesPedagogicas("Orientações");
        a1.setDataInicio(d1);
        a1.setDataFim(d2);
        a1.setTempoLimiteMinutos(25);
        a1.setAtiva(false);
        a1.setCriadaEm(agora);

        assertEquals(50L, a1.getId());
        assertEquals(t, a1.getTurma());
        assertEquals(c, a1.getCasoClinico());
        assertEquals("Auditoria Teste", a1.getTitulo());
        assertEquals("Orientações", a1.getOrientacoesPedagogicas());
        assertEquals(d1, a1.getDataInicio());
        assertEquals(d2, a1.getDataFim());
        assertEquals(25, a1.getTempoLimiteMinutos());
        assertFalse(a1.getAtiva());
        assertEquals(agora, a1.getCriadaEm());

        AtividadeEducacional a2 = new AtividadeEducacional();
        a2.setId(50L);
        AtividadeEducacional a3 = new AtividadeEducacional();
        a3.setId(60L);

        assertEquals(a1, a1);
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);
        assertNotEquals(a1, "outro");
        assertEquals(a1.hashCode(), a2.hashCode());

        String str = a1.toString();
        assertTrue(str.contains("Auditoria Teste"));
    }

    @Test
    @DisplayName("Deve permitir datas nulas e tempo limite nulo na validação de invariantes")
    void devePermitirDatasETempoLimiteNulos() {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setTitulo("Atividade");
        a.setTurma(new Turma());
        a.setCasoClinico(new CasoClinico());
        a.setDataInicio(null);
        a.setDataFim(LocalDateTime.now());
        a.setTempoLimiteMinutos(null);
        assertDoesNotThrow(a::validarInvariantes);

        a.setDataInicio(LocalDateTime.now());
        a.setDataFim(null);
        assertDoesNotThrow(a::validarInvariantes);
    }
}
