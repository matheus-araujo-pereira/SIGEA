import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IdentifiedTriggerData, TriggerDraftFormState } from '../../../models/activity.model';
import { GttTrigger } from '../../../../../gtt/trigger/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../../gtt/severity/models/harm-severity.model';
import {
  autoResizeTextarea,
  getTriggerDisplayEvidence,
  getTriggerEvidenceText
} from '../../../utils/quality-tools-display.util';

/**
 * Componente para rastreamento dos 53 gatilhos clínicos do IHI-GTT e classificação de severidade NCC MERP.
 */
@Component({
  selector: 'app-trigger-detector-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trigger-detector-panel.component.html'
})
export class TriggerDetectorPanelComponent implements OnChanges {
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
   * Indica se a resolução já foi submetida definitivamente (bloqueia adições, edições e exclusões).
   */
  @Input() readOnly = false;

  /**
   * Estado persistido do formulário vindo do componente pai para sobreviver à troca de abas.
   */
  @Input() formState: TriggerDraftFormState | null = null;

  /**
   * Notifica a adição de um novo gatilho identificado.
   */
  @Output() addTrigger = new EventEmitter<IdentifiedTriggerData>();

  /**
   * Notifica a atualização de um gatilho previamente adicionado durante o rascunho.
   */
  @Output() updateTrigger = new EventEmitter<{ index: number; trigger: IdentifiedTriggerData }>();

  /**
   * Notifica a exclusão de um gatilho adicionado por índice.
   */
  @Output() removeTrigger = new EventEmitter<number>();

  /**
   * Notifica alterações parciais do formulário ao componente pai para persistência entre abas e rascunho.
   */
  @Output() formStateChange = new EventEmitter<TriggerDraftFormState>();

  /** Filtro de Módulo GTT selecionado (vazio = Todos os módulos) */
  selectedModuleCode = '';
  /** Código do gatilho clínico atualmente selecionado */
  selectedTriggerCode = '';
  /** Flag indicando se o gatilho gerou dano ao paciente */
  isHarmSelected = false;
  /** Letra de gravidade NCC MERP selecionada para o dano */
  selectedHarmSeverityLetter = 'E';
  /** Anotações e justificativa do discente sobre o achado clínico */
  triggerNotes = '';
  /** Índice do gatilho atualmente em edição controlada (ou null ao inserir novo) */
  editingIndex: number | null = null;
  /** Controla exibição da mensagem de erro quando evidência obrigatória estiver vazia */
  showEvidenceError = false;

  /**
   * Reidrata o estado do formulário quando restaurado pelo componente pai.
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['formState'] && this.formState) {
      this.selectedModuleCode = this.formState.selectedModuleCode;
      this.selectedTriggerCode = this.formState.selectedTriggerCode;
      this.isHarmSelected = this.formState.isHarmSelected;
      this.selectedHarmSeverityLetter = this.formState.selectedHarmSeverityLetter;
      this.triggerNotes = this.formState.triggerNotes;
      this.editingIndex = this.formState.editingIndex;
    }
  }

  /**
   * Retorna os módulos GTT presentes no catálogo com nome e contagem de gatilhos.
   */
  getAvailableModules(): { code: string; name: string; count: number }[] {
    const map = new Map<string, { code: string; name: string; count: number }>();
    for (const trigger of this.availableTriggers) {
      const existing = map.get(trigger.moduleCode);
      if (existing) {
        existing.count++;
      } else {
        map.set(trigger.moduleCode, {
          code: trigger.moduleCode,
          name: trigger.moduleName || trigger.moduleCode,
          count: 1
        });
      }
    }
    return Array.from(map.values());
  }

  /**
   * Filtra o catálogo oficial de gatilhos de acordo com o módulo selecionado.
   */
  getFilteredTriggers(): GttTrigger[] {
    if (!this.selectedModuleCode) {
      return this.availableTriggers;
    }
    return this.availableTriggers.filter((t) => t.moduleCode === this.selectedModuleCode);
  }

  /**
   * Atualiza o filtro por módulo GTT e preserva a sincronização do rascunho.
   */
  onModuleFilterChange(moduleCode: string): void {
    this.selectedModuleCode = moduleCode;
    if (
      this.selectedTriggerCode &&
      !this.getFilteredTriggers().some((t) => t.code === this.selectedTriggerCode)
    ) {
      this.selectedTriggerCode = '';
    }
    this.emitFormState();
  }

  /**
   * Verifica se o campo obrigatório de evidência clínica possui conteúdo não vazio.
   */
  isEvidenceValid(): boolean {
    return this.triggerNotes.trim().length > 0;
  }

  /**
   * Trata digitação no campo de evidência: ajusta altura e atualiza o estado persistido.
   */
  onEvidenceInput(event: Event): void {
    autoResizeTextarea(event, 96);
    this.onFieldChange();
  }

  /**
   * Atualiza flags de validação e notifica o componente pai após edição de campos do formulário.
   */
  onFieldChange(): void {
    if (this.isEvidenceValid()) {
      this.showEvidenceError = false;
    }
    this.emitFormState();
  }

  /**
   * Carrega um gatilho já adicionado para edição segura enquanto a atividade estiver em rascunho.
   */
  startEditTrigger(index: number): void {
    if (this.readOnly) {
      return;
    }
    const item = this.identifiedTriggers[index];
    if (!item) {
      return;
    }

    this.editingIndex = index;
    this.selectedModuleCode = item.moduleCode || '';
    this.selectedTriggerCode = item.triggerCode;
    this.isHarmSelected = Boolean(item.isHarm);
    this.selectedHarmSeverityLetter = item.harmSeverityLetter || 'E';
    this.triggerNotes = getTriggerEvidenceText(item);
    this.showEvidenceError = false;
    this.emitFormState();
  }

  /**
   * Cancela a edição do gatilho selecionado e limpa os campos do formulário.
   */
  cancelEditTrigger(): void {
    this.resetTriggerFields();
    this.emitFormState();
  }

  /**
   * Retorna o texto de evidência clínica formatado (com retrocompatibilidade para registros legados).
   */
  getDisplayEvidence(item: IdentifiedTriggerData): string {
    return getTriggerDisplayEvidence(item);
  }

  /**
   * Processa os campos internos, valida a evidência obrigatória e emite adição ou atualização.
   */
  onAddTrigger(): void {
    if (this.readOnly || !this.selectedTriggerCode) {
      return;
    }
    if (!this.isEvidenceValid()) {
      this.showEvidenceError = true;
      return;
    }

    const trigger = this.availableTriggers.find((t) => t.code === this.selectedTriggerCode);
    const trimmedEvidence = this.triggerNotes.trim();

    const payload: IdentifiedTriggerData = {
      triggerId: trigger?.id,
      triggerCode: this.selectedTriggerCode,
      triggerName: trigger?.name,
      moduleCode: trigger?.moduleCode,
      notes: trimmedEvidence,
      clinicalJustification: trimmedEvidence,
      isHarm: this.isHarmSelected,
      harmSeverityLetter: this.isHarmSelected ? this.selectedHarmSeverityLetter : undefined,
    };

    if (this.editingIndex !== null) {
      this.updateTrigger.emit({ index: this.editingIndex, trigger: payload });
    } else {
      this.addTrigger.emit(payload);
    }

    this.resetTriggerFields();
    this.emitFormState();
  }

  private resetTriggerFields(): void {
    this.editingIndex = null;
    this.selectedTriggerCode = '';
    this.triggerNotes = '';
    this.isHarmSelected = false;
    this.selectedHarmSeverityLetter = 'E';
    this.showEvidenceError = false;
  }

  private emitFormState(): void {
    this.formStateChange.emit({
      selectedModuleCode: this.selectedModuleCode,
      selectedTriggerCode: this.selectedTriggerCode,
      isHarmSelected: this.isHarmSelected,
      selectedHarmSeverityLetter: this.selectedHarmSeverityLetter,
      triggerNotes: this.triggerNotes,
      editingIndex: this.editingIndex,
    });
  }
}
