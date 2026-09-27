/**
 * Tipos de severidade cromática para notificações flutuantes (Toast) da paleta clínica.
 */
export type ToastType = 'success' | 'error' | 'warning' | 'info';

/**
 * Representação de uma notificação Toast individual ativa na interface.
 */
export interface Toast {
  /** Identificador único alfanumérico da notificação. */
  id: string;
  /** Tipo e variante cromática da notificação. */
  type: ToastType;
  /** Título em destaque da notificação. */
  title: string;
  /** Mensagem explicativa ou detalhamento textual. */
  message: string;
  /** Tempo de exibição em milissegundos antes do descarte automático. */
  duration?: number;
}
