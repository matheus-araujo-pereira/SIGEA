package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TurmaTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        Turma turma = new Turma();
        assertTrue(turma.getAtiva());
        assertEquals("Segurança do Paciente e Auditoria Clínica", turma.getNomeDisciplina());
        assertNotNull(turma.getCriadaEm());
    }

    @Test
    @DisplayName("Deve construir com construtor de 7 parametros")
    void deveConstruirComSeteParametros() {
        Usuario professor = new Usuario();
        professor.setId(1L);
        professor.setPerfil(PerfilUsuario.PROFESSOR);

        LocalDateTime agora = LocalDateTime.now();
        Turma turma = new Turma(10L, professor, "MED001", "2026.1", "2026/1", true, agora);

        assertEquals(10L, turma.getId());
        assertEquals(professor, turma.getProfessorResponsavel());
        assertEquals("MED001", turma.getCodigoDisciplina());
        assertEquals("Segurança do Paciente e Auditoria Clínica", turma.getNomeDisciplina());
        assertEquals("2026.1", turma.getPeriodoLetivo());
        assertEquals("2026/1", turma.getAnoSemestre());
        assertTrue(turma.getAtiva());
        assertEquals(agora, turma.getCriadaEm());
    }

    @Test
    @DisplayName(
            "Deve construir com construtor de 8 parametros e tratar nomeDisciplina e datas nulas")
    void deveConstruirComOitoParametros() {
        Usuario admin = new Usuario();
        admin.setId(2L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        Turma t1 =
                new Turma(
                        11L,
                        admin,
                        "MED002",
                        "Internato em Clínica Médica",
                        "2026.2",
                        "2026/2",
                        false,
                        null);
        assertEquals("Internato em Clínica Médica", t1.getNomeDisciplina());
        assertFalse(t1.getAtiva());
        assertNotNull(t1.getCriadaEm());

        Turma t2 = new Turma(12L, admin, "MED003", "", "2026.2", "2026/2", null, null);
        assertEquals("Segurança do Paciente e Auditoria Clínica", t2.getNomeDisciplina());
        assertTrue(t2.getAtiva());
    }

    @Test
    @DisplayName("Deve validar com sucesso turma com professor e campos obrigatorios")
    void deveValidarComSucesso() {
        Usuario professor = new Usuario();
        professor.setPerfil(PerfilUsuario.PROFESSOR);
        Turma turma = new Turma();
        turma.setProfessorResponsavel(professor);
        turma.setCodigoDisciplina("MED001");
        turma.setPeriodoLetivo("2026.1");

        assertDoesNotThrow(turma::validarInvariantes);

        // Também deve validar se o perfil for ADMINISTRADOR
        professor.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(turma::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando professor for nulo ou nao tiver perfil docente")
    void deveLancarExcecaoQuandoProfessorInvalido() {
        Turma turma = new Turma();
        turma.setCodigoDisciplina("MED001");
        turma.setPeriodoLetivo("2026.1");

        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals("O professor responsável pela turma é obrigatório.", ex1.getMessage());

        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);
        turma.setProfessorResponsavel(aluno);

        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals(
                "Apenas usuários com perfil docente podem ser responsáveis por turmas.",
                ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando codigo da disciplina for nulo ou em branco")
    void deveLancarExcecaoQuandoCodigoDisciplinaInvalido() {
        Usuario professor = new Usuario();
        professor.setPerfil(PerfilUsuario.PROFESSOR);
        Turma turma = new Turma();
        turma.setProfessorResponsavel(professor);
        turma.setPeriodoLetivo("2026.1");

        turma.setCodigoDisciplina(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals("O código da disciplina é obrigatório.", ex1.getMessage());

        turma.setCodigoDisciplina("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals("O código da disciplina é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando periodo letivo for nulo ou em branco")
    void deveLancarExcecaoQuandoPeriodoLetivoInvalido() {
        Usuario professor = new Usuario();
        professor.setPerfil(PerfilUsuario.PROFESSOR);
        Turma turma = new Turma();
        turma.setProfessorResponsavel(professor);
        turma.setCodigoDisciplina("MED001");

        turma.setPeriodoLetivo(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals("O período letivo é obrigatório.", ex1.getMessage());

        turma.setPeriodoLetivo("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, turma::validarInvariantes);
        assertEquals("O período letivo é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        Turma t1 = new Turma();
        t1.setId(100L);
        t1.setCodigoDisciplina("MED100");
        t1.setNomeDisciplina("Nome");
        t1.setPeriodoLetivo("2026.1");
        t1.setAnoSemestre("2026/1");
        t1.setAtiva(true);
        LocalDateTime data = LocalDateTime.now();
        t1.setCriadaEm(data);

        assertEquals(100L, t1.getId());
        assertEquals("MED100", t1.getCodigoDisciplina());
        assertEquals("Nome", t1.getNomeDisciplina());
        assertEquals("2026.1", t1.getPeriodoLetivo());
        assertEquals("2026/1", t1.getAnoSemestre());
        assertTrue(t1.getAtiva());
        assertEquals(data, t1.getCriadaEm());

        Turma t2 = new Turma();
        t2.setId(100L);
        Turma t3 = new Turma();
        t3.setId(200L);

        assertEquals(t1, t1);
        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, null);
        assertNotEquals(t1, "outro");
        assertEquals(t1.hashCode(), t2.hashCode());

        String str = t1.toString();
        assertTrue(str.contains("MED100"));
        assertTrue(str.contains("2026.1"));
    }
}
