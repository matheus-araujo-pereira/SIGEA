import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GttMetricsDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição dos 4 cartões de indicadores oficiais do IHI-GTT:
 * 1. EAs por 1.000 Pacientes-Dia
 * 2. EAs por 100 Admissões
 * 3. % Admissões com >= 1 EA
 * 4. Total de Danos Detectados (NCC MERP E a I)
 */
@Component({
  selector: 'app-gtt-rates-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gtt-rates-cards.component.html',
})
export class GttRatesCardsComponent {
  /** Métricas epidemiológicas oficiais da metodologia IHI Global Trigger Tool */
  readonly metrics = input.required<GttMetricsDTO>();
}
