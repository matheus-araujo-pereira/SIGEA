package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.UsuarioSpringDataRepository;
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
class UsuarioPersistenceAdapterTest {

    @Mock private UsuarioSpringDataRepository repository;

    @InjectMocks private UsuarioPersistenceAdapter adapter;

    private UsuarioJpaEntity criarEntidade(Long id) {
        return UsuarioJpaEntity.builder()
                .id(id)
                .nomeCompleto("Usuario Teste")
                .email("teste@ufs.br")
                .senha("hash")
                .primeiroAcesso(true)
                .matriculaSigaa("123456")
                .perfil(PerfilUsuario.ALUNO)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();
    }

    private Usuario criarDominio(Long id) {
        return new Usuario(
                id,
                "Usuario Teste",
                "teste@ufs.br",
                "hash",
                true,
                "123456",
                PerfilUsuario.ALUNO,
                true,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todos os usuarios")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(criarEntidade(1L)));

        List<Usuario> lista = adapter.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("Usuario Teste", lista.get(0).getNomeCompleto());
    }

    @Test
    @DisplayName("Deve buscar usuario por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar usuario por email existente e nulo")
    void deveBuscarPorEmail() {
        when(repository.findByEmail("teste@ufs.br")).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorEmail("teste@ufs.br").isPresent());
        assertTrue(adapter.buscarPorEmail(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por matricula SIGAA existente e nula")
    void deveBuscarPorMatricula() {
        when(repository.findByMatriculaSigaa("123456")).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorMatriculaSigaa("123456").isPresent());
        assertTrue(adapter.buscarPorMatriculaSigaa(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por email e ID diferente")
    void deveBuscarPorEmailEIdDiferente() {
        when(repository.findByEmailAndIdNot("teste@ufs.br", 2L))
                .thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorEmailEIdDiferente("teste@ufs.br", 2L).isPresent());
        assertTrue(adapter.buscarPorEmailEIdDiferente(null, 2L).isEmpty());
        assertTrue(adapter.buscarPorEmailEIdDiferente("teste@ufs.br", null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por matricula e ID diferente")
    void deveBuscarPorMatriculaEIdDiferente() {
        when(repository.findByMatriculaSigaaAndIdNot("123456", 2L))
                .thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorMatriculaEIdDiferente("123456", 2L).isPresent());
        assertTrue(adapter.buscarPorMatriculaEIdDiferente(null, 2L).isEmpty());
        assertTrue(adapter.buscarPorMatriculaEIdDiferente("123456", null).isEmpty());
    }

    @Test
    @DisplayName("Deve contar usuarios por perfil e ativo")
    void deveContarPorPerfilEAtivo() {
        when(repository.countByPerfilAndAtivoTrue(PerfilUsuario.ALUNO)).thenReturn(5L);

        assertEquals(5L, adapter.contarPorPerfilEAtivo(PerfilUsuario.ALUNO));
    }

    @Test
    @DisplayName("Deve salvar usuario")
    void deveSalvar() {
        UsuarioJpaEntity entity = criarEntidade(1L);
        when(repository.save(any(UsuarioJpaEntity.class))).thenReturn(entity);

        Usuario salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(UsuarioPersistenceAdapter.paraDominio(null));
        assertNull(UsuarioPersistenceAdapter.paraEntidade(null));

        Usuario dom = UsuarioPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("teste@ufs.br", dom.getEmail());

        UsuarioJpaEntity ent = UsuarioPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("teste@ufs.br", ent.getEmail());
    }
}
