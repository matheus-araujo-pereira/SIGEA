package br.ufs.sigea.auth.controller;

import br.ufs.sigea.auth.dto.ChangePasswordDTO;
import br.ufs.sigea.auth.dto.FirstLoginChangePasswordDTO;
import br.ufs.sigea.auth.dto.LoginRequestDTO;
import br.ufs.sigea.auth.dto.LoginResponseDTO;
import br.ufs.sigea.auth.service.AuthService;
import br.ufs.sigea.common.dto.ApiResponse;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.dto.UserProfileUpdateDTO;
import br.ufs.sigea.user.dto.UserResponseDTO;
import br.ufs.sigea.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operações de autenticação e gestão do perfil do usuário autenticado.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Autenticação e Perfil", description = "Endpoints de login, primeiro acesso e gestão do próprio perfil")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Realiza a autenticação de um usuário no sistema gerando o token JWT.
     *
     * @param request Credenciais de acesso
     * @return Resposta com token JWT e dados do usuário
     */
    @PostMapping("/auth/login")
    @Operation(summary = "Realizar login institucional", description = "Autentica usuário com e-mail @academico.ufs.br e senha.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login realizado com sucesso."));
    }

    /**
     * Altera a senha provisória de forma obrigatória no primeiro acesso.
     *
     * @param user    Usuário autenticado na requisição
     * @param request Senhas provisória e nova
     * @return Resposta com novo token JWT atualizado
     */
    @PostMapping("/auth/first-login-change-password")
    @Operation(summary = "Redefinição de senha no primeiro login", description = "Troca obrigatória da senha provisória gerada no cadastro.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> firstLoginChangePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FirstLoginChangePasswordDTO request) {
        LoginResponseDTO response = authService.firstLoginChangePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Senha de primeiro acesso alterada com sucesso."));
    }

    /**
     * Retorna as informações do perfil do usuário atualmente autenticado.
     *
     * @param user Usuário autenticado
     * @return Dados do perfil
     */
    @GetMapping("/profile")
    @Operation(summary = "Consultar dados do próprio perfil", description = "Obtém os dados cadastrais do usuário autenticado.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getProfile(@AuthenticationPrincipal User user) {
        UserResponseDTO profile = userService.getUserById(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile, "Perfil recuperado com sucesso."));
    }

    /**
     * Atualiza as informações básicas do próprio perfil (ex: nome completo).
     *
     * @param user    Usuário autenticado
     * @param request Dados atualizados
     * @return Dados atualizados
     */
    @PutMapping("/profile")
    @Operation(summary = "Atualizar o próprio perfil", description = "Atualiza o nome do usuário autenticado.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserProfileUpdateDTO request) {
        UserResponseDTO profile = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(profile, "Perfil atualizado com sucesso."));
    }

    /**
     * Altera voluntariamente a senha do usuário autenticado.
     *
     * @param user    Usuário autenticado
     * @param request Senha atual e nova
     * @return Confirmação da operação
     */
    @PostMapping("/profile/change-password")
    @Operation(summary = "Alterar senha voluntariamente", description = "Permite ao usuário autenticado trocar sua senha a qualquer momento.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordDTO request) {
        authService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Senha alterada com sucesso."));
    }
}
