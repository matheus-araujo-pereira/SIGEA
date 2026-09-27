import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { LoadingService } from '../services/loading.service';

/**
 * Interceptor HTTP funcional para acionar e desativar automaticamente o overlay de carregamento global.
 *
 * Ignora requisições que possuam o cabeçalho 'X-Skip-Loading' ou chamadas de health check ('/ping').
 *
 * @param req  Requisição HTTP enviada.
 * @param next Próximo manipulador na cadeia de interceptores HTTP.
 * @returns Observable do fluxo de eventos HTTP.
 */
export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.headers.has('X-Skip-Loading') || req.url.includes('/ping')) {
    return next(req);
  }

  const loadingService = inject(LoadingService);
  loadingService.show();

  return next(req).pipe(
    finalize(() => {
      loadingService.hide();
    })
  );
};
