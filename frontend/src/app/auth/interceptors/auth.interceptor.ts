import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP funcional para anexar automaticamente o Bearer Token JWT no header Authorization
 * de todas as requisições direcionadas à API RESTful do SIGEA.
 *
 * @param req  Requisição HTTP enviada.
 * @param next Próximo manipulador na cadeia de interceptores HTTP.
 * @returns Observable do fluxo de eventos HTTP com cabeçalho de autenticação anexado.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token && (req.url.startsWith('/api') || req.url.includes('/api/') || req.url.startsWith('api/'))) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(authReq);
  }

  return next(req);
};
