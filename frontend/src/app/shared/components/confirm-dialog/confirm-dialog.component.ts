import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Modal de confirmação reutilizável para operações críticas e destrutivas.
 */
@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (isOpen()) {
      <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-opacity"
        role="dialog"
        aria-modal="true"
        [attr.aria-labelledby]="'dialog-title'"
      >
        <div
          class="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100 transform transition-all animate-in fade-in zoom-in-95 duration-200"
        >
          <!-- Cabeçalho com Ícone -->
          <div class="flex items-center gap-4">
            <div
              class="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0"
              [ngClass]="isDestructive() ? 'bg-rose-100 text-rose-600' : 'bg-amber-100 text-amber-600'"
            >
              @if (isDestructive()) {
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              } @else {
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              }
            </div>
            <div>
              <h3 id="dialog-title" class="text-lg font-bold text-slate-900 leading-snug">{{ title() }}</h3>
              <p class="text-xs text-slate-500 mt-0.5">Ação com impacto no sistema</p>
            </div>
          </div>

          <!-- Mensagem -->
          <p class="mt-4 text-sm text-slate-600 leading-relaxed">{{ message() }}</p>

          <!-- Ações -->
          <div class="mt-6 flex items-center justify-end gap-3">
            <button
              type="button"
              (click)="onCancel()"
              class="btn-secondary"
            >
              {{ cancelText() }}
            </button>
            <button
              type="button"
              (click)="onConfirm()"
              [ngClass]="isDestructive() ? 'btn-danger' : 'btn-primary'"
            >
              {{ confirmText() }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
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

  onConfirm(): void {
    this.confirmed.emit();
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
