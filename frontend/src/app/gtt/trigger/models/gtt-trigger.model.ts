/**
 * @file gtt-trigger.model.ts
 * @description Modelos de dados e contratos DTO para Gatilhos Clínicos do IHI Global Trigger Tool (53 gatilhos canônicos).
 * @module GttTriggerModel
 */

/**
 * Representa um Gatilho Clínico (Trigger) do protocolo oficial IHI-GTT.
 * Cada gatilho pertence a um dos 6 módulos (C, M, S, I, P, E) e orienta a detecção de eventos adversos.
 */
export interface GttTrigger {
  /** Identificador único UUID */
  id: string;
  /** UUID do módulo assistencial associado */
  moduleId: string;
  /** Código do módulo assistencial (ex: 'C', 'M') */
  moduleCode: string;
  /** Nome do módulo assistencial */
  moduleName: string;
  /** Código identificador canônico do gatilho (ex: 'C1', 'M2', 'S5', etc.) */
  code: string;
  /** Nome descritivo do gatilho clínico */
  name: string;
  /** Descrição detalhada, critérios de inclusão e orientações metodológicas do IHI */
  description: string;
  /** Indica se o gatilho está ativo para auditorias */
  isActive: boolean;
  /** Data e hora ISO de criação */
  createdAt: string;
}

/**
 * DTO para cadastro de um novo Gatilho IHI-GTT.
 */
export interface GttTriggerCreateRequest {
  /** UUID do módulo vinculado */
  moduleId: string;
  /** Código identificador (máx 10 caracteres) */
  code: string;
  /** Nome descritivo (2 a 150 caracteres) */
  name: string;
  /** Descrição clínica e regras metodológicas */
  description: string;
}

/**
 * DTO para atualização de um Gatilho IHI-GTT.
 */
export interface GttTriggerUpdateRequest {
  /** UUID do módulo vinculado */
  moduleId: string;
  /** Código identificador */
  code: string;
  /** Nome descritivo */
  name: string;
  /** Descrição clínica */
  description: string;
}

/**
 * DTO para alteração de status ativo/inativo de um Gatilho IHI-GTT.
 */
export interface GttTriggerStatusUpdateRequest {
  /** Novo status ativo/inativo */
  isActive: boolean;
}
