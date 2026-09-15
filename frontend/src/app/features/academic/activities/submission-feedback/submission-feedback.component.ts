import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Componente para visualização da resolução com nota e parecer pedagógico do professor.
 */
@Component({
  selector: 'app-submission-feedback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6 max-w-6xl mx-auto">
      <!-- Topo & Voltar -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Voltar para Minhas Atividades"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <div>
            <h1 class="text-2xl font-black text-slate-900 tracking-tight">Feedback Pedagógico</h1>
            <p class="text-xs text-slate-500 font-medium">Nota atribuída, parecer do professor e espelho da resolução enviada</p>
          </div>
        </div>
      </div>

      @if (isLoading()) {
        <div class="py-24 text-center text-slate-400 bg-white rounded-2xl border border-slate-200/80">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-clinical-600 border-t-transparent mb-3"></div>
          <p class="text-xs font-medium">Carregando avaliação pedagógica...</p>
        </div>
      } @else {
        @if (submission(); as sub) {
          <!-- CARD DE NOTA E PARECER DO PROFESSOR -->
        <div class="rounded-2xl p-6 border shadow-xs"
             [ngClass]="sub.isGraded
               ? (sub.grade! >= 7 ? 'bg-gradient-to-br from-emerald-900 to-slate-900 text-white border-emerald-700/50' : 'bg-gradient-to-br from-amber-900 to-slate-900 text-white border-amber-700/50')
               : 'bg-white border-slate-200 text-slate-900'">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 border-b pb-6"
               [ngClass]="sub.isGraded ? 'border-white/10' : 'border-slate-100'">
            <div class="space-y-1">
              <span class="text-[10px] uppercase font-bold tracking-wider"
                    [ngClass]="sub.isGraded ? 'text-emerald-300' : 'text-slate-400'">
                Status da Avaliação
              </span>
              <h2 class="text-xl font-black tracking-tight flex items-center gap-2">
                @if (sub.isGraded) {
                  <svg class="w-6 h-6 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Atividade Avaliada
                } @else {
                  <svg class="w-6 h-6 text-amber-500 animate-pulse" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Aguardando Correção pelo Docente
                }
              </h2>
              <p class="text-xs" [ngClass]="sub.isGraded ? 'text-slate-300' : 'text-slate-500'">
                Submetido em {{ sub.submissionDate | date:'dd/MM/yyyy HH:mm' }} por {{ sub.studentName }}
              </p>
            </div>

            <!-- Placar da Nota -->
            <div class="flex items-center gap-4">
              <div class="text-right">
                <span class="text-[10px] uppercase font-bold tracking-wider block"
                      [ngClass]="sub.isGraded ? 'text-slate-300' : 'text-slate-400'">
                  Nota Final
                </span>
                @if (sub.isGraded) {
                  <div class="flex items-baseline gap-1 font-mono font-black text-3xl md:text-4xl text-emerald-400">
                    {{ sub.grade | number:'1.2-2' }}
                    <span class="text-sm font-medium text-slate-400">/ 10.0</span>
                  </div>
                } @else {
                  <span class="text-xl font-bold font-mono text-amber-500">Pendente</span>
                }
              </div>
            </div>
          </div>

          <!-- Parecer Pedagógico Formatado -->
          <div class="mt-6">
            <span class="text-[11px] uppercase font-bold tracking-wider block mb-2"
                  [ngClass]="sub.isGraded ? 'text-emerald-300' : 'text-slate-500'">
              Parecer Pedagógico do Docente
            </span>
            @if (sub.isGraded && sub.pedagogicalFeedback) {
              <div class="p-4 rounded-xl text-xs leading-relaxed"
                   [ngClass]="sub.isGraded ? 'bg-white/10 text-slate-100 border border-white/15' : 'bg-slate-50 text-slate-800 border border-slate-200'">
                <p class="whitespace-pre-line">{{ sub.pedagogicalFeedback }}</p>
              </div>
            } @else {
              <p class="text-xs italic" [ngClass]="sub.isGraded ? 'text-slate-400' : 'text-slate-400'">
                O professor ainda não disponibilizou os comentários pedagógicos desta resolução.
              </p>
            }
          </div>
        </div>

        <!-- ESPELHO DA RESOLUÇÃO DO ESTUDANTE -->
        <div class="space-y-6">
          <h3 class="text-base font-bold text-slate-900 border-b border-slate-200 pb-2">
            Espelho da Resolução Enviada
          </h3>

          <!-- Gatilhos Identificados -->
          <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
              <span class="w-6 h-6 rounded-lg bg-clinical-50 text-clinical-700 flex items-center justify-center font-bold text-xs">
                1
              </span>
              Gatilhos Clínicos e Dano Identificados ({{ sub.identifiedTriggers.length }})
            </h4>

            @if (sub.identifiedTriggers.length === 0) {
              <p class="text-xs text-slate-400 italic">Nenhum gatilho registrado.</p>
            } @else {
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                @for (trig of sub.identifiedTriggers; track trig.triggerCode) {
                  <div class="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-2">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center gap-2">
                        <span class="font-mono text-xs font-bold px-2 py-0.5 rounded bg-clinical-100 text-clinical-800 border border-clinical-200">
                          {{ trig.triggerCode }}
                        </span>
                        <span class="text-xs font-bold text-slate-800">{{ trig.triggerName }}</span>
                      </div>
                      <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-900 border border-amber-300">
                        {{ trig.harmCategory }}
                      </span>
                    </div>
                    <div class="text-xs text-slate-600 bg-white p-2.5 rounded-lg border border-slate-100">
                      <span class="font-semibold text-slate-700 block text-[10px] uppercase">Raciocínio Clínico:</span>
                      {{ trig.rationale }}
                    </div>
                  </div>
                }
              </div>
            }
          </div>

          <!-- Ferramentas da Qualidade -->
          <!-- Ishikawa 6M -->
          @if (sub.qualityTools?.ishikawa; as ish) {
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
                <span class="w-6 h-6 rounded-lg bg-blue-50 text-blue-700 flex items-center justify-center font-bold text-xs">
                  I
                </span>
                Diagrama de Ishikawa (6M)
              </h4>
              <div class="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs">
                <strong class="text-rose-800 block uppercase text-[10px]">Problema Central / Efeito:</strong>
                <p class="text-rose-950 font-bold mt-0.5">{{ ish.problem || 'Não informado' }}</p>
              </div>
              <div class="grid grid-cols-2 md:grid-cols-3 gap-3 text-xs">
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Método</span>
                  <p class="text-slate-600 mt-1">{{ ish.method || '-' }}</p>
                </div>
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Mão de Obra</span>
                  <p class="text-slate-600 mt-1">{{ ish.manpower || '-' }}</p>
                </div>
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Material</span>
                  <p class="text-slate-600 mt-1">{{ ish.material || '-' }}</p>
                </div>
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Máquina</span>
                  <p class="text-slate-600 mt-1">{{ ish.machine || '-' }}</p>
                </div>
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Meio Ambiente</span>
                  <p class="text-slate-600 mt-1">{{ ish.environment || '-' }}</p>
                </div>
                <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                  <span class="font-bold text-slate-700 block">Medida</span>
                  <p class="text-slate-600 mt-1">{{ ish.measurement || '-' }}</p>
                </div>
              </div>
            </div>
          }

          <!-- Matriz GUT -->
          @if (sub.qualityTools?.gutItems?.length) {
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
                <span class="w-6 h-6 rounded-lg bg-amber-50 text-amber-700 flex items-center justify-center font-bold text-xs">
                  G
                </span>
                Matriz GUT
              </h4>
              <div class="overflow-x-auto">
                <table class="w-full text-xs text-left">
                  <thead>
                    <tr class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] border-b border-slate-200">
                      <th class="py-2 px-3">Problema</th>
                      <th class="py-2 px-3 text-center">G</th>
                      <th class="py-2 px-3 text-center">U</th>
                      <th class="py-2 px-3 text-center">T</th>
                      <th class="py-2 px-3 text-center font-mono">Score GUT</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-slate-100">
                    @for (item of sub.qualityTools!.gutItems; track item.problem) {
                      <tr>
                        <td class="py-2 px-3 font-medium text-slate-800">{{ item.problem }}</td>
                        <td class="py-2 px-3 text-center">{{ item.gravity }}</td>
                        <td class="py-2 px-3 text-center">{{ item.urgency }}</td>
                        <td class="py-2 px-3 text-center">{{ item.tendency }}</td>
                        <td class="py-2 px-3 text-center font-bold font-mono text-clinical-700">{{ item.score }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          }

          <!-- Matriz 5W2H -->
          @if (sub.qualityTools?.fiveWTwoHItems?.length) {
            <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
                <span class="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold text-xs">
                  5W
                </span>
                Plano de Ação 5W2H
              </h4>
              <div class="overflow-x-auto">
                <table class="w-full text-xs text-left">
                  <thead>
                    <tr class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] border-b border-slate-200">
                      <th class="py-2 px-3">O Que</th>
                      <th class="py-2 px-3">Por Que</th>
                      <th class="py-2 px-3">Onde</th>
                      <th class="py-2 px-3">Quando</th>
                      <th class="py-2 px-3">Quem</th>
                      <th class="py-2 px-3">Como</th>
                      <th class="py-2 px-3">Quanto</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-slate-100">
                    @for (item of sub.qualityTools!.fiveWTwoHItems; track $index) {
                      <tr>
                        <td class="py-2 px-3 font-semibold text-slate-800">{{ item.what }}</td>
                        <td class="py-2 px-3 text-slate-600">{{ item.why }}</td>
                        <td class="py-2 px-3 text-slate-600">{{ item.where }}</td>
                        <td class="py-2 px-3 text-slate-600">{{ item.when }}</td>
                        <td class="py-2 px-3 text-slate-600 font-medium">{{ item.who }}</td>
                        <td class="py-2 px-3 text-slate-600">{{ item.how }}</td>
                        <td class="py-2 px-3 font-mono text-slate-700">{{ item.howMuch }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          }

          <!-- PDCA & SWOT -->
          @if (sub.qualityTools?.pdca || sub.qualityTools?.swot) {
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              @if (sub.qualityTools?.pdca; as pdca) {
                <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-3">
                  <h4 class="text-sm font-bold text-slate-800">Ciclo PDCA</h4>
                  <div class="space-y-2 text-xs">
                    <div class="p-2.5 rounded-lg bg-blue-50/60 border border-blue-100">
                      <strong class="text-blue-800 block">Plan:</strong>
                      <p class="text-slate-700">{{ pdca.plan || '-' }}</p>
                    </div>
                    <div class="p-2.5 rounded-lg bg-emerald-50/60 border border-emerald-100">
                      <strong class="text-emerald-800 block">Do:</strong>
                      <p class="text-slate-700">{{ pdca.doAction || '-' }}</p>
                    </div>
                    <div class="p-2.5 rounded-lg bg-amber-50/60 border border-amber-100">
                      <strong class="text-amber-800 block">Check:</strong>
                      <p class="text-slate-700">{{ pdca.checkAction || '-' }}</p>
                    </div>
                    <div class="p-2.5 rounded-lg bg-purple-50/60 border border-purple-100">
                      <strong class="text-purple-800 block">Act:</strong>
                      <p class="text-slate-700">{{ pdca.act || '-' }}</p>
                    </div>
                  </div>
                </div>
              }

              @if (sub.qualityTools?.swot; as swot) {
                <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-3">
                  <h4 class="text-sm font-bold text-slate-800">Matriz SWOT & Brainstorming</h4>
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
                  @if (sub.qualityTools?.brainstormingNotes) {
                    <div class="p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs">
                      <strong class="text-slate-700 block">Notas de Brainstorming:</strong>
                      <p class="text-slate-600">{{ sub.qualityTools!.brainstormingNotes }}</p>
                    </div>
                  }
                </div>
              }
            </div>
          }
        </div>
      }
    }
  </div>
  `
})
export class SubmissionFeedbackComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
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

  goBack(): void {
    this.router.navigate(['/academic/student/activities']);
  }
}
