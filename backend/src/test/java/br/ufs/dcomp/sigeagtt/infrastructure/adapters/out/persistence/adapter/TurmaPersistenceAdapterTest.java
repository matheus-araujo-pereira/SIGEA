package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.TurmaSpringDataRepository;
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
class TurmaPersistenceAdapterTest {

    @Mock private TurmaSpringDataRepository repository;

    @InjectMocks private TurmaPersistenceAdapter adapter;

    private TurmaJpaEntity criarEntidade(Long id) {
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(2L).nomeCompleto("Prof").build();
        return TurmaJpaEntity.builder()
                .id(id)
                .professorResponsavel(prof)
                .codigoDisciplina("MED001")
                .nomeDisciplina("Segurança")
                .periodoLetivo("2026.1")
                .anoSemestre("2026/1")
                .ativa(true)
                .criadaEm(LocalDateTime.now())
                .build();
    }

    private Turma criarDominio(Long id) {
        Usuario prof = new Usuario();
        prof.setId(2L);
        return new Turma(
                id, prof, "MED001", "Segurança", "2026.1", "2026/1", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todas as turmas")
    void deveListarTodas() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<Turma> lista = adapter.listarTodas();
        assertEquals(1, lista.size());
        assertEquals("MED001", lista.get(0).getCodigoDisciplina());
    }

    @Test
    @DisplayName("Deve listar por professor ID")
    void deveListarPorProfessorId() {
        when(repository.findByProfessorResponsavelId(2L)).thenReturn(List.of(criarEntidade(1L)));

        List<Turma> lista = adapter.listarPorProfessorId(2L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorProfessorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar turma")
    void deveSalvar() {
        when(repository.save(any(TurmaJpaEntity.class))).thenReturn(criarEntidade(1L));

        Turma salva = adapter.salvar(criarDominio(1L));
        assertNotNull(salva);
        assertEquals(1L, salva.getId());
    }

    @Test
    @DisplayName("Deve excluir turma")
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
        assertNull(TurmaPersistenceAdapter.paraDominio(null));
        assertNull(TurmaPersistenceAdapter.paraEntidade(null));

        Turma dom = TurmaPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("MED001", dom.getCodigoDisciplina());

        TurmaJpaEntity ent = TurmaPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("MED001", ent.getCodigoDisciplina());
    }
}
