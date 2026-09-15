/**
 * Tipos de perfis de usuário homologados no SIGEA-GTT.
 */
export type UserRole = 'ADMIN' | 'PROFESSOR' | 'STUDENT';

/**
 * Modelo de Usuário do sistema.
 */
export interface User {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
  registrationNumber?: string | null;
  isActive: boolean;
  mustChangePassword: boolean;
  createdAt: string;
}

/**
 * Payload para criação de usuário pelo Administrador.
 */
export interface UserCreateRequest {
  fullName: string;
  email: string;
  role: UserRole;
  registrationNumber?: string | null;
}

/**
 * Resposta de criação de usuário contendo a senha provisória gerada.
 */
export interface UserCreateResponse extends User {
  provisionalPassword?: string;
}

/**
 * Payload para atualização cadastral de usuário.
 */
export interface UserUpdateRequest {
  fullName: string;
  email: string;
  role: UserRole;
  registrationNumber?: string | null;
}

/**
 * Payload para alteração de status ativo/inativo.
 */
export interface UserStatusUpdateRequest {
  isActive: boolean;
}

/**
 * Payload para atualização do próprio perfil do usuário autenticado.
 */
export interface UserProfileUpdateRequest {
  fullName: string;
}
