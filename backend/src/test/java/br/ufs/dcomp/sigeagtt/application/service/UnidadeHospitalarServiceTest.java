package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UnidadeHospitalarRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnidadeHospitalarServiceTest {

    @Mock private UnidadeHospitalarRepositoryPort repositorio;

    @InjectMocks private UnidadeHospitalarService service;

    @Test
    @DisplayName("Deve listar todas as unidades")
    void deveListarTodas() {
        when(repositorio.listarTodas()).thenReturn(List.of(new UnidadeHospitalar()));
        assertEquals(1, service.listarTodas().size());
    }

    @Test
    @DisplayName("Deve buscar unidade por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        UnidadeHospitalar u = new UnidadeHospitalar();
        u.setId(1L);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar unidade com sucesso")
    void deveCadastrarComSucesso() {
        when(repositorio.buscarPorSigla("UTI-A")).thenReturn(Optional.empty());
        when(repositorio.salvar(any(UnidadeHospitalar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UnidadeHospitalar u = service.cadastrar("uti-a", "UTI Adulto");
        assertEquals("UTI-A", u.getSigla());
        assertEquals("UTI Adulto", u.getNome());
        assertTrue(u.getAtiva());
    }

    @Test
    @DisplayName("Deve lancar excecao quando sigla ja cadastrada")
    void deveLancarExcecaoQuandoSiglaJaExiste() {
        when(repositorio.buscarPorSigla("UTI-A")).thenReturn(Optional.of(new UnidadeHospitalar()));

        assertThrows(ConflitoDadosException.class, () -> service.cadastrar("UTI-A", "UTI Adulto"));
    }

    @Test
    @DisplayName("Deve editar unidade com sucesso")
    void deveEditarComSucesso() {
        UnidadeHospitalar existente = new UnidadeHospitalar(1L, "Nome Antigo", "SIG1", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorSiglaEIdDiferente("SIG2", 1L)).thenReturn(Optional.empty());
        when(repositorio.salvar(any(UnidadeHospitalar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UnidadeHospitalar editada = service.editar(1L, "sig2", "Nome Novo");
        assertEquals("SIG2", editada.getSigla());
        assertEquals("Nome Novo", editada.getNome());
    }

    @Test
    @DisplayName("Deve lancar excecao quando sigla ja em uso por outra unidade ao editar")
    void deveLancarExcecaoQuandoSiglaJaEmUsoAoEditar() {
        UnidadeHospitalar existente = new UnidadeHospitalar(1L, "Nome Antigo", "SIG1", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorSiglaEIdDiferente("SIG2", 1L))
                .thenReturn(Optional.of(new UnidadeHospitalar()));

        assertThrows(ConflitoDadosException.class, () -> service.editar(1L, "SIG2", "Nome Novo"));
    }

    @Test
    @DisplayName("Deve excluir unidade com sucesso")
    void deveExcluirComSucesso() {
        UnidadeHospitalar u = new UnidadeHospitalar(1L, "Nome", "SIG", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));
        doNothing().when(repositorio).excluir(1L);

        assertDoesNotThrow(() -> service.excluir(1L));
        verify(repositorio).excluir(1L);
    }

    @Test
    @DisplayName("Deve alternar status da unidade de ativa para inativa e vice-versa")
    void deveAlternarStatus() {
        UnidadeHospitalar u = new UnidadeHospitalar(1L, "Nome", "SIG", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(repositorio.salvar(any(UnidadeHospitalar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UnidadeHospitalar alterada1 = service.alternarStatus(1L);
        assertFalse(alterada1.getAtiva());

        UnidadeHospitalar alterada2 = service.alternarStatus(1L);
        assertTrue(alterada2.getAtiva());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com sigla ou nome nulos")
    void deveFalharCadastrarComCamposNulos() {
        when(repositorio.buscarPorSigla("")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar(null, "Nome"));

        when(repositorio.buscarPorSigla("SIG")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar("SIG", null));
    }

    @Test
    @DisplayName("Deve falhar ao editar com sigla ou nome nulos")
    void deveFalharEditarComCamposNulos() {
        UnidadeHospitalar existente = new UnidadeHospitalar(1L, "Nome", "SIG", true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repositorio.buscarPorSiglaEIdDiferente("", 1L)).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(1L, null, "Nome"));

        when(repositorio.buscarPorSiglaEIdDiferente("SIG2", 1L)).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(1L, "SIG2", null));
    }
}
