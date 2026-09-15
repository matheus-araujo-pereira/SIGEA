/**
 * Tipos de notificação toast.
 */
export type ToastType = 'success' | 'error' | 'warning' | 'info';

/**
 * Representação de uma notificação individual.
 */
export interface Toast {
  id: string;
  type: ToastType;
  title: string;
  message: string;
  duration?: number;
}
