/**
 * Modelo de resposta paginada padronizada (exatamente 10 registros por página, norma SIGEA).
 *
 * @template T Tipo dos itens contidos na listagem paginada.
 */
export interface PageResponse<T> {
  /** Array de elementos correspondentes à página atual. */
  content: T[];
  /** Índice da página atual (0-based). */
  page: number;
  /** Quantidade máxima de registros por página (padrão institucional: 10). */
  size: number;
  /** Número total de elementos registrados em todas as páginas. */
  totalElements: number;
  /** Quantidade total de páginas disponíveis. */
  totalPages: number;
  /** Indica se a página atual é a primeira. */
  first: boolean;
  /** Indica se a página atual é a última. */
  last: boolean;
}
