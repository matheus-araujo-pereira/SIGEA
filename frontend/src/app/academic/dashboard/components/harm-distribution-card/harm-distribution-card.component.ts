import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GttMetricsDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para visualização da distribuição de eventos adversos
 * pelas categorias de gravidade de dano NCC MERP (E a I).
 */
@Component({
  selector: 'app-harm-distribution-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './harm-distribution-card.component.html',
})
export class HarmDistributionCardComponent {
  /** Métricas epidemiológicas com contagem e percentuais de gravidade de dano */
  readonly metrics = input.required<GttMetricsDTO>();

  /** Retorna a contagem absoluta de ocorrências de dano para a categoria especificada */
  getHarmCount(letter: string): number {
    const dist = this.metrics().harmDistribution;
    return dist && dist[letter] ? dist[letter] : 0;
  }

  /** Retorna a proporção percentual da gravidade em relação ao total de danos */
  getHarmPercentage(letter: string): number {
    const m = this.metrics();
    if (!m || m.totalAdverseEvents === 0) return 0;
    return m.harmPercentages?.[letter] ?? 0;
  }
}
