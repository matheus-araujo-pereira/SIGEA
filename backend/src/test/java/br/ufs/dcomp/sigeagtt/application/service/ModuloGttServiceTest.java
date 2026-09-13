package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.ports.output.ModuloGttRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuloGttServiceTest {

    @Mock private ModuloGttRepositoryPort repositorio;

    @InjectMocks private ModuloGttService service;

    @Test
    @DisplayName("Deve listar todos os modulos")
    void deveListarTodos() {
        when(repositorio.listarTodos()).thenReturn(List.of(new ModuloGtt()));
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @DisplayName("Deve buscar modulo por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        ModuloGtt m = new ModuloGtt();
        m.setId(1L);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));

        assertEquals(1L, service.buscarPorId(1L).getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar por ID inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar modulo com sucesso")
    void deveCadastrarComSucesso() {
        when(repositorio.buscarPorCodigo("MED")).thenReturn(Optional.empty());
        when(repositorio.salvar(any(ModuloGtt.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuloGtt m = service.cadastrar("med", "Medicamentoso", "Eventos com medicamentos");
        assertEquals("MED", m.getCodigo());
        assertEquals("Medicamentoso", m.getNome());
        assertEquals("Eventos com medicamentos", m.getDescricao());
        assertTrue(m.getAtivo());
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar modulo com codigo duplicado")
    void deveLancarExcecaoAoCadastrarCodigoDuplicado() {
        when(repositorio.buscarPorCodigo("MED")).thenReturn(Optional.of(new ModuloGtt()));

        assertThrows(
                ConflitoDadosException.class,
                () -> service.cadastrar("MED", "Medicamentos", "Desc"));
    }

    @Test
    @DisplayName("Deve editar modulo com sucesso")
    void deveEditarComSucesso() {
        ModuloGtt m = new ModuloGtt(1L, "ANTIGO", "Nome Antigo", "Desc Antiga", true, null);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        when(repositorio.buscarPorCodigoEIdDiferente("NOVO", 1L)).thenReturn(Optional.empty());
        when(repositorio.salvar(any(ModuloGtt.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuloGtt editado = service.editar(1L, "novo", "Nome Novo", "Desc Nova");
        assertEquals("NOVO", editado.getCodigo());
        assertEquals("Nome Novo", editado.getNome());
        assertEquals("Desc Nova", editado.getDescricao());
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar modulo com codigo em uso por outro")
    void deveLancarExcecaoAoEditarCodigoEmUso() {
        ModuloGtt m = new ModuloGtt(1L, "ANTIGO", "Nome Antigo", "Desc Antiga", true, null);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        when(repositorio.buscarPorCodigoEIdDiferente("NOVO", 1L))
                .thenReturn(Optional.of(new ModuloGtt()));

        assertThrows(
                ConflitoDadosException.class,
                () -> service.editar(1L, "NOVO", "Nome Novo", "Desc Nova"));
    }

    @Test
    @DisplayName("Deve excluir modulo com sucesso")
    void deveExcluirComSucesso() {
        ModuloGtt m = new ModuloGtt();
        m.setId(1L);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        doNothing().when(repositorio).excluir(1L);

        assertDoesNotThrow(() -> service.excluir(1L));
        verify(repositorio).excluir(1L);
    }

    @Test
    @DisplayName("Deve alternar status do modulo")
    void deveAlternarStatus() {
        ModuloGtt m = new ModuloGtt();
        m.setId(1L);
        m.setAtivo(true);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        when(repositorio.salvar(any(ModuloGtt.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuloGtt mAlt = service.alternarStatus(1L);
        assertFalse(mAlt.getAtivo());

        // Alternar quando inativo
        m.setAtivo(false);
        ModuloGtt mAlt2 = service.alternarStatus(1L);
        assertTrue(mAlt2.getAtivo());
    }

    @Test
    @DisplayName("Deve cadastrar e editar com descricao nula")
    void deveTratarDescricaoNula() {
        when(repositorio.buscarPorCodigo("MED")).thenReturn(Optional.empty());
        when(repositorio.salvar(any(ModuloGtt.class))).thenAnswer(inv -> inv.getArgument(0));

        ModuloGtt m = service.cadastrar("MED", "Medicamentos", null);
        assertNull(m.getDescricao());

        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        when(repositorio.buscarPorCodigoEIdDiferente("MED", 1L)).thenReturn(Optional.empty());
        ModuloGtt mEdit = service.editar(1L, "MED", "Medicamentos Atualizados", null);
        assertNull(mEdit.getDescricao());
    }

    @Test
    @DisplayName("Deve falhar com codigo ou nome nulos")
    void deveFalharComCamposNulos() {
        when(repositorio.buscarPorCodigo("")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar(null, "Nome", "Desc"));

        when(repositorio.buscarPorCodigo("COD")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar("COD", null, "Desc"));

        ModuloGtt m = new ModuloGtt(1L, "C", "N", "D", true, null);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(m));
        when(repositorio.buscarPorCodigoEIdDiferente("", 1L)).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(1L, null, "Nome", "Desc"));

        when(repositorio.buscarPorCodigoEIdDiferente("COD", 1L)).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(1L, "COD", null, "Desc"));
    }
}
