/**
 * Estrutura genérica padronizada de resposta de sucesso emitida pela API RESTful Spring Boot.
 *
 * @template T Tipo do payload de dados contido na resposta.
 */
export interface ApiResponse<T> {
  /** Indica se a operação foi bem-sucedida. */
  success: boolean;
  /** Mensagem informativa ou de feedback do servidor. */
  message: string;
  /** Payload contendo os dados resultantes da operação. */
  data: T;
  /** Carimbo de data/hora ISO-8601 do processamento no servidor. */
  timestamp: string;
}

/**
 * Detalhe de violação de validação em campo individual de formulário (RFC 7807).
 */
export interface FieldErrorDetail {
  /** Nome do campo ou propriedade violada. */
  field: string;
  /** Mensagem descritiva da regra violada. */
  message: string;
  /** Valor rejeitado recebido na requisição. */
  rejectedValue?: unknown;
}

/**
 * Estrutura canônica de erro da API do SIGEA em conformidade com RFC 7807 (ProblemDetail).
 */
export interface ErrorResponse {
  /** Sinalizador booleano indicando falha na operação (sempre false). */
  success: boolean;
  /** Código de status HTTP (ex: 400, 401, 403, 404, 422, 500). */
  status: number;
  /** Título abreviado do erro HTTP (ex: 'Bad Request', 'Unauthorized'). */
  error: string;
  /** Mensagem detalhada com a causa da falha ou regra de negócio violada. */
  message: string;
  /** URI ou endpoint onde o erro ocorreu. */
  path: string;
  /** Lista opcional de violações em campos específicos. */
  fieldErrors?: FieldErrorDetail[];
  /** Carimbo de data/hora ISO-8601 da ocorrência do erro. */
  timestamp: string;
}
