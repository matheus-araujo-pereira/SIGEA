/**
 * @file clinical-case-template.model.ts
 * @description Modelos de dados e contratos DTO para Modelos de Casos Clínicos Simulados canônicos do protocolo IHI-GTT.
 * @module ClinicalCaseTemplateModel
 */

import { ClinicalCaseData } from '../../activity/models/activity.model';

/**
 * Representa um Modelo de Caso Clínico Simulado pré-configurado no catálogo para reutilização docente.
 */
export interface ClinicalCaseTemplateResponseDTO {
  /** Identificador único UUID do template */
  id: string;
  /** Título do caso clínico */
  title: string;
  /** Descrição do caso e objetivos pedagógicos */
  description: string;
  /** Código do módulo GTT relacionado (ex: 'C', 'M', 'S') */
  moduleCode: string;
  /** Código do gatilho primário esperado (ex: 'M5') */
  primaryTriggerCode: string;
  /** Gravidade esperada NCC MERP (ex: 'F') */
  expectedSeverity: string;
  /** Dados estruturados do prontuário simulado */
  clinicalCaseData: ClinicalCaseData;
  /** Indica se é um template canônico do sistema SIGEA */
  isSystemTemplate: boolean;
  /** Nome do autor criador do modelo */
  createdByName?: string;
  /** Data e hora ISO de criação */
  createdAt: string;
  /** Data e hora ISO de atualização */
  updatedAt?: string;
}

/**
 * DTO para cadastro de novo modelo de caso clínico simulado.
 */
export interface ClinicalCaseTemplateCreateDTO {
  /** Título do modelo */
  title: string;
  /** Descrição detalhada */
  description: string;
  /** Código do módulo GTT */
  moduleCode: string;
  /** Código do gatilho clínico primário */
  primaryTriggerCode: string;
  /** Severidade esperada NCC MERP */
  expectedSeverity: string;
  /** Estrutura completa do prontuário simulado */
  clinicalCaseData: ClinicalCaseData;
}

/**
 * DTO para atualização de modelo de caso clínico existente.
 */
export interface ClinicalCaseTemplateUpdateDTO {
  /** Título atualizado */
  title: string;
  /** Descrição atualizada */
  description: string;
  /** Código do módulo */
  moduleCode: string;
  /** Código do gatilho primário */
  primaryTriggerCode: string;
  /** Severidade esperada */
  expectedSeverity: string;
  /** Prontuário simulado atualizado */
  clinicalCaseData: ClinicalCaseData;
}
