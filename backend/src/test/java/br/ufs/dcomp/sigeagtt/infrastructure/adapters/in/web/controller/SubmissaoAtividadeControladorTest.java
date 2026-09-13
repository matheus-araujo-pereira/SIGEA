package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosSalvarSubmissao;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.ItemMinhaAtividade;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.AvaliarSubmissaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.MinhaAtividadeItemDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.SalvarSubmissaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.SubmissaoDTO;
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
class SubmissaoAtividadeControladorTest {

    @Mock private SubmissaoAtividadeUseCase useCase;

    @InjectMocks private SubmissaoAtividadeControlador controlador;

    private SubmissaoAtividade criarSubmissaoMock(Long id) {
        SubmissaoAtividade s = new SubmissaoAtividade(new AtividadeEducacional(), new Usuario());
        s.setId(id);
        s.setStatus(StatusSubmissao.EM_ANDAMENTO);
        return s;
    }

    @Test
    @DisplayName("Deve listar minhas atividades como discente")
    void deveListarMinhasAtividades() {
        Usuario aluno = new Usuario();
        aluno.setPerfil(PerfilUsuario.ALUNO);

        ItemMinhaAtividade item =
                new ItemMinhaAtividade(
                        1L,
                        "Titulo",
                        10L,
                        "MED",
                        "Disc",
                        "Prof",
                        20L,
                        "Caso",
                        "UTI",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        20,
                        50L,
                        StatusSubmissao.EM_ANDAMENTO,
                        null,
                        100,
                        null,
                        null);

        when(useCase.listarMinhasAtividades(aluno)).thenReturn(List.of(item));

        ResponseEntity<List<MinhaAtividadeItemDTO>> resp =
                controlador.listarMinhasAtividades(aluno);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve iniciar ou continuar submissao")
    void deveIniciarOuContinuar() {
        Usuario aluno = new Usuario();
        when(useCase.iniciarOuContinuar(1L, aluno)).thenReturn(criarSubmissaoMock(10L));

        ResponseEntity<SubmissaoDTO> resp = controlador.iniciarOuContinuar(1L, aluno);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve buscar submissao por ID")
    void deveBuscarPorId() {
        Usuario aluno = new Usuario();
        when(useCase.buscarPorId(10L, aluno)).thenReturn(criarSubmissaoMock(10L));

        ResponseEntity<SubmissaoDTO> resp = controlador.buscarPorId(10L, aluno);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve salvar progresso da submissao")
    void deveSalvarProgresso() {
        Usuario aluno = new Usuario();
        when(useCase.salvarOuSubmeter(eq(10L), eq(aluno), any(DadosSalvarSubmissao.class)))
                .thenReturn(criarSubmissaoMock(10L));

        SalvarSubmissaoDTO dto =
                new SalvarSubmissaoDTO(1200, true, List.of(), null, List.of(), null);
        ResponseEntity<SubmissaoDTO> resp = controlador.salvarProgresso(10L, dto, aluno);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve avaliar submissao")
    void deveAvaliar() {
        Usuario prof = new Usuario();
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        SubmissaoAtividade subAvaliada = criarSubmissaoMock(10L);
        subAvaliada.setStatus(StatusSubmissao.AVALIADA);
        subAvaliada.setNota(new BigDecimal("9.0"));

        when(useCase.avaliar(10L, prof, new BigDecimal("9.0"), "Bom")).thenReturn(subAvaliada);

        AvaliarSubmissaoDTO dto = new AvaliarSubmissaoDTO(new BigDecimal("9.0"), "Bom");
        ResponseEntity<SubmissaoDTO> resp = controlador.avaliar(10L, dto, prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve listar pendentes de correcao")
    void deveListarPendentes() {
        Usuario prof = new Usuario();
        when(useCase.listarPendentesCorrecao(prof)).thenReturn(List.of(criarSubmissaoMock(10L)));

        ResponseEntity<List<SubmissaoDTO>> resp = controlador.listarPendentes(prof);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve listar categorias de eventos adversos")
    void deveListarCategorias() {
        when(useCase.listarCategoriasAtivas())
                .thenReturn(List.of(new CategoriaEventoAdverso(1L, "IRAS", "Infecção", true)));

        ResponseEntity<List<CategoriaEventoAdverso>> resp = controlador.listarCategorias();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }
}
