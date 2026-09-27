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
  SusEvaluationResponseDTO
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
  template: `
    <div class="space-y-6 max-w-5xl mx-auto pb-12">
      <!-- Topo & Navegação -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Voltar"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <div>
            <div class="flex items-center gap-2">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold tracking-wide uppercase bg-clinical-100 text-clinical-800">
                Psicometria & Usabilidade
              </span>
              <span class="text-xs text-slate-400 font-mono">ISO 9241-11</span>
            </div>
            <h1 class="text-2xl font-black text-slate-900 tracking-tight mt-1">Escala de Usabilidade do Sistema (SUS)</h1>
            <p class="text-xs text-slate-500 font-medium">Questionário padronizado internacional para validação de aceitação tecnológica</p>
          </div>
        </div>

        @if (selectedClassId()) {
          <div class="flex items-center gap-2">
            <span class="text-xs font-semibold text-slate-600">Turma:</span>
            <span class="px-3 py-1.5 rounded-xl bg-slate-100 text-slate-800 text-xs font-bold border border-slate-200">
              {{ currentClassName() || 'Turma Selecionada' }}
            </span>
          </div>
        }
      </div>

      @if (isLoading()) {
        <div class="py-24 text-center text-slate-400 bg-white rounded-2xl border border-slate-200 shadow-xs">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-clinical-600 border-t-transparent mb-3"></div>
          <p class="text-xs font-medium">Carregando dados da avaliação...</p>
        </div>
      } @else {
        <!-- SE JÁ RESPONDEU: EXIBIR CARD DE RESULTADO CONSOLIDADO -->
        @if (existingEvaluation(); as eval) {
          <div class="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
            <!-- Header do Resultado -->
            <div class="p-6 md:p-8 bg-gradient-to-br from-slate-900 via-clinical-950 to-slate-900 text-white">
              <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div class="space-y-2">
                  <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                    <svg class="w-4 h-4 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    Avaliação Enviada com Sucesso
                  </div>
                  <h2 class="text-2xl font-black tracking-tight">Seu Diagnóstico de Usabilidade</h2>
                  <p class="text-xs text-slate-300 max-w-xl">
                    Sua contribuição foi registrada anonimizada para o relatório da pesquisa institucional perante o CEP/UFS (CAAE nº 91836925.8.0000.5546).
                  </p>
                </div>

                <!-- Placar do Escore -->
                <div class="flex items-center gap-4 bg-white/10 backdrop-blur-md p-4 rounded-2xl border border-white/10">
                  <div class="text-center px-2">
                    <span class="text-[10px] font-bold uppercase tracking-wider text-slate-300 block">Escore SUS</span>
                    <div class="text-4xl font-black font-mono text-emerald-400">
                      {{ eval.score | number:'1.1-1' }}
                    </div>
                    <span class="text-[10px] text-slate-400 block mt-0.5">Escala 0 a 100</span>
                  </div>
                  <div class="border-l border-white/10 pl-4 space-y-1">
                    <span class="inline-block px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-500 text-white shadow-xs">
                      {{ eval.adjectiveRating }}
                    </span>
                    <div class="text-[11px] text-slate-300 font-medium">
                      Aceitabilidade: <strong class="text-white">{{ eval.acceptability }}</strong>
                    </div>
                    <div class="text-[11px] text-slate-300 font-medium">
                      Conceito: <strong class="text-white font-mono">Grau {{ eval.gradeLevel }}</strong>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Termômetro Psicométrico de Bangor -->
              <div class="mt-8 pt-6 border-t border-white/10">
                <div class="flex items-center justify-between text-[11px] text-slate-300 font-medium mb-2">
                  <span>Pobre (&lt; 50)</span>
                  <span>Regular (50 - 69)</span>
                  <span>Bom (70 - 84)</span>
                  <span>Melhor Imaginável (85 - 100)</span>
                </div>
                <div class="w-full bg-slate-800 rounded-full h-3 relative overflow-hidden p-0.5 flex">
                  <div class="w-[50%] bg-rose-500/70 h-full rounded-l-full"></div>
                  <div class="w-[20%] bg-amber-500/70 h-full"></div>
                  <div class="w-[15%] bg-blue-500/70 h-full"></div>
                  <div class="w-[15%] bg-emerald-500/90 h-full rounded-r-full"></div>
                </div>
                <div class="flex justify-between items-center text-[10px] text-slate-400 mt-1 font-mono">
                  <span>0</span>
                  <span>50</span>
                  <span>70</span>
                  <span>85</span>
                  <span>100</span>
                </div>
              </div>
            </div>

            <!-- Resumo das Respostas Dadas -->
            <div class="p-6 md:p-8 space-y-6">
              <h3 class="text-sm font-bold uppercase tracking-wider text-slate-700">Espelho das Suas Respostas</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                @for (q of questions; track q.id) {
                  <div class="p-4 rounded-xl border border-slate-100 bg-slate-50/60 flex items-start justify-between gap-4">
                    <div class="space-y-1">
                      <span class="text-[10px] font-bold text-slate-400 uppercase">Questão {{ q.id }}</span>
                      <p class="text-xs text-slate-700 font-medium leading-relaxed">{{ q.text }}</p>
                    </div>
                    <div class="flex-shrink-0 text-center">
                      <span class="inline-flex items-center justify-center w-8 h-8 rounded-xl font-mono font-bold text-xs shadow-xs"
                            [ngClass]="getAnswerClass(getAnswerValue(eval, q.id), q.isPositive)">
                        {{ getAnswerValue(eval, q.id) }}
                      </span>
                    </div>
                  </div>
                }
              </div>

              @if (eval.suggestions) {
                <div class="p-4 rounded-xl bg-clinical-50/50 border border-clinical-100 space-y-1">
                  <span class="text-[10px] font-bold text-clinical-800 uppercase tracking-wider">Suas Sugestões e Comentários</span>
                  <p class="text-xs text-slate-700 leading-relaxed">{{ eval.suggestions }}</p>
                </div>
              }
            </div>
          </div>
        } @else {
          <!-- FORMULÁRIO DE PREENCHIMENTO DO QUESTIONÁRIO SUS -->
          <div class="space-y-6">
            <!-- Instruções -->
            <div class="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-3">
              <h2 class="text-sm font-bold uppercase tracking-wider text-clinical-900 flex items-center gap-2">
                <svg class="w-5 h-5 text-clinical-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Instruções de Resposta
              </h2>
              <p class="text-xs text-slate-600 leading-relaxed">
                Por favor, avalie sua experiência geral ao utilizar o sistema <strong>SIGEA</strong> para o rastreamento retrospectivo de gatilhos clínicos e análise de prontuários.
                Marque para cada afirmação o nível de concordância de <strong>1 (Discordo Totalmente)</strong> a <strong>5 (Concordo Totalmente)</strong>.
                Não há respostas certas ou erradas; o objetivo é validar a carga cognitiva e facilidade de operação.
              </p>

              <!-- Seletor de Turma (se houver turmas disponíveis) -->
              @if (availableClasses().length > 0) {
                <div class="pt-3 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center gap-3">
                  <label for="class-select" class="text-xs font-bold text-slate-700">Vincular à Turma:</label>
                  <select
                    id="class-select"
                    [(ngModel)]="selectedClassId"
                    (ngModelChange)="onClassChange()"
                    class="px-3 py-2 rounded-xl border border-slate-200 text-xs font-semibold text-slate-800 bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                  >
                    <option [ngValue]="null">Geral (Experiência Geral com a Plataforma)</option>
                    @for (c of availableClasses(); track c.id) {
                      <option [ngValue]="c.id">{{ c.subjectName }} - {{ c.classCode }} ({{ c.academicPeriod }})</option>
                    }
                  </select>
                </div>
              }
            </div>

            <!-- Lista das 10 Questões -->
            <div class="space-y-4">
              @for (q of questions; track q.id) {
                <div class="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:border-clinical-200 transition-all">
                  <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div class="space-y-1 max-w-2xl">
                      <div class="flex items-center gap-2">
                        <span class="inline-flex items-center justify-center w-5 h-5 rounded-full text-[10px] font-bold bg-slate-100 text-slate-600">
                          {{ q.id }}
                        </span>
                        <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                          {{ q.isPositive ? 'Aspecto Positivo' : 'Aspecto Negativo' }}
                        </span>
                      </div>
                      <p class="text-sm font-semibold text-slate-800 leading-snug">
                        {{ q.text }}
                      </p>
                    </div>

                    <!-- Escala Likert 1 a 5 -->
                    <div class="flex items-center gap-1.5 sm:gap-2">
                      @for (opt of likertOptions; track opt.value) {
                        <button
                          type="button"
                          (click)="setAnswer(q.id, opt.value)"
                          class="flex flex-col items-center justify-center w-12 h-14 sm:w-16 sm:h-16 rounded-xl border text-center transition-all"
                          [ngClass]="answers()[q.id - 1] === opt.value
                            ? 'bg-clinical-600 text-white border-clinical-600 shadow-md ring-2 ring-clinical-400/50 scale-105'
                            : 'bg-slate-50 hover:bg-slate-100 text-slate-600 border-slate-200 hover:border-slate-300'"
                          [title]="opt.label"
                        >
                          <span class="font-bold text-base sm:text-lg font-mono leading-none">
                            {{ opt.shortLabel }}
                          </span>
                          <span class="text-[9px] uppercase font-bold mt-1 line-clamp-1 px-1 opacity-90">
                            {{ opt.value === 1 ? 'Discordo' : (opt.value === 5 ? 'Concordo' : (opt.value === 3 ? 'Neutro' : '')) }}
                          </span>
                        </button>
                      }
                    </div>
                  </div>
                </div>
              }
            </div>

            <!-- Sugestões Qualitativas -->
            <div class="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-2">
              <label for="suggestions" class="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Sugestões de Melhoria e Observações (Opcional)
              </label>
              <textarea
                id="suggestions"
                [(ngModel)]="suggestions"
                rows="3"
                maxlength="2000"
                placeholder="Compartilhe impressões qualitativas, dificuldades encontradas ou sugestões para o aprimoramento da plataforma..."
                class="w-full px-4 py-3 rounded-xl border border-slate-200 text-xs text-slate-800 focus:outline-none focus:ring-2 focus:ring-clinical-500 focus:border-transparent transition-all placeholder:text-slate-400"
              ></textarea>
              <div class="text-right text-[10px] text-slate-400">
                {{ suggestions.length }} / 2000 caracteres
              </div>
            </div>

            <!-- Barra Inferior de Envio com Preview em Tempo Real -->
            <div class="p-6 rounded-2xl bg-slate-900 text-white shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-6">
              <div class="space-y-1">
                <div class="flex items-center gap-3">
                  <span class="text-xs font-bold uppercase tracking-wider text-slate-400">Progresso:</span>
                  <div class="w-32 bg-slate-700 rounded-full h-2 overflow-hidden">
                    <div
                      class="bg-emerald-400 h-full rounded-full transition-all duration-300"
                      [style.width.%]="(answeredCount() / 10) * 100"
                    ></div>
                  </div>
                  <span class="text-xs font-mono font-bold text-emerald-400">{{ answeredCount() }} / 10 respondidas</span>
                </div>
                @if (answeredCount() === 10) {
                  <p class="text-xs text-slate-300">
                    Escore estimado: <strong class="text-emerald-400 font-mono">{{ estimatedScore() | number:'1.1-1' }}</strong> / 100 ({{ estimatedRating() }})
                  </p>
                } @else {
                  <p class="text-xs text-slate-400">
                    Responda às {{ 10 - answeredCount() }} questões restantes para habilitar o envio.
                  </p>
                }
              </div>

              <div class="flex items-center gap-3">
                <button
                  type="button"
                  (click)="goBack()"
                  class="px-4 py-2.5 rounded-xl border border-slate-700 text-slate-300 hover:text-white hover:bg-slate-800 text-xs font-semibold transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  (click)="submitEvaluation()"
                  [disabled]="answeredCount() < 10 || isSubmitting()"
                  class="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 disabled:bg-slate-700 disabled:text-slate-500 text-white font-bold text-xs shadow-md transition-all flex items-center gap-2 cursor-pointer disabled:cursor-not-allowed"
                >
                  @if (isSubmitting()) {
                    <div class="inline-block animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                    <span>Gravando Avaliação...</span>
                  } @else {
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>Finalizar Avaliação SUS</span>
                  }
                </button>
              </div>
            </div>
          </div>
        }
      }
    </div>
  `
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

  readonly estimatedScore = computed(() => {
    if (this.answeredCount() < 10) return 0;
    const [q1, q2, q3, q4, q5, q6, q7, q8, q9, q10] = this.answers();
    const sum = (q1 - 1) + (5 - q2) + (q3 - 1) + (5 - q4) + (q5 - 1)
              + (5 - q6) + (q7 - 1) + (5 - q8) + (q9 - 1) + (5 - q10);
    return sum * 2.5;
  });

  readonly estimatedRating = computed(() => {
    const score = this.estimatedScore();
    if (score >= 85) return 'Melhor Imaginável';
    if (score >= 70) return 'Bom';
    if (score >= 50) return 'Regular';
    return 'Pobre';
  });

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
    }
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
