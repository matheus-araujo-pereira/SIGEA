import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageResponse } from '../../../core/models/page.model';

/**
 * Componente de controle de paginação padronizado para 10 registros por página.
 */
@Component({
  selector: 'app-data-table-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (pageData()) {
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4 px-6 py-4 bg-white border-t border-slate-200">
        <!-- Indicador de Registros -->
        <div class="text-xs text-slate-500 font-medium">
          @if (totalElements() === 0) {
            Nenhum registro encontrado
          } @else {
            Exibindo <span class="font-semibold text-slate-700">{{ startItem() }}</span> a
            <span class="font-semibold text-slate-700">{{ endItem() }}</span> de
            <span class="font-semibold text-slate-700">{{ totalElements() }}</span> registros (10 por página)
          }
        </div>

        <!-- Controles de Navegação -->
        <div class="flex items-center gap-1">
          <!-- Primeira Página -->
          <button
            type="button"
            (click)="onPage(0)"
            [disabled]="isFirst() || loading()"
            class="p-2 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            title="Primeira página"
            aria-label="Primeira página"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
            </svg>
          </button>

          <!-- Página Anterior -->
          <button
            type="button"
            (click)="onPage(currentPage() - 1)"
            [disabled]="isFirst() || loading()"
            class="p-2 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            title="Página anterior"
            aria-label="Página anterior"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>

          <!-- Indicador Numérico -->
          <span class="px-3 py-1 text-xs font-semibold text-slate-700 bg-slate-100 rounded-md border border-slate-200">
            Página {{ currentPage() + 1 }} de {{ totalPages() > 0 ? totalPages() : 1 }}
          </span>

          <!-- Próxima Página -->
          <button
            type="button"
            (click)="onPage(currentPage() + 1)"
            [disabled]="isLast() || loading()"
            class="p-2 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            title="Próxima página"
            aria-label="Próxima página"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>

          <!-- Última Página -->
          <button
            type="button"
            (click)="onPage(totalPages() - 1)"
            [disabled]="isLast() || loading()"
            class="p-2 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            title="Última página"
            aria-label="Última página"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>
    }
  `,
})
export class DataTablePaginationComponent {
  readonly pageData = input<PageResponse<unknown> | null>(null);
  readonly loading = input<boolean>(false);

  readonly pageChange = output<number>();

  get currentPage(): () => number {
    return () => this.pageData()?.page ?? 0;
  }

  get totalPages(): () => number {
    return () => this.pageData()?.totalPages ?? 0;
  }

  get totalElements(): () => number {
    return () => this.pageData()?.totalElements ?? 0;
  }

  get isFirst(): () => boolean {
    return () => this.pageData()?.first ?? true;
  }

  get isLast(): () => boolean {
    return () => this.pageData()?.last ?? true;
  }

  get startItem(): () => number {
    return () => {
      const total = this.totalElements();
      if (total === 0) return 0;
      return this.currentPage() * (this.pageData()?.size ?? 10) + 1;
    };
  }

  get endItem(): () => number {
    return () => {
      const total = this.totalElements();
      if (total === 0) return 0;
      return Math.min(this.startItem() + (this.pageData()?.content?.length ?? 0) - 1, total);
    };
  }

  onPage(page: number): void {
    if (page >= 0 && page < this.totalPages() && page !== this.currentPage()) {
      this.pageChange.emit(page);
    }
  }
}
