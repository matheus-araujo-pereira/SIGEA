package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UnidadeHospitalarUseCase;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UnidadeHospitalarRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.UnidadeHospitalarRespostaDTO;
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

/** Controlador REST para gestão das unidades e enfermarias hospitalares. */
@RestController
@RequestMapping("/api/unidades")
@Tag(
        name = "Unidades Hospitalares",
        description = "Endpoints para gerenciamento de setores e enfermarias do hospital de ensino")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeHospitalarControlador {

    private final UnidadeHospitalarUseCase useCase;

    public UnidadeHospitalarControlador(UnidadeHospitalarUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Lista todas as unidades hospitalares cadastradas.
     *
     * @return Lista de unidades
     */
    @Operation(
            summary = "Listar unidades hospitalares",
            description = "Retorna todos os setores hospitalares ativos e inativos.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<List<UnidadeHospitalarRespostaDTO>> listar() {
        List<UnidadeHospitalarRespostaDTO> lista =
                useCase.listarTodas().stream()
                        .map(UnidadeHospitalarRespostaDTO::deEntidade)
                        .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca uma unidade hospitalar pelo ID.
     *
     * @param id Identificador da unidade
     * @return Unidade localizada
     */
    @Operation(
            summary = "Buscar unidade por ID",
            description = "Localiza uma unidade hospitalar pelo seu respectivo identificador.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Unidade localizada",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        UnidadeHospitalarRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Unidade não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeHospitalarRespostaDTO> buscarPorId(@PathVariable Long id) {
        UnidadeHospitalar u = useCase.buscarPorId(id);
        return ResponseEntity.ok(UnidadeHospitalarRespostaDTO.deEntidade(u));
    }

    /**
     * Cadastra uma nova unidade hospitalar.
     *
     * @param dto Dados da unidade
     * @return Unidade cadastrada
     */
    @Operation(
            summary = "Cadastrar unidade hospitalar",
            description = "Registra uma nova enfermaria ou clínica assistencial.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Unidade cadastrada com sucesso",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        UnidadeHospitalarRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da requisição inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Sigla da unidade já existente",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UnidadeHospitalarRespostaDTO> cadastrar(
            @Valid @RequestBody UnidadeHospitalarRequisicaoDTO dto) {
        UnidadeHospitalar u = useCase.cadastrar(dto.sigla(), dto.nome());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnidadeHospitalarRespostaDTO.deEntidade(u));
    }

    /**
     * Atualiza os dados de uma unidade hospitalar.
     *
     * @param id Identificador da unidade
     * @param dto Novos dados
     * @return Unidade atualizada
     */
    @Operation(
            summary = "Editar unidade hospitalar",
            description = "Atualiza o nome e a sigla de uma unidade existente.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Unidade atualizada com sucesso",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        UnidadeHospitalarRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Unidade não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UnidadeHospitalarRespostaDTO> editar(
            @PathVariable Long id, @Valid @RequestBody UnidadeHospitalarRequisicaoDTO dto) {
        UnidadeHospitalar u = useCase.editar(id, dto.sigla(), dto.nome());
        return ResponseEntity.ok(UnidadeHospitalarRespostaDTO.deEntidade(u));
    }

    /**
     * Exclui uma unidade hospitalar.
     *
     * @param id Identificador da unidade
     * @return Mensagem de confirmação
     */
    @Operation(
            summary = "Excluir unidade hospitalar",
            description = "Remove uma unidade hospitalar sem vínculos históricos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unidade excluída com sucesso"),
        @ApiResponse(
                responseCode = "404",
                description = "Unidade não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> excluir(@PathVariable Long id) {
        useCase.excluir(id);
        return ResponseEntity.ok(Map.of("mensagem", "Unidade hospitalar excluída com sucesso."));
    }

    /**
     * Alterna o status ativo/inativo de uma unidade hospitalar.
     *
     * @param id Identificador da unidade
     * @return Unidade com status atualizado
     */
    @Operation(
            summary = "Alternar status da unidade",
            description = "Inverte a disponibilidade operacional da unidade hospitalar.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Status alternado com sucesso",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        UnidadeHospitalarRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Unidade não localizada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/alternar-status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UnidadeHospitalarRespostaDTO> alternarStatus(@PathVariable Long id) {
        UnidadeHospitalar u = useCase.alternarStatus(id);
        return ResponseEntity.ok(UnidadeHospitalarRespostaDTO.deEntidade(u));
    }
}
