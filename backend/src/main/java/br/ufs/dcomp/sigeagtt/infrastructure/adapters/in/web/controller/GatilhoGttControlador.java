package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.input.GatilhoGttUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.GatilhoGttRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.GatilhoGttRespostaDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Controlador REST para gestão dos gatilhos rastreadores clínicos da metodologia IHI-GTT. */
@RestController
@RequestMapping("/api/gatilhos")
@Tag(
        name = "Gatilhos GTT",
        description =
                "Endpoints para gerenciamento do catálogo de 45 gatilhos de rastreamento de eventos adversos")
@SecurityRequirement(name = "bearerAuth")
public class GatilhoGttControlador {

    private final GatilhoGttUseCase useCase;

    public GatilhoGttControlador(GatilhoGttUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Lista os gatilhos clínicos cadastrados.
     *
     * @param moduloId Filtro opcional por identificador de módulo
     * @return Lista de gatilhos
     */
    @Operation(
            summary = "Listar gatilhos GTT",
            description =
                    "Recupera os gatilhos clínicos com ordenação natural por código, opcionalmente filtrados por módulo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de gatilhos retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<GatilhoGttRespostaDTO>> listar(
            @RequestParam(required = false) Long moduloId) {
        List<GatilhoGttRespostaDTO> lista =
                useCase.listar(moduloId).stream().map(GatilhoGttRespostaDTO::deEntidade).toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca um gatilho pelo ID.
     *
     * @param id Identificador do gatilho
     * @return Gatilho localizado
     */
    @Operation(
            summary = "Buscar gatilho por ID",
            description = "Localiza um gatilho clínico pelo respectivo ID.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Gatilho localizado com sucesso",
                content = @Content(schema = @Schema(implementation = GatilhoGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Gatilho não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<GatilhoGttRespostaDTO> buscarPorId(@PathVariable Long id) {
        GatilhoGtt g = useCase.buscarPorId(id);
        return ResponseEntity.ok(GatilhoGttRespostaDTO.deEntidade(g));
    }

    /**
     * Cadastra um novo gatilho clínico.
     *
     * @param dto Dados do gatilho
     * @return Gatilho cadastrado
     */
    @Operation(
            summary = "Cadastrar gatilho GTT",
            description = "Registra um novo gatilho clínico associado a um módulo GTT.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Gatilho criado com sucesso",
                content = @Content(schema = @Schema(implementation = GatilhoGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da requisição inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Código de gatilho já cadastrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GatilhoGttRespostaDTO> cadastrar(
            @Valid @RequestBody GatilhoGttRequisicaoDTO dto) {
        GatilhoGtt g =
                useCase.cadastrar(
                        dto.moduloId(), dto.codigo(), dto.descricao(), dto.limiarReferencia());
        return ResponseEntity.status(HttpStatus.CREATED).body(GatilhoGttRespostaDTO.deEntidade(g));
    }

    /**
     * Atualiza dados de um gatilho existente.
     *
     * @param id Identificador do gatilho
     * @param dto Novos dados
     * @return Gatilho atualizado
     */
    @Operation(
            summary = "Editar gatilho GTT",
            description = "Atualiza o código, módulo pai ou regras clínicas do gatilho.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Gatilho atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = GatilhoGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Gatilho não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GatilhoGttRespostaDTO> editar(
            @PathVariable Long id, @Valid @RequestBody GatilhoGttRequisicaoDTO dto) {
        GatilhoGtt g =
                useCase.editar(
                        id, dto.moduloId(), dto.codigo(), dto.descricao(), dto.limiarReferencia());
        return ResponseEntity.ok(GatilhoGttRespostaDTO.deEntidade(g));
    }

    /**
     * Exclui um gatilho do sistema.
     *
     * @param id Identificador do gatilho
     * @return Mensagem de confirmação
     */
    @Operation(
            summary = "Excluir gatilho GTT",
            description = "Remove um gatilho clínico do catálogo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gatilho excluído com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Gatilho não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> excluir(@PathVariable Long id) {
        useCase.excluir(id);
        return ResponseEntity.ok(Map.of("mensagem", "Gatilho excluído com sucesso."));
    }

    /**
     * Alterna o status ativo/inativo do gatilho.
     *
     * @param id Identificador do gatilho
     * @return Gatilho com status atualizado
     */
    @Operation(
            summary = "Alternar status do gatilho",
            description = "Ativa ou inativa o rastreamento deste gatilho clínico.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Status alternado com sucesso",
                content = @Content(schema = @Schema(implementation = GatilhoGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Permissão negada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Gatilho não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/alternar-status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GatilhoGttRespostaDTO> alternarStatus(@PathVariable Long id) {
        GatilhoGtt g = useCase.alternarStatus(id);
        return ResponseEntity.ok(GatilhoGttRespostaDTO.deEntidade(g));
    }
}
