import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SusService } from '../../services/sus.service';
import { ToastService } from '../../../../common/services/toast.service';
import {
  SUS_QUESTIONS,
  SusQuestion,
  SusClassSummaryDTO,
  SusGeneralSummaryDTO,
} from '../../models/sus.model';
import { SusScoreHeroComponent } from './sus-score-hero/sus-score-hero.component';
import { SusQuestionsBreakdownComponent } from './sus-questions-breakdown/sus-questions-breakdown.component';
import { SusEvaluationsTableComponent } from './sus-evaluations-table/sus-evaluations-table.component';

/**
 * Painel analítico e psicométrico de resultados da Escala de Usabilidade do Sistema (SUS).
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 * Orquestra visualização consolidada por turma ou visão institucional geral.
 */
@Component({
  selector: 'app-sus-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    SusScoreHeroComponent,
    SusQuestionsBreakdownComponent,
    SusEvaluationsTableComponent,
  ],
  templateUrl: './sus-dashboard.component.html',
})
export class SusDashboardComponent implements OnInit {
  /** Rota ativa para identificação dos parâmetros de turma */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de dados da Escala SUS */
  private readonly susService = inject(SusService);
  /** Serviço de notificações de toast */
  private readonly toastService = inject(ToastService);

  /** Catálogo das 10 perguntas padrão do inventário SUS */
  readonly questions: SusQuestion[] = SUS_QUESTIONS;

  /** Estado de carregamento dos dados psicométricos */
  readonly isLoading = signal<boolean>(true);
  /** Flag de download de relatório CSV em andamento */
  readonly isDownloading = signal<boolean>(false);
  /** Identificador da turma filtrada, ou nulo para visão global */
  readonly classId = signal<string | null>(null);
  /** Resumo psicométrico da turma específica */
  readonly classSummary = signal<SusClassSummaryDTO | null>(null);
  /** Resumo psicométrico institucional geral */
  readonly generalSummary = signal<SusGeneralSummaryDTO | null>(null);

  /** Indica se o painel está operando em escopo de turma específica */
  readonly isClassMode = computed(() => !!this.classId());

  /** Total absoluto de avaliações SUS computadas */
  readonly totalEvaluations = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.totalEvaluations ?? 0)
      : (this.generalSummary()?.totalEvaluations ?? 0);
  });

  /** Escore médio do SUS normalizado (0 a 100) */
  readonly averageScore = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.averageScore ?? 0)
      : (this.generalSummary()?.averageScore ?? 0);
  });

  /** Classificação adjetiva de usabilidade (ex: Excelente, Bom, Pobre) */
  readonly adjectiveRating = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.adjectiveRating ?? '—')
      : (this.generalSummary()?.adjectiveRating ?? '—');
  });

  /** Grau de aceitabilidade da interface (Aceitável, Marginal, Inaceitável) */
  readonly acceptability = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.acceptability ?? '—')
      : (this.generalSummary()?.acceptability ?? '—');
  });

  /** Letra de classificação escolar correspondente (A a F) */
  readonly gradeLevel = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.gradeLevel ?? '—')
      : (this.generalSummary()?.gradeLevel ?? '—');
  });

  /** Listagem individualizada das avaliações submetidas na turma */
  readonly evaluationsList = computed(() => {
    return this.classSummary()?.evaluations ?? [];
  });

  /** Médias aritméticas das respostas para cada uma das 10 questões */
  readonly questionAverages = computed(() => {
    return this.isClassMode()
      ? (this.classSummary()?.questionAverages ?? [])
      : (this.generalSummary()?.questionAverages ?? []);
  });

  /** Inicialização: detecta turma da rota e carrega métricas */
  ngOnInit(): void {
    const idFromParam = this.route.snapshot.paramMap.get('classId');
    const idFromQuery = this.route.snapshot.queryParamMap.get('classId');
    const resolvedClassId = idFromParam || idFromQuery || null;

    this.classId.set(resolvedClassId);
    this.loadData();
  }

  /** Dispara a requisição de busca do sumário SUS conforme o modo */
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
        },
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
        },
      });
    }
  }

  /** Exporta a base de dados de avaliações em arquivo CSV */
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
        },
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
        },
      });
    }
  }

  /** Retorna à tela anterior de acordo com o contexto ativo */
  goBack(): void {
    const cid = this.classId();
    if (cid) {
      this.router.navigate(['/academic/classes', cid, 'dashboard']);
    } else {
      this.router.navigate(['/admin/users']);
    }
  }
}
