import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth.guard';
import { firstLoginGuard } from './auth/guards/first-login.guard';
import { roleGuard } from './auth/guards/role.guard';

/**
 * Rotas da aplicação SIGEA com divisão de código (Lazy Loading) para máxima performance.
 * Estrutura 100% espelhada aos domínios do backend Spring Boot.
 */
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/components/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'first-login',
    loadComponent: () =>
      import('./auth/components/first-login/first-login.component').then((m) => m.FirstLoginComponent),
    canActivate: [firstLoginGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./common/components/app-shell/app-shell.component').then((m) => m.AppShellComponent),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'gtt/modules',
      },
      // Rotas Educacionais / Consulta Clínica (Acesso a todos os perfis autenticados)
      {
        path: 'gtt/modules',
        loadComponent: () =>
          import('./gtt/module/components/module-catalog/module-catalog.component').then((m) => m.ModuleCatalogComponent),
      },
      {
        path: 'gtt/triggers',
        loadComponent: () =>
          import('./gtt/trigger/components/trigger-guide/trigger-guide.component').then((m) => m.TriggerGuideComponent),
      },
      {
        path: 'gtt/severities',
        loadComponent: () =>
          import('./gtt/severity/components/severity-guide/severity-guide.component').then((m) => m.SeverityGuideComponent),
      },
      // Gestão de Usuários (Admin)
      {
        path: 'users',
        loadComponent: () =>
          import('./user/components/user-list/user-list.component').then((m) => m.UserListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão Administrativa GTT (Admin)
      {
        path: 'admin/gtt/modules',
        loadComponent: () =>
          import('./gtt/module/components/module-list/module-list.component').then((m) => m.ModuleListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/triggers',
        loadComponent: () =>
          import('./gtt/trigger/components/trigger-list/trigger-list.component').then((m) => m.TriggerListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/severities',
        loadComponent: () =>
          import('./gtt/severity/components/severity-list/severity-list.component').then((m) => m.SeverityListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão de Turmas e Atividades Acadêmicas (Admin, Professor e Estudante)
      {
        path: 'academic/classes/my-classes',
        loadComponent: () =>
          import('./academic/clazz/components/class-list/class-list.component').then((m) => m.ClassListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes',
        loadComponent: () =>
          import('./academic/clazz/components/class-list/class-list.component').then((m) => m.ClassListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes/:id',
        loadComponent: () =>
          import('./academic/clazz/components/class-detail/class-detail.component').then((m) => m.ClassDetailComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes/:classId/dashboard',
        loadComponent: () =>
          import('./academic/dashboard/components/class-dashboard/class-dashboard.component').then((m) => m.ClassDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/new',
        loadComponent: () =>
          import('./academic/activity/components/activity-form/activity-form.component').then((m) => m.ActivityFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:id/edit',
        loadComponent: () =>
          import('./academic/activity/components/activity-form/activity-form.component').then((m) => m.ActivityFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:activityId/grading',
        loadComponent: () =>
          import('./academic/activity/components/activity-grading-list/activity-grading-list.component').then((m) => m.ActivityGradingListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/submissions/:submissionId/grade',
        loadComponent: () =>
          import('./academic/activity/components/activity-grading-detail/activity-grading-detail.component').then((m) => m.ActivityGradingDetailComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      // Portal do Estudante (Atividades e Resolução)
      {
        path: 'academic/student/activities',
        loadComponent: () =>
          import('./academic/activity/components/student-activities/student-activities.component').then((m) => m.StudentActivitiesComponent),
        canActivate: [roleGuard],
        data: { roles: ['STUDENT'] },
      },
      {
        path: 'academic/activities/my-activities',
        redirectTo: 'academic/student/activities',
        pathMatch: 'full',
      },
      {
        path: 'academic/activities/:activityId/resolve',
        loadComponent: () =>
          import('./academic/activity/components/activity-resolution/activity-resolution.component').then((m) => m.ActivityResolutionComponent),
        canActivate: [roleGuard],
        data: { roles: ['STUDENT'] },
      },
      {
        path: 'academic/submissions/:submissionId/feedback',
        loadComponent: () =>
          import('./academic/activity/components/submission-feedback/submission-feedback.component').then((m) => m.SubmissionFeedbackComponent),
        canActivate: [roleGuard],
        data: { roles: ['STUDENT', 'PROFESSOR', 'ADMIN'] },
      },
      // Perfil do Usuário
      {
        path: 'profile',
        loadComponent: () =>
          import('./user/components/profile/profile.component').then((m) => m.ProfileComponent),
      },
      // Módulo da Escala de Usabilidade do Sistema (SUS) - Brooke (1996) e Bangor et al. (2008)
      {
        path: 'sus/evaluation',
        loadComponent: () =>
          import('./academic/sus/components/sus-form/sus-form.component').then((m) => m.SusFormComponent),
      },
      {
        path: 'academic/classes/:classId/sus',
        loadComponent: () =>
          import('./academic/sus/components/sus-dashboard/sus-dashboard.component').then((m) => m.SusDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'admin/sus',
        loadComponent: () =>
          import('./academic/sus/components/sus-dashboard/sus-dashboard.component').then((m) => m.SusDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
