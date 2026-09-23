import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { firstLoginGuard } from './core/guards/first-login.guard';
import { roleGuard } from './core/guards/role.guard';

/**
 * Rotas da aplicação SIGEA com divisão de código (Lazy Loading) para máxima performance.
 */
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'first-login',
    loadComponent: () =>
      import('./features/auth/first-login/first-login.component').then((m) => m.FirstLoginComponent),
    canActivate: [firstLoginGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./shared/components/app-shell/app-shell.component').then((m) => m.AppShellComponent),
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
          import('./features/gtt/modules/module-catalog/module-catalog.component').then((m) => m.ModuleCatalogComponent),
      },
      {
        path: 'gtt/triggers',
        loadComponent: () =>
          import('./features/gtt/triggers/trigger-guide/trigger-guide.component').then((m) => m.TriggerGuideComponent),
      },
      {
        path: 'gtt/severities',
        loadComponent: () =>
          import('./features/gtt/severities/severity-guide/severity-guide.component').then((m) => m.SeverityGuideComponent),
      },
      // Gestão de Usuários (Admin)
      {
        path: 'users',
        loadComponent: () =>
          import('./features/users/user-list/user-list.component').then((m) => m.UserListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão Administrativa GTT (Admin)
      {
        path: 'admin/gtt/modules',
        loadComponent: () =>
          import('./features/gtt/modules/module-list/module-list.component').then((m) => m.ModuleListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/triggers',
        loadComponent: () =>
          import('./features/gtt/triggers/trigger-list/trigger-list.component').then((m) => m.TriggerListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/severities',
        loadComponent: () =>
          import('./features/gtt/severities/severity-list/severity-list.component').then((m) => m.SeverityListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão de Turmas e Atividades Acadêmicas (Admin, Professor e Estudante)
      {
        path: 'academic/classes/my-classes',
        loadComponent: () =>
          import('./features/academic/classes/class-list/class-list.component').then((m) => m.ClassListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes',
        loadComponent: () =>
          import('./features/academic/classes/class-list/class-list.component').then((m) => m.ClassListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes/:id',
        loadComponent: () =>
          import('./features/academic/classes/class-detail/class-detail.component').then((m) => m.ClassDetailComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] },
      },
      {
        path: 'academic/classes/:classId/dashboard',
        loadComponent: () =>
          import('./features/academic/dashboard/class-dashboard/class-dashboard.component').then((m) => m.ClassDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/new',
        loadComponent: () =>
          import('./features/academic/activities/activity-form/activity-form.component').then((m) => m.ActivityFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:id/edit',
        loadComponent: () =>
          import('./features/academic/activities/activity-form/activity-form.component').then((m) => m.ActivityFormComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:activityId/grading',
        loadComponent: () =>
          import('./features/academic/activities/activity-grading-list/activity-grading-list.component').then((m) => m.ActivityGradingListComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/submissions/:submissionId/grade',
        loadComponent: () =>
          import('./features/academic/activities/activity-grading-detail/activity-grading-detail.component').then((m) => m.ActivityGradingDetailComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      // Portal do Estudante (Atividades e Resolução)
      {
        path: 'academic/student/activities',
        loadComponent: () =>
          import('./features/academic/activities/student-activities/student-activities.component').then((m) => m.StudentActivitiesComponent),
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
          import('./features/academic/activities/activity-resolution/activity-resolution.component').then((m) => m.ActivityResolutionComponent),
        canActivate: [roleGuard],
        data: { roles: ['STUDENT'] },
      },
      {
        path: 'academic/submissions/:submissionId/feedback',
        loadComponent: () =>
          import('./features/academic/activities/submission-feedback/submission-feedback.component').then((m) => m.SubmissionFeedbackComponent),
        canActivate: [roleGuard],
        data: { roles: ['STUDENT', 'PROFESSOR', 'ADMIN'] },
      },
      // Perfil do Usuário
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
