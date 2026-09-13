import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot, UrlTree } from '@angular/router';
import { AutenticacaoService } from '../../modulos/autenticacao/servicos/autenticacao.service';
import { PerfilUsuario } from '../../modulos/usuario/modelos/usuario.modelos';

/**
 * Guarda de rota baseado em Perfis Institucionais (RBAC).
 *
 * Valida se o perfil do usuário logado consta na lista de perfis permitidos configurada
 * na propriedade `data.perfis` da rota.
 * Caso não seja permitido, redireciona o usuário para sua respectiva rota padrão.
 *
 * @param route Snapshot da rota ativada contendo os perfis permitidos em `route.data['perfis']`.
 * @returns `true` se o perfil do usuário for autorizado, ou `UrlTree` com a rota padrão do usuário.
 */
export const perfilGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const auth = inject(AutenticacaoService);
  const router = inject(Router);

  const perfisPermitidos = route.data['perfis'] as PerfilUsuario[] | undefined;
  const perfilAtual = auth.usuarioLogado()?.perfil;

  if (perfilAtual && perfisPermitidos?.includes(perfilAtual)) {
    return true;
  }

  return router.createUrlTree([auth.obterRotaPadrao()]);
};
