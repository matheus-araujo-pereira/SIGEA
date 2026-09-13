package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase.DadosSalvarCasoClinico;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.CasoClinicoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.SalvarCasoClinicoDTO;
import java.time.LocalDate;
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
class CasoClinicoControladorTest {

    @Mock private CasoClinicoUseCase useCase;

    @InjectMocks private CasoClinicoControlador controlador;

    private CasoClinico criarCasoMock(Long id) {
        CasoClinico c = new CasoClinico();
        c.setId(id);
        c.setTitulo("Caso Teste");
        return c;
    }

    private SalvarCasoClinicoDTO criarDto() {
        return new SalvarCasoClinicoDTO(
                1L,
                "Caso Teste",
                "Desc",
                "Obj",
                "ATD100",
                50,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 5),
                4,
                "Sum",
                "Presc",
                "Exames",
                "Cir",
                "Evol");
    }

    @Test
    @DisplayName("Deve listar casos clinicos")
    void deveListarCasos() {
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);
        when(useCase.listar(prof)).thenReturn(List.of(criarCasoMock(1L)));

        ResponseEntity<List<CasoClinicoDTO>> resp = controlador.listar(prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar caso clinico por ID")
    void deveBuscarPorId() {
        when(useCase.buscarPorId(1L)).thenReturn(criarCasoMock(1L));

        ResponseEntity<CasoClinicoDTO> resp = controlador.buscarPorId(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve cadastrar caso clinico com status 201 Created")
    void deveCadastrarCaso() {
        Usuario prof = new Usuario();
        when(useCase.salvar(eq(prof), any(DadosSalvarCasoClinico.class)))
                .thenReturn(criarCasoMock(5L));

        ResponseEntity<CasoClinicoDTO> resp = controlador.criar(criarDto(), prof);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(5L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve atualizar caso clinico")
    void deveAtualizarCaso() {
        Usuario prof = new Usuario();
        when(useCase.atualizar(eq(5L), eq(prof), any(DadosSalvarCasoClinico.class)))
                .thenReturn(criarCasoMock(5L));

        ResponseEntity<CasoClinicoDTO> resp = controlador.atualizar(5L, criarDto(), prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve excluir caso clinico com status 204 No Content")
    void deveExcluirCaso() {
        Usuario prof = new Usuario();
        doNothing().when(useCase).excluir(5L, prof);

        ResponseEntity<Void> resp = controlador.excluir(5L, prof);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(useCase).excluir(5L, prof);
    }
}
