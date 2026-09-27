/**
 * @file harm-severity.model.ts
 * @description Modelos de dados e contratos DTO para Categorias de Gravidade de Dano (NCC MERP adaptado pelo IHI).
 * @module HarmSeverityModel
 */

/**
 * Representa uma Categoria de Gravidade de Dano da escala NCC MERP (A a I).
 * Classifica a ocorrência quanto à existência de erro, alcance ao paciente e magnitude do dano.
 */
export interface HarmSeverity {
  /** Identificador único UUID */
  id: string;
  /** Letra identificadora da categoria ('A' a 'I') */
  categoryLetter: string;
  /** Nome descritivo da categoria */
  name: string;
  /** Descrição detalhada do impacto clínico */
  description: string;
  /** Definição técnica expandida */
  definition?: string;
  /** Indica se a categoria representa Dano (Categorias E a I são consideradas Dano / Evento Adverso) */
  isHarm: boolean;
  /** Indica se a gravidade está ativa no catálogo */
  isActive: boolean;
}

/**
 * DTO para requisição de criação de uma Categoria de Gravidade.
 */
export interface HarmSeverityCreateRequest {
  /** Letra da categoria ('A' a 'I') */
  categoryLetter: string;
  /** Nome resumido */
  name: string;
  /** Descrição clínica */
  description: string;
  /** Indica se configura dano ao paciente */
  isHarm: boolean;
}

/**
 * DTO para requisição de atualização de uma Categoria de Gravidade.
 */
export interface HarmSeverityUpdateRequest {
  /** Letra da categoria ('A' a 'I') */
  categoryLetter: string;
  /** Nome resumido */
  name: string;
  /** Descrição clínica */
  description: string;
  /** Indica se configura dano ao paciente */
  isHarm: boolean;
}

/**
 * DTO para alteração de status ativo/inativo de uma Categoria de Gravidade.
 */
export interface HarmSeverityStatusUpdateRequest {
  /** Novo status ativo/inativo */
  isActive: boolean;
}
