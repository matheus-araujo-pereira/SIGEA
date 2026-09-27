import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassDashboardService } from '../../../../core/services/class-dashboard.service';
import { ClassDashboardDTO } from '../../../../core/models/class-dashboard.model';
import { ToastService } from '../../../../core/services/toast.service';
import { ReportService } from '../../../../core/services/report.service';

/**
 * Painel analítico de indicadores oficiais IHI-GTT e desempenho pedagógico da turma.
 */
@Component({
  selector: 'app-class-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './class-dashboard.component.html',
})
export class ClassDashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly classDashboardService = inject(ClassDashboardService);
  private readonly toastService = inject(ToastService);
  private readonly reportService = inject(ReportService);

  readonly classId = signal<string>('');
  readonly dashboard = signal<ClassDashboardDTO | null>(null);
  readonly isLoading = signal<boolean>(true);

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
      }
    });
  }

  downloadBulletinPdf(): void {
    if (this.classId()) {
      this.reportService.downloadClassBulletinPdf(this.classId()).subscribe();
    }
  }

  downloadResearchCsv(): void {
    if (this.classId()) {
      this.reportService.downloadClassResearchCsv(this.classId()).subscribe();
    }
  }

  getHarmCount(letter: string): number {
    const dist = this.dashboard()?.gttMetrics.harmDistribution;
    return dist && dist[letter] ? dist[letter] : 0;
  }

  getHarmPercentage(letter: string): number {
    const metrics = this.dashboard()?.gttMetrics;
    if (!metrics || metrics.totalAdverseEvents === 0) return 0;
    return metrics.harmPercentages?.[letter] ?? 0;
  }

  goToSusReport(): void {
    if (this.classId()) {
      this.router.navigate(['/academic/classes', this.classId(), 'sus']);
    }
  }

  goBack(): void {
    this.router.navigate(['/academic/classes', this.classId()]);
  }
}
