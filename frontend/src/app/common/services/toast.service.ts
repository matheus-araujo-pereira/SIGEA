import { Injectable, signal } from '@angular/core';
import { Toast, ToastType } from '../models/toast.model';

/**
 * Serviço reativo para emissão, gerenciamento e descarte de notificações Toast no SIGEA via Signals.
 */
@Injectable({
  providedIn: 'root',
})
export class ToastService {
  /**
   * Signal contendo a lista ordenada de notificações ativas exibidas na interface.
   */
  readonly toasts = signal<Toast[]>([]);

  /**
   * Emite uma nova notificação Toast com parâmetros customizados.
   *
   * @param type     Tipo da notificação clínica (success, error, warning, info)
   * @param title    Título em destaque da notificação
   * @param message  Mensagem explicativa ou detalhamento textual
   * @param duration Duração da exibição em milissegundos (padrão: 4000ms)
   * @returns void
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
   * Emite uma notificação Toast afirmativa de sucesso na cor verde clínica.
   *
   * @param title   Título da notificação
   * @param message Mensagem explicativa
   * @returns void
   */
  success(title: string, message: string = ''): void {
    this.show('success', title, message);
  }

  /**
   * Emite uma notificação Toast de erro crítico na cor carmim hospitalar (5000ms).
   *
   * @param title   Título da notificação
   * @param message Mensagem explicativa
   * @returns void
   */
  error(title: string, message: string = ''): void {
    this.show('error', title, message, 5000);
  }

  /**
   * Emite uma notificação Toast de alerta ou advertência na cor âmbar.
   *
   * @param title   Título da notificação
   * @param message Mensagem explicativa
   * @returns void
   */
  warning(title: string, message: string = ''): void {
    this.show('warning', title, message);
  }

  /**
   * Emite uma notificação Toast informativa na cor azul primário.
   *
   * @param title   Título da notificação
   * @param message Mensagem explicativa
   * @returns void
   */
  info(title: string, message: string = ''): void {
    this.show('info', title, message);
  }

  /**
   * Descarta e remove uma notificação específica da lista de toasts ativos.
   *
   * @param id Identificador único da notificação a ser removida
   * @returns void
   */
  remove(id: string): void {
    this.toasts.update((current) => current.filter((t) => t.id !== id));
  }
}
