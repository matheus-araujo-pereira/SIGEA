import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassDashboardService } from '../../services/class-dashboard.service';
import { ClassDashboardDTO } from '../../models/class-dashboard.model';
import { ToastService } from '../../../../common/services/toast.service';
import { ReportService } from '../../../report/services/report.service';
import { GttRatesCardsComponent } from '../gtt-rates-cards/gtt-rates-cards.component';
import { HarmDistributionCardComponent } from '../harm-distribution-card/harm-distribution-card.component';
import { PedagogicalMetricsCardComponent } from '../pedagogical-metrics-card/pedagogical-metrics-card.component';
import { TopTriggersCardComponent } from '../top-triggers-card/top-triggers-card.component';

/**
 * Painel analítico de indicadores oficiais IHI-GTT e desempenho pedagógico da turma.
 * Orquestra a visualização das taxas epidemiológicas e subcomponentes analíticos.
 */
@Component({
  selector: 'app-class-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    GttRatesCardsComponent,
    HarmDistributionCardComponent,
    PedagogicalMetricsCardComponent,
    TopTriggersCardComponent,
  ],
  templateUrl: './class-dashboard.component.html',
})
export class ClassDashboardComponent implements OnInit {
  /** Rota ativa para obtenção do ID da turma */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de obtenção dos dados do dashboard da turma */
  private readonly classDashboardService = inject(ClassDashboardService);
  /** Serviço de notificações de toast */
  private readonly toastService = inject(ToastService);
  /** Serviço de geração e download de relatórios acadêmicos */
  private readonly reportService = inject(ReportService);

  /** Identificador da turma acadêmica atual */
  readonly classId = signal<string>('');
  /** Dados agregados do painel da turma (taxas GTT e métricas pedagógicas) */
  readonly dashboard = signal<ClassDashboardDTO | null>(null);
  /** Indicador de carregamento assíncrono dos indicadores */
  readonly isLoading = signal<boolean>(true);

  /** Inicialização: lê o classId da rota e dispara carga do painel */
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('classId');
    if (id) {
      this.classId.set(id);
      this.loadDashboard();
    } else {
      this.toastService.error('Turma não identificada.');
      this.router.navigate(['/academic/classes']);
    }
  }

  /** Carrega os dados analíticos consolidados do backend */
  loadDashboard(): void {
    this.isLoading.set(true);
    this.classDashboardService.getClassDashboard(this.classId()).subscribe({
      next: (res) => {
        this.dashboard.set(res.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar indicadores da turma.');
      },
    });
  }

  /** Dispara o download do boletim acadêmico em formato PDF */
  downloadBulletinPdf(): void {
    if (this.classId()) {
      this.reportService.downloadClassBulletinPdf(this.classId()).subscribe();
    }
  }

  /** Dispara o download dos dados de pesquisa científica em formato CSV */
  downloadResearchCsv(): void {
    if (this.classId()) {
      this.reportService.downloadClassResearchCsv(this.classId()).subscribe();
    }
  }

  /** Navega para a visão dos indicadores psicométricos SUS da turma */
  goToSusReport(): void {
    if (this.classId()) {
      this.router.navigate(['/academic/classes', this.classId(), 'sus']);
    }
  }

  /** Retorna para a página de detalhes da turma */
  goBack(): void {
    this.router.navigate(['/academic/classes', this.classId()]);
  }
}
