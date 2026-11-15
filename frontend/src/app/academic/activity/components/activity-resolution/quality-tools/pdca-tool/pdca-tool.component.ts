import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { PdcaData } from '../../../../models/activity.model';
import { autoResizeTextarea } from '../../../../utils/quality-tools-display.util';

/**
 * Componente interativo para estruturação de ciclos de melhoria contínua PDCA / PDSA.
 */
@Component({
  selector: 'app-pdca-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pdca-tool.component.html'
})
export class PdcaToolComponent {
  /**
   * Dados das 4 fases do ciclo PDCA.
   */
  @Input({ required: true }) pdca!: PdcaData;

  /**
   * Bloqueia edição após submissão definitiva da atividade.
   */
  @Input() readOnly = false;

  /**
   * Notifica alterações de preenchimento para disparo do auto-save.
   */
  @Output() dataChange = new EventEmitter<void>();

  /**
   * Expande dinamicamente a altura do campo de texto conforme o conteúdo digitado.
   */
  onAutoResize(event: Event): void {
    autoResizeTextarea(event, 104);
  }
}
