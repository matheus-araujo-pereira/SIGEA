import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Componente para correção e avaliação pedagógica da submissão do estudante pelo docente.
 */
@Component({
  selector: 'app-activity-grading-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="space-y-6 max-w-7xl mx-auto">
      <!-- Topo & Navegação -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Voltar para lista de submissões"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <div>
            <h1 class="text-2xl font-black text-slate-900 tracking-tight">Avaliação da Resolução</h1>
            <p class="text-xs text-slate-500 font-medium">Análise de gatilhos clínicos identificados e aplicação das Ferramentas da Qualidade</p>
          </div>
        </div>
      </div>

      @if (isLoading()) {
        <div class="py-24 text-center text-slate-400 bg-white rounded-2xl border border-slate-200/80">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-clinical-600 border-t-transparent mb-3"></div>
          <p class="text-xs font-medium">Carregando resolução do estudante...</p>
        </div>
      } @else {
        @if (submission(); as sub) {
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Coluna Principal (2/3): Resolução do Estudante -->
          <div class="lg:col-span-2 space-y-6">
            <!-- Dados do Estudante & Submissão -->
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs flex items-center justify-between">
              <div>
                <span class="text-[10px] uppercase font-bold text-slate-400">Estudante</span>
                <h2 class="text-lg font-bold text-slate-900">{{ sub.studentName }}</h2>
                <span class="text-xs text-slate-500 font-mono">{{ sub.studentEmail }}</span>
              </div>
              <div class="text-right">
                <span class="text-[10px] uppercase font-bold text-slate-400">Data de Submissão</span>
                <p class="text-xs font-semibold text-slate-700">{{ sub.submissionDate | date:'dd/MM/yyyy HH:mm' }}</p>
                <div class="mt-1">
                  @if (sub.isGraded) {
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                      Nota Atual: {{ sub.grade | number:'1.2-2' }}
                    </span>
                  } @else {
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                      Pendente de Nota
                    </span>
                  }
                </div>
              </div>
            </div>

            <!-- 1. Gatilhos Clínicos e Dano Identificados -->
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-lg bg-clinical-50 text-clinical-700 flex items-center justify-center font-bold text-xs">
                    1
                  </span>
                  <h3 class="text-sm font-bold text-slate-800">Gatilhos Clínicos Identificados ({{ sub.identifiedTriggers.length }})</h3>
                </div>
              </div>

              @if (sub.identifiedTriggers.length === 0) {
                <p class="text-xs text-slate-400 italic py-2">Nenhum gatilho foi identificado pelo estudante.</p>
              } @else {
                <div class="space-y-3">
                  @for (trig of sub.identifiedTriggers; track trig.triggerCode) {
                    <div class="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-2">
                      <div class="flex items-center justify-between">
                        <div class="flex items-center gap-2">
                          <span class="font-mono text-xs font-bold px-2 py-0.5 rounded bg-clinical-100 text-clinical-800 border border-clinical-200">
                            {{ trig.triggerCode }}
                          </span>
                          <span class="text-xs font-bold text-slate-800">{{ trig.triggerName }}</span>
                        </div>
                        <span class="px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-100 text-amber-900 border border-amber-300">
                          {{ trig.harmCategory }}
                        </span>
                      </div>
                      <div class="text-xs text-slate-600 bg-white p-3 rounded-lg border border-slate-100">
                        <span class="font-semibold text-slate-700 block mb-0.5 text-[11px] uppercase">Justificativa Clínica:</span>
                        {{ trig.rationale }}
                      </div>
                    </div>
                  }
                </div>
              }
            </div>

            <!-- 2. Ferramentas da Qualidade Aplicadas -->
            <div class="space-y-4">
              <h3 class="text-sm font-bold text-slate-800 uppercase tracking-wider text-slate-500">
                Ferramentas da Qualidade Submetidas
              </h3>

              <!-- Ishikawa 6M -->
              <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
                <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
                  <span class="w-6 h-6 rounded-lg bg-blue-50 text-blue-700 flex items-center justify-center font-bold text-xs">
                    I
                  </span>
                  <h4 class="text-sm font-bold text-slate-800">Diagrama de Ishikawa (Causa e Efeito - 6M)</h4>
                </div>

                @if (sub.qualityTools?.ishikawa; as ish) {
                  <div class="bg-rose-50 border border-rose-200 rounded-xl p-3">
                    <span class="text-[10px] font-bold uppercase text-rose-700 block">Efeito / Problema Central:</span>
                    <p class="text-xs font-bold text-rose-950">{{ ish.problem || 'Não informado' }}</p>
                  </div>

                  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 text-xs">
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Método</span>
                      <p class="text-slate-600">{{ ish.method || 'Sem observações' }}</p>
                    </div>
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Mão de Obra</span>
                      <p class="text-slate-600">{{ ish.manpower || 'Sem observações' }}</p>
                    </div>
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Material</span>
                      <p class="text-slate-600">{{ ish.material || 'Sem observações' }}</p>
                    </div>
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Máquina</span>
                      <p class="text-slate-600">{{ ish.machine || 'Sem observações' }}</p>
                    </div>
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Meio Ambiente</span>
                      <p class="text-slate-600">{{ ish.environment || 'Sem observações' }}</p>
                    </div>
                    <div class="p-3 rounded-xl bg-slate-50 border border-slate-200">
                      <span class="font-bold text-slate-700 block mb-1">Medida</span>
                      <p class="text-slate-600">{{ ish.measurement || 'Sem observações' }}</p>
                    </div>
                  </div>
                } @else {
                  <p class="text-xs text-slate-400 italic">Ishikawa não preenchido.</p>
                }
              </div>

              <!-- Matriz GUT -->
              <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
                <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
                  <span class="w-6 h-6 rounded-lg bg-amber-50 text-amber-700 flex items-center justify-center font-bold text-xs">
                    G
                  </span>
                  <h4 class="text-sm font-bold text-slate-800">Matriz GUT (Priorização de Problemas)</h4>
                </div>

                @if (sub.qualityTools?.gutItems?.length) {
                  <div class="overflow-x-auto">
                    <table class="w-full text-xs text-left">
                      <thead>
                        <tr class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] border-b border-slate-200">
                          <th class="py-2 px-3">Problema</th>
                          <th class="py-2 px-3 text-center">G (1-5)</th>
                          <th class="py-2 px-3 text-center">U (1-5)</th>
                          <th class="py-2 px-3 text-center">T (1-5)</th>
                          <th class="py-2 px-3 text-center font-mono">GUT (Score)</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-slate-100">
                        @for (item of sub.qualityTools!.gutItems; track item.problem) {
                          <tr>
                            <td class="py-2.5 px-3 font-medium text-slate-800">{{ item.problem }}</td>
                            <td class="py-2.5 px-3 text-center">{{ item.gravity }}</td>
                            <td class="py-2.5 px-3 text-center">{{ item.urgency }}</td>
                            <td class="py-2.5 px-3 text-center">{{ item.tendency }}</td>
                            <td class="py-2.5 px-3 text-center font-bold font-mono text-clinical-700">{{ item.score }}</td>
                          </tr>
                        }
                      </tbody>
                    </table>
                  </div>
                } @else {
                  <p class="text-xs text-slate-400 italic">Nenhum item adicionado na Matriz GUT.</p>
                }
              </div>

              <!-- Matriz 5W2H -->
              <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
                <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
                  <span class="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold text-xs">
                    5W
                  </span>
                  <h4 class="text-sm font-bold text-slate-800">Plano de Ação 5W2H</h4>
                </div>

                @if (sub.qualityTools?.fiveWTwoHItems?.length) {
                  <div class="overflow-x-auto">
                    <table class="w-full text-xs text-left">
                      <thead>
                        <tr class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] border-b border-slate-200">
                          <th class="py-2 px-3">O Que (What)</th>
                          <th class="py-2 px-3">Por Que (Why)</th>
                          <th class="py-2 px-3">Onde (Where)</th>
                          <th class="py-2 px-3">Quando (When)</th>
                          <th class="py-2 px-3">Quem (Who)</th>
                          <th class="py-2 px-3">Como (How)</th>
                          <th class="py-2 px-3">Quanto (Cost)</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-slate-100">
                        @for (item of sub.qualityTools!.fiveWTwoHItems; track $index) {
                          <tr>
                            <td class="py-2.5 px-3 font-semibold text-slate-800">{{ item.what }}</td>
                            <td class="py-2.5 px-3 text-slate-600">{{ item.why }}</td>
                            <td class="py-2.5 px-3 text-slate-600">{{ item.where }}</td>
                            <td class="py-2.5 px-3 text-slate-600 whitespace-nowrap">{{ item.when }}</td>
                            <td class="py-2.5 px-3 text-slate-600 font-medium">{{ item.who }}</td>
                            <td class="py-2.5 px-3 text-slate-600">{{ item.how }}</td>
                            <td class="py-2.5 px-3 font-mono text-slate-700">{{ item.howMuch }}</td>
                          </tr>
                        }
                      </tbody>
                    </table>
                  </div>
                } @else {
                  <p class="text-xs text-slate-400 italic">Nenhum plano 5W2H cadastrado.</p>
                }
              </div>

              <!-- PDCA & SWOT / Brainstorming -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- Ciclo PDCA -->
                <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-3">
                  <h4 class="text-sm font-bold text-slate-800">Ciclo PDCA (Melhoria Contínua)</h4>
                  @if (sub.qualityTools?.pdca; as pdca) {
                    <div class="space-y-2 text-xs">
                      <div class="p-2.5 rounded-lg bg-blue-50/60 border border-blue-100">
                        <strong class="text-blue-800 block">Plan (Planejar):</strong>
                        <p class="text-slate-700">{{ pdca.plan || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-emerald-50/60 border border-emerald-100">
                        <strong class="text-emerald-800 block">Do (Executar):</strong>
                        <p class="text-slate-700">{{ pdca.doAction || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-amber-50/60 border border-amber-100">
                        <strong class="text-amber-800 block">Check (Verificar):</strong>
                        <p class="text-slate-700">{{ pdca.checkAction || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-purple-50/60 border border-purple-100">
                        <strong class="text-purple-800 block">Act (Agir/Padronizar):</strong>
                        <p class="text-slate-700">{{ pdca.act || '-' }}</p>
                      </div>
                    </div>
                  } @else {
                    <p class="text-xs text-slate-400 italic">PDCA não preenchido.</p>
                  }
                </div>

                <!-- Matriz SWOT & Brainstorming -->
                <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-3">
                  <h4 class="text-sm font-bold text-slate-800">Matriz SWOT (FOFA) & Brainstorming</h4>
                  @if (sub.qualityTools?.swot; as swot) {
                    <div class="grid grid-cols-2 gap-2 text-xs">
                      <div class="p-2.5 rounded-lg bg-emerald-50/50 border border-emerald-100">
                        <strong class="text-emerald-800 block">Forças:</strong>
                        <p class="text-slate-700">{{ swot.strengths || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-rose-50/50 border border-rose-100">
                        <strong class="text-rose-800 block">Fraquezas:</strong>
                        <p class="text-slate-700">{{ swot.weaknesses || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-blue-50/50 border border-blue-100">
                        <strong class="text-blue-800 block">Oportunidades:</strong>
                        <p class="text-slate-700">{{ swot.opportunities || '-' }}</p>
                      </div>
                      <div class="p-2.5 rounded-lg bg-amber-50/50 border border-amber-100">
                        <strong class="text-amber-800 block">Ameaças:</strong>
                        <p class="text-slate-700">{{ swot.threats || '-' }}</p>
                      </div>
                    </div>
                  }
                  @if (sub.qualityTools?.brainstormingNotes) {
                    <div class="mt-2 p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs">
                      <strong class="text-slate-700 block">Notas de Brainstorming:</strong>
                      <p class="text-slate-600">{{ sub.qualityTools!.brainstormingNotes }}</p>
                    </div>
                  }
                </div>
              </div>
            </div>
          </div>

          <!-- Coluna Lateral (1/3): Painel de Atribuição de Nota & Parecer Pedagógico -->
          <div class="lg:col-span-1">
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs sticky top-24 space-y-6">
              <div>
                <h3 class="text-base font-bold text-slate-900">Parecer e Atribuição de Nota</h3>
                <p class="text-xs text-slate-500 font-medium">Defina a nota final (0.00 a 10.00) e o feedback formativo</p>
              </div>

              <form [formGroup]="gradeForm" (ngSubmit)="submitGrade()" class="space-y-4">
                <!-- Campo Nota -->
                <div>
                  <label for="grade" class="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                    Nota Final (0.00 a 10.00) *
                  </label>
                  <div class="relative">
                    <input
                      id="grade"
                      type="number"
                      step="0.1"
                      min="0"
                      max="10"
                      formControlName="grade"
                      placeholder="Ex: 8.5"
                      class="w-full text-lg font-mono font-bold px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-clinical-600 focus:border-transparent transition-all"
                      [ngClass]="{'border-rose-400 bg-rose-50/20': gradeForm.get('grade')?.invalid && gradeForm.get('grade')?.touched}"
                    />
                    <span class="absolute right-4 top-3 text-xs font-bold text-slate-400 font-mono">/ 10.0</span>
                  </div>
                  @if (gradeForm.get('grade')?.invalid && gradeForm.get('grade')?.touched) {
                    <p class="text-rose-600 text-xs mt-1">A nota é obrigatória e deve situar-se entre 0.00 e 10.00.</p>
                  }
                </div>

                <!-- Campo Parecer Pedagógico -->
                <div>
                  <label for="pedagogicalFeedback" class="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
                    Parecer Pedagógico / Feedback *
                  </label>
                  <textarea
                    id="pedagogicalFeedback"
                    rows="6"
                    formControlName="pedagogicalFeedback"
                    placeholder="Elabore o parecer formativo com apontamentos sobre acertos clínicos, identificação de danos e uso das ferramentas da qualidade..."
                    class="w-full text-xs px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-clinical-600 focus:border-transparent transition-all resize-y"
                    [ngClass]="{'border-rose-400 bg-rose-50/20': gradeForm.get('pedagogicalFeedback')?.invalid && gradeForm.get('pedagogicalFeedback')?.touched}"
                  ></textarea>
                  @if (gradeForm.get('pedagogicalFeedback')?.invalid && gradeForm.get('pedagogicalFeedback')?.touched) {
                    <p class="text-rose-600 text-xs mt-1">O parecer pedagógico é obrigatório (mínimo de 10 caracteres).</p>
                  }
                </div>

                <!-- Botões de Ação -->
                <div class="pt-2 space-y-2">
                  <button
                    type="submit"
                    [disabled]="gradeForm.invalid || isSubmitting()"
                    class="w-full py-3 px-4 rounded-xl font-bold text-xs text-white bg-clinical-600 hover:bg-clinical-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-md flex items-center justify-center gap-2"
                  >
                    @if (isSubmitting()) {
                      <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      <span>Salvando Avaliação...</span>
                    } @else {
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                      <span>{{ sub.isGraded ? 'Atualizar Avaliação' : 'Registrar Avaliação' }}</span>
                    }
                  </button>

                  <button
                    type="button"
                    (click)="goBack()"
                    class="w-full py-2.5 px-4 rounded-xl font-semibold text-xs text-slate-600 hover:bg-slate-100 transition-colors border border-slate-200"
                  >
                    Voltar
                  </button>
                </div>
              </form>
                </div>
              </div>
            </div>
          }
        }
      </div>
  `
})
export class ActivityGradingDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly activityService = inject(ActivityService);
  private readonly toastService = inject(ToastService);

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
}
