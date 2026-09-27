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
  templateUrl: './sus-questions-breakdown.component.html',
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
