package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.ModuloGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.ModuloGttSpringDataRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuloGttPersistenceAdapterTest {

    @Mock private ModuloGttSpringDataRepository repository;

    @InjectMocks private ModuloGttPersistenceAdapter adapter;

    private ModuloGttJpaEntity criarEntidade(Long id) {
        return ModuloGttJpaEntity.builder()
                .id(id)
                .codigo("CUIDADOS")
                .nome("Cuidados Gerais")
                .descricao("Desc")
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();
    }

    private ModuloGtt criarDominio(Long id) {
        return new ModuloGtt(id, "CUIDADOS", "Cuidados Gerais", "Desc", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todos os modulos")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<ModuloGtt> lista = adapter.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("CUIDADOS", lista.get(0).getCodigo());
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
        when(repository.findByCodigo("CUIDADOS")).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorCodigo("CUIDADOS").isPresent());
        assertTrue(adapter.buscarPorCodigo(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por codigo e ID diferente")
    void deveBuscarPorCodigoEIdDiferente() {
        when(repository.findByCodigoAndIdNot("CUIDADOS", 2L))
                .thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorCodigoEIdDiferente("CUIDADOS", 2L).isPresent());
        assertTrue(adapter.buscarPorCodigoEIdDiferente(null, 2L).isEmpty());
        assertTrue(adapter.buscarPorCodigoEIdDiferente("CUIDADOS", null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar modulo")
    void deveSalvar() {
        when(repository.save(any(ModuloGttJpaEntity.class))).thenReturn(criarEntidade(1L));

        ModuloGtt salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve excluir modulo")
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
        assertNull(ModuloGttPersistenceAdapter.paraDominio(null));
        assertNull(ModuloGttPersistenceAdapter.paraEntidade(null));

        ModuloGtt dom = ModuloGttPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("CUIDADOS", dom.getCodigo());

        ModuloGttJpaEntity ent = ModuloGttPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("CUIDADOS", ent.getCodigo());
    }
}
