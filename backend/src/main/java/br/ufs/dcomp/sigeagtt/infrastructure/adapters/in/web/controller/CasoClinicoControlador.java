package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.CasoClinicoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.SalvarCasoClinicoDTO;
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

/** Controlador REST para gestão de casos clínicos e prontuários simulados. */
@RestController
@RequestMapping("/api/casos-clinicos")
@Tag(
        name = "Casos Clínicos",
        description =
                "Endpoints para gerenciamento de prontuários simulados completos e histórias clínicas")
@SecurityRequirement(name = "bearerAuth")
public class CasoClinicoControlador {

    private final CasoClinicoUseCase casoUseCase;

    public CasoClinicoControlador(CasoClinicoUseCase casoUseCase) {
        this.casoUseCase = casoUseCase;
    }

    /**
     * Lista casos clínicos visíveis para o usuário autenticado.
     *
     * @param usuarioLogado Usuário em sessão
     * @return Lista de casos clínicos
     */
    @Operation(
            summary = "Listar casos clínicos",
            description = "Recupera prontuários simulados visíveis para o perfil autenticado.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<List<CasoClinicoDTO>> listar(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        List<CasoClinicoDTO> lista =
                casoUseCase.listar(usuarioLogado).stream().map(CasoClinicoDTO::deEntidade).toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca um caso clínico pelo ID.
     *
     * @param id Identificador do caso
     * @return Prontuário simulado completo
     */
    @Operation(
            summary = "Buscar caso clínico por ID",
            description =
                    "Recupera o prontuário simulado completo incluindo evoluções e prescrições.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Caso clínico localizado",
                content = @Content(schema = @Schema(implementation = CasoClinicoDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Caso clínico não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<CasoClinicoDTO> buscarPorId(@PathVariable Long id) {
        CasoClinico caso = casoUseCase.buscarPorId(id);
        return ResponseEntity.ok(CasoClinicoDTO.deEntidade(caso));
    }

    /**
     * Cadastra um novo caso clínico simulado.
     *
     * @param dto Dados do prontuário
     * @param usuarioLogado Docente criador
     * @return Caso criado
     */
    @Operation(
            summary = "Cadastrar caso clínico",
            description =
                    "Cria um prontuário simulado completo para uso em atividades pedagógicas.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Caso clínico criado com sucesso",
                content = @Content(schema = @Schema(implementation = CasoClinicoDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados do prontuário inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Apenas docentes ou administradores podem cadastrar casos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<CasoClinicoDTO> criar(
            @Valid @RequestBody SalvarCasoClinicoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        CasoClinico criado = casoUseCase.salvar(usuarioLogado, dto.paraComando());
        return ResponseEntity.status(HttpStatus.CREATED).body(CasoClinicoDTO.deEntidade(criado));
    }

    /**
     * Atualiza um caso clínico existente.
     *
     * @param id Identificador do caso
     * @param dto Novos dados
     * @param usuarioLogado Usuário solicitante
     * @return Caso atualizado
     */
    @Operation(
            summary = "Editar caso clínico",
            description = "Atualiza prescrições, evoluções ou dados do prontuário simulado.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Caso clínico atualizado",
                content = @Content(schema = @Schema(implementation = CasoClinicoDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso negado (não é o autor nem administrador)",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Caso clínico não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<CasoClinicoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalvarCasoClinicoDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        CasoClinico atualizado = casoUseCase.atualizar(id, usuarioLogado, dto.paraComando());
        return ResponseEntity.ok(CasoClinicoDTO.deEntidade(atualizado));
    }

    /**
     * Exclui um caso clínico do sistema.
     *
     * @param id Identificador do caso
     * @param usuarioLogado Usuário solicitante
     * @return Resposta sem conteúdo
     */
    @Operation(
            summary = "Excluir caso clínico",
            description = "Remove o caso clínico se não houver atividades vinculadas.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Caso clínico excluído com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso negado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Caso não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        casoUseCase.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
