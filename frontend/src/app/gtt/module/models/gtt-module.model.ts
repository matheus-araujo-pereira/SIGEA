/**
 * @file gtt-module.model.ts
 * @description Modelos de dados e contratos de transferência para Módulos do IHI Global Trigger Tool (IHI-GTT).
 * @module GttModuleModel
 */

/**
 * Representa um Módulo do protocolo IHI Global Trigger Tool (IHI-GTT).
 * Exemplos: Cuidados Gerais (C), Medicamentos (M), Cirúrgico (S), etc.
 */
export interface GttModule {
  /** Identificador único UUID do módulo */
  id: string;
  /** Código identificador canônico do módulo (ex: 'C', 'M', 'S', 'I', 'P', 'E') */
  code: string;
  /** Nome descritivo do módulo clínico */
  name: string;
  /** Descrição detalhada do escopo assistencial e critérios de inclusão */
  description: string | null;
  /** Ordem de exibição visual em catálogos e formulários */
  displayOrder?: number;
  /** Indica se o módulo está ativo para auditorias clínicas */
  isActive: boolean;
  /** Data e hora ISO de criação do registro */
  createdAt: string;
  /** Quantidade total de gatilhos cadastrados associados ao módulo */
  triggerCount?: number;
}

/**
 * DTO para requisição de criação de um novo Módulo GTT.
 */
export interface GttModuleCreateRequest {
  /** Código único de identificação (máx 10 caracteres) */
  code: string;
  /** Nome do módulo clínico (2 a 100 caracteres) */
  name: string;
  /** Descrição opcional do escopo do módulo */
  description?: string | null;
  /** Ordem opcional de ordenação na visualização */
  displayOrder?: number;
}

/**
 * DTO para requisição de atualização cadastral de um Módulo GTT existente.
 */
export interface GttModuleUpdateRequest {
  /** Código do módulo */
  code: string;
  /** Nome do módulo */
  name: string;
  /** Descrição detalhada */
  description?: string | null;
  /** Ordem de ordenação */
  displayOrder?: number;
}

/**
 * DTO para requisição de alteração de status ativo/inativo de um Módulo GTT.
 */
export interface GttModuleStatusUpdateRequest {
  /** Novo status ativo/inativo */
  isActive: boolean;
}
