package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubmissaoAtividadeTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        assertEquals(StatusSubmissao.EM_ANDAMENTO, sub.getStatus());
        assertEquals(0, sub.getTempoGastoSegundos());
        assertNotNull(sub.getDataInicio());
        assertNotNull(sub.getAchadosGatilhos());
        assertNotNull(sub.getPlanos5w3h());
    }

    @Test
    @DisplayName("Deve inicializar com construtor de atividade e aluno")
    void deveInicializarComAtividadeEAluno() {
        AtividadeEducacional ativ = new AtividadeEducacional();
        Usuario aluno = new Usuario();
        SubmissaoAtividade sub = new SubmissaoAtividade(ativ, aluno);

        assertEquals(ativ, sub.getAtividade());
        assertEquals(aluno, sub.getAluno());
        assertEquals(StatusSubmissao.EM_ANDAMENTO, sub.getStatus());
        assertEquals(0, sub.getTempoGastoSegundos());
        assertNotNull(sub.getDataInicio());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando atributos consistentes")
    void deveValidarComSucesso() {
        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), new Usuario());
        sub.setNota(new BigDecimal("9.50"));
        assertDoesNotThrow(sub::validarInvariantes);

        // Sem nota
        sub.setNota(null);
        assertDoesNotThrow(sub::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando atividade educacional vinculada for nula")
    void deveLancarExcecaoQuandoAtividadeNula() {
        SubmissaoAtividade sub = new SubmissaoAtividade(null, new Usuario());
        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, sub::validarInvariantes);
        assertEquals("A atividade educacional vinculada é obrigatória.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando discente vinculado for nulo")
    void deveLancarExcecaoQuandoAlunoNulo() {
        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), null);
        RegraNegocioException ex =
                assertThrows(RegraNegocioException.class, sub::validarInvariantes);
        assertEquals("O discente vinculado é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando nota estiver fora do intervalo 0.00 a 10.00")
    void deveLancarExcecaoQuandoNotaInvalida() {
        SubmissaoAtividade sub = new SubmissaoAtividade(new AtividadeEducacional(), new Usuario());

        sub.setNota(new BigDecimal("-0.01"));
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, sub::validarInvariantes);
        assertEquals(
                "A nota de avaliação docente deve situar-se estritamente entre 0.00 e 10.00.",
                ex1.getMessage());

        sub.setNota(new BigDecimal("10.01"));
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, sub::validarInvariantes);
        assertEquals(
                "A nota de avaliação docente deve situar-se estritamente entre 0.00 e 10.00.",
                ex2.getMessage());
    }

    @Test
    @DisplayName("Deve submeter atividade atualizando status, tempo e dataSubmissao")
    void deveSubmeterComSucesso() {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.submeter(900);

        assertEquals(StatusSubmissao.SUBMETIDA, sub.getStatus());
        assertEquals(900, sub.getTempoGastoSegundos());
        assertNotNull(sub.getDataSubmissao());

        // Submeter com tempo nulo preserva o tempo anterior
        sub.submeter(null);
        assertEquals(900, sub.getTempoGastoSegundos());
    }

    @Test
    @DisplayName("Deve avaliar com sucesso quando professor ou administrador atribui nota valida")
    void deveAvaliarComSucesso() {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        Usuario professor = new Usuario();
        professor.setPerfil(PerfilUsuario.PROFESSOR);

        sub.avaliar(professor, new BigDecimal("8.50"), "Excelente análise");

        assertEquals(StatusSubmissao.AVALIADA, sub.getStatus());
        assertEquals(professor, sub.getProfessorCorretor());
        assertEquals(new BigDecimal("8.50"), sub.getNota());
        assertEquals("Excelente análise", sub.getParecerDocente());
        assertNotNull(sub.getDataAvaliacao());

        // Testar também com administrador
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        sub.avaliar(admin, null, "Homologado");
        assertEquals(admin, sub.getProfessorCorretor());
    }

    @Test
    @DisplayName("Deve lancar excecao ao avaliar quando avaliador for nulo ou aluno")
    void deveLancarExcecaoQuandoAvaliadorInvalido() {
        SubmissaoAtividade sub = new SubmissaoAtividade();

        RegraNegocioException ex1 =
                assertThrows(
                        RegraNegocioException.class,
                        () -> sub.avaliar(null, new BigDecimal("7.0"), "Parecer"));
        assertEquals(
                "Apenas docentes ou administradores podem avaliar submissões.", ex1.getMessage());

        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);
        RegraNegocioException ex2 =
                assertThrows(
                        RegraNegocioException.class,
                        () -> sub.avaliar(aluno, new BigDecimal("7.0"), "Parecer"));
        assertEquals(
                "Apenas docentes ou administradores podem avaliar submissões.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao ao avaliar quando nota for menor que zero ou maior que 10")
    void deveLancarExcecaoAoAvaliarComNotaInvalida() {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        RegraNegocioException ex1 =
                assertThrows(
                        RegraNegocioException.class,
                        () -> sub.avaliar(prof, new BigDecimal("-1.0"), "Parecer"));
        assertEquals("A nota deve estar entre 0.00 e 10.00.", ex1.getMessage());

        RegraNegocioException ex2 =
                assertThrows(
                        RegraNegocioException.class,
                        () -> sub.avaliar(prof, new BigDecimal("10.5"), "Parecer"));
        assertEquals("A nota deve estar entre 0.00 e 10.00.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals e hashCode")
    void deveTestarGettersSettersEqualsHashCode() {
        SubmissaoAtividade s1 = new SubmissaoAtividade();
        s1.setId(100L);
        AtividadeEducacional ativ = new AtividadeEducacional();
        Usuario aluno = new Usuario();
        Usuario prof = new Usuario();
        LocalDateTime d1 = LocalDateTime.now();
        LocalDateTime d2 = d1.plusHours(1);
        LocalDateTime d3 = d2.plusDays(1);
        List<SubmissaoGatilho> gatilhos = new ArrayList<>();
        SubmissaoIshikawa ishikawa = new SubmissaoIshikawa();
        List<SubmissaoPlano5w3h> planos = new ArrayList<>();
        SubmissaoPdca pdca = new SubmissaoPdca();

        s1.setAtividade(ativ);
        s1.setAluno(aluno);
        s1.setStatus(StatusSubmissao.SUBMETIDA);
        s1.setTempoGastoSegundos(600);
        s1.setDataInicio(d1);
        s1.setDataSubmissao(d2);
        s1.setProfessorCorretor(prof);
        s1.setNota(new BigDecimal("9.0"));
        s1.setParecerDocente("Bom");
        s1.setDataAvaliacao(d3);
        s1.setAchadosGatilhos(gatilhos);
        s1.setIshikawa(ishikawa);
        s1.setPlanos5w3h(planos);
        s1.setPdca(pdca);

        assertEquals(100L, s1.getId());
        assertEquals(ativ, s1.getAtividade());
        assertEquals(aluno, s1.getAluno());
        assertEquals(StatusSubmissao.SUBMETIDA, s1.getStatus());
        assertEquals(600, s1.getTempoGastoSegundos());
        assertEquals(d1, s1.getDataInicio());
        assertEquals(d2, s1.getDataSubmissao());
        assertEquals(prof, s1.getProfessorCorretor());
        assertEquals(new BigDecimal("9.0"), s1.getNota());
        assertEquals("Bom", s1.getParecerDocente());
        assertEquals(d3, s1.getDataAvaliacao());
        assertEquals(gatilhos, s1.getAchadosGatilhos());
        assertEquals(ishikawa, s1.getIshikawa());
        assertEquals(planos, s1.getPlanos5w3h());
        assertEquals(pdca, s1.getPdca());

        SubmissaoAtividade s2 = new SubmissaoAtividade();
        s2.setId(100L);
        SubmissaoAtividade s3 = new SubmissaoAtividade();
        s3.setId(200L);

        assertEquals(s1, s1);
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertNotEquals(s1, null);
        assertNotEquals(s1, "outro");
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
