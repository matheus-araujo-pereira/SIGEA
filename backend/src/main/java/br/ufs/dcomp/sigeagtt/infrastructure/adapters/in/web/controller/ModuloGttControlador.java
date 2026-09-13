package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.ports.input.ModuloGttUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ModuloGttRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ModuloGttRespostaDTO;
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

/** Controlador REST para gestão dos módulos metodológicos do IHI Global Trigger Tool. */
@RestController
@RequestMapping("/api/modulos-gtt")
@Tag(
        name = "Módulos GTT",
        description =
                "Endpoints para gerenciamento dos módulos do método IHI-GTT (Cuidados Gerais, Cirúrgico, Medicamentos, UTI, Perinatal)")
@SecurityRequirement(name = "bearerAuth")
public class ModuloGttControlador {

    private final ModuloGttUseCase useCase;

    public ModuloGttControlador(ModuloGttUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Lista todos os módulos do IHI-GTT.
     *
     * @return Lista de módulos
     */
    @Operation(
            summary = "Listar módulos GTT",
            description = "Recupera todos os módulos metodológicos cadastrados no sistema.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<List<ModuloGttRespostaDTO>> listar() {
        List<ModuloGttRespostaDTO> lista =
                useCase.listarTodos().stream().map(ModuloGttRespostaDTO::deEntidade).toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca um módulo pelo ID.
     *
     * @param id Identificador do módulo
     * @return Módulo localizado
     */
    @Operation(
            summary = "Buscar módulo por ID",
            description = "Localiza um módulo GTT pelo seu ID numérico.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Módulo localizado com sucesso",
                content = @Content(schema = @Schema(implementation = ModuloGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Módulo não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ModuloGttRespostaDTO> buscarPorId(@PathVariable Long id) {
        ModuloGtt m = useCase.buscarPorId(id);
        return ResponseEntity.ok(ModuloGttRespostaDTO.deEntidade(m));
    }

    /**
     * Cadastra um novo módulo GTT.
     *
     * @param dto Dados do módulo
     * @return Módulo cadastrado
     */
    @Operation(summary = "Cadastrar módulo GTT", description = "Cria um novo módulo metodológico.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Módulo criado com sucesso",
                content = @Content(schema = @Schema(implementation = ModuloGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Apenas administradores podem cadastrar módulos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Código de módulo já existente",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ModuloGttRespostaDTO> cadastrar(
            @Valid @RequestBody ModuloGttRequisicaoDTO dto) {
        ModuloGtt m = useCase.cadastrar(dto.codigo(), dto.nome(), dto.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuloGttRespostaDTO.deEntidade(m));
    }

    /**
     * Atualiza dados de um módulo GTT.
     *
     * @param id Identificador do módulo
     * @param dto Novos dados
     * @return Módulo atualizado
     */
    @Operation(
            summary = "Editar módulo GTT",
            description = "Atualiza nome, código ou escopo de auditoria do módulo.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Módulo atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = ModuloGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Módulo não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ModuloGttRespostaDTO> editar(
            @PathVariable Long id, @Valid @RequestBody ModuloGttRequisicaoDTO dto) {
        ModuloGtt m = useCase.editar(id, dto.codigo(), dto.nome(), dto.descricao());
        return ResponseEntity.ok(ModuloGttRespostaDTO.deEntidade(m));
    }

    /**
     * Exclui um módulo GTT.
     *
     * @param id Identificador do módulo
     * @return Mensagem de confirmação
     */
    @Operation(
            summary = "Excluir módulo GTT",
            description = "Remove o módulo e todos os seus gatilhos associados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Módulo excluído com sucesso"),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Módulo não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> excluir(@PathVariable Long id) {
        useCase.excluir(id);
        return ResponseEntity.ok(
                Map.of("mensagem", "Módulo e gatilhos associados excluídos com sucesso."));
    }

    /**
     * Alterna o status do módulo.
     *
     * @param id Identificador do módulo
     * @return Módulo com status atualizado
     */
    @Operation(
            summary = "Alternar status do módulo",
            description = "Ativa ou inativa o módulo no catálogo IHI-GTT.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Status alternado com sucesso",
                content = @Content(schema = @Schema(implementation = ModuloGttRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Módulo não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/alternar-status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ModuloGttRespostaDTO> alternarStatus(@PathVariable Long id) {
        ModuloGtt m = useCase.alternarStatus(id);
        return ResponseEntity.ok(ModuloGttRespostaDTO.deEntidade(m));
    }
}
