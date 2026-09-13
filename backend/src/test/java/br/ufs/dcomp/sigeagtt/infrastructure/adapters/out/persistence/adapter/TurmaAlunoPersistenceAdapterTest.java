package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoIdJpa;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaAlunoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.TurmaJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.TurmaAlunoSpringDataRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TurmaAlunoPersistenceAdapterTest {

    @Mock private TurmaAlunoSpringDataRepository repository;

    @InjectMocks private TurmaAlunoPersistenceAdapter adapter;

    private TurmaAlunoJpaEntity criarEntidade() {
        TurmaJpaEntity t = TurmaJpaEntity.builder().id(10L).codigoDisciplina("MED001").build();
        UsuarioJpaEntity u = UsuarioJpaEntity.builder().id(20L).nomeCompleto("Aluno").build();
        return TurmaAlunoJpaEntity.builder()
                .id(new TurmaAlunoIdJpa(10L, 20L))
                .turma(t)
                .aluno(u)
                .matriculadoEm(LocalDateTime.now())
                .build();
    }

    private TurmaAluno criarDominio() {
        Turma t = new Turma();
        t.setId(10L);
        Usuario u = new Usuario();
        u.setId(20L);
        return new TurmaAluno(t, u, LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve contar alunos por turma ID")
    void deveContarAlunosPorTurmaId() {
        when(repository.countByTurmaId(10L)).thenReturn(5L);

        assertEquals(5L, adapter.contarAlunosPorTurmaId(10L));
        assertEquals(0L, adapter.contarAlunosPorTurmaId(null));
    }

    @Test
    @DisplayName("Deve listar por turma ID")
    void deveListarPorTurmaId() {
        when(repository.findByTurmaId(10L)).thenReturn(List.of(criarEntidade()));

        List<TurmaAluno> lista = adapter.listarPorTurmaId(10L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorTurmaId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se existe matricula")
    void deveVerificarExisteMatricula() {
        when(repository.existsByTurmaIdAndAlunoId(10L, 20L)).thenReturn(true);

        assertTrue(adapter.existeMatricula(10L, 20L));
        assertFalse(adapter.existeMatricula(null, 20L));
        assertFalse(adapter.existeMatricula(10L, null));
    }

    @Test
    @DisplayName("Deve salvar matricula")
    void deveSalvar() {
        when(repository.save(any(TurmaAlunoJpaEntity.class))).thenReturn(criarEntidade());

        TurmaAluno salvo = adapter.salvar(criarDominio());
        assertNotNull(salvo);
    }

    @Test
    @DisplayName("Deve desmatricular aluno")
    void deveDesmatricular() {
        doNothing().when(repository).deleteByTurmaIdAndAlunoId(10L, 20L);

        adapter.desmatricular(10L, 20L);
        verify(repository).deleteByTurmaIdAndAlunoId(10L, 20L);

        adapter.desmatricular(null, 20L);
        adapter.desmatricular(10L, null);
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(TurmaAlunoPersistenceAdapter.paraDominio(null));
        assertNull(TurmaAlunoPersistenceAdapter.paraEntidade(null));

        TurmaAluno dom = TurmaAlunoPersistenceAdapter.paraDominio(criarEntidade());
        assertNotNull(dom);

        TurmaAlunoJpaEntity ent = TurmaAlunoPersistenceAdapter.paraEntidade(criarDominio());
        assertNotNull(ent);

        // Caso sem turma e sem aluno no domínio
        TurmaAluno domVazio = new TurmaAluno();
        TurmaAlunoJpaEntity entVazio = TurmaAlunoPersistenceAdapter.paraEntidade(domVazio);
        assertNotNull(entVazio);
        assertNull(entVazio.getId().getTurmaId());
    }
}
