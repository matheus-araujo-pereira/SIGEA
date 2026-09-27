import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Guarda de rota funcional para a tela de Primeiro Acesso Obrigatório (`/first-login`).
 *
 * Garante que apenas usuários com `mustChangePassword === true` acessem esta tela.
 * Redireciona usuários anônimos para `/login` e usuários com senha já definida para `/profile`.
 *
 * @returns Booleano ou UrlTree de redirecionamento.
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
