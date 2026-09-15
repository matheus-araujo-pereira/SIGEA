/**
 * Estrutura genérica de resposta de sucesso da API.
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

/**
 * Detalhe de violação em campo individual de formulário.
 */
export interface FieldErrorDetail {
  field: string;
  message: string;
  rejectedValue?: unknown;
}

/**
 * Estrutura de erro da API.
 */
export interface ErrorResponse {
  success: boolean;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: FieldErrorDetail[];
  timestamp: string;
}
