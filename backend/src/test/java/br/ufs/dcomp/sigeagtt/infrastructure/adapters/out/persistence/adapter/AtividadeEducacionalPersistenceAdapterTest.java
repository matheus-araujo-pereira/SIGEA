package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.AtividadeEducacionalJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CasoClinicoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.AtividadeEducacionalSpringDataRepository;
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
class AtividadeEducacionalPersistenceAdapterTest {

    @Mock private AtividadeEducacionalSpringDataRepository repository;

    @InjectMocks private AtividadeEducacionalPersistenceAdapter adapter;

    private AtividadeEducacionalJpaEntity criarEntidade(Long id) {
        TurmaJpaEntity t = TurmaJpaEntity.builder().id(10L).build();
        CasoClinicoJpaEntity c = CasoClinicoJpaEntity.builder().id(20L).build();
        return AtividadeEducacionalJpaEntity.builder()
                .id(id)
                .turma(t)
                .casoClinico(c)
                .titulo("Auditoria 1")
                .orientacoesPedagogicas("Ori")
                .dataInicio(LocalDateTime.now())
                .dataFim(LocalDateTime.now().plusDays(5))
                .tempoLimiteMinutos(20)
                .ativa(true)
                .criadaEm(LocalDateTime.now())
                .build();
    }

    private AtividadeEducacional criarDominio(Long id) {
        Turma t = new Turma();
        t.setId(10L);
        CasoClinico c = new CasoClinico();
        c.setId(20L);
        return new AtividadeEducacional(
                id,
                t,
                c,
                "Auditoria 1",
                "Ori",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                20,
                true,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todas as atividades")
    void deveListarTodas() {
        when(repository.findAllByOrderByCriadaEmDesc()).thenReturn(List.of(criarEntidade(1L)));

        List<AtividadeEducacional> lista = adapter.listarTodas();
        assertEquals(1, lista.size());
        assertEquals("Auditoria 1", lista.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deve listar por professor ID")
    void deveListarPorProfessorId() {
        when(repository.findByProfessorId(2L)).thenReturn(List.of(criarEntidade(1L)));

        List<AtividadeEducacional> lista = adapter.listarPorProfessorId(2L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorProfessorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve listar para aluno")
    void deveListarParaAluno() {
        when(repository.findAtividadesParaAluno(100L)).thenReturn(List.of(criarEntidade(1L)));

        List<AtividadeEducacional> lista = adapter.listarParaAluno(100L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarParaAluno(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar atividade")
    void deveSalvar() {
        when(repository.save(any(AtividadeEducacionalJpaEntity.class)))
                .thenReturn(criarEntidade(1L));

        AtividadeEducacional salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve excluir atividade")
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
        assertNull(AtividadeEducacionalPersistenceAdapter.paraDominio(null));
        assertNull(AtividadeEducacionalPersistenceAdapter.paraEntidade(null));

        AtividadeEducacional dom =
                AtividadeEducacionalPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("Auditoria 1", dom.getTitulo());

        AtividadeEducacionalJpaEntity ent =
                AtividadeEducacionalPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("Auditoria 1", ent.getTitulo());
    }
}
