import { Component, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SusEvaluationResponseDTO } from '../../../models/sus.model';

/**
 * Componente atômico para listagem das respostas individuais dos alunos à escala SUS
 * e modal de visualização de comentários e sugestões qualitativas.
 */
@Component({
  selector: 'app-sus-evaluations-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sus-evaluations-table.component.html',
})
export class SusEvaluationsTableComponent {
  /** Lista de avaliações SUS individuais registradas pelos estudantes */
  readonly evaluations = input.required<SusEvaluationResponseDTO[]>();
  /** Texto da sugestão qualitativa selecionada para exibição em modal */
  readonly selectedSuggestion = signal<string | null>(null);

  /** Retorna a classe de cor correspondente ao escore individual */
  getScoreTextColor(score: number): string {
    if (score >= 85) return 'text-emerald-600';
    if (score >= 70) return 'text-blue-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-rose-600';
  }

  /** Retorna a classe de badge correspondente ao escore */
  getScoreBadgeClass(score: number): string {
    if (score >= 85) return 'bg-emerald-100 text-emerald-800 border border-emerald-300';
    if (score >= 70) return 'bg-blue-100 text-blue-800 border border-blue-300';
    if (score >= 50) return 'bg-amber-100 text-amber-800 border border-amber-300';
    return 'bg-rose-100 text-rose-800 border border-rose-300';
  }

  /** Abre a janela modal com a sugestão completa do estudante */
  openSuggestion(text: string): void {
    this.selectedSuggestion.set(text);
  }

  /** Fecha a janela modal de sugestão qualitativa */
  closeSuggestion(): void {
    this.selectedSuggestion.set(null);
  }
}
