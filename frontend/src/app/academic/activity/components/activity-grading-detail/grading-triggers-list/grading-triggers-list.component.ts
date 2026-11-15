import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IdentifiedTriggerData } from '../../../models/activity.model';
import { getTriggerDisplayEvidence } from '../../../utils/quality-tools-display.util';

/**
 * Componente que exibe a lista detalhada de gatilhos clínicos identificados pelo estudante
 * durante a auditoria, com suporte retrocompatível a registros legados sem evidência.
 */
@Component({
  selector: 'app-grading-triggers-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grading-triggers-list.component.html'
})
export class GradingTriggersListComponent {
  /**
   * Lista de gatilhos clínicos identificados na submissão.
   */
  @Input() triggers: IdentifiedTriggerData[] = [];

  /**
   * Obtém a justificativa clínica consolidada ou indicação explícita de ausência em registro legado.
   */
  getEvidenceText(trigger: IdentifiedTriggerData): string {
    return getTriggerDisplayEvidence(trigger);
  }

  /**
   * Formata o rótulo da categoria de dano preservando compatibilidade com campos atuais e legados.
   */
  getHarmBadgeText(trigger: IdentifiedTriggerData): string {
    if (trigger.harmCategory) {
      return trigger.harmCategory;
    }
    if (trigger.isHarm) {
      return `Com Dano (Cat. ${trigger.harmSeverityLetter || 'E'})`;
    }
    return 'Sem Dano Associado';
  }
}
