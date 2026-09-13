package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnumsAndExceptionsTest {

    @Test
    @DisplayName("Deve cobrir todos os valores e metodos de PerfilUsuario")
    void deveCobrirPerfilUsuario() {
        assertEquals(3, PerfilUsuario.values().length);
        assertEquals(PerfilUsuario.ADMINISTRADOR, PerfilUsuario.valueOf("ADMINISTRADOR"));
        assertEquals(PerfilUsuario.PROFESSOR, PerfilUsuario.valueOf("PROFESSOR"));
        assertEquals(PerfilUsuario.ALUNO, PerfilUsuario.valueOf("ALUNO"));
    }

    @Test
    @DisplayName("Deve cobrir todos os valores e metodos de GravidadeNccMerp")
    void deveCobrirGravidadeNccMerp() {
        assertEquals(5, GravidadeNccMerp.values().length);
        for (GravidadeNccMerp gravidade : GravidadeNccMerp.values()) {
            assertNotNull(gravidade.getRotulo());
            assertNotNull(gravidade.getDescricao());
            assertEquals(gravidade, GravidadeNccMerp.valueOf(gravidade.name()));
        }
        assertEquals("Categoria E", GravidadeNccMerp.CATEGORIA_E.getRotulo());
        assertEquals(
                "Dano temporário com necessidade de intervenção",
                GravidadeNccMerp.CATEGORIA_E.getDescricao());
    }

    @Test
    @DisplayName("Deve cobrir todos os valores e metodos de StatusSubmissao")
    void deveCobrirStatusSubmissao() {
        assertEquals(3, StatusSubmissao.values().length);
        for (StatusSubmissao status : StatusSubmissao.values()) {
            assertNotNull(status.getDescricao());
            assertEquals(status, StatusSubmissao.valueOf(status.name()));
        }
        assertEquals("Em andamento", StatusSubmissao.EM_ANDAMENTO.getDescricao());
        assertEquals("Submetida", StatusSubmissao.SUBMETIDA.getDescricao());
        assertEquals("Avaliada", StatusSubmissao.AVALIADA.getDescricao());
    }

    @Test
    @DisplayName("Deve cobrir excecoes customizadas de dominio")
    void deveCobrirExcecoesCustomizadas() {
        AcessoProibidoException ex1 = new AcessoProibidoException("Acesso negado");
        assertEquals("Acesso negado", ex1.getMessage());

        ConflitoDadosException ex2 = new ConflitoDadosException("Dado duplicado");
        assertEquals("Dado duplicado", ex2.getMessage());

        RecursoNaoEncontradoException ex3 = new RecursoNaoEncontradoException("Não encontrado");
        assertEquals("Não encontrado", ex3.getMessage());

        RegraNegocioException ex4 = new RegraNegocioException("Regra violada");
        assertEquals("Regra violada", ex4.getMessage());
    }
}
