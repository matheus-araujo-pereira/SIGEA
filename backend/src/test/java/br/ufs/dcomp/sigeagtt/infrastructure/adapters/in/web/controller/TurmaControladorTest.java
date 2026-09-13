package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.TurmaUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.input.TurmaUseCase.ItemTurmaComTotal;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.TurmaRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.TurmaRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UsuarioRespostaDTO;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TurmaControladorTest {

    @Mock private TurmaUseCase turmaUseCase;

    @InjectMocks private TurmaControlador controlador;

    private Usuario admin;
    private Usuario professor;
    private Usuario outroProfessor;

    @BeforeEach
    void setUp() {
        admin = new Usuario();
        admin.setId(1L);
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);

        professor = new Usuario();
        professor.setId(2L);
        professor.setPerfil(PerfilUsuario.PROFESSOR);

        outroProfessor = new Usuario();
        outroProfessor.setId(3L);
        outroProfessor.setPerfil(PerfilUsuario.PROFESSOR);
    }

    private ItemTurmaComTotal criarItemMock(Long id, Usuario prof) {
        Turma t = new Turma();
        t.setId(id);
        t.setCodigoDisciplina("MED001");
        t.setProfessorResponsavel(prof);
        return new ItemTurmaComTotal(t, 10L);
    }

    @Test
    @DisplayName("Deve listar turmas como admin")
    void deveListarTurmasComoAdmin() {
        when(turmaUseCase.listar(1L)).thenReturn(List.of(criarItemMock(10L, professor)));

        ResponseEntity<List<TurmaRespostaDTO>> resp = controlador.listar(1L, admin);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve listar turmas filtrando pelo ID do professor quando usuário for professor")
    void deveListarTurmasComoProfessor() {
        when(turmaUseCase.listar(2L)).thenReturn(List.of(criarItemMock(10L, professor)));

        ResponseEntity<List<TurmaRespostaDTO>> resp = controlador.listar(null, professor);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
        verify(turmaUseCase).listar(2L);
    }

    @Test
    @DisplayName("Deve buscar turma por ID com sucesso")
    void deveBuscarPorId() {
        when(turmaUseCase.buscarPorId(10L)).thenReturn(criarItemMock(10L, professor));

        ResponseEntity<TurmaRespostaDTO> resp = controlador.buscarPorId(10L, professor);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());

        ResponseEntity<TurmaRespostaDTO> respAdmin = controlador.buscarPorId(10L, admin);
        assertEquals(HttpStatus.OK, respAdmin.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao buscar turma de outro professor")
    void deveFalharBuscarTurmaDeOutroProfessor() {
        when(turmaUseCase.buscarPorId(10L)).thenReturn(criarItemMock(10L, outroProfessor));

        assertThrows(AccessDeniedException.class, () -> controlador.buscarPorId(10L, professor));
    }

    @Test
    @DisplayName("Deve cadastrar turma com status 201 Created quando for admin")
    void deveCadastrarTurma() {
        when(turmaUseCase.cadastrar(2L, "MED001", "2026.1", "2026/1"))
                .thenReturn(criarItemMock(10L, professor));

        TurmaRequisicaoDTO dto = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
        ResponseEntity<TurmaRespostaDTO> resp = controlador.cadastrar(dto, admin);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(10L, resp.getBody().id());
    }

    @Test
    @DisplayName("Deve negar cadastro de turma se não for admin")
    void deveNegarCadastroSeNaoAdmin() {
        TurmaRequisicaoDTO dto = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
        assertThrows(AccessDeniedException.class, () -> controlador.cadastrar(dto, professor));
    }

    @Test
    @DisplayName("Deve editar turma quando for admin")
    void deveEditarTurma() {
        when(turmaUseCase.editar(10L, 2L, "MED001", "2026.1", "2026/1"))
                .thenReturn(criarItemMock(10L, professor));

        TurmaRequisicaoDTO dto = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
        ResponseEntity<TurmaRespostaDTO> resp = controlador.editar(10L, dto, admin);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve negar edição de turma se não for admin")
    void deveNegarEdicaoSeNaoAdmin() {
        TurmaRequisicaoDTO dto = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
        assertThrows(AccessDeniedException.class, () -> controlador.editar(10L, dto, professor));
    }

    @Test
    @DisplayName("Deve excluir turma com status 200 OK quando for admin")
    void deveExcluirTurma() {
        doNothing().when(turmaUseCase).excluir(10L);

        ResponseEntity<Map<String, String>> resp = controlador.excluir(10L, admin);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(turmaUseCase).excluir(10L);
    }

    @Test
    @DisplayName("Deve negar exclusão de turma se não for admin")
    void deveNegarExclusaoSeNaoAdmin() {
        assertThrows(AccessDeniedException.class, () -> controlador.excluir(10L, professor));
    }

    @Test
    @DisplayName("Deve alternar status da turma quando for admin")
    void deveAlternarStatus() {
        when(turmaUseCase.alternarStatus(10L)).thenReturn(criarItemMock(10L, professor));

        ResponseEntity<TurmaRespostaDTO> resp = controlador.alternarStatus(10L, admin);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Deve negar alternar status se não for admin")
    void deveNegarAlternarStatusSeNaoAdmin() {
        assertThrows(AccessDeniedException.class, () -> controlador.alternarStatus(10L, professor));
    }

    @Test
    @DisplayName("Deve listar alunos da turma")
    void deveListarAlunos() {
        Usuario aluno = new Usuario();
        aluno.setId(20L);
        when(turmaUseCase.listarAlunosDaTurma(10L)).thenReturn(List.of(aluno));

        ResponseEntity<List<UsuarioRespostaDTO>> resp = controlador.listarAlunos(10L, admin);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve permitir docente responsável listar alunos")
    void devePermitirDocenteResponsavelListarAlunos() {
        Usuario aluno = new Usuario();
        aluno.setId(20L);
        when(turmaUseCase.buscarPorId(10L)).thenReturn(criarItemMock(10L, professor));
        when(turmaUseCase.listarAlunosDaTurma(10L)).thenReturn(List.of(aluno));

        ResponseEntity<List<UsuarioRespostaDTO>> resp = controlador.listarAlunos(10L, professor);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("Deve negar acesso aos alunos de turma que não pertence ao professor")
    void deveNegarAcessoAAlunosDeOutroProfessor() {
        when(turmaUseCase.buscarPorId(10L)).thenReturn(criarItemMock(10L, outroProfessor));

        assertThrows(AccessDeniedException.class, () -> controlador.listarAlunos(10L, professor));
    }

    @Test
    @DisplayName("Deve matricular aluno com status 201 Created quando for admin")
    void deveMatricularAluno() {
        doNothing().when(turmaUseCase).matricularAluno(10L, 20L);

        ResponseEntity<Map<String, String>> resp = controlador.matricularAluno(10L, 20L, admin);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        verify(turmaUseCase).matricularAluno(10L, 20L);
    }

    @Test
    @DisplayName("Deve negar matrícula de aluno se não for admin")
    void deveNegarMatriculaSeNaoAdmin() {
        assertThrows(
                AccessDeniedException.class,
                () -> controlador.matricularAluno(10L, 20L, professor));
    }

    @Test
    @DisplayName("Deve desmatricular aluno com status 200 OK quando for admin")
    void deveDesmatricularAluno() {
        doNothing().when(turmaUseCase).desmatricularAluno(10L, 20L);

        ResponseEntity<Map<String, String>> resp = controlador.desmatricularAluno(10L, 20L, admin);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(turmaUseCase).desmatricularAluno(10L, 20L);
    }

    @Test
    @DisplayName("Deve negar desmatrícula se não for admin")
    void deveNegarDesmatriculaSeNaoAdmin() {
        assertThrows(
                AccessDeniedException.class,
                () -> controlador.desmatricularAluno(10L, 20L, professor));
    }

    @Test
    @DisplayName(
            "Deve permitir operações quando usuarioLogado for nulo (permissão delegada ao security filter)")
    void devePermitirOperacoesComUsuarioLogadoNulo() {
        when(turmaUseCase.listar(null)).thenReturn(List.of(criarItemMock(10L, professor)));
        ResponseEntity<List<TurmaRespostaDTO>> respListar = controlador.listar(null, null);
        assertEquals(HttpStatus.OK, respListar.getStatusCode());

        when(turmaUseCase.buscarPorId(10L)).thenReturn(criarItemMock(10L, professor));
        ResponseEntity<TurmaRespostaDTO> respBuscar = controlador.buscarPorId(10L, null);
        assertEquals(HttpStatus.OK, respBuscar.getStatusCode());

        TurmaRequisicaoDTO dto = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
        when(turmaUseCase.cadastrar(2L, "MED001", "2026.1", "2026/1"))
                .thenReturn(criarItemMock(10L, professor));
        ResponseEntity<TurmaRespostaDTO> respCad = controlador.cadastrar(dto, null);
        assertEquals(HttpStatus.CREATED, respCad.getStatusCode());

        when(turmaUseCase.editar(10L, 2L, "MED001", "2026.1", "2026/1"))
                .thenReturn(criarItemMock(10L, professor));
        ResponseEntity<TurmaRespostaDTO> respEd = controlador.editar(10L, dto, null);
        assertEquals(HttpStatus.OK, respEd.getStatusCode());

        doNothing().when(turmaUseCase).excluir(10L);
        ResponseEntity<Map<String, String>> respExc = controlador.excluir(10L, null);
        assertEquals(HttpStatus.OK, respExc.getStatusCode());

        when(turmaUseCase.alternarStatus(10L)).thenReturn(criarItemMock(10L, professor));
        ResponseEntity<TurmaRespostaDTO> respAlt = controlador.alternarStatus(10L, null);
        assertEquals(HttpStatus.OK, respAlt.getStatusCode());

        when(turmaUseCase.listarAlunosDaTurma(10L)).thenReturn(List.of());
        ResponseEntity<List<UsuarioRespostaDTO>> respAlunos = controlador.listarAlunos(10L, null);
        assertEquals(HttpStatus.OK, respAlunos.getStatusCode());

        doNothing().when(turmaUseCase).matricularAluno(10L, 20L);
        ResponseEntity<Map<String, String>> respMat = controlador.matricularAluno(10L, 20L, null);
        assertEquals(HttpStatus.CREATED, respMat.getStatusCode());

        doNothing().when(turmaUseCase).desmatricularAluno(10L, 20L);
        ResponseEntity<Map<String, String>> respDesmat =
                controlador.desmatricularAluno(10L, 20L, null);
        assertEquals(HttpStatus.OK, respDesmat.getStatusCode());
    }

    @Test
    @DisplayName(
            "Deve permitir docente acessar turma e alunos quando professorResponsavel for nulo na turma")
    void devePermitirAcessoQuandoProfessorResponsavelForNulo() {
        ItemTurmaComTotal turmaSemProf = criarItemMock(10L, null);
        when(turmaUseCase.buscarPorId(10L)).thenReturn(turmaSemProf);
        when(turmaUseCase.listarAlunosDaTurma(10L)).thenReturn(List.of());

        ResponseEntity<TurmaRespostaDTO> respBuscar = controlador.buscarPorId(10L, professor);
        assertEquals(HttpStatus.OK, respBuscar.getStatusCode());

        ResponseEntity<List<UsuarioRespostaDTO>> respAlunos =
                controlador.listarAlunos(10L, professor);
        assertEquals(HttpStatus.OK, respAlunos.getStatusCode());
    }
}
