import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ReportService } from '../../../../core/services/report.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Componente para correção e avaliação pedagógica da submissão do estudante pelo docente.
 */
@Component({
  selector: 'app-activity-grading-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activity-grading-detail.component.html'
})
export class ActivityGradingDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly activityService = inject(ActivityService);
  private readonly toastService = inject(ToastService);
  private readonly reportService = inject(ReportService);

  readonly submissionId = signal<string>('');
  readonly submission = signal<SubmissionResponseDTO | null>(null);
  readonly isLoading = signal<boolean>(true);
  readonly isSubmitting = signal<boolean>(false);

  gradeForm: FormGroup = this.fb.group({
    grade: [null, [Validators.required, Validators.min(0), Validators.max(10)]],
    pedagogicalFeedback: ['', [Validators.required, Validators.minLength(10)]]
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('submissionId');
    if (id) {
      this.submissionId.set(id);
      this.loadSubmission();
    } else {
      this.toastService.error('Submissão não informada.');
      this.router.navigate(['/academic/classes']);
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
        if (sub?.isGraded) {
          this.gradeForm.patchValue({
            grade: sub.grade,
            pedagogicalFeedback: sub.pedagogicalFeedback
          });
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar dados da submissão.');
      }
    });
  }

  submitGrade(): void {
    if (this.gradeForm.invalid) {
      this.gradeForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const formVal = this.gradeForm.value;

    this.activityService.gradeSubmission(this.submissionId(), {
      grade: Number(formVal.grade),
      pedagogicalFeedback: formVal.pedagogicalFeedback
    }).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.submission.set(res.data);
        this.toastService.success('Avaliação registrada com sucesso!');
        this.goBack();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao registrar avaliação.');
      }
    });
  }

  goBack(): void {
    const sub = this.submission();
    if (sub?.activityId) {
      this.router.navigate(['/academic/activities', sub.activityId, 'grading']);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }

  downloadSubmissionPdf(): void {
    const sub = this.submission();
    if (sub) {
      this.reportService.downloadSubmissionPdf(sub.id).subscribe();
    }
  }
}
