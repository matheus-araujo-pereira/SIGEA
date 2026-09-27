import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Modal de confirmação reutilizável para operações críticas e destrutivas.
 */
@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
})
export class ConfirmDialogComponent {
  readonly isOpen = input<boolean>(false);
  readonly title = input<string>('Confirmar Ação');
  readonly message = input<string>('Esta ação não poderá ser desfeita. Deseja continuar?');
  readonly confirmText = input<string>('Confirmar');
  readonly cancelText = input<string>('Cancelar');
  readonly isDestructive = input<boolean>(true);

  readonly confirmed = output<void>();
  readonly cancelled = output<void>();
  readonly confirm = output<void>();
  readonly cancel = output<void>();

  onConfirm(): void {
    this.confirmed.emit();
    this.confirm.emit();
  }

  onCancel(): void {
    this.cancelled.emit();
    this.cancel.emit();
  }
}
