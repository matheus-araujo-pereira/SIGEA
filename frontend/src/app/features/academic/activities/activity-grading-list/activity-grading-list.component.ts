import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ActivityDetailDTO, SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Componente para listagem de submissões de discentes para correção pedagógica pelo docente.
 */
@Component({
  selector: 'app-activity-grading-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="space-y-6 max-w-7xl mx-auto">
      <!-- Breadcrumb & Navegação -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Voltar"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <div>
            <h1 class="text-2xl font-black text-slate-900 tracking-tight">Correção de Atividade</h1>
            <p class="text-xs text-slate-500 font-medium">Submissões dos discentes para avaliação pedagógica e atribuição de nota</p>
          </div>
        </div>
      </div>

      <!-- Card de Contexto da Atividade -->
      @if (activity(); as act) {
        <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="space-y-1">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-clinical-50 text-clinical-800 border border-clinical-200">
              Atividade Avaliativa
            </span>
            <h2 class="text-xl font-bold text-slate-900">{{ act.title }}</h2>
            <p class="text-xs text-slate-600 line-clamp-2">{{ act.description }}</p>
          </div>
          <div class="flex items-center gap-6 border-t md:border-t-0 md:border-l border-slate-100 pt-4 md:pt-0 md:pl-6 text-xs text-slate-600 flex-shrink-0">
            <div>
              <span class="block text-[10px] uppercase font-bold text-slate-400">Prazo Limite</span>
              <span class="font-semibold text-slate-800">{{ act.deadline | date:'dd/MM/yyyy HH:mm' }}</span>
            </div>
            <div>
              <span class="block text-[10px] uppercase font-bold text-slate-400">Pontuação Máxima</span>
              <span class="font-semibold text-clinical-700 font-mono text-sm">10.0 pts</span>
            </div>
          </div>
        </div>
      }

      <!-- Tabela de Submissões -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <h3 class="text-sm font-bold text-slate-800">Envios dos Estudantes</h3>
            <span class="px-2 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600">
              {{ totalElements() }} total
            </span>
          </div>
          <button
            type="button"
            (click)="loadSubmissions()"
            class="text-xs text-clinical-600 hover:text-clinical-800 font-semibold flex items-center gap-1 transition-colors"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Atualizar
          </button>
        </div>

        @if (isLoading()) {
          <div class="py-16 text-center text-slate-400">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-clinical-600 border-t-transparent mb-3"></div>
            <p class="text-xs font-medium">Carregando submissões dos estudantes...</p>
          </div>
        } @else if (submissions().length === 0) {
          <div class="py-16 text-center text-slate-400">
            <svg class="w-12 h-12 mx-auto text-slate-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-sm font-semibold text-slate-700">Nenhuma submissão enviada ainda</p>
            <p class="text-xs text-slate-500 mt-1">Os estudantes matriculados que enviarem a resolução aparecerão nesta lista.</p>
          </div>
        } @else {
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="bg-slate-50/75 border-b border-slate-200/80 text-[11px] font-bold uppercase tracking-wider text-slate-500">
                  <th class="py-3.5 px-6">Estudante</th>
                  <th class="py-3.5 px-6">Data de Envio</th>
                  <th class="py-3.5 px-6">Gatilhos Identificados</th>
                  <th class="py-3.5 px-6">Status da Avaliação</th>
                  <th class="py-3.5 px-6 text-center">Nota</th>
                  <th class="py-3.5 px-6 text-right">Ações</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 text-xs">
                @for (sub of submissions(); track sub.id) {
                  <tr class="hover:bg-slate-50/60 transition-colors">
                    <td class="py-4 px-6">
                      <div class="font-bold text-slate-900">{{ sub.studentName }}</div>
                      <div class="text-[11px] text-slate-500 font-mono">{{ sub.studentEmail }}</div>
                    </td>
                    <td class="py-4 px-6 text-slate-600 font-medium whitespace-nowrap">
                      {{ sub.submissionDate | date:'dd/MM/yyyy HH:mm' }}
                    </td>
                    <td class="py-4 px-6">
                      <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold bg-clinical-50 text-clinical-700 border border-clinical-200">
                        {{ sub.identifiedTriggers.length }} gatilho(s)
                      </span>
                    </td>
                    <td class="py-4 px-6">
                      @if (sub.isGraded) {
                        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                          <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                          Avaliado
                        </span>
                      } @else {
                        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                          <span class="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse"></span>
                          Aguardando Correção
                        </span>
                      }
                    </td>
                    <td class="py-4 px-6 text-center">
                      @if (sub.isGraded && sub.grade !== null && sub.grade !== undefined) {
                        <span class="font-mono font-bold text-sm" [ngClass]="sub.grade >= 7 ? 'text-emerald-700' : 'text-amber-700'">
                          {{ sub.grade | number:'1.2-2' }}
                        </span>
                      } @else {
                        <span class="text-slate-400 font-mono font-medium">-</span>
                      }
                    </td>
                    <td class="py-4 px-6 text-right whitespace-nowrap">
                      <button
                        type="button"
                        [routerLink]="['/academic/submissions', sub.id, 'grade']"
                        class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-white transition-all shadow-xs"
                        [ngClass]="sub.isGraded ? 'bg-slate-700 hover:bg-slate-800' : 'bg-clinical-600 hover:bg-clinical-700'"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                        {{ sub.isGraded ? 'Revisar Nota' : 'Avaliar' }}
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <!-- Paginação Estrita (10 por página) -->
          @if (totalPages() > 1) {
            <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span>Página {{ currentPage() + 1 }} de {{ totalPages() }}</span>
              <div class="flex items-center gap-2">
                <button
                  type="button"
                  [disabled]="currentPage() === 0"
                  (click)="changePage(currentPage() - 1)"
                  class="px-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                  Anterior
                </button>
                <button
                  type="button"
                  [disabled]="currentPage() >= totalPages() - 1"
                  (click)="changePage(currentPage() + 1)"
                  class="px-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                  Próxima
                </button>
              </div>
            </div>
          }
        }
      </div>
    </div>
  `
})
export class ActivityGradingListComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
  private readonly toastService = inject(ToastService);

  readonly activityId = signal<string>('');
  readonly activity = signal<ActivityDetailDTO | null>(null);
  readonly submissions = signal<SubmissionResponseDTO[]>([]);
  readonly isLoading = signal<boolean>(true);
  readonly currentPage = signal<number>(0);
  readonly totalPages = signal<number>(0);
  readonly totalElements = signal<number>(0);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('activityId');
    if (id) {
      this.activityId.set(id);
      this.loadActivityDetails();
      this.loadSubmissions();
    } else {
      this.toastService.error('Identificador de atividade inválido.');
      this.router.navigate(['/academic/classes']);
    }
  }

  loadActivityDetails(): void {
    this.activityService.getActivityById(this.activityId()).subscribe({
      next: (res) => {
        this.activity.set(res.data);
      },
      error: (err) => {
        this.toastService.error(err?.error?.message || 'Erro ao carregar detalhes da atividade.');
      }
    });
  }

  loadSubmissions(): void {
    this.isLoading.set(true);
    this.activityService.listSubmissions(this.activityId(), this.currentPage(), 10).subscribe({
      next: (res) => {
        this.submissions.set(res.data.content);
        this.totalPages.set(res.data.totalPages);
        this.totalElements.set(res.data.totalElements);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar lista de submissões.');
      }
    });
  }

  changePage(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages()) {
      this.currentPage.set(newPage);
      this.loadSubmissions();
    }
  }

  goBack(): void {
    if (this.activity()?.academicClassId) {
      this.router.navigate(['/academic/classes', this.activity()!.academicClassId]);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }
}
