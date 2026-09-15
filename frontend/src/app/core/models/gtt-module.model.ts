/**
 * Modelo de dados para um Módulo do IHI Global Trigger Tool (IHI-GTT).
 */
export interface GttModule {
  id: string;
  code: string;
  name: string;
  description: string | null;
  displayOrder?: number;
  isActive: boolean;
  createdAt: string;
  triggerCount?: number;
}

/**
 * Payload para criação de um novo Módulo GTT.
 */
export interface GttModuleCreateRequest {
  code: string;
  name: string;
  description?: string | null;
  displayOrder?: number;
}

/**
 * Payload para atualização cadastral de um Módulo GTT.
 */
export interface GttModuleUpdateRequest {
  code: string;
  name: string;
  description?: string | null;
  displayOrder?: number;
}

/**
 * Payload para alteração de status ativo/inativo de um Módulo GTT.
 */
export interface GttModuleStatusUpdateRequest {
  isActive: boolean;
}
