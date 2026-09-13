package br.ufs.dcomp.sigeagtt.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.ports.output.GatilhoGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.ModuloGttRepositoryPort;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GatilhoGttServiceTest {

    @Mock private GatilhoGttRepositoryPort gatilhoRepositorio;
    @Mock private ModuloGttRepositoryPort moduloRepositorio;

    @InjectMocks private GatilhoGttService service;

    @Test
    @DisplayName("Deve listar gatilhos com ordenacao natural por modulo e codigo")
    void deveListarGatilhosComOrdenacaoNatural() {
        ModuloGtt modC = new ModuloGtt();
        modC.setCodigo("CUIDADOS");
        ModuloGtt modM = new ModuloGtt();
        modM.setCodigo("MEDICAMENTOSO");

        GatilhoGtt gC10 = new GatilhoGtt(1L, "C10", modC, "C10 desc", null, true);
        GatilhoGtt gC2 = new GatilhoGtt(2L, "C2", modC, "C2 desc", null, true);
        GatilhoGtt gC1 = new GatilhoGtt(3L, "C1", modC, "C1 desc", null, true);
        GatilhoGtt gM1 = new GatilhoGtt(4L, "M1", modM, "M1 desc", null, true);
        GatilhoGtt gSemModulo = new GatilhoGtt(5L, "X1", null, "X1 desc", null, true);
        GatilhoGtt gLetras = new GatilhoGtt(6L, "OUTRO", modM, "Outro desc", null, true);

        when(gatilhoRepositorio.listarTodos())
                .thenReturn(List.of(gC10, gC2, gC1, gM1, gSemModulo, gLetras));

        List<GatilhoGtt> ordenados = service.listar(null);

        assertEquals(6, ordenados.size());
        // gSemModulo tem mod "" então vem primeiro
        assertEquals("X1", ordenados.get(0).getCodigo());
        // modC: C1, C2, C10
        assertEquals("C1", ordenados.get(1).getCodigo());
        assertEquals("C2", ordenados.get(2).getCodigo());
        assertEquals("C10", ordenados.get(3).getCodigo());

        // listar por modulo
        when(gatilhoRepositorio.listarPorModuloId(1L)).thenReturn(List.of(gC10, gC1));
        List<GatilhoGtt> ordMod = service.listar(1L);
        assertEquals(2, ordMod.size());
        assertEquals("C1", ordMod.get(0).getCodigo());
        assertEquals("C10", ordMod.get(1).getCodigo());

        // testar lista nula ou vazia
        when(gatilhoRepositorio.listarTodos()).thenReturn(Collections.emptyList());
        assertTrue(service.listar(null).isEmpty());
    }

    @Test
    @DisplayName("Deve cobrir ramos de comparacao de codigos na ordenacao natural")
    void deveCobrirRamosComparacaoCodigos() {
        ModuloGtt mod = new ModuloGtt();
        mod.setCodigo("MOD");

        GatilhoGtt g1 = new GatilhoGtt(1L, null, mod, "desc", null, true);
        GatilhoGtt g2 = new GatilhoGtt(2L, "A1", mod, "desc", null, true);
        GatilhoGtt g3 = new GatilhoGtt(3L, "B1", mod, "desc", null, true);
        GatilhoGtt g4 = new GatilhoGtt(4L, "A2", mod, "desc", null, true);
        GatilhoGtt g5 = new GatilhoGtt(5L, null, mod, "desc", null, true);

        when(gatilhoRepositorio.listarTodos()).thenReturn(List.of(g1, g2, g3, g4, g5));
        List<GatilhoGtt> res = service.listar(null);
        assertNotNull(res);
        assertEquals(5, res.size());
    }

    @Test
    @DisplayName("Deve buscar gatilho por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        GatilhoGtt g = new GatilhoGtt();
        g.setId(10L);
        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(g));

        assertEquals(10L, service.buscarPorId(10L).getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar por ID inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(gatilhoRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar gatilho com sucesso")
    void deveCadastrarComSucesso() {
        ModuloGtt mod = new ModuloGtt();
        mod.setId(1L);

        when(gatilhoRepositorio.buscarPorCodigo("C1")).thenReturn(Optional.empty());
        when(moduloRepositorio.buscarPorId(1L)).thenReturn(Optional.of(mod));
        when(gatilhoRepositorio.salvar(any(GatilhoGtt.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GatilhoGtt g = service.cadastrar(1L, "c1", "Parada Cardíaca", "Manobras");
        assertEquals("C1", g.getCodigo());
        assertEquals("Parada Cardíaca", g.getDescricao());
        assertEquals("Manobras", g.getLimiarReferencia());
        assertEquals(mod, g.getModulo());
        assertTrue(g.getAtivo());
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar gatilho com codigo duplicado")
    void deveLancarExcecaoAoCadastrarCodigoDuplicado() {
        when(gatilhoRepositorio.buscarPorCodigo("C1")).thenReturn(Optional.of(new GatilhoGtt()));

        assertThrows(
                ConflitoDadosException.class, () -> service.cadastrar(1L, "C1", "Desc", "Limiar"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar gatilho com modulo inexistente")
    void deveLancarExcecaoAoCadastrarModuloInexistente() {
        when(gatilhoRepositorio.buscarPorCodigo("C1")).thenReturn(Optional.empty());
        when(moduloRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.cadastrar(99L, "C1", "Desc", "Limiar"));
    }

    @Test
    @DisplayName("Deve editar gatilho com sucesso")
    void deveEditarComSucesso() {
        ModuloGtt modAntigo = new ModuloGtt();
        modAntigo.setId(1L);
        GatilhoGtt existente = new GatilhoGtt(10L, "C1", modAntigo, "Desc", "Limiar", true);

        ModuloGtt modNovo = new ModuloGtt();
        modNovo.setId(2L);

        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(existente));
        when(gatilhoRepositorio.buscarPorCodigoEIdDiferente("C2", 10L))
                .thenReturn(Optional.empty());
        when(moduloRepositorio.buscarPorId(2L)).thenReturn(Optional.of(modNovo));
        when(gatilhoRepositorio.salvar(any(GatilhoGtt.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GatilhoGtt editado = service.editar(10L, 2L, "c2", "Nova desc", "Novo limiar");
        assertEquals("C2", editado.getCodigo());
        assertEquals(modNovo, editado.getModulo());
    }

    @Test
    @DisplayName(
            "Deve lancar excecao ao editar gatilho com codigo em uso por outro ou modulo inexistente")
    void deveLancarExcecaoAoEditarInvalido() {
        GatilhoGtt g = new GatilhoGtt();
        g.setId(10L);
        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(g));
        when(gatilhoRepositorio.buscarPorCodigoEIdDiferente("C2", 10L))
                .thenReturn(Optional.of(new GatilhoGtt()));

        assertThrows(
                ConflitoDadosException.class,
                () -> service.editar(10L, 2L, "C2", "Desc", "Limiar"));

        when(gatilhoRepositorio.buscarPorCodigoEIdDiferente("C2", 10L))
                .thenReturn(Optional.empty());
        when(moduloRepositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(10L, 99L, "C2", "Desc", "Limiar"));
    }

    @Test
    @DisplayName("Deve excluir gatilho com sucesso")
    void deveExcluirComSucesso() {
        GatilhoGtt g = new GatilhoGtt();
        g.setId(10L);
        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(g));
        doNothing().when(gatilhoRepositorio).excluir(10L);

        assertDoesNotThrow(() -> service.excluir(10L));
        verify(gatilhoRepositorio).excluir(10L);
    }

    @Test
    @DisplayName("Deve alternar status do gatilho")
    void deveAlternarStatus() {
        GatilhoGtt g = new GatilhoGtt();
        g.setId(10L);
        g.setAtivo(true);
        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(g));
        when(gatilhoRepositorio.salvar(any(GatilhoGtt.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GatilhoGtt gAlt = service.alternarStatus(10L);
        assertFalse(gAlt.getAtivo());

        // Alternar quando inativo
        g.setAtivo(false);
        GatilhoGtt gAlt2 = service.alternarStatus(10L);
        assertTrue(gAlt2.getAtivo());
    }

    @Test
    @DisplayName("Deve cobrir ramos de lista nula e codigos especiais na ordenacao natural")
    void deveCobrirRamosEspeciaisOrdenacao() {
        when(gatilhoRepositorio.listarTodos()).thenReturn(null);
        assertNull(service.listar(null));

        ModuloGtt mod = new ModuloGtt();
        mod.setCodigo(null);
        ModuloGtt mod2 = new ModuloGtt();
        mod2.setCodigo("MED");

        GatilhoGtt gSemDigito1 = new GatilhoGtt(1L, "ALPHA", mod, "d", null, true);
        GatilhoGtt gSemDigito2 = new GatilhoGtt(2L, "BETA", mod, "d", null, true);
        GatilhoGtt gOverflow1 =
                new GatilhoGtt(3L, "C999999999999999999999999", mod2, "d", null, true);
        GatilhoGtt gOverflow2 =
                new GatilhoGtt(4L, "C888888888888888888888888", mod2, "d", null, true);
        GatilhoGtt gComDigito = new GatilhoGtt(5L, "M1", mod2, "d", null, true);
        GatilhoGtt gSemDigitoMesmoPref = new GatilhoGtt(6L, "M", mod2, "d", null, true);
        GatilhoGtt gSemMod = new GatilhoGtt(7L, "M2", null, "d", null, true);
        GatilhoGtt gModComCodigo = new GatilhoGtt(8L, "Z1", mod2, "d", null, true);
        GatilhoGtt gModSemCodigo = new GatilhoGtt(9L, "Z2", mod, "d", null, true);

        // Testar ordem M1 antes de M para testar digits1 não vazio e digits2 vazio
        when(gatilhoRepositorio.listarTodos())
                .thenReturn(
                        List.of(
                                gModComCodigo,
                                gModSemCodigo,
                                gComDigito,
                                gSemDigitoMesmoPref,
                                gSemDigito1,
                                gSemDigito2,
                                gOverflow1,
                                gOverflow2,
                                gSemMod));
        List<GatilhoGtt> res = service.listar(null);
        assertEquals(9, res.size());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar ou editar com campos obrigatorios nulos")
    void deveFalharCadastrarOuEditarCamposNulos() {
        ModuloGtt mod = new ModuloGtt();
        mod.setId(1L);
        when(moduloRepositorio.buscarPorId(1L)).thenReturn(Optional.of(mod));

        when(gatilhoRepositorio.buscarPorCodigo("")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar(1L, null, "Desc", null));

        when(gatilhoRepositorio.buscarPorCodigo("G1")).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.cadastrar(1L, "G1", null, null));

        GatilhoGtt g = new GatilhoGtt(10L, "G1", mod, "Desc", null, true);
        when(gatilhoRepositorio.buscarPorId(10L)).thenReturn(Optional.of(g));

        when(gatilhoRepositorio.buscarPorCodigoEIdDiferente("", 10L)).thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(10L, 1L, null, "Desc", null));

        when(gatilhoRepositorio.buscarPorCodigoEIdDiferente("G1", 10L))
                .thenReturn(Optional.empty());
        assertThrows(
                br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException.class,
                () -> service.editar(10L, 1L, "G1", null, null));
    }

    @Test
    @DisplayName("Deve cobrir todas as ramificações de compararCodigosNaturalmente diretamente")
    void deveTestarCompararCodigosNaturalmenteDiretamente() {
        assertEquals(0, service.compararCodigosNaturalmente(null, null));
        assertTrue(service.compararCodigosNaturalmente(null, "A1") < 0);
        assertTrue(service.compararCodigosNaturalmente("A1", null) > 0);
        assertTrue(service.compararCodigosNaturalmente("A1", "B1") < 0);
        assertTrue(service.compararCodigosNaturalmente("B1", "A1") > 0);
        assertEquals(0, service.compararCodigosNaturalmente("A", "A"));
        assertTrue(service.compararCodigosNaturalmente("A", "A1") < 0);
        assertTrue(service.compararCodigosNaturalmente("A1", "A") > 0);
        assertTrue(service.compararCodigosNaturalmente("A1", "A2") < 0);
        assertTrue(service.compararCodigosNaturalmente("A2", "A1") > 0);
        assertTrue(
                service.compararCodigosNaturalmente(
                                "A99999999999999999999", "A88888888888888888888")
                        > 0);
    }
}
