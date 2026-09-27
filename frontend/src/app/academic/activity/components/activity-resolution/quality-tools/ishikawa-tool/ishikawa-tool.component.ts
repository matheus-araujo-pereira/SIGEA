import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { IshikawaData } from '../../../../models/activity.model';

/**
 * Componente interativo para preenchimento do Diagrama de Causa e Efeito (Ishikawa 6M).
 */
@Component({
  selector: 'app-ishikawa-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ishikawa-tool.component.html'
})
export class IshikawaToolComponent {
  /**
   * Dados do diagrama de Ishikawa preenchidos pelo aluno.
   */
  @Input({ required: true }) ishikawa!: IshikawaData;

  /**
   * Notifica a adição de uma causa a uma das 6 dimensões.
   */
  @Output() addCause = new EventEmitter<'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement'>();

  /**
   * Notifica a remoção de uma causa específica.
   */
  @Output() removeCause = new EventEmitter<{ type: 'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement'; index: number }>();
}
