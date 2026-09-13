package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UnidadeHospitalarJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.UnidadeHospitalarSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnidadeHospitalarPersistenceAdapterTest {

    @Mock private UnidadeHospitalarSpringDataRepository repository;

    @InjectMocks private UnidadeHospitalarPersistenceAdapter adapter;

    private UnidadeHospitalarJpaEntity criarEntidade(Long id) {
        return UnidadeHospitalarJpaEntity.builder()
                .id(id)
                .nome("UTI Adulto")
                .sigla("UTI-A")
                .ativa(true)
                .build();
    }

    private UnidadeHospitalar criarDominio(Long id) {
        return new UnidadeHospitalar(id, "UTI Adulto", "UTI-A", true);
    }

    @Test
    @DisplayName("Deve listar todas as unidades")
    void deveListarTodas() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<UnidadeHospitalar> lista = adapter.listarTodas();
        assertEquals(1, lista.size());
        assertEquals("UTI-A", lista.get(0).getSigla());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por sigla existente e nula")
    void deveBuscarPorSigla() {
        when(repository.findBySigla("UTI-A")).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorSigla("UTI-A").isPresent());
        assertTrue(adapter.buscarPorSigla(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por sigla e ID diferente")
    void deveBuscarPorSiglaEIdDiferente() {
        when(repository.findBySiglaAndIdNot("UTI-A", 2L))
                .thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorSiglaEIdDiferente("UTI-A", 2L).isPresent());
        assertTrue(adapter.buscarPorSiglaEIdDiferente(null, 2L).isEmpty());
        assertTrue(adapter.buscarPorSiglaEIdDiferente("UTI-A", null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar unidade")
    void deveSalvar() {
        when(repository.save(any(UnidadeHospitalarJpaEntity.class))).thenReturn(criarEntidade(1L));

        UnidadeHospitalar salva = adapter.salvar(criarDominio(1L));
        assertNotNull(salva);
        assertEquals(1L, salva.getId());
    }

    @Test
    @DisplayName("Deve excluir unidade")
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
        assertNull(UnidadeHospitalarPersistenceAdapter.paraDominio(null));
        assertNull(UnidadeHospitalarPersistenceAdapter.paraEntidade(null));

        UnidadeHospitalar dom = UnidadeHospitalarPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("UTI-A", dom.getSigla());

        UnidadeHospitalarJpaEntity ent =
                UnidadeHospitalarPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("UTI-A", ent.getSigla());
    }
}
