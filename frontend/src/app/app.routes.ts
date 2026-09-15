import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { FirstLoginComponent } from './features/auth/first-login/first-login.component';
import { AppShellComponent } from './shared/components/app-shell/app-shell.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { ProfileComponent } from './features/profile/profile.component';
import { ModuleListComponent } from './features/gtt/modules/module-list/module-list.component';
import { ModuleCatalogComponent } from './features/gtt/modules/module-catalog/module-catalog.component';
import { TriggerListComponent } from './features/gtt/triggers/trigger-list/trigger-list.component';
import { TriggerGuideComponent } from './features/gtt/triggers/trigger-guide/trigger-guide.component';
import { SeverityListComponent } from './features/gtt/severities/severity-list/severity-list.component';
import { SeverityGuideComponent } from './features/gtt/severities/severity-guide/severity-guide.component';
import { ClassListComponent } from './features/academic/classes/class-list/class-list.component';
import { ClassDetailComponent } from './features/academic/classes/class-detail/class-detail.component';
import { ClassDashboardComponent } from './features/academic/dashboard/class-dashboard/class-dashboard.component';
import { ActivityFormComponent } from './features/academic/activities/activity-form/activity-form.component';
import { ActivityGradingListComponent } from './features/academic/activities/activity-grading-list/activity-grading-list.component';
import { ActivityGradingDetailComponent } from './features/academic/activities/activity-grading-detail/activity-grading-detail.component';
import { StudentActivitiesComponent } from './features/academic/activities/student-activities/student-activities.component';
import { ActivityResolutionComponent } from './features/academic/activities/activity-resolution/activity-resolution.component';
import { SubmissionFeedbackComponent } from './features/academic/activities/submission-feedback/submission-feedback.component';
import { authGuard } from './core/guards/auth.guard';
import { firstLoginGuard } from './core/guards/first-login.guard';
import { roleGuard } from './core/guards/role.guard';

/**
 * Rotas da aplicação SIGEA-GTT.
 */
export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'first-login',
    component: FirstLoginComponent,
    canActivate: [firstLoginGuard],
  },
  {
    path: '',
    component: AppShellComponent,
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
        component: ModuleCatalogComponent,
      },
      {
        path: 'gtt/triggers',
        component: TriggerGuideComponent,
      },
      {
        path: 'gtt/severities',
        component: SeverityGuideComponent,
      },
      // Gestão de Usuários (Admin)
      {
        path: 'users',
        component: UserListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão Administrativa GTT (Admin)
      {
        path: 'admin/gtt/modules',
        component: ModuleListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/triggers',
        component: TriggerListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'admin/gtt/severities',
        component: SeverityListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      // Gestão de Turmas e Atividades Acadêmicas (Admin e Professor)
      {
        path: 'academic/classes',
        component: ClassListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/classes/:id',
        component: ClassDetailComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/classes/:classId/dashboard',
        component: ClassDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/new',
        component: ActivityFormComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:id/edit',
        component: ActivityFormComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/activities/:activityId/grading',
        component: ActivityGradingListComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      {
        path: 'academic/submissions/:submissionId/grade',
        component: ActivityGradingDetailComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'PROFESSOR'] },
      },
      // Portal do Estudante (Atividades e Resolução)
      {
        path: 'academic/student/activities',
        component: StudentActivitiesComponent,
        canActivate: [roleGuard],
        data: { roles: ['STUDENT'] },
      },
      {
        path: 'academic/activities/:activityId/resolve',
        component: ActivityResolutionComponent,
        canActivate: [roleGuard],
        data: { roles: ['STUDENT'] },
      },
      {
        path: 'academic/submissions/:submissionId/feedback',
        component: SubmissionFeedbackComponent,
        canActivate: [roleGuard],
        data: { roles: ['STUDENT', 'PROFESSOR', 'ADMIN'] },
      },
      // Perfil do Usuário
      {
        path: 'profile',
        component: ProfileComponent,
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
