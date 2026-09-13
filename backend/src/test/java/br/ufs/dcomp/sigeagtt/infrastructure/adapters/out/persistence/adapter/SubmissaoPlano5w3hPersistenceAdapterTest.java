package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoPlano5w3hJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoPlano5w3hSpringDataRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissaoPlano5w3hPersistenceAdapterTest {

    @Mock private SubmissaoPlano5w3hSpringDataRepository repository;

    @InjectMocks private SubmissaoPlano5w3hPersistenceAdapter adapter;

    private SubmissaoPlano5w3hJpaEntity criarEntidade(Long id) {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        return SubmissaoPlano5w3hJpaEntity.builder()
                .id(id)
                .submissao(sub)
                .oQue("O que")
                .porQue("Por que")
                .quem("Quem")
                .onde("Onde")
                .quando("Quando")
                .como("Como")
                .quantoCusta(new BigDecimal("100.00"))
                .comoMedir("Como medir")
                .build();
    }

    private SubmissaoPlano5w3h criarDominio(Long id) {
        SubmissaoAtividade sub = new SubmissaoAtividade();
        sub.setId(100L);
        return new SubmissaoPlano5w3h(
                id,
                sub,
                "O que",
                "Por que",
                "Quem",
                "Onde",
                "Quando",
                "Como",
                new BigDecimal("100.00"),
                "Como medir");
    }

    @Test
    @DisplayName("Deve listar por submissao ID")
    void deveListarPorSubmissaoId() {
        when(repository.findBySubmissaoId(100L)).thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoPlano5w3h> lista = adapter.listarPorSubmissaoId(100L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorSubmissaoId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar plano 5W3H")
    void deveSalvar() {
        when(repository.save(any(SubmissaoPlano5w3hJpaEntity.class))).thenReturn(criarEntidade(1L));

        SubmissaoPlano5w3h salvo = adapter.salvar(criarDominio(1L));
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
        assertNull(SubmissaoPlano5w3hPersistenceAdapter.paraDominio(null));
        assertNull(SubmissaoPlano5w3hPersistenceAdapter.paraEntidade(null));

        SubmissaoPlano5w3h dom =
                SubmissaoPlano5w3hPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals(1L, dom.getId());
        assertEquals(100L, dom.getSubmissao().getId());

        // Sem submissao
        SubmissaoPlano5w3hJpaEntity entSemSub = criarEntidade(1L);
        entSemSub.setSubmissao(null);
        SubmissaoPlano5w3h domSemSub = SubmissaoPlano5w3hPersistenceAdapter.paraDominio(entSemSub);
        assertNotNull(domSemSub);
        assertNull(domSemSub.getSubmissao());

        SubmissaoPlano5w3hJpaEntity ent =
                SubmissaoPlano5w3hPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals(1L, ent.getId());
        assertEquals(100L, ent.getSubmissao().getId());

        SubmissaoPlano5w3h domSemSubObj = criarDominio(1L);
        domSemSubObj.setSubmissao(null);
        SubmissaoPlano5w3hJpaEntity entSemSubObj =
                SubmissaoPlano5w3hPersistenceAdapter.paraEntidade(domSemSubObj);
        assertNotNull(entSemSubObj);
        assertNull(entSemSubObj.getSubmissao());
    }
}
