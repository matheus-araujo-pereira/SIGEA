package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.TurmaUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.TurmaRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.TurmaRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UsuarioRespostaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Controlador REST para gerenciamento de turmas acadêmicas e matrículas de alunos. */
@RestController
@RequestMapping("/api/turmas")
@Tag(
        name = "Turmas",
        description =
                "Endpoints para criação de turmas, controle de vigência e matrícula de discentes")
@SecurityRequirement(name = "bearerAuth")
public class TurmaControlador {

    private final TurmaUseCase turmaUseCase;

    public TurmaControlador(TurmaUseCase turmaUseCase) {
        this.turmaUseCase = turmaUseCase;
    }

    /**
     * Lista as turmas acadêmicas cadastradas com total de alunos.
     *
     * @param professorId Filtro opcional por identificador de professor
     * @param usuarioLogado Usuário em sessão
     * @return Lista de turmas
     */
    @Operation(
            summary = "Listar turmas",
            description =
                    "Retorna a relação de turmas cadastradas, filtradas automaticamente para docentes logados.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<List<TurmaRespostaDTO>> listar(
            @RequestParam(required = false) Long professorId,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        Long filtroProfId = professorId;
        if (usuarioLogado != null && usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR) {
            filtroProfId = usuarioLogado.getId();
        }
        List<TurmaRespostaDTO> lista =
                turmaUseCase.listar(filtroProfId).stream()
                        .map(item -> TurmaRespostaDTO.deEntidade(item.turma(), item.totalAlunos()))
                        .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca uma turma pelo ID com controle de autorização.
     *
     * @param id Identificador da turma
     * @param usuarioLogado Usuário autenticado
     * @return Dados da turma
     */
    @Operation(
            summary = "Buscar turma por ID",
            description =
                    "Obtém os detalhes da turma com validação de pertinência pedagógica do docente.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Turma localizada com sucesso",
                content = @Content(schema = @Schema(implementation = TurmaRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Docente não responsável pela turma",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Turma não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TurmaRespostaDTO> buscarPorId(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        TurmaUseCase.ItemTurmaComTotal item = turmaUseCase.buscarPorId(id);
        if (usuarioLogado != null && usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR) {
            if (item.turma().getProfessorResponsavel() != null
                    && !item.turma()
                            .getProfessorResponsavel()
                            .getId()
                            .equals(usuarioLogado.getId())) {
                throw new AccessDeniedException("Acesso não autorizado a esta turma.");
            }
        }
        return ResponseEntity.ok(TurmaRespostaDTO.deEntidade(item.turma(), item.totalAlunos()));
    }

    /**
     * Cadastra uma nova turma.
     *
     * @param dto Dados da nova turma
     * @param usuarioLogado Usuário autenticado
     * @return Turma cadastrada
     */
    @Operation(
            summary = "Cadastrar turma",
            description = "Cria uma nova turma vinculada a um professor responsável.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Turma criada com sucesso",
                content = @Content(schema = @Schema(implementation = TurmaRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da turma inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada (apenas administradores)",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    public ResponseEntity<TurmaRespostaDTO> cadastrar(
            @Valid @RequestBody TurmaRequisicaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem cadastrar turmas.");
        }
        TurmaUseCase.ItemTurmaComTotal item =
                turmaUseCase.cadastrar(
                        dto.professorResponsavelId(),
                        dto.codigoDisciplina(),
                        dto.periodoLetivo(),
                        dto.anoSemestre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TurmaRespostaDTO.deEntidade(item.turma(), item.totalAlunos()));
    }

    /**
     * Atualiza dados de uma turma existente.
     *
     * @param id Identificador da turma
     * @param dto Novos dados
     * @param usuarioLogado Usuário autenticado
     * @return Turma atualizada
     */
    @Operation(
            summary = "Editar turma",
            description = "Atualiza código da disciplina, período letivo ou docente responsável.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Turma atualizada com sucesso",
                content = @Content(schema = @Schema(implementation = TurmaRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Apenas administradores podem editar turmas",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Turma não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TurmaRespostaDTO> editar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequisicaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem editar turmas.");
        }
        TurmaUseCase.ItemTurmaComTotal item =
                turmaUseCase.editar(
                        id,
                        dto.professorResponsavelId(),
                        dto.codigoDisciplina(),
                        dto.periodoLetivo(),
                        dto.anoSemestre());
        return ResponseEntity.ok(TurmaRespostaDTO.deEntidade(item.turma(), item.totalAlunos()));
    }

    /**
     * Exclui uma turma do sistema.
     *
     * @param id Identificador da turma
     * @param usuarioLogado Usuário autenticado
     * @return Mensagem de confirmação
     */
    @Operation(
            summary = "Excluir turma",
            description = "Remove a turma e todos os vínculos de matrículas dependentes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turma excluída com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Turma não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> excluir(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem excluir turmas.");
        }
        turmaUseCase.excluir(id);
        return ResponseEntity.ok(
                Map.of("mensagem", "Turma e matrículas associadas excluídas com sucesso."));
    }

    /**
     * Alterna status ativo/inativo da turma.
     *
     * @param id Identificador da turma
     * @param usuarioLogado Usuário autenticado
     * @return Turma com status alternado
     */
    @Operation(
            summary = "Alternar status da turma",
            description = "Ativa ou inativa o período de vigência da turma.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Status alterado com sucesso",
                content = @Content(schema = @Schema(implementation = TurmaRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Turma não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/alternar-status")
    public ResponseEntity<TurmaRespostaDTO> alternarStatus(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException(
                    "Apenas administradores podem alterar o status da turma.");
        }
        TurmaUseCase.ItemTurmaComTotal item = turmaUseCase.alternarStatus(id);
        return ResponseEntity.ok(TurmaRespostaDTO.deEntidade(item.turma(), item.totalAlunos()));
    }

    /**
     * Lista discentes matriculados em uma turma.
     *
     * @param id Identificador da turma
     * @param usuarioLogado Usuário autenticado
     * @return Lista de alunos matriculados
     */
    @Operation(
            summary = "Listar alunos da turma",
            description = "Recupera todos os discentes matriculados na respectiva turma.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alunos listados com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Docente não responsável pela turma",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Turma não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}/alunos")
    public ResponseEntity<List<UsuarioRespostaDTO>> listarAlunos(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR) {
            TurmaUseCase.ItemTurmaComTotal turma = turmaUseCase.buscarPorId(id);
            if (turma.turma().getProfessorResponsavel() != null
                    && !turma.turma()
                            .getProfessorResponsavel()
                            .getId()
                            .equals(usuarioLogado.getId())) {
                throw new AccessDeniedException("Acesso não autorizado aos alunos desta turma.");
            }
        }
        List<UsuarioRespostaDTO> alunos =
                turmaUseCase.listarAlunosDaTurma(id).stream()
                        .map(UsuarioRespostaDTO::deEntidade)
                        .toList();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Matricula um aluno na turma.
     *
     * @param turmaId Identificador da turma
     * @param alunoId Identificador do aluno
     * @param usuarioLogado Usuário autenticado
     * @return Mensagem de sucesso
     */
    @Operation(
            summary = "Matricular aluno",
            description = "Vincula um discente à turma acadêmica informada.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aluno matriculado com sucesso"),
        @ApiResponse(
                responseCode = "400",
                description = "Usuário informado não é aluno",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Apenas administradores podem matricular alunos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Aluno já matriculado nesta turma",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Map<String, String>> matricularAluno(
            @PathVariable Long turmaId,
            @PathVariable Long alunoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException(
                    "Apenas administradores podem matricular alunos em turmas.");
        }
        turmaUseCase.matricularAluno(turmaId, alunoId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Aluno matriculado com sucesso."));
    }

    /**
     * Desmatricula um discente de uma turma.
     *
     * @param turmaId Identificador da turma
     * @param alunoId Identificador do aluno
     * @param usuarioLogado Usuário autenticado
     * @return Mensagem de sucesso
     */
    @Operation(
            summary = "Desmatricular aluno",
            description = "Remove o vínculo de matrícula do aluno na turma informada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno desmatriculado com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Apenas administradores podem desmatricular alunos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Map<String, String>> desmatricularAluno(
            @PathVariable Long turmaId,
            @PathVariable Long alunoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException(
                    "Apenas administradores podem desmatricular alunos de turmas.");
        }
        turmaUseCase.desmatricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(Map.of("mensagem", "Aluno desmatriculado com sucesso."));
    }
}
