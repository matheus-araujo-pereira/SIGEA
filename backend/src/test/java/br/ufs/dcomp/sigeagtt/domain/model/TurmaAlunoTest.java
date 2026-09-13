package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TurmaAlunoTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        TurmaAluno ta = new TurmaAluno();
        assertNotNull(ta.getMatriculadoEm());
        assertNull(ta.getTurma());
        assertNull(ta.getAluno());
    }

    @Test
    @DisplayName("Deve construir com construtor de turma e aluno")
    void deveConstruirComTurmaEAluno() {
        Turma turma = new Turma();
        turma.setId(1L);
        Usuario aluno = new Usuario();
        aluno.setId(10L);
        aluno.setPerfil(PerfilUsuario.ALUNO);

        TurmaAluno ta = new TurmaAluno(turma, aluno);
        assertEquals(turma, ta.getTurma());
        assertEquals(aluno, ta.getAluno());
        assertNotNull(ta.getMatriculadoEm());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar data de matricula default")
    void deveConstruirComConstrutorCompleto() {
        Turma turma = new Turma();
        Usuario aluno = new Usuario();
        LocalDateTime agora = LocalDateTime.now();

        TurmaAluno ta1 = new TurmaAluno(turma, aluno, agora);
        assertEquals(agora, ta1.getMatriculadoEm());

        TurmaAluno ta2 = new TurmaAluno(turma, aluno, null);
        assertNotNull(ta2.getMatriculadoEm());
    }

    @Test
    @DisplayName("Deve validar com sucesso discente vinculado")
    void deveValidarComSucesso() {
        Turma turma = new Turma();
        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);

        TurmaAluno ta = new TurmaAluno(turma, aluno);
        assertDoesNotThrow(ta::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando turma for nula")
    void deveLancarExcecaoQuandoTurmaNula() {
        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);
        TurmaAluno ta = new TurmaAluno(null, aluno);

        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, ta::validarInvariantes);
        assertEquals("A turma é obrigatória para a matrícula.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando aluno for nulo")
    void deveLancarExcecaoQuandoAlunoNulo() {
        Turma turma = new Turma();
        TurmaAluno ta = new TurmaAluno(turma, null);

        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, ta::validarInvariantes);
        assertEquals("O discente é obrigatório para a matrícula.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando perfil do usuario nao for ALUNO")
    void deveLancarExcecaoQuandoPerfilNaoAluno() {
        Turma turma = new Turma();
        Usuario professor = new Usuario();
        professor.setPerfil(PerfilUsuario.PROFESSOR);
        TurmaAluno ta = new TurmaAluno(turma, professor);

        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, ta::validarInvariantes);
        assertEquals(
                "Apenas usuários com perfil de ALUNO podem ser matriculados em turmas.",
                ex.getMessage());
    }

    @Test
    @DisplayName("Deve testar setters, equals e hashCode")
    void deveTestarSettersEqualsHashCode() {
        Turma t1 = new Turma();
        t1.setId(1L);
        Usuario u1 = new Usuario();
        u1.setId(2L);
        LocalDateTime data = LocalDateTime.now();

        TurmaAluno ta1 = new TurmaAluno();
        ta1.setTurma(t1);
        ta1.setAluno(u1);
        ta1.setMatriculadoEm(data);

        assertEquals(t1, ta1.getTurma());
        assertEquals(u1, ta1.getAluno());
        assertEquals(data, ta1.getMatriculadoEm());

        TurmaAluno ta2 = new TurmaAluno(t1, u1);

        assertEquals(ta1, ta1);
        assertEquals(ta1, ta2);
        assertNotEquals(ta1, null);
        assertNotEquals(ta1, "outro");
        assertEquals(ta1.hashCode(), ta2.hashCode());

        Turma t2 = new Turma();
        t2.setId(99L);
        TurmaAluno ta3 = new TurmaAluno(t2, u1);
        assertNotEquals(ta1, ta3);

        Usuario u2 = new Usuario();
        u2.setId(99L);
        TurmaAluno ta4 = new TurmaAluno(t1, u2);
        assertNotEquals(ta1, ta4);
    }
}
