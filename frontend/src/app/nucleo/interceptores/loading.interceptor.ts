import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { LoadingService } from '../servicos/loading.service';

/**
 * Interceptor HTTP funcional que notifica o `LoadingService` no início e fim de cada requisição.
 *
 * Utiliza o operador `finalize` do RxJS para garantir a diminuição da contagem de requisições ativas
 * tanto em casos de sucesso quanto em erros HTTP.
 *
 * @param req Requisição HTTP em trânsito.
 * @param next Handler para o próximo interceptor ou backend.
 */
export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  loadingService.mostrar();

  return next(req).pipe(finalize(() => loadingService.ocultar()));
};
