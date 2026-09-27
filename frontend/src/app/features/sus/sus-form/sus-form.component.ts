import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SusService } from '../../../core/services/sus.service';
import { AcademicClassService } from '../../../core/services/academic-class.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  SUS_QUESTIONS,
  LIKERT_OPTIONS,
  SusQuestion,
  SusEvaluationCreateDTO,
  SusEvaluationResponseDTO,
  SusEvaluationPreviewDTO,
} from '../../../core/models/sus.model';
import { AcademicClassResponseDTO } from '../../../core/models/academic-class.model';

/**
 * Componente interativo para preenchimento e consulta da Escala de Usabilidade do Sistema (SUS).
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 */
@Component({
  selector: 'app-sus-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sus-form.component.html'
})
export class SusFormComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly susService = inject(SusService);
  private readonly academicClassService = inject(AcademicClassService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);

  readonly questions: SusQuestion[] = SUS_QUESTIONS;
  readonly likertOptions = LIKERT_OPTIONS;

  readonly isLoading = signal<boolean>(true);
  readonly isSubmitting = signal<boolean>(false);
  readonly existingEvaluation = signal<SusEvaluationResponseDTO | null>(null);
  readonly availableClasses = signal<AcademicClassResponseDTO[]>([]);
  readonly selectedClassId = signal<string | null>(null);

  readonly answers = signal<number[]>([0, 0, 0, 0, 0, 0, 0, 0, 0, 0]);
  suggestions: string = '';

  readonly currentClassName = computed(() => {
    const id = this.selectedClassId();
    if (!id) return null;
    const cls = this.availableClasses().find((c) => c.id === id);
    return cls ? `${cls.subjectName} - ${cls.classCode}` : null;
  });

  readonly answeredCount = computed(() => {
    return this.answers().filter((a) => a >= 1 && a <= 5).length;
  });

  readonly previewData = signal<SusEvaluationPreviewDTO | null>(null);
  readonly estimatedScore = computed(() => this.previewData()?.score ?? 0);
  readonly estimatedRating = computed(() => this.previewData()?.adjectiveRating ?? '');

  ngOnInit(): void {
    const queryClassId = this.route.snapshot.queryParamMap.get('classId');
    if (queryClassId) {
      this.selectedClassId.set(queryClassId);
    }

    this.loadInitialData();
  }

  loadInitialData(): void {
    this.isLoading.set(true);

    this.academicClassService.getMyClasses().subscribe({
      next: (res) => {
        this.availableClasses.set(res.data?.content || []);
        this.checkExistingEvaluation();
      },
      error: () => {
        this.availableClasses.set([]);
        this.checkExistingEvaluation();
      }
    });
  }

  onClassChange(): void {
    this.checkExistingEvaluation();
  }

  checkExistingEvaluation(): void {
    this.isLoading.set(true);
    const classId = this.selectedClassId() || undefined;

    this.susService.getMyEvaluation(classId).subscribe({
      next: (res) => {
        this.existingEvaluation.set(res.data || null);
        this.isLoading.set(false);
      },
      error: () => {
        this.existingEvaluation.set(null);
        this.isLoading.set(false);
      }
    });
  }

  setAnswer(questionId: number, value: number): void {
    if (questionId >= 1 && questionId <= 10) {
      this.answers.update((arr) => {
        const next = [...arr];
        next[questionId - 1] = value;
        return next;
      });
      this.updateScorePreview();
    }
  }

  private updateScorePreview(): void {
    if (this.answeredCount() < 10) {
      this.previewData.set(null);
      return;
    }
    const ans = this.answers();
    const dto: SusEvaluationCreateDTO = {
      academicClassId: this.selectedClassId() || null,
      q1: ans[0],
      q2: ans[1],
      q3: ans[2],
      q4: ans[3],
      q5: ans[4],
      q6: ans[5],
      q7: ans[6],
      q8: ans[7],
      q9: ans[8],
      q10: ans[9],
      suggestions: this.suggestions.trim() || undefined
    };
    this.susService.calculatePreview(dto).subscribe({
      next: (res) => {
        if (res.data) {
          this.previewData.set(res.data);
        }
      },
      error: () => {
        this.previewData.set(null);
      }
    });
  }

  submitEvaluation(): void {
    if (this.answeredCount() < 10) {
      this.toastService.warning('Por favor, responda às 10 questões antes de enviar.');
      return;
    }

    this.isSubmitting.set(true);
    const ans = this.answers();

    const dto: SusEvaluationCreateDTO = {
      academicClassId: this.selectedClassId() || null,
      q1: ans[0],
      q2: ans[1],
      q3: ans[2],
      q4: ans[3],
      q5: ans[4],
      q6: ans[5],
      q7: ans[6],
      q8: ans[7],
      q9: ans[8],
      q10: ans[9],
      suggestions: this.suggestions.trim() || undefined
    };

    this.susService.submitEvaluation(dto).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        if (res.data) {
          this.existingEvaluation.set(res.data);
        }
      },
      error: () => {
        this.isSubmitting.set(false);
      }
    });
  }

  getAnswerValue(evalObj: SusEvaluationResponseDTO, questionId: number): number {
    switch (questionId) {
      case 1: return evalObj.q1;
      case 2: return evalObj.q2;
      case 3: return evalObj.q3;
      case 4: return evalObj.q4;
      case 5: return evalObj.q5;
      case 6: return evalObj.q6;
      case 7: return evalObj.q7;
      case 8: return evalObj.q8;
      case 9: return evalObj.q9;
      case 10: return evalObj.q10;
      default: return 0;
    }
  }

  getAnswerClass(val: number, isPositive: boolean): string {
    const isDesirable = (isPositive && val >= 4) || (!isPositive && val <= 2);
    if (isDesirable) {
      return 'bg-emerald-100 text-emerald-800 border border-emerald-300';
    }
    const isNeutral = val === 3;
    if (isNeutral) {
      return 'bg-slate-100 text-slate-700 border border-slate-300';
    }
    return 'bg-rose-100 text-rose-800 border border-rose-300';
  }

  goBack(): void {
    const classId = this.selectedClassId();
    if (classId) {
      this.router.navigate(['/academic/classes', classId]);
    } else if (this.authService.isStudent()) {
      this.router.navigate(['/academic/student/activities']);
    } else {
      this.router.navigate(['/gtt/modules']);
    }
  }
}
