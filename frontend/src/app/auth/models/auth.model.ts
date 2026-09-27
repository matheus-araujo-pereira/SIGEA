import { User } from '../../user/models/user.model';

/**
 * DTO contendo as credenciais submetidas na requisição de autenticação (Login).
 */
export interface LoginRequest {
  /** E-mail institucional (@academico.ufs.br). */
  email: string;
  /** Senha em texto claro (trafegada via HTTPS). */
  password: string;
}

/**
 * DTO contendo a resposta de autenticação emitida pelo Spring Security com JWT.
 */
export interface LoginResponse {
  /** Token JWT HMAC-SHA256 assinado pelo backend. */
  token: string;
  /** Tipo do esquema de autorização (padrão: 'Bearer'). */
  tokenType: string;
  /** Dados estruturados do usuário autenticado. */
  user: User;
}

/**
 * DTO para alteração obrigatória de senha exigida no primeiro login institucional.
 */
export interface FirstLoginChangePasswordRequest {
  /** Senha provisória recebida pelo usuário. */
  currentPassword: string;
  /** Nova senha pessoal forte escolhida. */
  newPassword: string;
  /** Confirmação da nova senha pessoal. */
  confirmPassword: string;
}

/**
 * DTO para alteração voluntária de senha pessoal na tela de perfil do usuário.
 */
export interface ChangePasswordRequest {
  /** Senha atual do usuário para validação de segurança. */
  currentPassword: string;
  /** Nova senha pessoal. */
  newPassword: string;
  /** Confirmação da nova senha. */
  confirmPassword: string;
}
