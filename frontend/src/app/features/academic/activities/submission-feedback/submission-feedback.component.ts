import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ReportService } from '../../../../core/services/report.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Componente para visualização da resolução com nota e parecer pedagógico do professor.
 */
@Component({
  selector: 'app-submission-feedback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './submission-feedback.component.html'
})
export class SubmissionFeedbackComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
  private readonly reportService = inject(ReportService);
  private readonly toastService = inject(ToastService);

  readonly submissionId = signal<string>('');
  readonly submission = signal<SubmissionResponseDTO | null>(null);
  readonly isLoading = signal<boolean>(true);

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
      }
    });
  }

  downloadSubmissionPdf(): void {
    const sub = this.submission();
    if (sub) {
      this.reportService.downloadSubmissionPdf(sub.id).subscribe();
    }
  }

  goBack(): void {
    this.router.navigate(['/academic/student/activities']);
  }
}
