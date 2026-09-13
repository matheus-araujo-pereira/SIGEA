package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UnidadeHospitalarUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UnidadeHospitalarRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UnidadeHospitalarRespostaDTO;
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
class UnidadeHospitalarControladorTest {

    @Mock private UnidadeHospitalarUseCase useCase;

    @InjectMocks private UnidadeHospitalarControlador controlador;

    @Test
    @DisplayName("Deve listar unidades hospitalares")
    void deveListarUnidades() {
        when(useCase.listarTodas())
                .thenReturn(List.of(new UnidadeHospitalar(1L, "UTI", "UTI-A", true)));

        ResponseEntity<List<UnidadeHospitalarRespostaDTO>> resp = controlador.listar();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar unidade por ID")
    void deveBuscarPorId() {
        when(useCase.buscarPorId(1L)).thenReturn(new UnidadeHospitalar(1L, "UTI", "UTI-A", true));

        ResponseEntity<UnidadeHospitalarRespostaDTO> resp = controlador.buscarPorId(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("UTI-A", resp.getBody().sigla());
    }

    @Test
    @DisplayName("Deve cadastrar unidade com status 201 Created")
    void deveCadastrarUnidade() {
        when(useCase.cadastrar("UTI-A", "UTI"))
                .thenReturn(new UnidadeHospitalar(1L, "UTI", "UTI-A", true));

        UnidadeHospitalarRequisicaoDTO dto = new UnidadeHospitalarRequisicaoDTO("UTI-A", "UTI");
        ResponseEntity<UnidadeHospitalarRespostaDTO> resp = controlador.cadastrar(dto);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(1L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve editar unidade")
    void deveEditarUnidade() {
        when(useCase.editar(1L, "UTI-A", "UTI Nova"))
                .thenReturn(new UnidadeHospitalar(1L, "UTI Nova", "UTI-A", true));

        UnidadeHospitalarRequisicaoDTO dto =
                new UnidadeHospitalarRequisicaoDTO("UTI-A", "UTI Nova");
        ResponseEntity<UnidadeHospitalarRespostaDTO> resp = controlador.editar(1L, dto);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("UTI Nova", resp.getBody().nome());
    }

    @Test
    @DisplayName("Deve excluir unidade com status 200 OK")
    void deveExcluirUnidade() {
        doNothing().when(useCase).excluir(1L);

        ResponseEntity<?> resp = controlador.excluir(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(useCase).excluir(1L);
    }

    @Test
    @DisplayName("Deve alternar status da unidade")
    void deveAlternarStatus() {
        when(useCase.alternarStatus(1L))
                .thenReturn(new UnidadeHospitalar(1L, "UTI", "UTI-A", false));

        ResponseEntity<UnidadeHospitalarRespostaDTO> resp = controlador.alternarStatus(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().ativa());
    }
}
