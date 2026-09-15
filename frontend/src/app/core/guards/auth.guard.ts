import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Guarda de rota para proteger páginas que exigem usuário autenticado.
 * Redireciona usuários com pendência de primeiro acesso para /first-login.
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
