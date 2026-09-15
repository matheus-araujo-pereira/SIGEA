package br.ufs.sigea.user.controller;

import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.common.dto.PageResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.domain.UserRole;
import br.ufs.sigea.user.dto.UserCreateDTO;
import br.ufs.sigea.user.dto.UserCreateResponseDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.dto.UserStatusUpdateDTO;
import br.ufs.sigea.user.dto.UserUpdateDTO;
import br.ufs.sigea.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para gestão administrativa de usuários do sistema.
 * Acesso restrito ao perfil ADMIN.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gestão de Usuários (Admin)", description = "CRUD de usuários, alteração de status e controle de perfis")
public class UserController {

    private final UserService userService;

    /**
     * Lista usuários do sistema de forma paginada com filtros.
     * Padrão da plataforma: 10 registros por página ordenados por nome.
     *
     * @param search   Termo de pesquisa (nome, e-mail ou matrícula)
     * @param role     Filtro opcional por perfil de acesso
     * @param isActive Filtro opcional por status ativo/inativo
     * @param pageable Configurações de paginação (default: page=0, size=10, sort=fullName)
     * @return Página de usuários encontrados
     */
    @GetMapping
    @Operation(summary = "Listar usuários paginados", description = "Retorna lista de usuários com paginação padrão de 10 registros e filtros.")
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {

        PageResponse<UserResponseDTO> response = userService.listUsers(search, role, isActive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Usuários listados com sucesso."));
    }

    /**
     * Cadastra um novo usuário gerando senha provisória aleatória.
     *
     * @param request Dados do novo usuário
     * @return Resposta contendo os dados e a senha provisória gerada
     */
    @PostMapping
    @Operation(summary = "Cadastrar novo usuário", description = "Cria usuário com senha provisória e must_change_password=true.")
    public ResponseEntity<ApiResponse<UserCreateResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO request) {

        UserCreateResponseDTO response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Usuário cadastrado com sucesso."));
    }

    /**
     * Obtém os detalhes de um usuário específico pelo ID.
     *
     * @param id Identificador único (UUID)
     * @return Detalhes do usuário
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Recupera os dados completos de um usuário cadastrado.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(user, "Usuário recuperado com sucesso."));
    }

    /**
     * Atualiza os dados cadastrais de um usuário existente.
     *
     * @param id      Identificador único do usuário
     * @param request Dados cadastrais atualizados
     * @return Dados salvos do usuário
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Altera os dados cadastrais de um usuário.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO request) {

        UserResponseDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok(user, "Usuário atualizado com sucesso."));
    }

    /**
     * Altera o status de ativação (ativo/inativo) de um usuário.
     *
     * @param id           Identificador do usuário
     * @param request      Novo status desejado
     * @param currentAdmin Administrador autenticado que está executando a operação
     * @return Dados atualizados do usuário
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou Inativar usuário", description = "Altera o status da conta do usuário.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusUpdateDTO request,
            @AuthenticationPrincipal User currentAdmin) {

        UserResponseDTO user = userService.updateStatus(id, request, currentAdmin.getId());
        return ResponseEntity.ok(ApiResponse.ok(user, "Status do usuário atualizado com sucesso."));
    }

    /**
     * Exclui permanentemente um usuário do sistema.
     *
     * @param id           Identificador único do usuário a ser excluído
     * @param currentAdmin Administrador autenticado executando a exclusão
     * @return Confirmação de exclusão
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove permanentemente um usuário do sistema.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentAdmin) {

        userService.deleteUser(id, currentAdmin.getId());
        return ResponseEntity.ok(ApiResponse.ok("Usuário excluído com sucesso."));
    }
}
