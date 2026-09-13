package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase.DadosSalvarCasoClinico;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CasoClinicoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UnidadeHospitalarRepositoryPort;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CasoClinicoServiceTest {

    @Mock private CasoClinicoRepositoryPort casoRepositorio;
    @Mock private UnidadeHospitalarRepositoryPort unidadeRepositorio;

    @InjectMocks private CasoClinicoService service;

    private DadosSalvarCasoClinico criarComando(Long unidadeId) {
        return new DadosSalvarCasoClinico(
                unidadeId,
                "Caso Choque",
                "Desc",
                "Obj",
                "ATD100",
                60,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 10),
                9,
                "Sum",
                "Presc",
                "Exames",
                "Cir",
                "Evol");
    }

    @Test
    @DisplayName("Deve listar todos para admin e apenas proprios para professor")
    void deveListarCasos() {
        Usuario admin = new Usuario();
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        when(casoRepositorio.listarTodos()).thenReturn(List.of(new CasoClinico()));
        when(casoRepositorio.listarPorProfessorCriadorId(10L))
                .thenReturn(List.of(new CasoClinico()));

        assertEquals(1, service.listar(admin).size());
        assertEquals(1, service.listar(prof).size());
    }

    @Test
    @DisplayName("Deve buscar caso clinico por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        CasoClinico c = new CasoClinico();
        c.setId(1L);
        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar caso clinico inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(casoRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve salvar caso clinico com sucesso")
    void deveSalvarComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        UnidadeHospitalar unid = new UnidadeHospitalar();
        unid.setId(1L);

        when(unidadeRepositorio.buscarPorId(1L)).thenReturn(Optional.of(unid));
        when(casoRepositorio.salvar(any(CasoClinico.class))).thenAnswer(inv -> inv.getArgument(0));

        CasoClinico salvo = service.salvar(prof, criarComando(1L));
        assertEquals("Caso Choque", salvo.getTitulo());
        assertEquals(prof, salvo.getProfessorCriador());
        assertEquals(unid, salvo.getUnidadeHospitalar());
    }

    @Test
    @DisplayName("Deve lancar excecao ao salvar com unidade hospitalar inexistente")
    void deveLancarExcecaoAoSalvarUnidadeInexistente() {
        Usuario prof = new Usuario();
        when(unidadeRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class, () -> service.salvar(prof, criarComando(99L)));
    }

    @Test
    @DisplayName("Deve atualizar caso clinico com sucesso pelo criador ou admin")
    void deveAtualizarComSucesso() {
        Usuario prof = new Usuario();
        prof.setId(10L);
        prof.setPerfil(PerfilUsuario.PROFESSOR);

        CasoClinico c = new CasoClinico();
        c.setId(1L);
        c.setProfessorCriador(prof);

        UnidadeHospitalar unid = new UnidadeHospitalar();
        unid.setId(2L);

        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));
        when(unidadeRepositorio.buscarPorId(2L)).thenReturn(Optional.of(unid));
        when(casoRepositorio.salvar(any(CasoClinico.class))).thenAnswer(inv -> inv.getArgument(0));

        CasoClinico atualizado = service.atualizar(1L, prof, criarComando(2L));
        assertEquals(unid, atualizado.getUnidadeHospitalar());

        // Testar com Administrador
        Usuario admin = new Usuario();
        admin.setId(999L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.atualizar(1L, admin, criarComando(2L)));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar por outro professor")
    void deveLancarExcecaoAoAtualizarPorOutroProfessor() {
        Usuario autor = new Usuario();
        autor.setId(10L);

        Usuario outroProf = new Usuario();
        outroProf.setId(20L);
        outroProf.setPerfil(PerfilUsuario.PROFESSOR);

        CasoClinico c = new CasoClinico();
        c.setId(1L);
        c.setProfessorCriador(autor);

        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));

        assertThrows(
                AcessoProibidoException.class,
                () -> service.atualizar(1L, outroProf, criarComando(1L)));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar quando unidade hospitalar nao encontrada")
    void deveLancarExcecaoAoAtualizarUnidadeInexistente() {
        Usuario autor = new Usuario();
        autor.setId(10L);
        autor.setPerfil(PerfilUsuario.PROFESSOR);

        CasoClinico c = new CasoClinico();
        c.setId(1L);
        c.setProfessorCriador(autor);

        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));
        when(unidadeRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.atualizar(1L, autor, criarComando(99L)));
    }

    @Test
    @DisplayName("Deve excluir caso clinico com sucesso pelo autor ou admin")
    void deveExcluirComSucesso() {
        Usuario autor = new Usuario();
        autor.setId(10L);
        autor.setPerfil(PerfilUsuario.PROFESSOR);

        CasoClinico c = new CasoClinico();
        c.setId(1L);
        c.setProfessorCriador(autor);

        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));
        doNothing().when(casoRepositorio).excluir(1L);

        assertDoesNotThrow(() -> service.excluir(1L, autor));

        // Testar com Administrador
        Usuario admin = new Usuario();
        admin.setId(99L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> service.excluir(1L, admin));
    }

    @Test
    @DisplayName("Deve lancar excecao ao excluir por outro professor")
    void deveLancarExcecaoAoExcluirPorOutroProfessor() {
        Usuario autor = new Usuario();
        autor.setId(10L);

        Usuario outro = new Usuario();
        outro.setId(20L);
        outro.setPerfil(PerfilUsuario.PROFESSOR);

        CasoClinico c = new CasoClinico();
        c.setId(1L);
        c.setProfessorCriador(autor);

        when(casoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(c));

        assertThrows(AcessoProibidoException.class, () -> service.excluir(1L, outro));
    }
}
