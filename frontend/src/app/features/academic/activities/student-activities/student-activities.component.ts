import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';

/**
 * Painel "Minhas Atividades" e histórico de resoluções do estudante.
 */
@Component({
  selector: 'app-student-activities',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DataTablePaginationComponent],
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Minhas Atividades Avaliativas</h2>
          <p class="text-xs text-slate-500 mt-1">
            Acompanhe prazos, resoluções com prontuários fictícios e pareceres pedagógicos docentes
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
          <span>Histórico de Turmas</span>
        </button>
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
                    Nenhuma atividade encontrada para o filtro selecionado.
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
  `,
})
export class StudentActivitiesComponent implements OnInit {
  private readonly activityService = inject(ActivityService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  readonly submissions = signal<SubmissionResponseDTO[]>([]);
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
      },
      error: () => this.toast.error('Erro ao carregar atividades do estudante.'),
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
