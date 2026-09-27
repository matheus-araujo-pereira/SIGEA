import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SusService } from '../../../core/services/sus.service';
import { ToastService } from '../../../core/services/toast.service';
import {
  SUS_QUESTIONS,
  SusQuestion,
  SusClassSummaryDTO,
  SusGeneralSummaryDTO,
  SusEvaluationResponseDTO
} from '../../../core/models/sus.model';

/**
 * Painel analítico e psicométrico de resultados da Escala de Usabilidade do Sistema (SUS).
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 * Suporta visualização consolidada por turma ou visão institucional geral.
 */
@Component({
  selector: 'app-sus-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sus-dashboard.component.html'
})
export class SusDashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly susService = inject(SusService);
  private readonly toastService = inject(ToastService);

  readonly questions: SusQuestion[] = SUS_QUESTIONS;

  readonly isLoading = signal<boolean>(true);
  readonly isDownloading = signal<boolean>(false);
  readonly classId = signal<string | null>(null);
  readonly classSummary = signal<SusClassSummaryDTO | null>(null);
  readonly generalSummary = signal<SusGeneralSummaryDTO | null>(null);
  readonly selectedSuggestion = signal<string | null>(null);

  readonly isClassMode = computed(() => !!this.classId());

  readonly totalEvaluations = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.totalEvaluations ?? 0)
      : (this.generalSummary()?.totalEvaluations ?? 0);
  });

  readonly averageScore = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.averageScore ?? 0)
      : (this.generalSummary()?.averageScore ?? 0);
  });

  readonly adjectiveRating = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.adjectiveRating ?? '—')
      : (this.generalSummary()?.adjectiveRating ?? '—');
  });

  readonly acceptability = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.acceptability ?? '—')
      : (this.generalSummary()?.acceptability ?? '—');
  });

  readonly gradeLevel = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.gradeLevel ?? '—')
      : (this.generalSummary()?.gradeLevel ?? '—');
  });

  readonly evaluationsList = computed(() => {
    return this.classSummary()?.evaluations ?? [];
  });

  ngOnInit(): void {
    const idFromParam = this.route.snapshot.paramMap.get('classId');
    const idFromQuery = this.route.snapshot.queryParamMap.get('classId');
    const resolvedClassId = idFromParam || idFromQuery || null;

    this.classId.set(resolvedClassId);
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    const cid = this.classId();

    if (cid) {
      this.susService.getClassSummary(cid).subscribe({
        next: (res) => {
          this.classSummary.set(res.data || null);
          this.isLoading.set(false);
        },
        error: () => {
          this.classSummary.set(null);
          this.isLoading.set(false);
          this.toastService.error('Falha ao carregar indicadores SUS da turma.');
        }
      });
    } else {
      this.susService.getGeneralSummary().subscribe({
        next: (res) => {
          this.generalSummary.set(res.data || null);
          this.isLoading.set(false);
        },
        error: () => {
          this.generalSummary.set(null);
          this.isLoading.set(false);
          this.toastService.error('Falha ao carregar indicadores SUS gerais.');
        }
      });
    }
  }

  downloadCsv(): void {
    const cid = this.classId();
    this.isDownloading.set(true);

    if (cid) {
      this.susService.downloadClassSusCsv(cid).subscribe({
        next: (blob) => {
          this.isDownloading.set(false);
          this.susService.saveBlob(blob, `sigea_sus_turma_${cid}.csv`);
          this.toastService.success('Base de dados SUS (CSV) exportada com sucesso.');
        },
        error: () => {
          this.isDownloading.set(false);
          this.toastService.error('Falha ao exportar CSV de usabilidade da turma.');
        }
      });
    } else {
      this.susService.downloadGlobalSusCsv().subscribe({
        next: (blob) => {
          this.isDownloading.set(false);
          this.susService.saveBlob(blob, 'sigea_sus_geral.csv');
          this.toastService.success('Base de dados SUS geral (CSV) exportada com sucesso.');
        },
        error: () => {
          this.isDownloading.set(false);
          this.toastService.error('Falha ao exportar CSV geral de usabilidade.');
        }
      });
    }
  }

  getQuestionAverage(questionId: number): number {
    const list = this.isClassMode()
      ? this.classSummary()?.questionAverages
      : this.generalSummary()?.questionAverages;
    if (!list || questionId < 1 || questionId > list.length) return 0;
    return list[questionId - 1];
  }

  getScoreTextColor(score: number): string {
    if (score >= 85) return 'text-emerald-600';
    if (score >= 70) return 'text-blue-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-rose-600';
  }

  getScoreBadgeClass(score: number): string {
    if (score >= 85) return 'bg-emerald-100 text-emerald-800 border border-emerald-300';
    if (score >= 70) return 'bg-blue-100 text-blue-800 border border-blue-300';
    if (score >= 50) return 'bg-amber-100 text-amber-800 border border-amber-300';
    return 'bg-rose-100 text-rose-800 border border-rose-300';
  }

  getQuestionColor(val: number, isPositive: boolean): string {
    const isDesirable = (isPositive && val >= 3.5) || (!isPositive && val <= 2.5);
    if (isDesirable) return 'text-emerald-600';
    const isNeutral = val > 2.5 && val < 3.5;
    if (isNeutral) return 'text-slate-600';
    return 'text-rose-600';
  }

  getQuestionBarClass(val: number, isPositive: boolean): string {
    const isDesirable = (isPositive && val >= 3.5) || (!isPositive && val <= 2.5);
    if (isDesirable) return 'bg-emerald-500';
    const isNeutral = val > 2.5 && val < 3.5;
    if (isNeutral) return 'bg-slate-400';
    return 'bg-rose-500';
  }

  openSuggestion(text: string): void {
    this.selectedSuggestion.set(text);
  }

  closeSuggestion(): void {
    this.selectedSuggestion.set(null);
  }

  goBack(): void {
    const cid = this.classId();
    if (cid) {
      this.router.navigate(['/academic/classes', cid, 'dashboard']);
    } else {
      this.router.navigate(['/admin/users']);
    }
  }
}
