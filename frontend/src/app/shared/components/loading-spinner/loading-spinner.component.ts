import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../../core/services/loading.service';

/**
 * Componente visual de overlay de carregamento global reativo com Signals.
 */
@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (loadingService.isLoading()) {
      <div
        class="fixed inset-0 z-50 flex flex-col items-center justify-center bg-slate-900/40 backdrop-blur-sm transition-opacity"
        role="status"
        aria-live="polite"
        aria-label="Carregando"
      >
        <div class="relative flex items-center justify-center">
          <div class="w-16 h-16 rounded-full border-4 border-clinical-200 border-t-clinical-600 animate-spin"></div>
          <div class="absolute w-8 h-8 rounded-full bg-clinical-50 flex items-center justify-center shadow-inner">
            <svg class="w-4 h-4 text-clinical-600 animate-pulse" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
        </div>
        <p class="mt-4 text-sm font-semibold text-white tracking-wide drop-shadow">Processando requisição...</p>
      </div>
    }
  `,
})
export class LoadingSpinnerComponent {
  readonly loadingService = inject(LoadingService);
}
