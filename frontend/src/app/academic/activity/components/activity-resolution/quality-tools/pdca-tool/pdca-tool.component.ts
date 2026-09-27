import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { PdcaData } from '../../../../models/activity.model';

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
}
