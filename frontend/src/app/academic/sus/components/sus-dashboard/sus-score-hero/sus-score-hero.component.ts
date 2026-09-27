import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Componente atômico para exibição dos indicadores principais da escala SUS:
 * Escore médio, aceitabilidade, conceito escolar e régua psicométrica de Bangor.
 */
@Component({
  selector: 'app-sus-score-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sus-score-hero.component.html',
})
export class SusScoreHeroComponent {
  /** Pontuação média do SUS (0 a 100) */
  readonly averageScore = input.required<number>();
  /** Classificação adjetiva (Pobre, Regular, Bom, Excelente, Melhor Imaginável) */
  readonly adjectiveRating = input.required<string>();
  /** Faixa de aceitabilidade segundo Bangor (Aceitável, Marginal, Inaceitável) */
  readonly acceptability = input.required<string>();
  /** Conceito acadêmico associado (A, B, C, D, F) */
  readonly gradeLevel = input.required<string>();
  /** Quantidade total de avaliações computadas */
  readonly totalEvaluations = input.required<number>();
  /** Indica se os dados estão filtrados por turma */
  readonly isClassMode = input<boolean>(false);
  /** Número total de discentes inscritos na turma */
  readonly enrolledStudentsCount = input<number>(0);
  /** Percentual de adesão dos discentes ao questionário */
  readonly responseRatePercentage = input<number>(0);

  /** Retorna a classe de cor de texto proporcional ao escore */
  getScoreTextColor(score: number): string {
    if (score >= 85) return 'text-emerald-600';
    if (score >= 70) return 'text-blue-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-rose-600';
  }

  /** Retorna a classe de badge semântico com base no escore */
  getScoreBadgeClass(score: number): string {
    if (score >= 85) return 'bg-emerald-100 text-emerald-800 border border-emerald-300';
    if (score >= 70) return 'bg-blue-100 text-blue-800 border border-blue-300';
    if (score >= 50) return 'bg-amber-100 text-amber-800 border border-amber-300';
    return 'bg-rose-100 text-rose-800 border border-rose-300';
  }
}
