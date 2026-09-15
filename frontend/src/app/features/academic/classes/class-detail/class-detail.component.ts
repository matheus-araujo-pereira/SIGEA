import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
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
  template: `
    <div class="space-y-6">
      <!-- Barra Superior / Botão Voltar -->
      <div class="flex items-center justify-between">
        <button
          type="button"
          (click)="goBack()"
          class="btn-secondary gap-2 text-xs"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span>Voltar para Turmas</span>
        </button>

        <div class="flex items-center gap-3">
          @if (isAdmin || isProfessor) {
            <button
              type="button"
              (click)="viewDashboard()"
              class="btn-secondary gap-2 text-emerald-700 hover:bg-emerald-50 border-emerald-200"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span>Painel Analítico GTT</span>
            </button>
          }

          @if ((isAdmin || isProfessor) && classData() && !classData()?.isClosed) {
            <button
              type="button"
              (click)="createNewActivity()"
              class="btn-primary gap-2"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Nova Atividade</span>
            </button>
          }
        </div>
      </div>

      <!-- Card Principal da Turma -->
      @if (classData(); as c) {
        <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div class="flex items-center gap-3">
                <h1 class="text-2xl font-black text-slate-900 tracking-tight">{{ c.formattedName }}</h1>
                @if (!c.isClosed) {
                  <span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                    Ativa
                  </span>
                } @else {
                  <span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">
                    Encerrada
                  </span>
                }
              </div>
              <p class="text-xs text-slate-500 mt-1">
                Disciplina: <strong class="text-slate-800">{{ c.subjectName }}</strong> • Turma: <strong class="text-slate-800">{{ c.classCode }}</strong> • Período: <strong class="text-slate-800">{{ c.academicPeriod }}</strong>
              </p>
            </div>

            <!-- Card Resumo do Professor -->
            <div class="p-3.5 bg-slate-50 border border-slate-200/80 rounded-2xl flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-clinical-100 text-clinical-700 flex items-center justify-center font-black text-sm">
                Doc
              </div>
              <div>
                <p class="text-[10px] uppercase font-bold tracking-wider text-slate-400">Docente Responsável</p>
                <p class="text-xs font-bold text-slate-900">{{ c.professorName }}</p>
                <p class="text-[11px] text-slate-500 font-mono">{{ c.professorEmail }}</p>
              </div>
            </div>
          </div>

          <!-- Métricas Resumo -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6 pt-6 border-t border-slate-100 text-xs">
            <div class="p-3 bg-slate-50/60 rounded-2xl">
              <span class="text-slate-400 font-medium block text-[11px]">Estudantes</span>
              <span class="text-xl font-black text-slate-900">{{ c.studentCount }}</span>
            </div>
            <div class="p-3 bg-slate-50/60 rounded-2xl">
              <span class="text-slate-400 font-medium block text-[11px]">Atividades Avaliativas</span>
              <span class="text-xl font-black text-slate-900">{{ activities().length }}</span>
            </div>
            <div class="p-3 bg-slate-50/60 rounded-2xl">
              <span class="text-slate-400 font-medium block text-[11px]">Período Letivo</span>
              <span class="text-xl font-black text-clinical-700">{{ c.academicPeriod }}</span>
            </div>
            <div class="p-3 bg-slate-50/60 rounded-2xl">
              <span class="text-slate-400 font-medium block text-[11px]">Instituição</span>
              <span class="text-xl font-black text-slate-900">UFS</span>
            </div>
          </div>
        </div>

        <!-- Seções: Atividades e Estudantes -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Lista de Atividades (2 Colunas no desktop) -->
          <div class="lg:col-span-2 space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-black text-slate-900 tracking-tight">Atividades Avaliativas</h3>
              <span class="text-xs font-bold text-slate-400">{{ activities().length }} atividade(s)</span>
            </div>

            <div class="space-y-3">
              @for (act of activities(); track act.id) {
                <div class="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs hover:border-clinical-200 transition-all">
                  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div class="space-y-1">
                      <div class="flex items-center gap-2">
                        <h4 class="text-sm font-bold text-slate-900">{{ act.title }}</h4>
                        @if (act.isExpired) {
                          <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-rose-50 text-rose-700 border border-rose-200">
                            Expirada
                          </span>
                        } @else {
                          <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                            Prazo Aberto
                          </span>
                        }
                      </div>
                      <p class="text-xs text-slate-500 line-clamp-2">{{ act.description }}</p>
                      <div class="flex items-center gap-4 text-[11px] text-slate-400 pt-1">
                        <span>Prazo: <strong class="text-slate-700">{{ act.deadline | date:'dd/MM/yyyy HH:mm' }}</strong></span>
                        <span>•</span>
                        <span>{{ act.submissionCount }} submissão(ões)</span>
                      </div>
                    </div>

                    <!-- Botões de Ação da Atividade -->
                    <div class="flex items-center gap-2 self-end sm:self-center shrink-0">
                      @if (isStudent) {
                        <button
                          type="button"
                          (click)="resolveActivity(act.id)"
                          class="btn-primary text-xs py-2 px-3 gap-1.5"
                        >
                          <span>Acessar Prontuário</span>
                          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                          </svg>
                        </button>
                      }

                      @if (isAdmin || isProfessor) {
                        <!-- Visualizar / Corrigir Submissões -->
                        <button
                          type="button"
                          (click)="viewSubmissions(act.id)"
                          class="btn-secondary text-xs py-2 px-3 gap-1.5 text-clinical-700 border-clinical-200 hover:bg-clinical-50"
                          title="Corrigir e avaliar submissões dos alunos"
                        >
                          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                          </svg>
                          <span>Correções ({{ act.submissionCount }})</span>
                        </button>

                        <!-- Editar Atividade -->
                        <button
                          type="button"
                          (click)="editActivity(act.id)"
                          class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors"
                          title="Editar atividade"
                        >
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                          </svg>
                        </button>

                        <!-- Excluir Atividade -->
                        <button
                          type="button"
                          (click)="confirmDeleteActivity(act)"
                          class="p-2 text-rose-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition-colors"
                          title="Excluir atividade"
                        >
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      }
                    </div>
                  </div>
                </div>
              } @empty {
                <div class="bg-white rounded-2xl border border-slate-200/80 p-8 text-center text-slate-400 text-xs">
                  Nenhuma atividade avaliativa cadastrada para esta turma até o momento.
                </div>
              }
            </div>
          </div>

          <!-- Relação de Estudantes Matriculados (1 Coluna) -->
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-black text-slate-900 tracking-tight">Estudantes</h3>
              <span class="text-xs font-bold text-slate-400">{{ c.students.length }} discente(s)</span>
            </div>

            <div class="bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs max-h-[500px] overflow-y-auto divide-y divide-slate-100">
              @for (st of c.students; track st.id) {
                <div class="py-3 first:pt-0 last:pb-0">
                  <p class="font-bold text-slate-900 text-xs truncate">{{ st.fullName }}</p>
                  <p class="text-[11px] text-slate-500 font-mono truncate">{{ st.email }}</p>
                  @if (st.registrationNumber) {
                    <span class="inline-block mt-1 text-[10px] font-bold text-clinical-700 bg-clinical-50 px-2 py-0.5 rounded-md font-mono">
                      Matrícula: {{ st.registrationNumber }}
                    </span>
                  }
                </div>
              } @empty {
                <p class="text-center text-slate-400 py-6 text-xs">Nenhum estudante matriculado nesta turma.</p>
              }
            </div>
          </div>
        </div>
      }

      <!-- Modal de Confirmação para exclusão de atividade -->
      <app-confirm-dialog
        [isOpen]="isConfirmDialogOpen()"
        [title]="confirmDialogTitle()"
        [message]="confirmDialogMessage()"
        confirmText="Excluir Definitivamente"
        [isDestructive]="true"
        (confirm)="executeDeleteActivity()"
        (cancel)="closeConfirmDialog()"
      />
    </div>
  `,
})
export class ClassDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly classService = inject(AcademicClassService);
  private readonly activityService = inject(ActivityService);
  private readonly toast = inject(ToastService);
  private readonly auth = inject(AuthService);

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
}
