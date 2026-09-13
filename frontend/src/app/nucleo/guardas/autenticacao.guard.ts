import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AutenticacaoService } from '../../modulos/autenticacao/servicos/autenticacao.service';

/**
 * Guarda de rota funcional para proteção de rotas privadas que exigem usuário autenticado.
 *
 * Se o usuário não possuir credencial válida, redireciona para `/login`.
 * Se o usuário precisar redefinir senha no primeiro acesso (`requerPrimeiroAcesso`), redireciona para `/primeiro-acesso`.
 *
 * @returns `true` se o acesso for autorizado, ou `UrlTree` com o redirecionamento adequado.
 */
export const autenticacaoGuard: CanActivateFn = (): boolean | UrlTree => {
  const auth = inject(AutenticacaoService);
  const router = inject(Router);

  if (!auth.estaAutenticado()) {
    return router.createUrlTree(['/login']);
  }

  if (auth.requerPrimeiroAcesso()) {
    return router.createUrlTree(['/primeiro-acesso']);
  }

  return true;
};
