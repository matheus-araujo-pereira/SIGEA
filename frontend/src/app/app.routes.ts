import { Routes, CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { MainLayoutComponent } from './layout/main-layout.component';
import { autenticacaoGuard } from './nucleo/guardas/autenticacao.guard';
import { perfilGuard } from './nucleo/guardas/perfil.guard';
import { AutenticacaoService } from './modulos/autenticacao/servicos/autenticacao.service';

/**
 * Guarda de redirecionamento dinâmico baseado no perfil do usuário autenticado.
 */
const redirecionamentoInicialGuard: CanActivateFn = () => {
  const auth = inject(AutenticacaoService);
  const router = inject(Router);
  router.navigate([auth.obterRotaPadrao()]);
  return false;
};

/**
 * Configuração das rotas da aplicação SIGEA-GTT com Code-Splitting e Lazy Loading assíncrono.
 */
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./modulos/autenticacao/componentes/login/login.component').then(
        (m) => m.LoginComponent,
      ),
  },
  {
    path: 'primeiro-acesso',
    loadComponent: () =>
      import('./modulos/autenticacao/componentes/primeiro-acesso/primeiro-acesso.component').then(
        (m) => m.PrimeiroAcessoComponent,
      ),
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [autenticacaoGuard],
    children: [
      { path: '', canActivate: [redirecionamentoInicialGuard], children: [] },

      // Administração de Usuários
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./modulos/usuario/componentes/gerenciar-usuarios/gerenciar-usuarios.component').then(
            (m) => m.GerenciarUsuariosComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'usuarios/novo',
        loadComponent: () =>
          import('./modulos/usuario/componentes/formulario-usuario/formulario-usuario.component').then(
            (m) => m.FormularioUsuarioComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'usuarios/:id/editar',
        loadComponent: () =>
          import('./modulos/usuario/componentes/formulario-usuario/formulario-usuario.component').then(
            (m) => m.FormularioUsuarioComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },

      // Meu Perfil (acessível por qualquer usuário autenticado)
      {
        path: 'perfil',
        loadComponent: () =>
          import('./modulos/usuario/componentes/meu-perfil/meu-perfil.component').then(
            (m) => m.MeuPerfilComponent,
          ),
      },

      // Gatilhos GTT
      {
        path: 'gatilhos',
        loadComponent: () =>
          import('./modulos/gtt/componentes/gerenciar-gatilhos/gerenciar-gatilhos.component').then(
            (m) => m.GerenciarGatilhosComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'gatilhos/novo',
        loadComponent: () =>
          import('./modulos/gtt/componentes/formulario-gatilho/formulario-gatilho.component').then(
            (m) => m.FormularioGatilhoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'gatilhos/:id/editar',
        loadComponent: () =>
          import('./modulos/gtt/componentes/formulario-gatilho/formulario-gatilho.component').then(
            (m) => m.FormularioGatilhoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },

      // Módulos GTT
      {
        path: 'modulos',
        loadComponent: () =>
          import('./modulos/gtt/componentes/gerenciar-modulos/gerenciar-modulos.component').then(
            (m) => m.GerenciarModulosComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'modulos/novo',
        loadComponent: () =>
          import('./modulos/gtt/componentes/formulario-modulo/formulario-modulo.component').then(
            (m) => m.FormularioModuloComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'modulos/:id/editar',
        loadComponent: () =>
          import('./modulos/gtt/componentes/formulario-modulo/formulario-modulo.component').then(
            (m) => m.FormularioModuloComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },

      // Unidades HU
      {
        path: 'unidades',
        loadComponent: () =>
          import('./modulos/unidade/componentes/gerenciar-unidades/gerenciar-unidades.component').then(
            (m) => m.GerenciarUnidadesComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'unidades/novo',
        loadComponent: () =>
          import('./modulos/unidade/componentes/formulario-unidade/formulario-unidade.component').then(
            (m) => m.FormularioUnidadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'unidades/:id/editar',
        loadComponent: () =>
          import('./modulos/unidade/componentes/formulario-unidade/formulario-unidade.component').then(
            (m) => m.FormularioUnidadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },

      // Turmas - Administração
      {
        path: 'turmas',
        loadComponent: () =>
          import('./modulos/turma/componentes/gerenciar-turmas/turmas.component').then(
            (m) => m.TurmasComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'turmas/novo',
        loadComponent: () =>
          import('./modulos/turma/componentes/formulario-turma/formulario-turma.component').then(
            (m) => m.FormularioTurmaComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'turmas/:id/editar',
        loadComponent: () =>
          import('./modulos/turma/componentes/formulario-turma/formulario-turma.component').then(
            (m) => m.FormularioTurmaComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'turmas/:id/alunos',
        loadComponent: () =>
          import('./modulos/turma/componentes/alunos-turma/alunos-turma.component').then(
            (m) => m.AlunosTurmaComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },

      // Minhas Turmas - Professor
      {
        path: 'minhas-turmas',
        loadComponent: () =>
          import('./modulos/turma/componentes/minhas-turmas/minhas-turmas.component').then(
            (m) => m.MinhasTurmasComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['PROFESSOR'] },
      },

      // Casos Clínicos & Prontuários Simulados
      {
        path: 'casos-clinicos',
        loadComponent: () =>
          import('./modulos/educacional/componentes/casos-clinicos/gerenciar-casos-clinicos/gerenciar-casos-clinicos.component').then(
            (m) => m.GerenciarCasosClinicosComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'casos-clinicos/novo',
        loadComponent: () =>
          import('./modulos/educacional/componentes/casos-clinicos/formulario-caso-clinico/formulario-caso-clinico.component').then(
            (m) => m.FormularioCasoClinicoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'casos-clinicos/:id/editar',
        loadComponent: () =>
          import('./modulos/educacional/componentes/casos-clinicos/formulario-caso-clinico/formulario-caso-clinico.component').then(
            (m) => m.FormularioCasoClinicoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },

      // Atividades Educacionais
      {
        path: 'atividades',
        loadComponent: () =>
          import('./modulos/educacional/componentes/atividades/gerenciar-atividades/gerenciar-atividades.component').then(
            (m) => m.GerenciarAtividadesComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'atividades/novo',
        loadComponent: () =>
          import('./modulos/educacional/componentes/atividades/formulario-atividade/formulario-atividade.component').then(
            (m) => m.FormularioAtividadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'atividades/:id/editar',
        loadComponent: () =>
          import('./modulos/educacional/componentes/atividades/formulario-atividade/formulario-atividade.component').then(
            (m) => m.FormularioAtividadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'atividades/:id/painel',
        loadComponent: () =>
          import('./modulos/educacional/componentes/atividades/painel-atividade/painel-atividade.component').then(
            (m) => m.PainelAtividadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'atividades/correcoes',
        loadComponent: () =>
          import('./modulos/educacional/componentes/avaliacoes/painel-correcoes/painel-correcoes.component').then(
            (m) => m.PainelCorrecoesComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },
      {
        path: 'avaliacoes',
        redirectTo: 'atividades/correcoes',
        pathMatch: 'full',
      },

      // Correção e Avaliação Docente
      {
        path: 'submissoes/:id/corrigir',
        loadComponent: () =>
          import('./modulos/educacional/componentes/avaliacoes/corrigir-submissao/corrigir-submissao.component').then(
            (m) => m.CorrigirSubmissaoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR', 'PROFESSOR'] },
      },

      // Ambiente do Aluno
      {
        path: 'minhas-atividades',
        loadComponent: () =>
          import('./modulos/educacional/componentes/aluno/minhas-atividades/minhas-atividades.component').then(
            (m) => m.MinhasAtividadesComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ALUNO', 'PROFESSOR', 'ADMINISTRADOR'] },
      },
      {
        path: 'atividades/:id/executar',
        loadComponent: () =>
          import('./modulos/educacional/componentes/aluno/execucao-atividade/execucao-atividade.component').then(
            (m) => m.ExecucaoAtividadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ALUNO', 'PROFESSOR', 'ADMINISTRADOR'] },
      },
      {
        path: 'submissoes/:id/resultado',
        loadComponent: () =>
          import('./modulos/educacional/componentes/aluno/resultado-atividade/resultado-atividade.component').then(
            (m) => m.ResultadoAtividadeComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ALUNO', 'PROFESSOR', 'ADMINISTRADOR'] },
      },

      // Indicadores Epidemiológicos (somente administradores)
      {
        path: 'indicadores',
        loadComponent: () =>
          import('./modulos/indicadores/componentes/dashboard-indicadores/indicadores.component').then(
            (m) => m.IndicadoresComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'indicadores/quadro-resumo',
        loadComponent: () =>
          import('./modulos/indicadores/componentes/quadro-resumo/quadro-resumo.component').then(
            (m) => m.QuadroResumoComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
      {
        path: 'indicadores/gatilhos',
        loadComponent: () =>
          import('./modulos/indicadores/componentes/rastreabilidade-gatilhos/rastreabilidade-gatilhos.component').then(
            (m) => m.RastreabilidadeGatilhosComponent,
          ),
        canActivate: [perfilGuard],
        data: { perfis: ['ADMINISTRADOR'] },
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
