import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { UserRole } from '../models/user.model';

/**
 * Guarda de rota para controle de permissões por perfil (RBAC).
 */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  const expectedRoles = route.data['roles'] as UserRole[] | undefined;
  const user = authService.currentUser();

  if (!user || !expectedRoles || !expectedRoles.includes(user.role)) {
    toastService.error('Acesso Restrito', 'Seu perfil não possui autorização para acessar esta página.');
    return router.createUrlTree(['/profile']);
  }

  return true;
};
