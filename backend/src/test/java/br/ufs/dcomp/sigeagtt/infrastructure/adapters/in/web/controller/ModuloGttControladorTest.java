package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.input.ModuloGttUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ModuloGttRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ModuloGttRespostaDTO;
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
class ModuloGttControladorTest {

    @Mock private ModuloGttUseCase useCase;

    @InjectMocks private ModuloGttControlador controlador;

    @Test
    @DisplayName("Deve listar modulos GTT")
    void deveListarModulos() {
        when(useCase.listarTodos())
                .thenReturn(
                        List.of(
                                new ModuloGtt(
                                        1L, "CUIDADOS", "Cuidados Gerais", "Desc", true, null)));

        ResponseEntity<List<ModuloGttRespostaDTO>> resp = controlador.listar();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar modulo por ID")
    void deveBuscarPorId() {
        when(useCase.buscarPorId(1L))
                .thenReturn(new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "Desc", true, null));

        ResponseEntity<ModuloGttRespostaDTO> resp = controlador.buscarPorId(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("CUIDADOS", resp.getBody().codigo());
    }

    @Test
    @DisplayName("Deve cadastrar modulo com status 201 Created")
    void deveCadastrarModulo() {
        when(useCase.cadastrar("CUIDADOS", "Cuidados Gerais", "Desc"))
                .thenReturn(new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "Desc", true, null));

        ModuloGttRequisicaoDTO dto =
                new ModuloGttRequisicaoDTO("CUIDADOS", "Cuidados Gerais", "Desc");
        ResponseEntity<ModuloGttRespostaDTO> resp = controlador.cadastrar(dto);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(1L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve editar modulo")
    void deveEditarModulo() {
        when(useCase.editar(1L, "CUIDADOS", "Novo Nome", "Nova Desc"))
                .thenReturn(new ModuloGtt(1L, "CUIDADOS", "Novo Nome", "Nova Desc", true, null));

        ModuloGttRequisicaoDTO dto =
                new ModuloGttRequisicaoDTO("CUIDADOS", "Novo Nome", "Nova Desc");
        ResponseEntity<ModuloGttRespostaDTO> resp = controlador.editar(1L, dto);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Novo Nome", resp.getBody().nome());
    }

    @Test
    @DisplayName("Deve excluir modulo com status 200 OK")
    void deveExcluirModulo() {
        doNothing().when(useCase).excluir(1L);

        ResponseEntity<?> resp = controlador.excluir(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(useCase).excluir(1L);
    }

    @Test
    @DisplayName("Deve alternar status do modulo")
    void deveAlternarStatus() {
        when(useCase.alternarStatus(1L))
                .thenReturn(new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "Desc", false, null));

        ResponseEntity<ModuloGttRespostaDTO> resp = controlador.alternarStatus(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().ativo());
    }
}
