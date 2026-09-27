import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';
import { Toast } from '../../models/toast.model';

/**
 * Container global fixado no canto superior direito para renderização de toasts clínicos reativos.
 *
 * Utiliza semiótica cromática hospitalar (verde suave para sucesso, carmim para erro,
 * âmbar para alerta e azul institucional para informação).
 */
@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
})
export class ToastContainerComponent {
  /**
   * Instância injetada do serviço de notificações Toast.
   */
  readonly toastService = inject(ToastService);

  /**
   * Retorna as classes utilitárias do TailwindCSS de acordo com o tipo e severidade do toast.
   *
   * @param toast Objeto de notificação com tipo e mensagens
   * @returns String com classes CSS para background, borda e texto
   */
  getToastClasses(toast: Toast): string {
    switch (toast.type) {
      case 'success':
        return 'bg-emerald-50 border-emerald-200 text-emerald-900';
      case 'error':
        return 'bg-rose-50 border-rose-200 text-rose-900';
      case 'warning':
        return 'bg-amber-50 border-amber-200 text-amber-900';
      case 'info':
        return 'bg-blue-50 border-blue-200 text-blue-900';
    }
  }
}
