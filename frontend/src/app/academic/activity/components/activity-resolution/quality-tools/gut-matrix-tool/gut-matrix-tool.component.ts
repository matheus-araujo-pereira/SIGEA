import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GutItemData } from '../../../../models/activity.model';
import { autoResizeTextarea } from '../../../../utils/quality-tools-display.util';

/**
 * Componente interativo para priorização de problemas assistenciais via Matriz GUT (Gravidade × Urgência × Tendência).
 */
@Component({
  selector: 'app-gut-matrix-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gut-matrix-tool.component.html'
})
export class GutMatrixToolComponent {
  /**
   * Coleção de problemas avaliados na Matriz GUT.
   */
  @Input() gutItems: GutItemData[] = [];

  /**
   * Bloqueia edição após submissão definitiva da atividade.
   */
  @Input() readOnly = false;

  /**
   * Notifica a adição de um novo problema à matriz.
   */
  @Output() addItem = new EventEmitter<void>();

  /**
   * Notifica a remoção de um problema por índice.
   */
  @Output() removeItem = new EventEmitter<number>();

  /**
   * Notifica alterações de preenchimento para disparo do auto-save.
   */
  @Output() dataChange = new EventEmitter<void>();

  /**
   * Expande dinamicamente a altura do campo de texto conforme o conteúdo digitado.
   */
  onAutoResize(event: Event): void {
    autoResizeTextarea(event, 84);
  }

  /**
   * Calcula o score ponderado (G × U × T), variando de 1 a 125.
   */
  calculateGutScore(item: GutItemData): number {
    const g = item.gravity || 1;
    const u = item.urgency || 1;
    const t = item.trend || 1;
    return g * u * t;
  }
}
