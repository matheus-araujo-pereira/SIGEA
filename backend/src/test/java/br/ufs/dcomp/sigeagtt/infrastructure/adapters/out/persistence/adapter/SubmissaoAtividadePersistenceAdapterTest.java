package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.AtividadeEducacionalJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.SubmissaoAtividadeJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.SubmissaoAtividadeSpringDataRepository;
import java.math.BigDecimal;
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
class SubmissaoAtividadePersistenceAdapterTest {

    @Mock private SubmissaoAtividadeSpringDataRepository repository;

    @InjectMocks private SubmissaoAtividadePersistenceAdapter adapter;

    private SubmissaoAtividadeJpaEntity criarEntidade(Long id) {
        AtividadeEducacionalJpaEntity ativ =
                AtividadeEducacionalJpaEntity.builder().id(10L).build();
        UsuarioJpaEntity aluno = UsuarioJpaEntity.builder().id(20L).build();
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(30L).build();
        return SubmissaoAtividadeJpaEntity.builder()
                .id(id)
                .atividade(ativ)
                .aluno(aluno)
                .professorCorretor(prof)
                .status(StatusSubmissao.AVALIADA)
                .tempoGastoSegundos(600)
                .dataInicio(LocalDateTime.now())
                .dataSubmissao(LocalDateTime.now())
                .nota(new BigDecimal("9.00"))
                .parecerDocente("Parecer")
                .dataAvaliacao(LocalDateTime.now())
                .build();
    }

    private SubmissaoAtividade criarDominio(Long id) {
        AtividadeEducacional ativ = new AtividadeEducacional();
        ativ.setId(10L);
        Usuario aluno = new Usuario();
        aluno.setId(20L);
        Usuario prof = new Usuario();
        prof.setId(30L);

        SubmissaoAtividade s = new SubmissaoAtividade(ativ, aluno);
        s.setId(id);
        s.setProfessorCorretor(prof);
        s.setStatus(StatusSubmissao.AVALIADA);
        s.setTempoGastoSegundos(600);
        s.setDataInicio(LocalDateTime.now());
        s.setDataSubmissao(LocalDateTime.now());
        s.setNota(new BigDecimal("9.00"));
        s.setParecerDocente("Parecer");
        s.setDataAvaliacao(LocalDateTime.now());
        return s;
    }

    @Test
    @DisplayName("Deve listar todas as submissoes")
    void deveListarTodas() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoAtividade> lista = adapter.listarTodas();
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve listar por atividade ID")
    void deveListarPorAtividadeId() {
        when(repository.findByAtividadeId(10L)).thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoAtividade> lista = adapter.listarPorAtividadeId(10L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorAtividadeId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve listar por aluno ID")
    void deveListarPorAlunoId() {
        when(repository.findByAlunoIdOrderByDataInicioDesc(20L))
                .thenReturn(List.of(criarEntidade(1L)));

        List<SubmissaoAtividade> lista = adapter.listarPorAlunoId(20L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorAlunoId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por atividade e aluno")
    void deveBuscarPorAtividadeEAluno() {
        when(repository.findByAtividadeIdAndAlunoId(10L, 20L))
                .thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorAtividadeEAluno(10L, 20L).isPresent());
        assertTrue(adapter.buscarPorAtividadeEAluno(null, 20L).isEmpty());
        assertTrue(adapter.buscarPorAtividadeEAluno(10L, null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar submissao")
    void deveSalvar() {
        when(repository.save(any(SubmissaoAtividadeJpaEntity.class))).thenReturn(criarEntidade(1L));

        SubmissaoAtividade salva = adapter.salvar(criarDominio(1L));
        assertNotNull(salva);
        assertEquals(1L, salva.getId());
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(SubmissaoAtividadePersistenceAdapter.paraDominio(null));
        assertNull(SubmissaoAtividadePersistenceAdapter.paraEntidade(null));

        SubmissaoAtividade dom =
                SubmissaoAtividadePersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals(1L, dom.getId());

        SubmissaoAtividadeJpaEntity ent =
                SubmissaoAtividadePersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals(1L, ent.getId());
    }
}
