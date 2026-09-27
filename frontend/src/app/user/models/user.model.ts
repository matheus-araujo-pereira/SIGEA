/**
 * Perfis canônicos de autorização RBAC homologados no SIGEA.
 *
 * - `ADMIN`: Administrador do sistema, responsável pela governança de usuários e catálogos.
 * - `PROFESSOR`: Docente responsável por turmas, atividades, prontuários simulados e consenso GTT.
 * - `STUDENT`: Acadêmico de graduação responsável pela auditoria primária de prontuários.
 */
export type UserRole = 'ADMIN' | 'PROFESSOR' | 'STUDENT';

/**
 * Entidade de domínio representando um usuário registrado no SIGEA.
 */
export interface User {
  /** Identificador único universal (UUID v4). */
  id: string;
  /** Nome civil completo do usuário. */
  fullName: string;
  /** Endereço de e-mail institucional estrito (@academico.ufs.br). */
  email: string;
  /** Perfil de controle de acesso atribuído. */
  role: UserRole;
  /** Matrícula acadêmica (obrigatória para STUDENT; nula para PROFESSOR e ADMIN). */
  registrationNumber?: string | null;
  /** Indicador booleano do status ativo/inativo da conta. */
  isActive: boolean;
  /** Sinalizador exigindo a troca obrigatória de senha no primeiro login. */
  mustChangePassword: boolean;
  /** Data/hora ISO-8601 de criação do registro no sistema. */
  createdAt: string;
}

/**
 * DTO para solicitação de cadastro de novo usuário pelo Administrador.
 */
export interface UserCreateRequest {
  /** Nome civil completo. */
  fullName: string;
  /** E-mail institucional (@academico.ufs.br). */
  email: string;
  /** Perfil pretendido (ADMIN, PROFESSOR ou STUDENT). */
  role: UserRole;
  /** Número de matrícula (apenas discentes). */
  registrationNumber?: string | null;
}

/**
 * DTO de resposta de criação de usuário contendo a senha provisória gerada pelo backend.
 */
export interface UserCreateResponse extends User {
  /** Senha provisória gerada com alta entropia para envio ao usuário. */
  provisionalPassword?: string;
}

/**
 * DTO para atualização cadastral de usuário por perfil Administrador.
 */
export interface UserUpdateRequest {
  /** Nome civil completo atualizado. */
  fullName: string;
  /** E-mail institucional. */
  email: string;
  /** Perfil RBAC. */
  role: UserRole;
  /** Número de matrícula. */
  registrationNumber?: string | null;
}

/**
 * DTO para alteração direta do status ativo/inativo de uma conta de usuário.
 */
export interface UserStatusUpdateRequest {
  /** Novo status booleano da conta (true: ativo, false: inativo). */
  isActive: boolean;
}

/**
 * DTO para auto-atualização dos dados cadastrais na tela de perfil pelo próprio usuário.
 */
export interface UserProfileUpdateRequest {
  /** Nome civil completo retificado. */
  fullName: string;
}
