package br.ufs.dcomp.sigeagtt.domain.ports.output;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;

/** Porta de saída (Output Port) para geração e validação de tokens de autenticação Bearer JWT. */
public interface TokenServicePort {

    /**
     * Gera um token JWT assinado digitalmente para o usuário autenticado.
     *
     * @param usuario Usuário logado no sistema.
     * @return String contendo o token JWT no formato RFC 7519.
     */
    String gerarToken(Usuario usuario);

    /**
     * Valida um token JWT e extrai as reivindicações de identidade do usuário.
     *
     * @param token Token JWT enviado no cabeçalho Authorization.
     * @return Dados decodificados do token, ou null caso expirado ou inválido.
     */
    DadosToken validarToken(String token);

    /**
     * Estrutura imutável com os dados extraídos do payload JWT.
     *
     * @param id Identificador do usuário.
     * @param email E-mail institucional do usuário.
     * @param perfil Perfil de autorização associado.
     */
    record DadosToken(Long id, String email, String perfil) {}
}
