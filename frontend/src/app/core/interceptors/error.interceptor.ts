import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP funcional para captura centralizada e feedback visual de erros da API.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (req.headers.has('X-Silent-Error') || req.url.includes('/ping')) {
        return throwError(() => error);
      }

      let errorMessage = 'Ocorreu um erro inesperado. Tente novamente.';

      if (error.error && typeof error.error === 'object') {
        if (error.error.message) {
          errorMessage = error.error.message;
        } else if (error.error.fieldErrors && error.error.fieldErrors.length > 0) {
          errorMessage = error.error.fieldErrors
            .map((fe: { field: string; message: string }) => `${fe.field}: ${fe.message}`)
            .join(' | ');
        }
      }

      switch (error.status) {
        case 401:
          if (!req.url.includes('/api/auth/login')) {
            toastService.error('Sessão Expirada', 'Por favor, realize o login novamente.');
            authService.logout();
          } else {
            toastService.error('Falha no Login', errorMessage);
          }
          break;
        case 403:
          toastService.error('Acesso Não Autorizado', error.error?.message || 'Você não tem permissão para esta ação.');
          break;
        case 404:
          toastService.warning('Não Encontrado', error.error?.message || 'O recurso solicitado não foi localizado.');
          break;
        case 400:
          toastService.error('Erro de Validação', errorMessage);
          break;
        case 500:
          toastService.error('Erro do Servidor', 'Ocorreu uma instabilidade interna. Tente novamente.');
          break;
        default:
          if (error.status === 0) {
            toastService.error('Sem Conexão', 'Não foi possível conectar ao servidor SIGEA-GTT.');
          } else {
            toastService.error(`Erro (${error.status})`, errorMessage);
          }
      }

      return throwError(() => error);
    })
  );
};
