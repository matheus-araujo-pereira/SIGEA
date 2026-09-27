import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedagogicalMetricsDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição do desempenho pedagógico da turma:
 * média geral, total de alunos, atividades, submissões avaliadas e pendentes.
 */
@Component({
  selector: 'app-pedagogical-metrics-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedagogical-metrics-card.component.html',
})
export class PedagogicalMetricsCardComponent {
  /** Métricas pedagógicas e acadêmicas de desempenho da turma */
  readonly metrics = input.required<PedagogicalMetricsDTO>();
}
