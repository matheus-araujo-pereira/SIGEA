import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ReportService } from '../../../../core/services/report.service';
import { AcademicClassDetailDTO } from '../../../../core/models/academic-class.model';
import { ActivityResponseDTO } from '../../../../core/models/activity.model';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

/**
 * Visualização completa da Turma Acadêmica: Dados do docente, lista de discentes e atividades avaliativas.
 */
@Component({
  selector: 'app-class-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ConfirmDialogComponent],
  templateUrl: './class-detail.component.html',
})
export class ClassDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly classService = inject(AcademicClassService);
  private readonly activityService = inject(ActivityService);
  private readonly toast = inject(ToastService);
  private readonly auth = inject(AuthService);
  private readonly reportService = inject(ReportService);

  readonly classData = signal<AcademicClassDetailDTO | null>(null);
  readonly activities = signal<ActivityResponseDTO[]>([]);

  readonly isConfirmDialogOpen = signal(false);
  readonly confirmDialogTitle = signal('');
  readonly confirmDialogMessage = signal('');
  private activityToDeleteId: string | null = null;

  classId: string | null = null;

  get isAdmin(): boolean {
    return this.auth.hasRole(['ADMIN']);
  }

  get isProfessor(): boolean {
    return this.auth.hasRole(['PROFESSOR']);
  }

  get isStudent(): boolean {
    return this.auth.hasRole(['STUDENT']);
  }

  ngOnInit(): void {
    this.classId = this.route.snapshot.paramMap.get('id');
    if (this.classId) {
      this.loadClassDetail(this.classId);
      this.loadActivities(this.classId);
    }
  }

  loadClassDetail(id: string): void {
    this.classService.getClassById(id).subscribe({
      next: (res) => this.classData.set(res.data),
      error: () => this.toast.error('Erro ao carregar detalhes da turma.'),
    });
  }

  loadActivities(classId: string): void {
    this.activityService.listActivities(classId, 0, 50).subscribe({
      next: (res) => this.activities.set(res.data.content),
      error: () => this.toast.error('Erro ao carregar atividades da turma.'),
    });
  }

  goBack(): void {
    this.router.navigate(['/academic/classes']);
  }

  viewDashboard(): void {
    if (this.classId) {
      this.router.navigate(['/academic/classes', this.classId, 'dashboard']);
    }
  }

  createNewActivity(): void {
    if (this.classId) {
      this.router.navigate(['/academic/activities/new'], { queryParams: { classId: this.classId } });
    }
  }

  resolveActivity(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'resolve']);
  }

  viewSubmissions(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'grading']);
  }

  editActivity(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'edit']);
  }

  confirmDeleteActivity(act: ActivityResponseDTO): void {
    this.activityToDeleteId = act.id;
    this.confirmDialogTitle.set('Excluir Atividade');
    this.confirmDialogMessage.set(
      `Deseja realmente excluir a atividade "${act.title}"? Todas as resoluções enviadas pelos estudantes serão perdidas.`
    );
    this.isConfirmDialogOpen.set(true);
  }

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

  closeConfirmDialog(): void {
    this.isConfirmDialogOpen.set(false);
    this.activityToDeleteId = null;
  }

  downloadBulletinPdf(): void {
    const c = this.classData();
    if (c) {
      this.reportService.downloadClassBulletinPdf(c.id).subscribe();
    }
  }

  downloadResearchCsv(): void {
    const c = this.classData();
    if (c) {
      this.reportService.downloadClassResearchCsv(c.id).subscribe();
    }
  }
}
