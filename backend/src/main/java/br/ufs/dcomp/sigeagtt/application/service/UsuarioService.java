package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UsuarioUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.PasswordEncoderPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link UsuarioUseCase} no SIGEA-GTT.
 *
 * <p>Responsável pelo ciclo de vida de usuários, validação de regras de integridade institucional,
 * unicidade de matrículas do SIGAA e e-mails acadêmicos, e garantia da existência de ao menos um
 * administrador ativo.
 */
@Service
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort repositorio;
    private final PasswordEncoderPort passwordEncoder;

    /**
     * Construtor com injeção das portas de persistência e codificação de senhas.
     *
     * @param repositorio Porta de saída do repositório de usuários.
     * @param passwordEncoder Porta de saída de criptografia de senhas.
     */
    public UsuarioService(UsuarioRepositoryPort repositorio, PasswordEncoderPort passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return repositorio.listarTodos();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repositorio
                .buscarPorId(id)
                .orElseThrow(
                        () ->
                                new RecursoNaoEncontradoException(
                                        "Usuário não encontrado com o ID: " + id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario cadastrar(
            String nomeCompleto, String email, String matriculaSigaa, PerfilUsuario perfil) {
        validarDominioEmail(email);
        String emailLimpo = email.trim().toLowerCase();

        if (repositorio.buscarPorEmail(emailLimpo).isPresent()) {
            throw new ConflitoDadosException(
                    "Já existe um usuário cadastrado com este e-mail institucional.");
        }

        String matriculaLimpa = null;
        if (perfil == PerfilUsuario.ALUNO) {
            matriculaLimpa =
                    (matriculaSigaa != null && !matriculaSigaa.isBlank())
                            ? matriculaSigaa.trim()
                            : null;
            if (matriculaLimpa == null || !matriculaLimpa.matches("^\\d{12}$")) {
                throw new RegraNegocioException(
                        "A Matrícula do SIGAA é obrigatória para discentes e deve conter exatamente 12 dígitos numéricos.");
            }
            if (repositorio.buscarPorMatriculaSigaa(matriculaLimpa).isPresent()) {
                throw new ConflitoDadosException(
                        "Já existe um aluno cadastrado com esta Matrícula do SIGAA.");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(nomeCompleto != null ? nomeCompleto.trim() : "");
        usuario.setEmail(emailLimpo);
        usuario.setSenha(passwordEncoder.codificar("Sigea@123"));
        usuario.setPrimeiroAcesso(true);
        usuario.setMatriculaSigaa(matriculaLimpa);
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        usuario.validarInvariantes();

        return repositorio.salvar(usuario);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario editar(
            Long id,
            String nomeCompleto,
            String email,
            String matriculaSigaa,
            PerfilUsuario perfil) {
        Usuario usuario = buscarPorId(id);

        validarDominioEmail(email);
        String emailLimpo = email.trim().toLowerCase();

        if (repositorio.buscarPorEmailEIdDiferente(emailLimpo, id).isPresent()) {
            throw new ConflitoDadosException(
                    "O e-mail informado já está em uso por outro usuário.");
        }

        String matriculaLimpa = null;
        if (perfil == PerfilUsuario.ALUNO) {
            matriculaLimpa =
                    (matriculaSigaa != null && !matriculaSigaa.isBlank())
                            ? matriculaSigaa.trim()
                            : null;
            if (matriculaLimpa == null || !matriculaLimpa.matches("^\\d{12}$")) {
                throw new RegraNegocioException(
                        "A Matrícula do SIGAA é obrigatória para discentes e deve conter exatamente 12 dígitos numéricos.");
            }
            if (repositorio.buscarPorMatriculaEIdDiferente(matriculaLimpa, id).isPresent()) {
                throw new ConflitoDadosException(
                        "Esta Matrícula do SIGAA já pertence a outro discente.");
            }
        }

        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR
                && perfil != PerfilUsuario.ADMINISTRADOR) {
            long totalAdmins = repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR);
            if (totalAdmins <= 1) {
                throw new RegraNegocioException(
                        "Operação cancelada: o sistema precisa manter ao menos um Administrador ativo.");
            }
        }

        usuario.setNomeCompleto(nomeCompleto != null ? nomeCompleto.trim() : "");
        usuario.setEmail(emailLimpo);
        usuario.setPerfil(perfil);
        usuario.setMatriculaSigaa(matriculaLimpa);
        usuario.validarInvariantes();

        return repositorio.salvar(usuario);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario resetarSenha(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setSenha(passwordEncoder.codificar("Sigea@123"));
        usuario.setPrimeiroAcesso(true);
        return repositorio.salvar(usuario);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario inativar(Long id) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            long totalAdmins = repositorio.contarPorPerfilEAtivo(PerfilUsuario.ADMINISTRADOR);
            if (totalAdmins <= 1) {
                throw new RegraNegocioException(
                        "Não é permitido inativar o único Administrador ativo do sistema.");
            }
        }

        usuario.setAtivo(false);
        return repositorio.salvar(usuario);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario reativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        return repositorio.salvar(usuario);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Usuario alterarSenha(
            Long id, String senhaAtual, String novaSenha, String confirmacaoNovaSenha) {
        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.corresponde(senhaAtual, usuario.getSenha())) {
            throw new RegraNegocioException("A senha atual informada está incorreta.");
        }

        if (!novaSenha.equals(confirmacaoNovaSenha)) {
            throw new RegraNegocioException("A confirmação da nova senha não confere.");
        }

        if (passwordEncoder.corresponde(novaSenha, usuario.getSenha())) {
            throw new RegraNegocioException("A nova senha deve ser diferente da senha atual.");
        }

        usuario.setSenha(passwordEncoder.codificar(novaSenha));
        usuario.setPrimeiroAcesso(false);
        return repositorio.salvar(usuario);
    }

    private void validarDominioEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail institucional é obrigatório.");
        }
        String emailLower = email.trim().toLowerCase();
        if (!emailLower.matches("^[a-z0-9._%+-]+@academico\\.ufs\\.br$")) {
            throw new RegraNegocioException(
                    "O e-mail deve pertencer obrigatoriamente ao domínio @academico.ufs.br");
        }
    }
}
