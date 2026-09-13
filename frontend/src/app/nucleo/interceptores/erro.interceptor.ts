import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AutenticacaoService } from '../../modulos/autenticacao/servicos/autenticacao.service';

/**
 * Interceptor HTTP para captura e tratamento de erros globais da aplicação.
 * Trata erros 401 (Não Autorizado) e 403 (Proibido) com logout defensivo e redirecionamento,
 * além de emitir notificações Toast via MessageService.
 */
export const erroInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const messageService = inject(MessageService);
  const authService = inject(AutenticacaoService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Ignora rotas públicas de login para não sobrepor mensagem de erro do formulário
      const ehRotaAutenticacao = req.url.includes('/api/autenticacao/entrar');

      if (error.status === 401 && !ehRotaAutenticacao) {
        messageService.add({
          severity: 'warn',
          summary: 'Sessão Expirada',
          detail: 'Sua sessão de acesso expirou. Por favor, autentique-se novamente.',
          life: 5000,
        });
        authService.limparSessao();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        messageService.add({
          severity: 'error',
          summary: 'Acesso Não Autorizado',
          detail: 'Seu perfil institucional não possui permissão para executar esta operação.',
          life: 5000,
        });
      } else if (error.status === 0) {
        messageService.add({
          severity: 'error',
          summary: 'Servidor Indisponível',
          detail: 'Não foi possível conectar ao backend do SIGEA-GTT. Verifique sua conexão.',
          life: 5000,
        });
      }

      return throwError(() => error);
    }),
  );
};
