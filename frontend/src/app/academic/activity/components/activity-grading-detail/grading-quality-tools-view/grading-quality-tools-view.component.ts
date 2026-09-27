import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QualityToolsDataDTO } from '../../../models/activity.model';

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
}
