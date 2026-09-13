package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.GatilhoGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.ModuloGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.GatilhoGttSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GatilhoGttPersistenceAdapterTest {

    @Mock private GatilhoGttSpringDataRepository repository;

    @InjectMocks private GatilhoGttPersistenceAdapter adapter;

    private GatilhoGttJpaEntity criarEntidade(Long id) {
        ModuloGttJpaEntity mod = ModuloGttJpaEntity.builder().id(5L).codigo("MED").build();
        return GatilhoGttJpaEntity.builder()
                .id(id)
                .codigo("M1")
                .modulo(mod)
                .descricao("Vitamina K")
                .limiarReferencia("INR > 5")
                .ativo(true)
                .build();
    }

    private GatilhoGtt criarDominio(Long id) {
        ModuloGtt mod = new ModuloGtt();
        mod.setId(5L);
        return new GatilhoGtt(id, "M1", mod, "Vitamina K", "INR > 5", true);
    }

    @Test
    @DisplayName("Deve listar todos os gatilhos")
    void deveListarTodos() {
        when(repository.findAllOrderByCodigo()).thenReturn(List.of(criarEntidade(1L)));

        List<GatilhoGtt> lista = adapter.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("M1", lista.get(0).getCodigo());
    }

    @Test
    @DisplayName("Deve listar por modulo ID")
    void deveListarPorModuloId() {
        when(repository.findAllByModuloId(5L)).thenReturn(List.of(criarEntidade(1L)));

        List<GatilhoGtt> lista = adapter.listarPorModuloId(5L);
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por codigo existente e nulo")
    void deveBuscarPorCodigo() {
        when(repository.findByCodigo("M1")).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorCodigo("M1").isPresent());
        assertTrue(adapter.buscarPorCodigo(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por codigo e ID diferente")
    void deveBuscarPorCodigoEIdDiferente() {
        when(repository.findByCodigoAndIdNot("M1", 2L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorCodigoEIdDiferente("M1", 2L).isPresent());
        assertTrue(adapter.buscarPorCodigoEIdDiferente(null, 2L).isEmpty());
        assertTrue(adapter.buscarPorCodigoEIdDiferente("M1", null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar gatilho")
    void deveSalvar() {
        when(repository.save(any(GatilhoGttJpaEntity.class))).thenReturn(criarEntidade(1L));

        GatilhoGtt salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve excluir gatilho")
    void deveExcluir() {
        doNothing().when(repository).deleteById(1L);

        adapter.excluir(1L);
        verify(repository).deleteById(1L);

        adapter.excluir(null);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(GatilhoGttPersistenceAdapter.paraDominio(null));
        assertNull(GatilhoGttPersistenceAdapter.paraEntidade(null));

        GatilhoGtt dom = GatilhoGttPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("M1", dom.getCodigo());

        GatilhoGttJpaEntity ent = GatilhoGttPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("M1", ent.getCodigo());
    }
}
