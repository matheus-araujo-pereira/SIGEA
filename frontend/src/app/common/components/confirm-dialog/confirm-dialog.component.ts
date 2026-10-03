import { Component, HostListener, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Modal de confirmação acessível reutilizável para operações críticas e destrutivas (norma SIGEA).
 *
 * Utiliza semiótica cromática hospitalar (carmim para ações destrutivas irreversíveis
 * e âmbar para confirmações de alerta/transição de status).
 */
@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
})
export class ConfirmDialogComponent {
  /**
   * Listener global de teclado para fechar o diálogo de confirmação via tecla Escape.
   */
  @HostListener('document:keydown.escape')
  handleEscape(): void {
    if (this.isOpen()) {
      this.onCancel();
    }
  }
  /**
   * Signal de entrada indicando se o modal está aberto e visível.
   */
  readonly isOpen = input<boolean>(false);

  /**
   * Título principal do diálogo de confirmação.
   */
  readonly title = input<string>('Confirmar Ação');

  /**
   * Mensagem explicativa ou de alerta sobre o impacto da operação.
   */
  readonly message = input<string>('Esta ação não poderá ser desfeita. Deseja continuar?');

  /**
   * Rótulo textual do botão afirmativo de confirmação.
   */
  readonly confirmText = input<string>('Confirmar');

  /**
   * Rótulo textual do botão neutro de cancelamento.
   */
  readonly cancelText = input<string>('Cancelar');

  /**
   * Indica se a ação é destrutiva (ex: exclusão definitiva), aplicando estilo carmim.
   */
  readonly isDestructive = input<boolean>(true);

  /** Emissor de evento disparado na confirmação afirmativa. */
  readonly confirmed = output<void>();
  /** Emissor de evento disparado no cancelamento da ação. */
  readonly cancelled = output<void>();
  /** Alias legado para confirmação. */
  readonly confirm = output<void>();
  /** Alias legado para cancelamento. */
  readonly cancel = output<void>();

  /**
   * Manipula o clique no botão afirmativo de confirmação e emite os eventos.
   *
   * @returns void
   */
  onConfirm(): void {
    this.confirmed.emit();
    this.confirm.emit();
  }

  /**
   * Manipula o clique no botão neutro de cancelamento e emite os eventos.
   *
   * @returns void
   */
  onCancel(): void {
    this.cancelled.emit();
    this.cancel.emit();
  }
}
