package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPdcaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoPdcaSpringDataRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissaoPdcaPersistenceAdapterTest {

    @Mock private SubmissaoPdcaSpringDataRepository repository;

    @InjectMocks private SubmissaoPdcaPersistenceAdapter adapter;

    private SubmissaoPdcaJpaEntity criarEntidade(Long id) {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        return SubmissaoPdcaJpaEntity.builder()
                .id(id)
                .submissao(sub)
                .planejar("P")
                .fazer("D")
                .checar("C")
                .agir("A")
                .build();
    }

    private SubmissaoPdca criarDominio(Long id) {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.setId(100L);
        return new SubmissaoPdca(id, sub, "P", "D", "C", "A");
    }

    @Test
    @DisplayName("Deve buscar por submissao ID")
    void deveBuscarPorSubmissaoId() {
        when(repository.findBySubmissaoId(100L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorSubmissaoId(100L).isPresent());
        assertTrue(adapter.buscarPorSubmissaoId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar ciclo PDCA")
    void deveSalvar() {
        when(repository.save(any(SubmissaoPdcaJpaEntity.class))).thenReturn(criarEntidade(1L));

        SubmissaoPdca salvo = adapter.salvar(criarDominio(1L));
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
        assertNull(SubmissaoPdcaPersistenceAdapter.paraDominio(null));
        assertNull(SubmissaoPdcaPersistenceAdapter.paraEntidade(null));

        SubmissaoPdca dom = SubmissaoPdcaPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals(1L, dom.getId());
        assertEquals(100L, dom.getSubmissao().getId());

        // Sem submissao
        SubmissaoPdcaJpaEntity entSemSub = criarEntidade(1L);
        entSemSub.setSubmissao(null);
        SubmissaoPdca domSemSub = SubmissaoPdcaPersistenceAdapter.paraDominio(entSemSub);
        assertNotNull(domSemSub);
        assertNull(domSemSub.getSubmissao());

        SubmissaoPdcaJpaEntity ent = SubmissaoPdcaPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals(1L, ent.getId());
        assertEquals(100L, ent.getSubmissao().getId());

        SubmissaoPdca domSemSubObj = criarDominio(1L);
        domSemSubObj.setSubmissao(null);
        SubmissaoPdcaJpaEntity entSemSubObj =
                SubmissaoPdcaPersistenceAdapter.paraEntidade(domSemSubObj);
        assertNotNull(entSemSubObj);
        assertNull(entSemSubObj.getSubmissao());
    }
}
