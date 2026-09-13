package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoIshikawaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoIshikawaSpringDataRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissaoIshikawaPersistenceAdapterTest {

    @Mock private SubmissaoIshikawaSpringDataRepository repository;

    @InjectMocks private SubmissaoIshikawaPersistenceAdapter adapter;

    private SubmissaoIshikawaJpaEntity criarEntidade(Long id) {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        return SubmissaoIshikawaJpaEntity.builder()
                .id(id)
                .submissao(sub)
                .efeitoPrincipal("Efeito")
                .metodo("Met")
                .maoDeObra("Mao")
                .material("Mat")
                .medida("Med")
                .meioAmbiente("Amb")
                .maquina("Maq")
                .build();
    }

    private SubmissaoIshikawa criarDominio(Long id) {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.setId(100L);
        return new SubmissaoIshikawa(id, sub, "Efeito", "Met", "Mao", "Mat", "Med", "Amb", "Maq");
    }

    @Test
    @DisplayName("Deve buscar por submissao ID")
    void deveBuscarPorSubmissaoId() {
        when(repository.findBySubmissaoId(100L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorSubmissaoId(100L).isPresent());
        assertTrue(adapter.buscarPorSubmissaoId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar diagrama de Ishikawa")
    void deveSalvar() {
        when(repository.save(any(SubmissaoIshikawaJpaEntity.class))).thenReturn(criarEntidade(1L));

        SubmissaoIshikawa salvo = adapter.salvar(criarDominio(1L));
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
        assertNull(SubmissaoIshikawaPersistenceAdapter.paraDominio(null));
        assertNull(SubmissaoIshikawaPersistenceAdapter.paraEntidade(null));

        SubmissaoIshikawa dom = SubmissaoIshikawaPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals(1L, dom.getId());
        assertEquals(100L, dom.getSubmissao().getId());

        // Sem submissao
        SubmissaoIshikawaJpaEntity entSemSub = criarEntidade(1L);
        entSemSub.setSubmissao(null);
        SubmissaoIshikawa domSemSub = SubmissaoIshikawaPersistenceAdapter.paraDominio(entSemSub);
        assertNotNull(domSemSub);
        assertNull(domSemSub.getSubmissao());

        SubmissaoIshikawaJpaEntity ent =
                SubmissaoIshikawaPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals(1L, ent.getId());
        assertEquals(100L, ent.getSubmissao().getId());

        SubmissaoIshikawa domSemSubObj = criarDominio(1L);
        domSemSubObj.setSubmissao(null);
        SubmissaoIshikawaJpaEntity entSemSubObj =
                SubmissaoIshikawaPersistenceAdapter.paraEntidade(domSemSubObj);
        assertNotNull(entSemSubObj);
        assertNull(entSemSubObj.getSubmissao());
    }
}
