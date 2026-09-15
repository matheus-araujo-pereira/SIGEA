import { Injectable, signal } from '@angular/core';
import { Toast, ToastType } from '../models/toast.model';

/**
 * Serviço reativo para emissão e gestão de notificações Toast com Signals.
 */
@Injectable({
  providedIn: 'root',
})
export class ToastService {
  /**
   * Signal contendo a lista de notificações ativas.
   */
  readonly toasts = signal<Toast[]>([]);

  /**
   * Exibe uma notificação com parâmetros customizados.
   *
   * @param type     Tipo da notificação (success, error, warning, info)
   * @param title    Título de destaque
   * @param message  Mensagem explicativa
   * @param duration Duração em milissegundos (padrão: 4000ms)
   */
  show(type: ToastType, title: string, message: string = '', duration = 4000): void {
    const id = Math.random().toString(36).substring(2, 9);
    const toast: Toast = { id, type, title, message, duration };

    this.toasts.update((current) => [...current, toast]);

    if (duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, duration);
    }
  }

  /**
   * Exibe notificação de sucesso.
   *
   * @param title   Título
   * @param message Mensagem detalhada
   */
  success(title: string, message: string = ''): void {
    this.show('success', title, message);
  }

  /**
   * Exibe notificação de erro.
   *
   * @param title   Título
   * @param message Mensagem detalhada
   */
  error(title: string, message: string = ''): void {
    this.show('error', title, message, 5000);
  }

  /**
   * Exibe notificação de alerta.
   *
   * @param title   Título
   * @param message Mensagem detalhada
   */
  warning(title: string, message: string = ''): void {
    this.show('warning', title, message);
  }

  /**
   * Exibe notificação informativa.
   *
   * @param title   Título
   * @param message Mensagem detalhada
   */
  info(title: string, message: string = ''): void {
    this.show('info', title, message);
  }

  /**
   * Remove uma notificação específica pelo ID.
   *
   * @param id Identificador único da notificação
   */
  remove(id: string): void {
    this.toasts.update((current) => current.filter((t) => t.id !== id));
  }
}
