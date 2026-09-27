import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TriggerOccurrenceDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição dos gatilhos clínicos mais identificados
 * nos prontuários simulados da turma.
 */
@Component({
  selector: 'app-top-triggers-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-triggers-card.component.html',
})
export class TopTriggersCardComponent {
  /** Ranking dos gatilhos clínicos mais frequentes nos prontuários da turma */
  readonly triggers = input.required<TriggerOccurrenceDTO[]>();
}
