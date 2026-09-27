import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageResponse } from '../../models/page.model';

/**
 * Componente de controle e navegação de paginação universal para tabelas de dados.
 *
 * Em estrita conformidade com a Norma SIGEA (exatamente 10 registros por página,
 * botões de primeira/anterior/próxima/última página e indicador numérico).
 */
@Component({
  selector: 'app-data-table-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-table.component.html',
})
export class DataTablePaginationComponent {
  /**
   * Dados da página atual emitidos pelo backend via PageResponse.
   */
  readonly pageData = input<PageResponse<unknown> | null>(null);

  /**
   * Sinalizador indicando se a tabela está em estado de carregamento assíncrono.
   */
  readonly loading = input<boolean>(false);

  /**
   * Evento emitido quando o usuário solicita transição para outra página.
   */
  readonly pageChange = output<number>();

  /**
   * Retorna o índice numérico da página atual (0-based).
   *
   * @returns Função retornando o número da página atual.
   */
  get currentPage(): () => number {
    return () => this.pageData()?.page ?? 0;
  }

  /**
   * Retorna o número total de páginas disponíveis.
   *
   * @returns Função retornando a contagem de páginas.
   */
  get totalPages(): () => number {
    return () => this.pageData()?.totalPages ?? 0;
  }

  /**
   * Retorna a quantidade total de registros encontrados na base.
   *
   * @returns Função retornando o total de elementos.
   */
  get totalElements(): () => number {
    return () => this.pageData()?.totalElements ?? 0;
  }

  /**
   * Informa se a visualização atual corresponde à primeira página.
   *
   * @returns Função booleana indicando primeira página.
   */
  get isFirst(): () => boolean {
    return () => this.pageData()?.first ?? true;
  }

  /**
   * Informa se a visualização atual corresponde à última página.
   *
   * @returns Função booleana indicando última página.
   */
  get isLast(): () => boolean {
    return () => this.pageData()?.last ?? true;
  }

  /**
   * Retorna o índice ordinal do primeiro registro exibido na página atual.
   *
   * @returns Função retornando o número inicial.
   */
  get startItem(): () => number {
    return () => {
      const total = this.totalElements();
      if (total === 0) return 0;
      return this.currentPage() * (this.pageData()?.size ?? 10) + 1;
    };
  }

  /**
   * Retorna o índice ordinal do último registro exibido na página atual.
   *
   * @returns Função retornando o número final.
   */
  get endItem(): () => number {
    return () => {
      const total = this.totalElements();
      if (total === 0) return 0;
      return Math.min(this.startItem() + (this.pageData()?.content?.length ?? 0) - 1, total);
    };
  }

  /**
   * Dispara a alteração de página validando os limites permitidos.
   *
   * @param page Novo índice de página selecionado (0-based)
   * @returns void
   */
  onPage(page: number): void {
    if (page >= 0 && page < this.totalPages() && page !== this.currentPage()) {
      this.pageChange.emit(page);
    }
  }
}
