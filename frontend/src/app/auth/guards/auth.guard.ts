import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Guarda de rota funcional para restringir acesso apenas a usuários autenticados.
 *
 * Bloqueia a navegação de usuários anônimos redirecionando-os para `/login`.
 * Se o usuário autenticado possuir a flag `mustChangePassword`, redireciona compulsoriamente para `/first-login`.
 *
 * @param route Informações da rota ativada.
 * @param state Snapshot do estado do roteador no momento da ativação.
 * @returns Booleano ou UrlTree de redirecionamento.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.mustChangePassword() && state.url !== '/first-login') {
    return router.createUrlTree(['/first-login']);
  }

  return true;
};
