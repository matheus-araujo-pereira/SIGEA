import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { ReportService } from '../../../report/services/report.service';
import { SubmissionResponseDTO } from '../../models/activity.model';
import { ToastService } from '../../../../common/services/toast.service';

/**
 * Componente para visualização da resolução com nota e parecer pedagógico do professor.
 */
@Component({
  selector: 'app-submission-feedback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './submission-feedback.component.html',
})
export class SubmissionFeedbackComponent implements OnInit {
  /** Rota ativa para identificação da submissão */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de atividades e submissões */
  private readonly activityService = inject(ActivityService);
  /** Serviço de exportação de relatórios em PDF */
  private readonly reportService = inject(ReportService);
  /** Serviço de emissão de notificações toast */
  private readonly toastService = inject(ToastService);

  /** Identificador da submissão consultada */
  readonly submissionId = signal<string>('');
  /** Dados completos da submissão avaliada com nota e parecer */
  readonly submission = signal<SubmissionResponseDTO | null>(null);
  /** Indicador de estado de carregamento assíncrono */
  readonly isLoading = signal<boolean>(true);

  /** Inicialização: lê o parâmetro de submissão e executa a carga */
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('submissionId');
    if (id) {
      this.submissionId.set(id);
      this.loadSubmission();
    } else {
      this.toastService.error('Identificador de submissão inválido.');
      this.router.navigate(['/academic/student/activities']);
    }
  }

  /** Carrega os dados da submissão com os gatilhos e ferramentas avaliadas */
  loadSubmission(): void {
    this.isLoading.set(true);
    this.activityService.getSubmissionById(this.submissionId()).subscribe({
      next: (res) => {
        const sub = res.data;
        if (sub) {
          sub.qualityTools = sub.qualityToolsData || sub.qualityTools;
          sub.pedagogicalFeedback = sub.professorFeedback || sub.pedagogicalFeedback;
          if (sub.identifiedTriggers) {
            sub.identifiedTriggers.forEach((t) => {
              t.harmCategory = t.harmSeverityLetter || t.harmCategory;
              t.rationale = t.clinicalJustification || t.notes || t.rationale;
            });
          }
        }
        this.submission.set(sub);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar parecer pedagógico.');
      },
    });
  }

  /** Dispara o download da resolução avaliada e assinada pelo docente em PDF */
  downloadSubmissionPdf(): void {
    const sub = this.submission();
    if (sub) {
      this.reportService.downloadSubmissionPdf(sub.id).subscribe();
    }
  }

  /** Retorna para a listagem de atividades do discente */
  goBack(): void {
    this.router.navigate(['/academic/student/activities']);
  }
}
