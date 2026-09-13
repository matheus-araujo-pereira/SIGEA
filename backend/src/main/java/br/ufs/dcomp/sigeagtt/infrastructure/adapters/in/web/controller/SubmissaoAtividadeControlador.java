package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para o fluxo completo de resolução, auditoria, submissão e avaliação docente.
 */
@RestController
@RequestMapping("/api/submissoes")
@Tag(
        name = "Submissões",
        description =
                "Endpoints para execução da auditoria retrospectiva IHI-GTT, Diagrama de Ishikawa, Plano 5W3H, PDCA e avaliação formativa")
@SecurityRequirement(name = "bearerAuth")
public class SubmissaoAtividadeControlador {

    private final SubmissaoAtividadeUseCase submissaoUseCase;

    public SubmissaoAtividadeControlador(SubmissaoAtividadeUseCase submissaoUseCase) {
        this.submissaoUseCase = submissaoUseCase;
    }

    /**
     * Lista atividades pedagógicas disponíveis para o discente autenticado com status da respectiva
     * submissão.
     *
     * @param usuarioLogado Discente logado
     * @return Lista de atividades com status da submissão
     */
    @Operation(
            summary = "Listar minhas atividades",
            description =
                    "Retorna todas as atividades educacionais abertas para as turmas nas quais o discente está matriculado.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping("/minhas")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMINISTRADOR')")
    public ResponseEntity<List<MinhaAtividadeItemDTO>> listarMinhasAtividades(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        List<MinhaAtividadeItemDTO> lista =
                submissaoUseCase.listarMinhasAtividades(usuarioLogado).stream()
                        .map(MinhaAtividadeItemDTO::deItemMinhaAtividade)
                        .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Inicia uma nova submissão em rascunho ou recupera uma submissão em andamento.
     *
     * @param atividadeId Identificador da atividade
     * @param usuarioLogado Discente autenticado
     * @return Submissão iniciada ou em andamento
     */
    @Operation(
            summary = "Iniciar ou continuar submissão",
            description =
                    "Cria o rascunho da submissão com cronometragem ou retoma resolução já iniciada.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Submissão aberta com sucesso",
                content = @Content(schema = @Schema(implementation = SubmissaoDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Atividade não iniciada ou encerrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Discente não matriculado na turma",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Atividade não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping("/iniciar/{atividadeId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMINISTRADOR')")
    public ResponseEntity<SubmissaoDTO> iniciarOuContinuar(
            @PathVariable Long atividadeId, @AuthenticationPrincipal Usuario usuarioLogado) {
        SubmissaoAtividade sub = submissaoUseCase.iniciarOuContinuar(atividadeId, usuarioLogado);
        return ResponseEntity.ok(SubmissaoDTO.deEntidade(sub));
    }

    /**
     * Busca uma submissão pelo ID com validação de perfil (aluno autor, docente responsável ou
     * admin).
     *
     * @param id Identificador da submissão
     * @param usuarioLogado Usuário autenticado
     * @return Submissão completa com todos os 4 módulos
     */
    @Operation(
            summary = "Buscar submissão por ID",
            description =
                    "Recupera todos os artefatos da auditoria clínica (gatilhos achados, Ishikawa, 5W3H e PDCA).")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Submissão localizada",
                content = @Content(schema = @Schema(implementation = SubmissaoDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso negado (não é o autor nem o docente da turma)",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Submissão não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMINISTRADOR')")
    public ResponseEntity<SubmissaoDTO> buscarPorId(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        SubmissaoAtividade sub = submissaoUseCase.buscarPorId(id, usuarioLogado);
        return ResponseEntity.ok(SubmissaoDTO.deEntidade(sub));
    }

    /**
     * Salva o progresso intermediário (rascunho) ou entrega a resolução definitiva.
     *
     * @param id Identificador da submissão
     * @param dto Dados da resolução clínica
     * @param usuarioLogado Discente autor
     * @return Submissão salva
     */
    @Operation(
            summary = "Salvar progresso ou submeter resolução",
            description =
                    "Persiste gatilhos clínicos identificados, diagrama de Ishikawa, matriz 5W3H e ciclo PDCA. Se finalizar=true, homologa entrega definitiva.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Progresso salvo com sucesso",
                content = @Content(schema = @Schema(implementation = SubmissaoDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da resolução inválidos ou prazo expirado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Usuário não é o autor da submissão",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Submissão não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}/progresso")
    @PreAuthorize("hasAnyRole('ALUNO', 'ADMINISTRADOR')")
    public ResponseEntity<SubmissaoDTO> salvarProgresso(
            @PathVariable Long id,
            @RequestBody SalvarSubmissaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        SubmissaoAtividade sub =
                submissaoUseCase.salvarOuSubmeter(id, usuarioLogado, dto.paraComando());
        return ResponseEntity.ok(SubmissaoDTO.deEntidade(sub));
    }

    /**
     * Registra a correção e homologação docente atribuindo nota e parecer formativo.
     *
     * @param id Identificador da submissão
     * @param dto Nota e parecer pedagógico
     * @param usuarioLogado Docente avaliador
     * @return Submissão avaliada
     */
    @Operation(
            summary = "Avaliar submissão",
            description =
                    "Homologa a correção pedagógica com nota de 0.00 a 10.00 e parecer formativo detalhado.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Submissão avaliada com sucesso",
                content = @Content(schema = @Schema(implementation = SubmissaoDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Nota ou parecer fora dos parâmetros permitidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Docente não responsável pela turma",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Submissão não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping("/{id}/avaliar")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMINISTRADOR')")
    public ResponseEntity<SubmissaoDTO> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliarSubmissaoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        SubmissaoAtividade sub =
                submissaoUseCase.avaliar(id, usuarioLogado, dto.nota(), dto.parecerDocente());
        return ResponseEntity.ok(SubmissaoDTO.deEntidade(sub));
    }

    /**
     * Lista todas as submissões entregues pendentes de correção docente.
     *
     * @param usuarioLogado Docente ou administrador
     * @return Lista de submissões pendentes
     */
    @Operation(
            summary = "Listar submissões pendentes de correção",
            description =
                    "Recupera todas as resoluções enviadas pelos discentes que aguardam avaliação do docente.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<SubmissaoDTO>> listarPendentes(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        List<SubmissaoDTO> lista =
                submissaoUseCase.listarPendentesCorrecao(usuarioLogado).stream()
                        .map(SubmissaoDTO::deEntidade)
                        .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Lista as categorias de eventos adversos ativas para preenchimento de achados.
     *
     * @return Lista de categorias de eventos adversos
     */
    @Operation(
            summary = "Listar categorias de eventos adversos",
            description =
                    "Retorna a taxonomia de categorias ativas para classificação dos danos identificados.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping("/categorias-ea")
    public ResponseEntity<List<CategoriaEventoAdverso>> listarCategorias() {
        return ResponseEntity.ok(submissaoUseCase.listarCategoriasAtivas());
    }
}
