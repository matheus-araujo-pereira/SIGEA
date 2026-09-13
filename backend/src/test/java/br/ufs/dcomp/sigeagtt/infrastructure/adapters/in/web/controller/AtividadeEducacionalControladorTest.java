package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosPainelAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosSalvarAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.ItemAtividadeResumo;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.AtividadeEducacionalDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.PainelAtividadeDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.SalvarAtividadeDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
class AtividadeEducacionalControladorTest {

    @Mock private AtividadeEducacionalUseCase useCase;

    @InjectMocks private AtividadeEducacionalControlador controlador;

    private ItemAtividadeResumo criarResumoMock(Long id) {
        AtividadeEducacional a = new AtividadeEducacional();
        a.setId(id);
        a.setTitulo("Atividade Teste");
        return new ItemAtividadeResumo(a, 30, 20, 10);
    }

    private SalvarAtividadeDTO criarDto() {
        return new SalvarAtividadeDTO(
                1L,
                2L,
                "Titulo",
                "Ori",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                20,
                true);
    }

    @Test
    @DisplayName("Deve listar atividades")
    void deveListarAtividades() {
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        when(useCase.listar(prof)).thenReturn(List.of(criarResumoMock(1L)));

        ResponseEntity<List<AtividadeEducacionalDTO>> resp = controlador.listar(prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar atividade por ID")
    void deveBuscarPorId() {
        when(useCase.buscarPorId(1L)).thenReturn(criarResumoMock(1L));

        ResponseEntity<AtividadeEducacionalDTO> resp = controlador.buscarPorId(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve buscar painel da atividade")
    void deveBuscarPainel() {
        Usuario prof = new Usuario();
        DadosPainelAtividade painel =
                new DadosPainelAtividade(
                        criarResumoMock(1L),
                        new CasoClinico(),
                        30,
                        20,
                        10,
                        10,
                        new BigDecimal("9.0"),
                        List.of());
        when(useCase.buscarPainelAtividade(1L, prof)).thenReturn(painel);

        ResponseEntity<PainelAtividadeDTO> resp = controlador.buscarPainel(1L, prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(30, resp.getBody().totalAlunosTurma());
    }

    @Test
    @DisplayName("Deve cadastrar atividade com status 201 Created")
    void deveCadastrarAtividade() {
        Usuario prof = new Usuario();
        when(useCase.salvar(eq(prof), any(DadosSalvarAtividade.class)))
                .thenReturn(criarResumoMock(5L));

        ResponseEntity<AtividadeEducacionalDTO> resp = controlador.criar(criarDto(), prof);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(5L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve atualizar atividade")
    void deveAtualizarAtividade() {
        Usuario prof = new Usuario();
        when(useCase.atualizar(eq(5L), eq(prof), any(DadosSalvarAtividade.class)))
                .thenReturn(criarResumoMock(5L));

        ResponseEntity<AtividadeEducacionalDTO> resp = controlador.atualizar(5L, criarDto(), prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve excluir atividade com status 204 No Content")
    void deveExcluirAtividade() {
        Usuario prof = new Usuario();
        doNothing().when(useCase).excluir(5L, prof);

        ResponseEntity<Void> resp = controlador.excluir(5L, prof);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(useCase).excluir(5L, prof);
    }
}
