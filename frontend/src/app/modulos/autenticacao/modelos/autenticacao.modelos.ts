/**
 * Modelo de payload para requisição de login no sistema SIGEA-GTT.
 */
export interface CredenciaisLogin {
  /** Matrícula institucional ou e-mail cadastrado. */
  identificador: string;
  /** Senha de acesso do usuário. */
  senha: string;
}

/**
 * Modelo de dados para redefinição obrigatória de credenciais no primeiro acesso.
 */
export interface PrimeiroAcessoPayload {
  /** ID identificador do usuário no sistema. */
  usuarioId: number;
  /** Senha padrão provisória gerada no cadastro. */
  senhaAtual: string;
  /** Nova senha definida pelo usuário. */
  novaSenha: string;
  /** Confirmação da nova senha digitada. */
  confirmacaoNovaSenha: string;
}
