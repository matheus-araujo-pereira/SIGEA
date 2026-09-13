package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.getPrimeiroAcesso());
        assertTrue(usuario.getAtivo());
        assertNotNull(usuario.getCriadoEm());
    }

    @Test
    @DisplayName("Deve construir usuario completo com todos os campos")
    void deveConstruirUsuarioCompleto() {
        LocalDateTime agora = LocalDateTime.now();
        Usuario usuario =
                new Usuario(
                        1L,
                        "Dr. Carlos Silva",
                        "carlos.silva@academico.ufs.br",
                        "senha123",
                        false,
                        null,
                        PerfilUsuario.PROFESSOR,
                        true,
                        agora);

        assertEquals(1L, usuario.getId());
        assertEquals("Dr. Carlos Silva", usuario.getNomeCompleto());
        assertEquals("carlos.silva@academico.ufs.br", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertFalse(usuario.getPrimeiroAcesso());
        assertNull(usuario.getMatriculaSigaa());
        assertEquals(PerfilUsuario.PROFESSOR, usuario.getPerfil());
        assertTrue(usuario.getAtivo());
        assertEquals(agora, usuario.getCriadoEm());
    }

    @Test
    @DisplayName("Deve atribuir valores default quando parametros nulos no construtor completo")
    void deveAtribuirValoresDefaultQuandoNulosNoConstrutorCompleto() {
        Usuario usuario =
                new Usuario(
                        2L,
                        "Aluno Teste",
                        "aluno@academico.ufs.br",
                        "hash",
                        null,
                        "202612345678",
                        PerfilUsuario.ALUNO,
                        null,
                        null);

        assertTrue(usuario.getPrimeiroAcesso());
        assertTrue(usuario.getAtivo());
        assertNotNull(usuario.getCriadoEm());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando usuario professor esta consistente")
    void deveValidarComSucessoUsuarioProfessor() {
        Usuario professor = new Usuario();
        professor.setNomeCompleto("Dra. Ana Maria");
        professor.setEmail("ana.maria@academico.ufs.br");
        professor.setPerfil(PerfilUsuario.PROFESSOR);

        assertDoesNotThrow(professor::validarInvariantes);
    }

    @Test
    @DisplayName("Deve validar com sucesso quando usuario aluno possui matricula de 12 digitos")
    void deveValidarComSucessoUsuarioAluno() {
        Usuario aluno = new Usuario();
        aluno.setNomeCompleto("João Pedro Santos");
        aluno.setEmail("joao.pedro@academico.ufs.br");
        aluno.setPerfil(PerfilUsuario.ALUNO);
        aluno.setMatriculaSigaa("202612345678");

        assertDoesNotThrow(aluno::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando nome completo for nulo ou em branco")
    void deveLancarExcecaoQuandoNomeInvalido() {
        Usuario u1 = new Usuario();
        u1.setNomeCompleto(null);
        u1.setEmail("teste@academico.ufs.br");

        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, u1::validarInvariantes);
        assertEquals("O nome completo do usuário é obrigatório.", ex1.getMessage());

        Usuario u2 = new Usuario();
        u2.setNomeCompleto("   ");
        u2.setEmail("teste@academico.ufs.br");

        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, u2::validarInvariantes);
        assertEquals("O nome completo do usuário é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando email for nulo ou em branco")
    void deveLancarExcecaoQuandoEmailInvalido() {
        Usuario u1 = new Usuario();
        u1.setNomeCompleto("Carlos Teste");
        u1.setEmail(null);

        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, u1::validarInvariantes);
        assertEquals("O e-mail institucional é obrigatório.", ex1.getMessage());

        Usuario u2 = new Usuario();
        u2.setNomeCompleto("Carlos Teste");
        u2.setEmail("   ");

        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, u2::validarInvariantes);
        assertEquals("O e-mail institucional é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando email nao pertencer ao dominio @academico.ufs.br")
    void deveLancarExcecaoQuandoEmailForaDoDominioInstitucional() {
        Usuario u = new Usuario();
        u.setNomeCompleto("Carlos Teste");
        u.setEmail("carlos@gmail.com");

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, u::validarInvariantes);
        assertEquals(
                "O e-mail deve pertencer obrigatoriamente ao domínio @academico.ufs.br",
                ex.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando aluno nao tiver matricula ou tiver tamanho invalido")
    void deveLancarExcecaoQuandoMatriculaAlunoInvalida() {
        Usuario aluno = new Usuario();
        aluno.setNomeCompleto("Aluno Teste");
        aluno.setEmail("aluno@academico.ufs.br");
        aluno.setPerfil(PerfilUsuario.ALUNO);
        aluno.setMatriculaSigaa(null);

        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, aluno::validarInvariantes);
        assertTrue(ex1.getMessage().contains("Matrícula do SIGAA é obrigatória para discentes"));

        aluno.setMatriculaSigaa("12345");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, aluno::validarInvariantes);
        assertTrue(ex2.getMessage().contains("exatamente 12 dígitos numéricos"));

        aluno.setMatriculaSigaa("20261234567A");
        RegraNegocioException ex3 =
                assertThrows(RegraNegocioException.class, aluno::validarInvariantes);
        assertTrue(ex3.getMessage().contains("exatamente 12 dígitos numéricos"));
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        LocalDateTime data = LocalDateTime.now();
        Usuario u1 = new Usuario();
        u1.setId(10L);
        u1.setNomeCompleto("Admin");
        u1.setEmail("admin@academico.ufs.br");
        u1.setSenha("hash");
        u1.setPrimeiroAcesso(false);
        u1.setMatriculaSigaa(null);
        u1.setPerfil(PerfilUsuario.ADMINISTRADOR);
        u1.setAtivo(true);
        u1.setCriadoEm(data);

        assertEquals(10L, u1.getId());
        assertEquals("Admin", u1.getNomeCompleto());
        assertEquals("admin@academico.ufs.br", u1.getEmail());
        assertEquals("hash", u1.getSenha());
        assertFalse(u1.getPrimeiroAcesso());
        assertNull(u1.getMatriculaSigaa());
        assertEquals(PerfilUsuario.ADMINISTRADOR, u1.getPerfil());
        assertTrue(u1.getAtivo());
        assertEquals(data, u1.getCriadoEm());

        Usuario u2 = new Usuario();
        u2.setId(10L);
        Usuario u3 = new Usuario();
        u3.setId(20L);

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u1, null);
        assertNotEquals(u1, "string");
        assertEquals(u1.hashCode(), u2.hashCode());

        String str = u1.toString();
        assertTrue(str.contains("id=10"));
        assertTrue(str.contains("admin@academico.ufs.br"));
    }
}
