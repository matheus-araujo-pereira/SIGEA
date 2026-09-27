import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../../common/services/toast.service';
import { UserRole } from '../../user/models/user.model';

/**
 * Guarda de rota funcional para Controle de Acesso Baseado em Papéis (RBAC).
 *
 * Avalia se o usuário autenticado possui o perfil necessário configurado no `data: { roles: [...] }`.
 * Em caso de permissão insuficiente, emite Toast de alerta e redireciona para `/profile`.
 *
 * @param route Rota contendo as permissões requeridas nos metadados.
 * @returns Booleano ou UrlTree de redirecionamento.
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
