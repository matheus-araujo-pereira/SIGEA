import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Guarda de rota específico para a tela de primeiro login obrigatório.
 * Impede acesso se o usuário não precisa alterar a senha.
 */
export const firstLoginGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (!authService.mustChangePassword()) {
    return router.createUrlTree(['/profile']);
  }

  return true;
};
