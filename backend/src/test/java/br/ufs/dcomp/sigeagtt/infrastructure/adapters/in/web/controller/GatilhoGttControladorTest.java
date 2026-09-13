package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.input.GatilhoGttUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.GatilhoGttRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.GatilhoGttRespostaDTO;
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
class GatilhoGttControladorTest {

    @Mock private GatilhoGttUseCase useCase;

    @InjectMocks private GatilhoGttControlador controlador;

    private GatilhoGtt criarGatilhoMock(Long id) {
        ModuloGtt m = new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "", true, null);
        return new GatilhoGtt(id, "C1", m, "PCR", "Manobras", true);
    }

    @Test
    @DisplayName("Deve listar gatilhos GTT")
    void deveListarGatilhos() {
        when(useCase.listar(1L)).thenReturn(List.of(criarGatilhoMock(10L)));

        ResponseEntity<List<GatilhoGttRespostaDTO>> resp = controlador.listar(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar gatilho por ID")
    void deveBuscarPorId() {
        when(useCase.buscarPorId(10L)).thenReturn(criarGatilhoMock(10L));

        ResponseEntity<GatilhoGttRespostaDTO> resp = controlador.buscarPorId(10L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve cadastrar gatilho com status 201 Created")
    void deveCadastrarGatilho() {
        when(useCase.cadastrar(1L, "C1", "PCR", "Manobras")).thenReturn(criarGatilhoMock(10L));

        GatilhoGttRequisicaoDTO dto = new GatilhoGttRequisicaoDTO(1L, "C1", "PCR", "Manobras");
        ResponseEntity<GatilhoGttRespostaDTO> resp = controlador.cadastrar(dto);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve editar gatilho")
    void deveEditarGatilho() {
        when(useCase.editar(10L, 1L, "C1", "Nova Desc", "Novo Limiar"))
                .thenReturn(criarGatilhoMock(10L));

        GatilhoGttRequisicaoDTO dto =
                new GatilhoGttRequisicaoDTO(1L, "C1", "Nova Desc", "Novo Limiar");
        ResponseEntity<GatilhoGttRespostaDTO> resp = controlador.editar(10L, dto);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve excluir gatilho com status 200 OK")
    void deveExcluirGatilho() {
        doNothing().when(useCase).excluir(10L);

        ResponseEntity<?> resp = controlador.excluir(10L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(useCase).excluir(10L);
    }

    @Test
    @DisplayName("Deve alternar status do gatilho")
    void deveAlternarStatus() {
        GatilhoGtt g = criarGatilhoMock(10L);
        g.setAtivo(false);
        when(useCase.alternarStatus(10L)).thenReturn(g);

        ResponseEntity<GatilhoGttRespostaDTO> resp = controlador.alternarStatus(10L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().ativo());
    }
}
