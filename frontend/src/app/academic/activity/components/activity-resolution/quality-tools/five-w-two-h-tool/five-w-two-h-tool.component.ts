import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FiveWTwoHItemData } from '../../../../models/activity.model';

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
   * Notifica a adição de uma nova ação ao plano.
   */
  @Output() addItem = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma ação por índice.
   */
  @Output() removeItem = new EventEmitter<number>();
}
