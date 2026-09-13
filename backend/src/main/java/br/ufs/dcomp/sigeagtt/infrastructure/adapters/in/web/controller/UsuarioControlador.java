package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UsuarioUseCase;
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
import org.springframework.web.bind.annotation.*;

/** Controlador REST para gestão cadastral de usuários institucionais. */
@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Usuários",
        description = "Endpoints para gerenciamento de contas, perfis e credenciais de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioControlador {

    private final UsuarioUseCase usuarioUseCase;

    public UsuarioControlador(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    /**
     * Lista todos os usuários cadastrados.
     *
     * @return Lista de usuários em formato DTO
     */
    @Operation(
            summary = "Listar todos os usuários",
            description =
                    "Retorna a relação completa de discentes, docentes e administradores cadastrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso"),
        @ApiResponse(
                responseCode = "401",
                description = "Não autenticado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Acesso proibido",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESSOR')")
    public ResponseEntity<List<UsuarioRespostaDTO>> listar() {
        List<UsuarioRespostaDTO> lista =
                usuarioUseCase.listarTodos().stream().map(UsuarioRespostaDTO::deEntidade).toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca usuário pelo ID.
     *
     * @param id Identificador do usuário
     * @return Dados do usuário
     */
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Localiza o cadastro do usuário pelo respectivo ID numérico.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário localizado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> buscarPorId(@PathVariable Long id) {
        Usuario u = usuarioUseCase.buscarPorId(id);
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(u));
    }

    /**
     * Cadastra um novo usuário no sistema.
     *
     * @param dto Dados cadastrais do usuário
     * @return Usuário criado
     */
    @Operation(
            summary = "Cadastrar novo usuário",
            description =
                    "Cria um novo usuário institucional atribuindo senha padrão inicial e primeiro acesso pendente.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Usuário criado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados cadastrais inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "409",
                description = "Conflito de e-mail ou matrícula duplicada",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(
            @Valid @RequestBody UsuarioRequisicaoDTO dto) {
        Usuario criado =
                usuarioUseCase.cadastrar(
                        dto.nomeCompleto(), dto.email(), dto.matriculaSigaa(), dto.perfil());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioRespostaDTO.deEntidade(criado));
    }

    /**
     * Edita dados de um usuário existente.
     *
     * @param id Identificador do usuário
     * @param dto Novos dados cadastrais
     * @return Usuário atualizado
     */
    @Operation(
            summary = "Editar usuário",
            description = "Atualiza os dados de identificação e perfil de um usuário.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioRespostaDTO> editar(
            @PathVariable Long id, @Valid @RequestBody UsuarioEdicaoDTO dto) {
        Usuario atualizado =
                usuarioUseCase.editar(
                        id, dto.nomeCompleto(), dto.email(), dto.matriculaSigaa(), dto.perfil());
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(atualizado));
    }

    /**
     * Reseta a senha do usuário para a senha inicial padrão.
     *
     * @param id Identificador do usuário
     * @return Usuário com senha resetada
     */
    @Operation(
            summary = "Resetar senha do usuário",
            description =
                    "Redefine a senha do usuário para o padrão institucional provisório e reativa primeiro acesso.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Senha resetada com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/resetar-senha")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioRespostaDTO> resetarSenha(@PathVariable Long id) {
        Usuario u = usuarioUseCase.resetarSenha(id);
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(u));
    }

    /**
     * Inativa a conta do usuário.
     *
     * @param id Identificador do usuário
     * @return Usuário inativado
     */
    @Operation(
            summary = "Inativar usuário",
            description = "Bloqueia o acesso do usuário impedindo novos logins.")
    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioRespostaDTO> inativar(@PathVariable Long id) {
        Usuario u = usuarioUseCase.inativar(id);
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(u));
    }

    /**
     * Reativa a conta do usuário.
     *
     * @param id Identificador do usuário
     * @return Usuário reativado
     */
    @Operation(
            summary = "Reativar usuário",
            description = "Desbloqueia a conta inativada de um usuário.")
    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioRespostaDTO> reativar(@PathVariable Long id) {
        Usuario u = usuarioUseCase.reativar(id);
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(u));
    }

    /**
     * Altera a senha do próprio usuário.
     *
     * @param id Identificador do usuário
     * @param dto Dados de troca de senha
     * @return Usuário com nova senha configurada
     */
    @Operation(
            summary = "Alterar senha",
            description =
                    "Permite ao usuário autenticado alterar sua própria senha mediante fornecimento da senha atual.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Senha alterada com sucesso",
                content = @Content(schema = @Schema(implementation = UsuarioRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Senha atual incorreta ou validações não atendidas",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PatchMapping("/{id}/alterar-senha")
    public ResponseEntity<UsuarioRespostaDTO> alterarSenha(
            @PathVariable Long id, @Valid @RequestBody AlterarSenhaDTO dto) {
        Usuario u =
                usuarioUseCase.alterarSenha(
                        id, dto.senhaAtual(), dto.novaSenha(), dto.confirmacaoNovaSenha());
        return ResponseEntity.ok(UsuarioRespostaDTO.deEntidade(u));
    }
}
