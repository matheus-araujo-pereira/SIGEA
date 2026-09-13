package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Controlador REST para gestão de atividades pedagógicas de auditoria clínica. */
@RestController
@RequestMapping("/api/atividades-educacionais")
@Tag(
        name = "Atividades Educacionais",
        description =
                "Endpoints para agendamento de auditorias clínicas retrospectivas, monitoramento de prazos e painel de correção")
@SecurityRequirement(name = "bearerAuth")
public class AtividadeEducacionalControlador {

    private final AtividadeEducacionalUseCase useCase;

    public AtividadeEducacionalControlador(AtividadeEducacionalUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Lista atividades visíveis para o usuário em sessão.
     *
     * @param usuarioLogado Usuário autenticado
     * @return Lista de atividades com métricas
     */
    @Operation(
            summary = "Listar atividades educacionais",
            description =
                    "Recupera todas as atividades ativas das turmas sob responsabilidade do docente ou vinculadas ao perfil.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<List<AtividadeEducacionalDTO>> listar(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        List<AtividadeEducacionalDTO> lista =
                useCase.listar(usuarioLogado).stream()
                        .map(
                                item ->
                                        AtividadeEducacionalDTO.deEntidade(
                                                item.atividade(),
                                                item.totalAlunos(),
                                                item.totalSubmissoes(),
                                                item.totalAvaliadas()))
                        .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca uma atividade pelo ID.
     *
     * @param id Identificador da atividade
     * @return Resumo da atividade
     */
    @Operation(
            summary = "Buscar atividade por ID",
            description = "Obtém detalhes da atividade com métricas consolidadas de submissões.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Atividade localizada com sucesso",
                content =
                        @Content(schema = @Schema(implementation = AtividadeEducacionalDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Atividade não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<AtividadeEducacionalDTO> buscarPorId(@PathVariable Long id) {
        AtividadeEducacionalUseCase.ItemAtividadeResumo item = useCase.buscarPorId(id);
        return ResponseEntity.ok(
                AtividadeEducacionalDTO.deEntidade(
                        item.atividade(),
                        item.totalAlunos(),
                        item.totalSubmissoes(),
                        item.totalAvaliadas()));
    }

    /**
     * Recupera o painel completo de acompanhamento e correção docente da atividade.
     *
     * @param id Identificador da atividade
     * @param usuarioLogado Docente ou administrador
     * @return Painel consolidado com a lista de alunos e status das submissões
     */
    @Operation(
            summary = "Obter painel da atividade",
            description =
                    "Retorna métricas da turma, média de notas e a lista de alunos matriculados com o status de cada resolução.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Painel retornado com sucesso",
                content = @Content(schema = @Schema(implementation = PainelAtividadeDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido (não é o docente responsável)",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Atividade não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}/painel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<PainelAtividadeDTO> buscarPainel(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        AtividadeEducacionalUseCase.DadosPainelAtividade dados =
                useCase.buscarPainelAtividade(id, usuarioLogado);
        return ResponseEntity.ok(PainelAtividadeDTO.deDadosPainel(dados));
    }

    /**
     * Cadastra uma nova atividade educacional.
     *
     * @param dto Dados da atividade
     * @param usuarioLogado Docente criador
     * @return Atividade criada
     */
    @Operation(
            summary = "Criar atividade educacional",
            description =
                    "Associa uma turma a um prontuário simulado com período de vigência e tempo limite de auditoria.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Atividade criada com sucesso",
                content =
                        @Content(schema = @Schema(implementation = AtividadeEducacionalDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da atividade inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<AtividadeEducacionalDTO> criar(
            @Valid @RequestBody SalvarAtividadeDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        AtividadeEducacionalUseCase.ItemAtividadeResumo item =
                useCase.salvar(usuarioLogado, dto.paraComando());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        AtividadeEducacionalDTO.deEntidade(
                                item.atividade(),
                                item.totalAlunos(),
                                item.totalSubmissoes(),
                                item.totalAvaliadas()));
    }

    /**
     * Atualiza uma atividade educacional existente.
     *
     * @param id Identificador da atividade
     * @param dto Novos dados
     * @param usuarioLogado Usuário solicitante
     * @return Atividade atualizada
     */
    @Operation(
            summary = "Editar atividade educacional",
            description =
                    "Atualiza orientações pedagógicas, prazos de envio ou tempo limite da auditoria.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Atividade atualizada com sucesso",
                content =
                        @Content(schema = @Schema(implementation = AtividadeEducacionalDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso negado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Atividade não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<AtividadeEducacionalDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalvarAtividadeDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        AtividadeEducacionalUseCase.ItemAtividadeResumo item =
                useCase.atualizar(id, usuarioLogado, dto.paraComando());
        return ResponseEntity.ok(
                AtividadeEducacionalDTO.deEntidade(
                        item.atividade(),
                        item.totalAlunos(),
                        item.totalSubmissoes(),
                        item.totalAvaliadas()));
    }

    /**
     * Exclui uma atividade educacional do sistema.
     *
     * @param id Identificador da atividade
     * @param usuarioLogado Usuário solicitante
     * @return Resposta sem conteúdo
     */
    @Operation(
            summary = "Excluir atividade educacional",
            description = "Remove a atividade pedagógica e suas submissões vinculadas.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Atividade excluída com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso negado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Atividade não encontrada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        useCase.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
