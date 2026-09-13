package br.ufs.dcomp.sigeagtt.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade rica de domínio puro que representa um Usuário no SIGEA-GTT.
 *
 * <p>Contém as regras de negócio de identidade, validação de e-mail institucional
 * (@academico.ufs.br), regras de matrícula SIGAA para alunos e estado de ativação no sistema.
 */
public class Usuario {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String senha;
    private Boolean primeiroAcesso;
    private String matriculaSigaa;
    private PerfilUsuario perfil;
    private Boolean ativo;
    private LocalDateTime criadoEm;

    /** Construtor padrão sem argumentos para instanciação e reflexão. */
    public Usuario() {
        this.primeiroAcesso = true;
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    /**
     * Construtor completo com todos os atributos de domínio do usuário.
     *
     * @param id Identificador único numérico do usuário.
     * @param nomeCompleto Nome civil completo institucional.
     * @param email E-mail institucional no domínio academico.ufs.br.
     * @param senha Hash criptográfico da senha de acesso.
     * @param primeiroAcesso Indicador de necessidade de redefinição de senha inicial.
     * @param matriculaSigaa Matrícula acadêmica do SIGAA (exatamente 12 dígitos para alunos).
     * @param perfil Perfil de autorização (ADMINISTRADOR, PROFESSOR ou ALUNO).
     * @param ativo Indicador de conta ativa para autenticação.
     * @param criadoEm Data e hora de criação do registro no sistema.
     */
    public Usuario(
            Long id,
            String nomeCompleto,
            String email,
            String senha,
            Boolean primeiroAcesso,
            String matriculaSigaa,
            PerfilUsuario perfil,
            Boolean ativo,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.primeiroAcesso = primeiroAcesso != null ? primeiroAcesso : true;
        this.matriculaSigaa = matriculaSigaa;
        this.perfil = perfil;
        this.ativo = ativo != null ? ativo : true;
        this.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
    }

    /**
     * Valida a conformidade das regras de negócio do usuário no contexto hospitalar da UFS.
     *
     * @throws RegraNegocioException Caso o e-mail não pertença ao domínio institucional ou a
     *     matrícula seja inválida.
     */
    public void validarInvariantes() {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new RegraNegocioException("O nome completo do usuário é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail institucional é obrigatório.");
        }
        String emailLower = email.trim().toLowerCase();
        if (!emailLower.matches("^[a-z0-9._%+-]+@academico\\.ufs\\.br$")) {
            throw new RegraNegocioException(
                    "O e-mail deve pertencer obrigatoriamente ao domínio @academico.ufs.br");
        }
        if (perfil == PerfilUsuario.ALUNO) {
            if (matriculaSigaa == null || !matriculaSigaa.matches("^\\d{12}$")) {
                throw new RegraNegocioException(
                        "A Matrícula do SIGAA é obrigatória para discentes e deve conter exatamente 12 dígitos numéricos.");
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getPrimeiroAcesso() {
        return primeiroAcesso;
    }

    public void setPrimeiroAcesso(Boolean primeiroAcesso) {
        this.primeiroAcesso = primeiroAcesso;
    }

    public String getMatriculaSigaa() {
        return matriculaSigaa;
    }

    public void setMatriculaSigaa(String matriculaSigaa) {
        this.matriculaSigaa = matriculaSigaa;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id="
                + id
                + ", nomeCompleto='"
                + nomeCompleto
                + '\''
                + ", email='"
                + email
                + '\''
                + ", perfil="
                + perfil
                + ", ativo="
                + ativo
                + '}';
    }
}
