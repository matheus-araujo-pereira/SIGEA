package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CategoriaEventoAdversoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.CategoriaEventoAdversoSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaEventoAdversoPersistenceAdapterTest {

    @Mock private CategoriaEventoAdversoSpringDataRepository repository;

    @InjectMocks private CategoriaEventoAdversoPersistenceAdapter adapter;

    private CategoriaEventoAdversoJpaEntity criarEntidade(Long id) {
        return CategoriaEventoAdversoJpaEntity.builder()
                .id(id)
                .nome("IRAS")
                .definicaoOperacional("Definicao")
                .ativa(true)
                .build();
    }

    private CategoriaEventoAdverso criarDominio(Long id) {
        return new CategoriaEventoAdverso(id, "IRAS", "Definicao", true);
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void deveListarTodas() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<CategoriaEventoAdverso> lista = adapter.listarTodas();
        assertEquals(1, lista.size());
        assertEquals("IRAS", lista.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar categoria")
    void deveSalvar() {
        when(repository.save(any(CategoriaEventoAdversoJpaEntity.class)))
                .thenReturn(criarEntidade(1L));

        CategoriaEventoAdverso salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(CategoriaEventoAdversoPersistenceAdapter.paraDominio(null));
        assertNull(CategoriaEventoAdversoPersistenceAdapter.paraEntidade(null));

        CategoriaEventoAdverso dom =
                CategoriaEventoAdversoPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("IRAS", dom.getNome());

        CategoriaEventoAdversoJpaEntity ent =
                CategoriaEventoAdversoPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("IRAS", ent.getNome());
    }
}
