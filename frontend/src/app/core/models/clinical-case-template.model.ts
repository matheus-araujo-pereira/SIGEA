import { ClinicalCaseData } from './activity.model';

/**
 * Interface que representa um Modelo Canônico de Caso Clínico Simulado para auditoria IHI-GTT.
 */
export interface ClinicalCaseTemplateResponseDTO {
  id: string;
  title: string;
  description: string;
  moduleCode: string;
  primaryTriggerCode: string;
  expectedSeverity: string;
  clinicalCaseData: ClinicalCaseData;
  isSystemTemplate: boolean;
  createdByName?: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * Interface para criação de novo modelo de caso clínico.
 */
export interface ClinicalCaseTemplateCreateDTO {
  title: string;
  description: string;
  moduleCode: string;
  primaryTriggerCode: string;
  expectedSeverity: string;
  clinicalCaseData: ClinicalCaseData;
}

/**
 * Interface para atualização de modelo de caso clínico existente.
 */
export interface ClinicalCaseTemplateUpdateDTO {
  title: string;
  description: string;
  moduleCode: string;
  primaryTriggerCode: string;
  expectedSeverity: string;
  clinicalCaseData: ClinicalCaseData;
}
