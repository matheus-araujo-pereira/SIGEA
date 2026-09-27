import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SusQuestion } from '../../../models/sus.model';

/**
 * Componente atômico para decomposição das 10 questões psicométricas da escala SUS:
 * Médias na escala Likert de 1.0 a 5.0 com barras de progresso e distinção entre aspectos positivos e negativos.
 */
@Component({
  selector: 'app-sus-questions-breakdown',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-sm font-bold uppercase tracking-wider text-slate-800">
            Desempenho por Afirmação Psicométrica
          </h2>
          <p class="text-xs text-slate-500">
            Médias na escala Likert de 1.0 (Discordo Totalmente) a 5.0 (Concordo Totalmente).
          </p>
        </div>
        <div class="flex items-center gap-3 text-[11px] font-medium text-slate-500">
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span> Desejável
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-slate-300"></span> Neutro
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-rose-500"></span> Atenção
          </span>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        @for (q of questions(); track q.id) {
          <div class="p-4 rounded-xl border border-slate-100 bg-slate-50/50 space-y-2">
            <div class="flex items-start justify-between gap-3">
              <div class="space-y-1">
                <div class="flex items-center gap-2">
                  <span class="inline-flex items-center justify-center w-5 h-5 rounded-full text-[10px] font-bold bg-slate-200 text-slate-700 font-mono">
                    {{ q.id }}
                  </span>
                  <span
                    class="text-[10px] font-bold uppercase tracking-wider"
                    [ngClass]="q.isPositive ? 'text-emerald-700' : 'text-rose-700'"
                  >
                    {{ q.isPositive ? 'Aspecto Positivo' : 'Aspecto Negativo' }}
                  </span>
                </div>
                <p class="text-xs font-semibold text-slate-800 leading-snug">
                  {{ q.text }}
                </p>
              </div>
              <div class="text-right flex-shrink-0">
                <span
                  class="text-lg font-black font-mono"
                  [ngClass]="getQuestionColor(getQuestionAverage(q.id), q.isPositive)"
                >
                  {{ getQuestionAverage(q.id) | number:'1.2-2' }}
                </span>
                <span class="text-[10px] text-slate-400 block font-mono">/ 5.0</span>
              </div>
            </div>

            <!-- Barra de Progresso Likert -->
            <div class="w-full bg-slate-200 rounded-full h-1.5 overflow-hidden">
              <div
                class="h-full rounded-full transition-all"
                [ngClass]="getQuestionBarClass(getQuestionAverage(q.id), q.isPositive)"
                [style.width.%]="(getQuestionAverage(q.id) / 5) * 100"
              ></div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class SusQuestionsBreakdownComponent {
  /** As 10 perguntas padrão da escala SUS */
  readonly questions = input.required<SusQuestion[]>();
  /** Médias calculadas para cada uma das questões (1 a 10) */
  readonly questionAverages = input.required<number[]>();

  /** Retorna a pontuação média correspondente ao identificador da pergunta */
  getQuestionAverage(questionId: number): number {
    const list = this.questionAverages();
    if (!list || questionId < 1 || questionId > list.length) return 0;
    return list[questionId - 1];
  }

  /** Retorna classe CSS de cor para o número da média de acordo com a desejabilidade */
  getQuestionColor(val: number, isPositive: boolean): string {
    const isDesirable = (isPositive && val >= 3.5) || (!isPositive && val <= 2.5);
    if (isDesirable) return 'text-emerald-600';
    const isNeutral = val > 2.5 && val < 3.5;
    if (isNeutral) return 'text-slate-600';
    return 'text-rose-600';
  }

  /** Retorna classe CSS para o preenchimento da barra de progresso da média */
  getQuestionBarClass(val: number, isPositive: boolean): string {
    const isDesirable = (isPositive && val >= 3.5) || (!isPositive && val <= 2.5);
    if (isDesirable) return 'bg-emerald-500';
    const isNeutral = val > 2.5 && val < 3.5;
    if (isNeutral) return 'bg-slate-400';
    return 'bg-rose-500';
  }
}
