/**
 * Modelo de dados para uma Categoria de Gravidade de Dano (NCC MERP adaptado pelo IHI).
 */
export interface HarmSeverity {
  id: string;
  categoryLetter: string;
  name: string;
  description: string;
  definition?: string;
  isHarm: boolean;
  isActive: boolean;
}

/**
 * Payload para criação de uma Categoria de Gravidade.
 */
export interface HarmSeverityCreateRequest {
  categoryLetter: string;
  name: string;
  description: string;
  isHarm: boolean;
}

/**
 * Payload para atualização de uma Categoria de Gravidade.
 */
export interface HarmSeverityUpdateRequest {
  categoryLetter: string;
  name: string;
  description: string;
  isHarm: boolean;
}

/**
 * Payload para alteração de status ativo/inativo de uma Categoria de Gravidade.
 */
export interface HarmSeverityStatusUpdateRequest {
  isActive: boolean;
}
