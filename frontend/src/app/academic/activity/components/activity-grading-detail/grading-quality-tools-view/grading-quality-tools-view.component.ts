import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GutItemData, QualityToolsDataDTO } from '../../../models/activity.model';
import {
  calculateGutItemScore,
  formatTextListOrString,
  getGutTrendValue
} from '../../../utils/quality-tools-display.util';

/**
 * Componente que exibe a consolidação das Ferramentas da Qualidade submetidas pelo estudante.
 * Apresenta Ishikawa (6M), Matriz GUT, Plano de Ação 5W2H, Ciclo PDCA e Matriz SWOT.
 */
@Component({
  selector: 'app-grading-quality-tools-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grading-quality-tools-view.component.html'
})
export class GradingQualityToolsViewComponent {
  /**
   * Dados das ferramentas da qualidade submetidas na auditoria.
   */
  @Input() qualityTools?: QualityToolsDataDTO | null = null;

  /**
   * Formata campos textuais que podem estar em lista (versão atual) ou string legada.
   */
  formatField(
    list?: string[] | string | null,
    legacyFallback?: string | null,
    emptyText?: string
  ): string {
    return formatTextListOrString(list, legacyFallback, emptyText || 'Sem observações');
  }

  /**
   * Retorna o valor de Tendência (T) suportando os campos trend e tendency (legado).
   */
  getGutTrend(item: GutItemData): number {
    return getGutTrendValue(item);
  }

  /**
   * Retorna a pontuação GUT (GxUxT) calculada ou persistida.
   */
  getGutScore(item: GutItemData): number {
    return calculateGutItemScore(item);
  }
}
