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
  templateUrl: './data-table.component.html',
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
