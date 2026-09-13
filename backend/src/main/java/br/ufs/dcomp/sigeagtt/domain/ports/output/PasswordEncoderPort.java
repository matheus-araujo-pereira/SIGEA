package br.ufs.dcomp.sigeagtt.domain.ports.output;

/**
 * Porta de saída (Output Port) para operações de criptografia e hashing de senhas.
 *
 * <p>Abstrai a dependência do Spring Security PasswordEncoder em relação às regras de negócio de
 * domínio.
 */
public interface PasswordEncoderPort {

    /**
     * Gera o hash criptográfico (ex: BCrypt) de uma senha em texto plano.
     *
     * @param senhaPlana Senha em texto puro.
     * @return Hash criptográfico gerado.
     */
    String codificar(String senhaPlana);

    /**
     * Verifica se a senha em texto puro corresponde ao hash criptográfico armazenado.
     *
     * @param senhaPlana Senha informada na autenticação.
     * @param hashCodificado Hash seguro armazenado no repositório.
     * @return Verdadeiro se a senha conferir com o hash.
     */
    boolean corresponde(String senhaPlana, String hashCodificado);
}
