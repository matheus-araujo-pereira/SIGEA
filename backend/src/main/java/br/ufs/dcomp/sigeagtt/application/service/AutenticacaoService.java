package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AutenticacaoUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.PasswordEncoderPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link AutenticacaoUseCase} do SIGEA-GTT.
 *
 * <p>Orquestra o processo de autenticação de usuários, checagem de integridade de contas ativas,
 * redefinição de senha no primeiro acesso e emissão de tokens JWT seguros.
 */
@Service
public class AutenticacaoService implements AutenticacaoUseCase {

    private final UsuarioRepositoryPort usuarioRepositorio;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenServicePort tokenService;

    /**
     * Construtor com injeção das portas de saída necessárias.
     *
     * @param usuarioRepositorio Porta de persistência de usuários.
     * @param passwordEncoder Porta de validação e hashing de senhas.
     * @param tokenService Porta de emissão de tokens JWT.
     */
    public AutenticacaoService(
            UsuarioRepositoryPort usuarioRepositorio,
            PasswordEncoderPort passwordEncoder,
            TokenServicePort tokenService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ResultadoAutenticacao autenticar(String email, String senha) {
        String emailLimpo = email != null ? email.trim().toLowerCase() : "";

        Usuario usuario =
                usuarioRepositorio
                        .buscarPorEmail(emailLimpo)
                        .orElseThrow(
                                () ->
                                        new RegraNegocioException(
                                                "Credenciais inválidas: e-mail institucional não localizado."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException("A conta deste usuário está inativa no sistema.");
        }

        if (!passwordEncoder.corresponde(senha, usuario.getSenha())) {
            throw new RegraNegocioException("Credenciais inválidas: senha incorreta.");
        }

        String token = tokenService.gerarToken(usuario);
        return new ResultadoAutenticacao(usuario, token);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ResultadoAutenticacao redefinirSenhaPrimeiroAcesso(
            Long usuarioId, String senhaAtual, String novaSenha, String confirmacaoNovaSenha) {
        Usuario usuario =
                usuarioRepositorio
                        .buscarPorId(usuarioId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Usuário não encontrado: " + usuarioId));

        if (!passwordEncoder.corresponde(senhaAtual, usuario.getSenha())) {
            throw new RegraNegocioException("A senha temporária atual informada está incorreta.");
        }

        if (!novaSenha.equals(confirmacaoNovaSenha)) {
            throw new RegraNegocioException("A nova senha e a confirmação não coincidem.");
        }

        if (passwordEncoder.corresponde(novaSenha, usuario.getSenha())) {
            throw new RegraNegocioException(
                    "A nova senha não pode ser idêntica à senha temporária.");
        }

        usuario.setSenha(passwordEncoder.codificar(novaSenha));
        usuario.setPrimeiroAcesso(false);
        Usuario salvo = usuarioRepositorio.salvar(usuario);
        String token = tokenService.gerarToken(salvo);
        return new ResultadoAutenticacao(salvo, token);
    }
}
