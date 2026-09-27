import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IdentifiedTriggerDTO } from '../../models/activity.model';

/**
 * Componente que exibe a listagem de gatilhos clínicos identificados pelo estudante na auditoria.
 */
@Component({
  selector: 'app-grading-triggers-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grading-triggers-list.component.html'
})
export class GradingTriggersListComponent {
  /**
   * Coleção de gatilhos clínicos identificados na resolução.
   */
  @Input() triggers: IdentifiedTriggerDTO[] = [];
}
