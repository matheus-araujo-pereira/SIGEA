import { User } from './user.model';

/**
 * Payload de requisição de login.
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/**
 * Resposta de autenticação contendo token JWT e dados do usuário.
 */
export interface LoginResponse {
  token: string;
  tokenType: string;
  user: User;
}

/**
 * Payload para alteração obrigatória de senha no primeiro login.
 */
export interface FirstLoginChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

/**
 * Payload para alteração voluntária de senha no perfil.
 */
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
