import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';

export interface PendingActivityItem {
  id: string;
  title: string;
  description: string;
  deadline: string;
  isExpired: boolean;
  classId: string;
  className: string;
  professorName: string;
}

/**
 * Painel "Minhas Atividades" e histórico de resoluções do estudante.
 */
@Component({
  selector: 'app-student-activities',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DataTablePaginationComponent],
  template: `
    <div class="space-y-8">
      <!-- Cabeçalho Principal -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Portal de Atividades do Estudante</h2>
          <p class="text-xs text-slate-500 mt-1">
            Resolva casos clínicos com prontuários simulados e acompanhe notas e pareceres pedagógicos docentes
          </p>
        </div>

        <button
          type="button"
          (click)="goToMyClasses()"
          class="btn-secondary gap-2 text-xs"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
          <span>Minhas Turmas (4)</span>
        </button>
      </div>

      <!-- SEÇÃO 1: Atividades Pendentes para Resolução (Abertas) -->
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-black text-slate-900 tracking-tight flex items-center gap-2">
              <span>Atividades Pendentes para Resolução</span>
              @if (pendingActivities().length > 0) {
                <span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-100 text-amber-800 border border-amber-300">
                  {{ pendingActivities().length }} pendente(s)
                </span>
              }
            </h3>
            <p class="text-xs text-slate-500">
              Casos clínicos com prontuários abertos aguardando o seu preenchimento de gatilhos GTT e categorização
            </p>
          </div>
        </div>

        @if (pendingActivities().length > 0) {
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            @for (act of pendingActivities(); track act.id) {
              <div class="bg-white rounded-2xl border-2 border-amber-300/80 p-5 shadow-xs hover:border-clinical-500 transition-all flex flex-col justify-between gap-4">
                <div class="space-y-2">
                  <div class="flex items-start justify-between gap-2">
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 uppercase tracking-wider">
                      Prazo Aberto
                    </span>
                    <span class="text-[11px] font-semibold text-slate-400">
                      Limite: <strong class="text-slate-700">{{ act.deadline | date:'dd/MM/yyyy HH:mm' }}</strong>
                    </span>
                  </div>

                  <h4 class="text-sm font-bold text-slate-900 leading-snug">{{ act.title }}</h4>
                  <p class="text-xs text-slate-500 line-clamp-2">{{ act.description }}</p>

                  <div class="pt-2 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between text-[11px] text-slate-500 gap-1">
                    <span class="truncate" [title]="act.className">Turma: <strong>{{ act.className }}</strong></span>
                    <span class="truncate">Docente: <strong>{{ act.professorName }}</strong></span>
                  </div>
                </div>

                <button
                  type="button"
                  (click)="continueResolution(act.id)"
                  class="btn-primary w-full text-xs py-2 gap-2 shadow-xs bg-clinical-700 hover:bg-clinical-800"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  <span>Acessar Prontuário e Resolver</span>
                </button>
              </div>
            }
          </div>
        } @else {
          <div class="p-6 bg-slate-50 border border-slate-200/80 rounded-2xl text-center text-xs text-slate-500">
            Você não possui atividades pendentes nas suas turmas ativas no momento.
          </div>
        }
      </div>

      <!-- SEÇÃO 2: Histórico de Submissões e Pareceres Docentes -->
      <div class="space-y-4 pt-4 border-t border-slate-200/80">
        <div>
          <h3 class="text-lg font-black text-slate-900 tracking-tight">
            Minhas Submissões e Pareceres Docentes (Histórico)
          </h3>
          <p class="text-xs text-slate-500">
            Acompanhe o status de correção, notas finais e justificativas pedagógicas atribuídas pelos professores
          </p>
        </div>

        <!-- Filtros Rápidos -->
        <div class="flex items-center gap-2">
          <button
            type="button"
            (click)="setFilter('ALL')"
            [class.bg-clinical-900]="selectedFilter() === 'ALL'"
            [class.text-white]="selectedFilter() === 'ALL'"
            [class.bg-white]="selectedFilter() !== 'ALL'"
            [class.text-slate-700]="selectedFilter() !== 'ALL'"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 shadow-2xs transition-all"
          >
            Todas ({{ submissions().length }})
          </button>
          <button
            type="button"
            (click)="setFilter('GRADED')"
            [class.bg-clinical-900]="selectedFilter() === 'GRADED'"
            [class.text-white]="selectedFilter() === 'GRADED'"
            [class.bg-white]="selectedFilter() !== 'GRADED'"
            [class.text-slate-700]="selectedFilter() !== 'GRADED'"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 shadow-2xs transition-all"
          >
            Avaliadas com Nota
          </button>
          <button
            type="button"
            (click)="setFilter('PENDING')"
            [class.bg-clinical-900]="selectedFilter() === 'PENDING'"
            [class.text-white]="selectedFilter() === 'PENDING'"
            [class.bg-white]="selectedFilter() !== 'PENDING'"
            [class.text-slate-700]="selectedFilter() !== 'PENDING'"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 shadow-2xs transition-all"
          >
            Aguardando Correção
          </button>
        </div>

        <!-- Tabela / Lista de Submissões -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
              <thead class="bg-slate-50/80 text-slate-500 font-bold uppercase tracking-wider text-[11px]">
                <tr>
                  <th scope="col" class="px-6 py-3.5">Atividade</th>
                  <th scope="col" class="px-6 py-3.5">Data de Envio</th>
                  <th scope="col" class="px-6 py-3.5 text-center">Gatilhos Identificados</th>
                  <th scope="col" class="px-6 py-3.5 text-center">Status da Avaliação</th>
                  <th scope="col" class="px-6 py-3.5 text-center">Nota</th>
                  <th scope="col" class="px-6 py-3.5 text-right">Ações</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 font-medium">
                @for (sub of filteredSubmissions(); track sub.id) {
                  <tr class="hover:bg-slate-50/60 transition-colors">
                    <td class="px-6 py-4">
                      <p class="font-bold text-slate-900 text-sm">{{ sub.activityTitle }}</p>
                      <p class="text-[11px] text-slate-400">ID da Submissão: {{ sub.id }}</p>
                    </td>
                    <td class="px-6 py-4 text-slate-600">
                      {{ sub.submissionDate | date:'dd/MM/yyyy HH:mm' }}
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-700">
                        {{ sub.identifiedTriggers.length }} gatilho(s)
                      </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      @if (sub.isGraded) {
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                          Corrigida
                        </span>
                      } @else {
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200">
                          Aguardando Docente
                        </span>
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      @if (sub.isGraded && sub.grade !== undefined && sub.grade !== null) {
                        <span class="text-sm font-black text-emerald-700">
                          {{ sub.grade | number:'1.2-2' }} / 10.00
                        </span>
                      } @else {
                        <span class="text-slate-300">—</span>
                      }
                    </td>
                    <td class="px-6 py-4 text-right space-x-2 whitespace-nowrap">
                      @if (sub.isGraded) {
                        <button
                          type="button"
                          (click)="viewFeedback(sub.id)"
                          class="btn-primary text-xs py-1.5 px-3 bg-emerald-600 hover:bg-emerald-700"
                        >
                          <span>Ver Parecer do Professor</span>
                        </button>
                      } @else {
                        <button
                          type="button"
                          (click)="continueResolution(sub.activityId)"
                          class="btn-secondary text-xs py-1.5 px-3 text-clinical-700 border-clinical-200"
                        >
                          <span>Revisar Resposta</span>
                        </button>
                      }
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-slate-400">
                      Nenhuma submissão encontrada para o filtro selecionado.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <app-data-table-pagination
            [pageData]="pageData()"
            (pageChange)="onPageChange($event)"
          />
        </div>
      </div>
    </div>
  `,
})
export class StudentActivitiesComponent implements OnInit {
  private readonly activityService = inject(ActivityService);
  private readonly classService = inject(AcademicClassService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  readonly submissions = signal<SubmissionResponseDTO[]>([]);
  readonly pendingActivities = signal<PendingActivityItem[]>([]);
  readonly pageData = signal<PageResponse<SubmissionResponseDTO> | null>(null);
  readonly selectedFilter = signal<'ALL' | 'GRADED' | 'PENDING'>('ALL');
  readonly currentPage = signal(0);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 10;

  ngOnInit(): void {
    this.loadSubmissions();
  }

  loadSubmissions(): void {
    this.activityService.getMySubmissions(this.currentPage(), this.pageSize).subscribe({
      next: (res) => {
        this.pageData.set(res.data);
        this.submissions.set(res.data.content);
        this.currentPage.set(res.data.page);
        this.totalElements.set(res.data.totalElements);
        this.totalPages.set(res.data.totalPages);
        this.loadPendingActivities();
      },
      error: () => this.toast.error('Erro ao carregar atividades do estudante.'),
    });
  }

  loadPendingActivities(): void {
    this.classService.getMyClasses(0, 10).subscribe({
      next: (res) => {
        const activeClasses = res.data.content.filter((c) => !c.isClosed);
        if (activeClasses.length === 0) {
          this.pendingActivities.set([]);
          return;
        }

        const submittedIds = new Set(this.submissions().map((s) => s.activityId));
        const pending: PendingActivityItem[] = [];
        let remaining = activeClasses.length;

        for (const clazz of activeClasses) {
          this.activityService.listActivities(clazz.id, 0, 20).subscribe({
            next: (actRes) => {
              for (const act of actRes.data.content) {
                if (!submittedIds.has(act.id)) {
                  pending.push({
                    id: act.id,
                    title: act.title,
                    description: act.description,
                    deadline: act.deadline,
                    isExpired: act.isExpired,
                    classId: clazz.id,
                    className: clazz.formattedName,
                    professorName: clazz.professorName,
                  });
                }
              }
              remaining--;
              if (remaining === 0) {
                pending.sort((a, b) => new Date(a.deadline).getTime() - new Date(b.deadline).getTime());
                this.pendingActivities.set(pending);
              }
            },
            error: () => {
              remaining--;
              if (remaining === 0) {
                this.pendingActivities.set(pending);
              }
            },
          });
        }
      },
      error: () => {
        // Ignora silenciosamente se houver erro ao buscar turmas
      },
    });
  }

  setFilter(filter: 'ALL' | 'GRADED' | 'PENDING'): void {
    this.selectedFilter.set(filter);
  }

  filteredSubmissions(): SubmissionResponseDTO[] {
    const all = this.submissions();
    const f = this.selectedFilter();
    if (f === 'GRADED') {
      return all.filter((s) => s.isGraded);
    } else if (f === 'PENDING') {
      return all.filter((s) => !s.isGraded);
    }
    return all;
  }

  onPageChange(p: number): void {
    this.currentPage.set(p);
    this.loadSubmissions();
  }

  goToMyClasses(): void {
    this.router.navigate(['/academic/classes/my-classes']);
  }

  continueResolution(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'resolve']);
  }

  viewFeedback(submissionId: string): void {
    this.router.navigate(['/academic/submissions', submissionId, 'feedback']);
  }
}
