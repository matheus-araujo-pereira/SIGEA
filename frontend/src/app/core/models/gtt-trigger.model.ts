/**
 * Modelo de dados para um Gatilho (Trigger) do IHI-GTT.
 */
export interface GttTrigger {
  id: string;
  moduleId: string;
  moduleCode: string;
  moduleName: string;
  code: string;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
}

/**
 * Payload para criação de um novo Gatilho IHI-GTT.
 */
export interface GttTriggerCreateRequest {
  moduleId: string;
  code: string;
  name: string;
  description: string;
}

/**
 * Payload para atualização cadastral de um Gatilho IHI-GTT.
 */
export interface GttTriggerUpdateRequest {
  moduleId: string;
  code: string;
  name: string;
  description: string;
}

/**
 * Payload para alteração de status ativo/inativo de um Gatilho IHI-GTT.
 */
export interface GttTriggerStatusUpdateRequest {
  isActive: boolean;
}
