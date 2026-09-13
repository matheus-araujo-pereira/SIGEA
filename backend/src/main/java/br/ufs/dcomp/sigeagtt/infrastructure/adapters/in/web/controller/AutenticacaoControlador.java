package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.controller;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AutenticacaoUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.ErroRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.LoginRequisicaoDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.LoginRespostaDTO;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto.PrimeiroAcessoRequisicaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/** Controlador REST para autenticação institucional e redefinição de primeiro acesso. */
@RestController
@RequestMapping("/api/autenticacao")
@Tag(
        name = "Autenticação",
        description =
                "Endpoints de autenticação, login JWT, primeiro acesso e encerramento de sessão")
public class AutenticacaoControlador {

    private final AutenticacaoUseCase autenticacaoUseCase;
    private final SecurityContextRepository securityContextRepository;
    private final UsuarioRepositoryPort usuarioRepositorio;

    public AutenticacaoControlador(
            AutenticacaoUseCase autenticacaoUseCase,
            SecurityContextRepository securityContextRepository,
            UsuarioRepositoryPort usuarioRepositorio) {
        this.autenticacaoUseCase = autenticacaoUseCase;
        this.securityContextRepository = securityContextRepository;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Autentica usuário com e-mail institucional e senha.
     *
     * @param dto Credenciais de acesso
     * @param request Requisição HTTP
     * @param response Resposta HTTP
     * @return Resposta com dados do usuário e token Bearer JWT
     */
    @Operation(
            summary = "Autenticar usuário",
            description =
                    "Valida as credenciais institucionais e retorna o token de sessão Bearer JWT.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Autenticação bem-sucedida",
                content = @Content(schema = @Schema(implementation = LoginRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Dados da requisição inválidos",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "401",
                description = "Credenciais inválidas ou usuário inativo",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping("/entrar")
    public ResponseEntity<LoginRespostaDTO> entrar(
            @Valid @RequestBody LoginRequisicaoDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {
        AutenticacaoUseCase.ResultadoAutenticacao resultado =
                autenticacaoUseCase.autenticar(dto.email(), dto.senha());
        LoginRespostaDTO resposta =
                LoginRespostaDTO.deEntidade(resultado.usuario(), resultado.token());
        autenticarSessao(resposta, request, response);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Redefine senha no primeiro acesso institucional.
     *
     * @param dto Dados de primeiro acesso
     * @param request Requisição HTTP
     * @param response Resposta HTTP
     * @return Resposta com dados atualizados e novo token JWT
     */
    @Operation(
            summary = "Redefinir senha de primeiro acesso",
            description = "Altera a senha inicial padrão obrigatória no primeiro login.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Senha redefinida com sucesso",
                content = @Content(schema = @Schema(implementation = LoginRespostaDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Senha atual incorreta ou critérios de força violados",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não localizado",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class))),
        @ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor",
                content = @Content(schema = @Schema(implementation = ErroRespostaDTO.class)))
    })
    @PostMapping("/primeiro-acesso")
    public ResponseEntity<LoginRespostaDTO> primeiroAcesso(
            @Valid @RequestBody PrimeiroAcessoRequisicaoDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {
        AutenticacaoUseCase.ResultadoAutenticacao resultado =
                autenticacaoUseCase.redefinirSenhaPrimeiroAcesso(
                        dto.usuarioId(),
                        dto.senhaAtual(),
                        dto.novaSenha(),
                        dto.confirmacaoNovaSenha());
        LoginRespostaDTO resposta =
                LoginRespostaDTO.deEntidade(resultado.usuario(), resultado.token());
        autenticarSessao(resposta, request, response);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Encerra a sessão do usuário.
     *
     * @param request Requisição HTTP
     * @return Mensagem de confirmação
     */
    @Operation(
            summary = "Encerrar sessão",
            description = "Limpa o contexto de segurança e invalida a sessão HTTP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sessão finalizada com sucesso")
    })
    @PostMapping("/sair")
    public ResponseEntity<Map<String, String>> sair(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        return ResponseEntity.ok(Map.of("mensagem", "Sessão finalizada."));
    }

    private void autenticarSessao(
            LoginRespostaDTO resposta, HttpServletRequest request, HttpServletResponse response) {
        String role = "ROLE_" + resposta.perfil().name();
        Usuario usuario =
                resposta.id() != null
                        ? usuarioRepositorio.buscarPorId(resposta.id()).orElse(null)
                        : usuarioRepositorio.buscarPorEmail(resposta.email()).orElse(null);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario != null ? usuario : resposta.email(),
                        null,
                        List.of(new SimpleGrantedAuthority(role)));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
