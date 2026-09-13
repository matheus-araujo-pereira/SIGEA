package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CategoriaEventoAdversoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.GatilhoGttJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoGatilhoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoGatilhoSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissaoGatilhoPersistenceAdapterTest {

    @Mock private SubmissaoGatilhoSpringDataRepository repository;

    @InjectMocks private SubmissaoGatilhoPersistenceAdapter adapter;

    private SubmissaoGatilhoJpaEntity criarEntidade(Long id) {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        GatilhoGttJpaEntity gat = GatilhoGttJpaEntity.builder().id(10L).codigo("M1").build();
        CategoriaEventoAdversoJpaEntity cat =
                CategoriaEventoAdversoJpaEntity.builder().id(20L).nome("IRAS").build();
        return SubmissaoGatilhoJpaEntity.builder()
                .id(id)
                .submissao(sub)
                .gatilho(gat)
                .categoriaEventoAdverso(cat)
                .confirmouDano(true)
                .justificativaDano("Dano")
                .danoPresenteAdmissao(false)
                .gravidade(GravidadeNccMerp.CATEGORIA_E)
                .build();
    }

    private SubmissaoGatilho criarDominio(Long id) {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.setId(100L);
        GatilhoGtt gat = new GatilhoGtt();
        gat.setId(10L);
        CategoriaEventoAdverso cat = new CategoriaEventoAdverso();
        cat.setId(20L);
        return new SubmissaoGatilho(
                id, sub, gat, cat, true, "Dano", false, GravidadeNccMerp.CATEGORIA_E);
    }

    @Test
    @DisplayName("Deve listar por submissao ID")
    void deveListarPorSubmissaoId() {
        when(repository.findBySubmissaoId(100L)).thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoGatilho> lista = adapter.listarPorSubmissaoId(100L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorSubmissaoId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve listar todos")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoGatilho> lista = adapter.listarTodos();
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
    @DisplayName("Deve salvar achado de gatilho")
    void deveSalvar() {
        when(repository.save(any(SubmissaoGatilhoJpaEntity.class))).thenReturn(criarEntidade(1L));

        SubmissaoGatilho salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve excluir por submissao ID")
    void deveExcluirPorSubmissaoId() {
        doNothing().when(repository).deleteBySubmissaoId(100L);

        adapter.excluirPorSubmissaoId(100L);
        verify(repository).deleteBySubmissaoId(100L);

        adapter.excluirPorSubmissaoId(null);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(SubmissaoGatilhoPersistenceAdapter.paraDominio(null));
        assertNull(SubmissaoGatilhoPersistenceAdapter.paraEntidade(null));

        SubmissaoGatilho dom = SubmissaoGatilhoPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals(1L, dom.getId());
        assertEquals(100L, dom.getSubmissao().getId());

        // Entidade sem submissao
        SubmissaoGatilhoJpaEntity entSemSub = criarEntidade(1L);
        entSemSub.setSubmissao(null);
        SubmissaoGatilho domSemSub = SubmissaoGatilhoPersistenceAdapter.paraDominio(entSemSub);
        assertNotNull(domSemSub);
        assertNull(domSemSub.getSubmissao());

        SubmissaoGatilhoJpaEntity ent =
                SubmissaoGatilhoPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals(1L, ent.getId());
        assertEquals(100L, ent.getSubmissao().getId());

        // Dominio sem submissao
        SubmissaoGatilho domSemSubObj = criarDominio(1L);
        domSemSubObj.setSubmissao(null);
        SubmissaoGatilhoJpaEntity entSemSubObj =
                SubmissaoGatilhoPersistenceAdapter.paraEntidade(domSemSubObj);
        assertNotNull(entSemSubObj);
        assertNull(entSemSubObj.getSubmissao());
    }
}
