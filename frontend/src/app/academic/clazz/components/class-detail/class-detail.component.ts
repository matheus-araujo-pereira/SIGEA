import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AcademicClassService } from '../../services/academic-class.service';
import { ActivityService } from '../../../activity/services/activity.service';
import { ToastService } from '../../../../common/services/toast.service';
import { AuthService } from '../../../../auth/services/auth.service';
import { ReportService } from '../../../report/services/report.service';
import { AcademicClassDetailDTO } from '../../models/academic-class.model';
import { ActivityResponseDTO } from '../../../activity/models/activity.model';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { ClassHeaderInfoComponent } from './class-header-info.component';
import { ClassActivitiesListComponent } from './class-activities-list.component';
import { ClassStudentsListComponent } from './class-students-list.component';

/**
 * Visualização completa da Turma Acadêmica: Orquestrador da página de detalhes,
 * integrando dados do docente, lista de discentes, atividades avaliativas e exportação de relatórios.
 */
@Component({
  selector: 'app-class-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ConfirmDialogComponent,
    ClassHeaderInfoComponent,
    ClassActivitiesListComponent,
    ClassStudentsListComponent,
  ],
  templateUrl: './class-detail.component.html',
})
export class ClassDetailComponent implements OnInit {
  /** Rota ativa para obtenção do ID da turma */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas */
  private readonly router = inject(Router);
  /** Serviço de turmas acadêmicas */
  private readonly classService = inject(AcademicClassService);
  /** Serviço de gestão de atividades */
  private readonly activityService = inject(ActivityService);
  /** Serviço de notificações de toast */
  private readonly toast = inject(ToastService);
  /** Serviço de autenticação e RBAC */
  private readonly auth = inject(AuthService);
  /** Serviço de emissão de relatórios acadêmicos */
  private readonly reportService = inject(ReportService);

  /** Sinal com os detalhes consolidados da turma */
  readonly classData = signal<AcademicClassDetailDTO | null>(null);
  /** Sinal contendo as atividades cadastradas na turma */
  readonly activities = signal<ActivityResponseDTO[]>([]);

  /** Estado de visibilidade da modal de confirmação */
  readonly isConfirmDialogOpen = signal(false);
  /** Título do diálogo de confirmação */
  readonly confirmDialogTitle = signal('');
  /** Mensagem contextual exibida no diálogo de confirmação */
  readonly confirmDialogMessage = signal('');
  /** Identificador temporário da atividade com exclusão pendente */
  private activityToDeleteId: string | null = null;

  /** Identificador único da turma acadêmica atual */
  classId: string | null = null;

  /** Identifica se o usuário autenticado possui perfil de Administrador */
  get isAdmin(): boolean {
    return this.auth.hasRole(['ADMIN']);
  }

  /** Identifica se o usuário autenticado possui perfil de Docente */
  get isProfessor(): boolean {
    return this.auth.hasRole(['PROFESSOR']);
  }

  /** Identifica se o usuário autenticado possui perfil de Estudante */
  get isStudent(): boolean {
    return this.auth.hasRole(['STUDENT']);
  }

  /** Inicialização: lê o ID da turma e dispara as requisições de carga */
  ngOnInit(): void {
    this.classId = this.route.snapshot.paramMap.get('id');
    if (this.classId) {
      this.loadClassDetail(this.classId);
      this.loadActivities(this.classId);
    }
  }

  /** Carrega os dados cadastrais e métricas da turma */
  loadClassDetail(id: string): void {
    this.classService.getClassById(id).subscribe({
      next: (res) => this.classData.set(res.data),
      error: () => this.toast.error('Erro ao carregar detalhes da turma.'),
    });
  }

  /** Carrega a listagem de atividades avaliativas vinculadas à turma */
  loadActivities(classId: string): void {
    this.activityService.listActivities(classId, 0, 50).subscribe({
      next: (res) => this.activities.set(res.data.content),
      error: () => this.toast.error('Erro ao carregar atividades da turma.'),
    });
  }

  /** Retorna à listagem geral de turmas */
  goBack(): void {
    this.router.navigate(['/academic/classes']);
  }

  /** Redireciona para o painel de métricas analíticas e SUS da turma */
  viewDashboard(): void {
    if (this.classId) {
      this.router.navigate(['/academic/classes', this.classId, 'dashboard']);
    }
  }

  /** Redireciona para a criação de nova atividade para a turma */
  createNewActivity(): void {
    if (this.classId) {
      this.router.navigate(['/academic/activities/new'], { queryParams: { classId: this.classId } });
    }
  }

  /** Redireciona o estudante para a tela de resolução de prontuário */
  resolveActivity(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'resolve']);
  }

  /** Redireciona o docente para a visão de notas e submissões da atividade */
  viewSubmissions(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'grading']);
  }

  /** Redireciona o docente para o formulário de edição da atividade */
  editActivity(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'edit']);
  }

  /** Abre modal de confirmação para remoção de atividade avaliativa */
  confirmDeleteActivity(act: ActivityResponseDTO): void {
    this.activityToDeleteId = act.id;
    this.confirmDialogTitle.set('Excluir Atividade');
    this.confirmDialogMessage.set(
      `Deseja realmente excluir a atividade "${act.title}"? Todas as resoluções enviadas pelos estudantes serão perdidas.`
    );
    this.isConfirmDialogOpen.set(true);
  }

  /** Executa a chamada à API para exclusão da atividade selecionada */
  executeDeleteActivity(): void {
    if (this.activityToDeleteId) {
      this.activityService.deleteActivity(this.activityToDeleteId).subscribe({
        next: () => {
          this.toast.success('Atividade excluída com sucesso!');
          if (this.classId) {
            this.loadActivities(this.classId);
          }
        },
        error: (err) => {
          this.toast.error(err?.error?.message || 'Erro ao excluir atividade.');
        },
      });
      this.activityToDeleteId = null;
    }
    this.isConfirmDialogOpen.set(false);
  }

  /** Cancela o diálogo de exclusão sem realizar mutações */
  closeConfirmDialog(): void {
    this.isConfirmDialogOpen.set(false);
    this.activityToDeleteId = null;
  }

  /** Dispara o download do boletim de notas em formato PDF */
  downloadBulletinPdf(): void {
    const c = this.classData();
    if (c) {
      this.reportService.downloadClassBulletinPdf(c.id).subscribe();
    }
  }

  /** Dispara o download dos dados analíticos de pesquisa da turma em formato CSV */
  downloadResearchCsv(): void {
    const c = this.classData();
    if (c) {
      this.reportService.downloadClassResearchCsv(c.id).subscribe();
    }
  }
}
