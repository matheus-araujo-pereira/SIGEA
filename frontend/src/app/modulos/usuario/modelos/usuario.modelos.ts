/**
 * Perfis de acesso institucionais do SIGEA-GTT.
 * - `ADMINISTRADOR`: Gestão plena do sistema, unidades, turmas, usuários e gatilhos.
 * - `PROFESSOR`: Criação de casos clínicos, elaboração de atividades e acompanhamento de auditorias.
 * - `ALUNO`: Realização de auditorias simuladas IHI-GTT e resolução de casos clínicos.
 */
export type PerfilUsuario = 'ADMINISTRADOR' | 'PROFESSOR' | 'ALUNO';

/**
 * Entidade de representação do usuário do sistema SIGEA-GTT.
 */
export interface Usuario {
  /** Identificador único no banco de dados. */
  id: number;
  /** Nome completo do usuário. */
  nomeCompleto: string;
  /** E-mail institucional ou de contato. */
  email: string;
  /** Matrícula acadêmica ou funcional no SIGAA/EBSERH. */
  matriculaSigaa?: string;
  /** Perfil de permissão do usuário. */
  perfil: PerfilUsuario;
  /** Flag que indica se o usuário ainda precisa alterar a senha temporária inicial. */
  primeiroAcesso?: boolean;
  /** Status de ativação da conta. */
  ativo: boolean;
  /** Token JWT retornado na autenticação. */
  token?: string;
  /** Data e hora de criação do registro no sistema. */
  criadoEm?: string;
}

/**
 * Payload de dados para criação ou atualização de um usuário.
 */
export interface UsuarioRequisicao {
  /** Nome completo do usuário. */
  nomeCompleto: string;
  /** E-mail institucional do usuário. */
  email: string;
  /** Matrícula acadêmica ou funcional no SIGAA/EBSERH. */
  matriculaSigaa?: string | null;
  /** Perfil de permissão atribuído ao usuário. */
  perfil: PerfilUsuario;
}

/**
 * Payload para alteração voluntária de senha pelo usuário autenticado.
 */
export interface AlterarSenhaPayload {
  /** Senha atual em uso. */
  senhaAtual: string;
  /** Nova senha desejada. */
  novaSenha: string;
  /** Confirmação da nova senha. */
  confirmacaoNovaSenha: string;
}
