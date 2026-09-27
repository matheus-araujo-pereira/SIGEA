import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IdentifiedTriggerData } from '../../models/activity.model';
import { GttTrigger } from '../../../../gtt/trigger/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../gtt/severity/models/harm-severity.model';

/**
 * Componente para rastreamento dos 53 gatilhos clínicos do IHI-GTT e classificação de severidade NCC MERP.
 */
@Component({
  selector: 'app-trigger-detector-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trigger-detector-panel.component.html'
})
export class TriggerDetectorPanelComponent {
  /**
   * Catálogo de gatilhos clínicos disponíveis.
   */
  @Input() availableTriggers: GttTrigger[] = [];

  /**
   * Categorias de gravidade de dano NCC MERP disponíveis.
   */
  @Input() harmSeverities: HarmSeverity[] = [];

  /**
   * Coleção de gatilhos identificados na auditoria atual.
   */
  @Input() identifiedTriggers: IdentifiedTriggerData[] = [];

  /**
   * Notifica a adição de um novo gatilho identificado.
   */
  @Output() addTrigger = new EventEmitter<IdentifiedTriggerData>();

  /**
   * Notifica a exclusão de um gatilho adicionado por índice.
   */
  @Output() removeTrigger = new EventEmitter<number>();

  /** Código do gatilho clínico atualmente selecionado */
  selectedTriggerCode = '';
  /** Flag indicando se o gatilho gerou dano ao paciente */
  isHarmSelected = false;
  /** Letra de gravidade NCC MERP selecionada para o dano */
  selectedHarmSeverityLetter = 'E';
  /** Anotações e justificativa do discente sobre o achado clínico */
  triggerNotes = '';

  /**
   * Processa os campos internos e emite o objeto `IdentifiedTriggerData`.
   */
  onAddTrigger(): void {
    if (!this.selectedTriggerCode) return;
    const trigger = this.availableTriggers.find((t) => t.code === this.selectedTriggerCode);

    const newItem: IdentifiedTriggerData = {
      triggerId: trigger?.id,
      triggerCode: this.selectedTriggerCode,
      triggerName: trigger?.name,
      moduleCode: trigger?.moduleCode,
      notes: this.triggerNotes,
      isHarm: this.isHarmSelected,
      harmSeverityLetter: this.isHarmSelected ? this.selectedHarmSeverityLetter : undefined,
    };

    this.addTrigger.emit(newItem);
    this.selectedTriggerCode = '';
    this.triggerNotes = '';
    this.isHarmSelected = false;
  }
}
