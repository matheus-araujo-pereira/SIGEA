import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FiveWTwoHItemData } from '../../../../models/activity.model';
import { autoResizeTextarea } from '../../../../utils/quality-tools-display.util';

/**
 * Componente interativo para elaboração do plano de ação 5W2H para prevenção de eventos adversos.
 */
@Component({
  selector: 'app-five-w-two-h-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './five-w-two-h-tool.component.html'
})
export class FiveWTwoHToolComponent {
  /**
   * Planos de ação estruturados pelo discente.
   */
  @Input() fiveWTwoHItems: FiveWTwoHItemData[] = [];

  /**
   * Bloqueia edição após submissão definitiva da atividade.
   */
  @Input() readOnly = false;

  /**
   * Notifica a adição de uma nova ação ao plano.
   */
  @Output() addItem = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma ação por índice.
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
}
