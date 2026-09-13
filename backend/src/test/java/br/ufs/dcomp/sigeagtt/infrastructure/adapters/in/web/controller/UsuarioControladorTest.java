package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UsuarioUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.AlterarSenhaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UsuarioEdicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UsuarioRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UsuarioRespostaDTO;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UsuarioControladorTest {

    @Mock private UsuarioUseCase usuarioUseCase;

    @InjectMocks private UsuarioControlador controlador;

    private Usuario criarUsuarioMock(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNomeCompleto("Usuario Teste");
        u.setEmail("teste@academico.ufs.br");
        u.setPerfil(PerfilUsuario.PROFESSOR);
        return u;
    }

    @Test
    @DisplayName("Deve listar usuarios")
    void deveListarUsuarios() {
        when(usuarioUseCase.listarTodos()).thenReturn(List.of(criarUsuarioMock(1L)));

        ResponseEntity<List<UsuarioRespostaDTO>> resp = controlador.listar();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar usuario por ID")
    void deveBuscarPorId() {
        when(usuarioUseCase.buscarPorId(1L)).thenReturn(criarUsuarioMock(1L));

        ResponseEntity<UsuarioRespostaDTO> resp = controlador.buscarPorId(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve cadastrar usuario com status 201 Created")
    void deveCadastrarUsuario() {
        Usuario criado = criarUsuarioMock(5L);
        when(usuarioUseCase.cadastrar(
                        "Nome", "email@academico.ufs.br", "123456789012", PerfilUsuario.ALUNO))
                .thenReturn(criado);

        UsuarioRequisicaoDTO dto =
                new UsuarioRequisicaoDTO(
                        "Nome", "email@academico.ufs.br", "123456789012", PerfilUsuario.ALUNO);
        ResponseEntity<UsuarioRespostaDTO> resp = controlador.cadastrar(dto);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(5L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve editar usuario")
    void deveEditarUsuario() {
        Usuario atualizado = criarUsuarioMock(5L);
        when(usuarioUseCase.editar(
                        5L, "Nome", "email@academico.ufs.br", null, PerfilUsuario.PROFESSOR))
                .thenReturn(atualizado);

        UsuarioEdicaoDTO dto =
                new UsuarioEdicaoDTO(
                        "Nome", "email@academico.ufs.br", null, PerfilUsuario.PROFESSOR);
        ResponseEntity<UsuarioRespostaDTO> resp = controlador.editar(5L, dto);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(5L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve resetar senha")
    void deveResetarSenha() {
        Usuario u = criarUsuarioMock(5L);
        when(usuarioUseCase.resetarSenha(5L)).thenReturn(u);

        ResponseEntity<UsuarioRespostaDTO> resp = controlador.resetarSenha(5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve inativar usuario")
    void deveInativarUsuario() {
        Usuario u = criarUsuarioMock(5L);
        u.setAtivo(false);
        when(usuarioUseCase.inativar(5L)).thenReturn(u);

        ResponseEntity<UsuarioRespostaDTO> resp = controlador.inativar(5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().ativo());
    }

    @Test
    @DisplayName("Deve reativar usuario")
    void deveReativarUsuario() {
        Usuario u = criarUsuarioMock(5L);
        u.setAtivo(true);
        when(usuarioUseCase.reativar(5L)).thenReturn(u);

        ResponseEntity<UsuarioRespostaDTO> resp = controlador.reativar(5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().ativo());
    }

    @Test
    @DisplayName("Deve alterar senha")
    void deveAlterarSenha() {
        Usuario u = criarUsuarioMock(5L);
        when(usuarioUseCase.alterarSenha(5L, "atual", "nova", "nova")).thenReturn(u);

        AlterarSenhaDTO dto = new AlterarSenhaDTO("atual", "nova", "nova");
        ResponseEntity<UsuarioRespostaDTO> resp = controlador.alterarSenha(5L, dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
