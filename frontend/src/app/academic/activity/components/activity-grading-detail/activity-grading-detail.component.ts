import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { ReportService } from '../../../report/services/report.service';
import { SubmissionResponseDTO } from '../../models/activity.model';
import { ToastService } from '../../../../common/services/toast.service';
import { GradingStudentCardComponent } from './grading-student-card/grading-student-card.component';
import { GradingTriggersListComponent } from './grading-triggers-list/grading-triggers-list.component';
import { GradingQualityToolsViewComponent } from './grading-quality-tools-view/grading-quality-tools-view.component';
import { GradingFeedbackFormComponent } from './grading-feedback-form/grading-feedback-form.component';

/**
 * Componente orquestrador para correção pedagógica e avaliação da submissão do estudante pelo docente.
 * Exibe dados do aluno, gatilhos clínicos identificados, ferramentas da qualidade e formulário de nota.
 */
@Component({
  selector: 'app-activity-grading-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    GradingStudentCardComponent,
    GradingTriggersListComponent,
    GradingQualityToolsViewComponent,
    GradingFeedbackFormComponent
  ],
  templateUrl: './activity-grading-detail.component.html'
})
export class ActivityGradingDetailComponent implements OnInit {
  /** Rota ativa para obtenção do ID da submissão */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Serviço de integração da API de atividades */
  private readonly activityService = inject(ActivityService);
  /** Serviço de notificações de toast */
  private readonly toastService = inject(ToastService);
  /** Serviço de emissão de relatórios acadêmicos */
  private readonly reportService = inject(ReportService);

  /**
   * Identificador único da submissão avaliada.
   */
  readonly submissionId = signal<string>('');

  /**
   * Resolução clínica detalhada do estudante.
   */
  readonly submission = signal<SubmissionResponseDTO | null>(null);

  /**
   * Indica se a submissão está sendo carregada do servidor.
   */
  readonly isLoading = signal<boolean>(true);

  /**
   * Indica se o registro de nota está em processamento.
   */
  readonly isSubmitting = signal<boolean>(false);

  /**
   * Formulário reativo para validação e registro de nota (0.00 a 10.00) e parecer pedagógico.
   */
  gradeForm: FormGroup = this.fb.group({
    grade: [null, [Validators.required, Validators.min(0), Validators.max(10)]],
    pedagogicalFeedback: ['', [Validators.required, Validators.minLength(10)]]
  });

  /**
   * Ciclo de inicialização: obtém o id da submissão da URL ou redireciona em caso de ausência.
   */
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

  /**
   * Carrega os dados da submissão clínica a partir do backend.
   */
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

  /**
   * Submete a avaliação pedagógica e atribuição de nota ao backend.
   */
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

  /**
   * Retorna para a tela de listagem de submissões da atividade.
   */
  goBack(): void {
    const sub = this.submission();
    if (sub?.activityId) {
      this.router.navigate(['/academic/activities', sub.activityId, 'grading']);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }

  /**
   * Dispara o download do relatório clínico oficial em formato PDF.
   */
  downloadSubmissionPdf(): void {
    const sub = this.submission();
    if (sub) {
      this.reportService.downloadSubmissionPdf(sub.id).subscribe();
    }
  }
}
