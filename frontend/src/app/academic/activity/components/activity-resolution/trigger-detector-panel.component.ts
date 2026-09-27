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
  template: `
    <div class="space-y-6">
      <!-- Card de Adição de Gatilho Identificado -->
      <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
        <div class="flex items-center justify-between border-b border-slate-100 pb-3">
          <h3 class="text-sm font-black text-slate-900">Rastrear Gatilho no Prontuário Simulado</h3>
          <span class="text-[11px] text-slate-400 font-medium">53 Gatilhos Oficiais IHI-GTT</span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
          <!-- Seleção do Gatilho -->
          <div class="md:col-span-2">
            <label class="block font-bold text-slate-700 mb-1.5">Gatilho IHI</label>
            <select
              [(ngModel)]="selectedTriggerCode"
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
            >
              <option value="">Selecione um dos 53 gatilhos oficiais...</option>
              @for (t of availableTriggers; track t.id) {
                <option [value]="t.code">
                  [{{ t.code }}] {{ t.name }} (Módulo {{ t.moduleCode }})
                </option>
              }
            </select>
          </div>

          <!-- É Evento Adverso com Dano? -->
          <div>
            <label class="block font-bold text-slate-700 mb-1.5">Gerou Dano (Evento Adverso)?</label>
            <select
              [(ngModel)]="isHarmSelected"
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-bold text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
            >
              <option [ngValue]="false">Não (Apenas Gatilho Sem Dano)</option>
              <option [ngValue]="true">Sim (Evento Adverso Confirmado)</option>
            </select>
          </div>
        </div>

        <!-- Se houver dano: Gravidade NCC MERP (E a I) -->
        @if (isHarmSelected) {
          <div class="p-4 bg-amber-50 border border-amber-200 rounded-2xl space-y-2">
            <label class="block font-bold text-amber-900 text-xs">
              Classificação de Severidade do Dano (Categorias E a I - NCC MERP)
            </label>
            <select
              [(ngModel)]="selectedHarmSeverityLetter"
              class="w-full px-3.5 py-2 bg-white border border-amber-200 rounded-xl text-xs font-bold text-amber-900 focus:outline-none focus:ring-2 focus:ring-amber-500"
            >
              @for (s of harmSeverities; track s.id) {
                <option [value]="s.categoryLetter">
                  Categoria {{ s.categoryLetter }} - {{ s.description }}
                </option>
              }
            </select>
          </div>
        }

        <!-- Justificativa / Evidência no Prontuário -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">
            Evidência Encontrada no Prontuário Simulado
          </label>
          <textarea
            rows="2"
            [(ngModel)]="triggerNotes"
            placeholder="Indique a evolução médica, prescrição ou exame laboratorial que comprova a presença deste gatilho..."
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          ></textarea>
        </div>

        <div class="flex justify-end">
          <button
            type="button"
            (click)="onAddTrigger()"
            [disabled]="!selectedTriggerCode"
            class="px-4 py-2 rounded-xl bg-clinical-600 hover:bg-clinical-700 text-white font-bold text-xs disabled:opacity-50 disabled:cursor-not-allowed shadow-sm transition-all"
          >
            Adicionar Gatilho ao Caso
          </button>
        </div>
      </div>

      <!-- Relação dos Gatilhos já adicionados pelo aluno -->
      <div class="space-y-3">
        <h3 class="text-sm font-black text-slate-900">
          Gatilhos Adicionados à sua Resolução ({{ identifiedTriggers.length }})
        </h3>

        @for (item of identifiedTriggers; track $index) {
          <div class="bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="space-y-1">
              <div class="flex items-center gap-2">
                <span class="px-2 py-0.5 rounded-md font-mono font-black text-xs bg-slate-100 text-slate-900">
                  {{ item.triggerCode }}
                </span>
                <span class="font-bold text-slate-900 text-xs">{{ item.triggerName }}</span>
                @if (item.isHarm) {
                  <span class="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-50 text-rose-700 border border-rose-200">
                    Evento Adverso (Dano Cat. {{ item.harmSeverityLetter || 'E' }})
                  </span>
                } @else {
                  <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 text-slate-600">
                    Sem Dano
                  </span>
                }
              </div>
              @if (item.notes) {
                <p class="text-xs text-slate-500 font-medium">Evidência: {{ item.notes }}</p>
              }
            </div>

            <button
              type="button"
              (click)="removeTrigger.emit($index)"
              class="text-slate-400 hover:text-rose-600 p-1.5 rounded-lg self-end sm:self-center"
              title="Remover gatilho"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        } @empty {
          <div class="bg-white rounded-2xl border border-slate-200/80 p-8 text-center text-slate-400 text-xs">
            Nenhum gatilho adicionado ainda. Revise o prontuário e selecione os gatilhos no formulário acima.
          </div>
        }
      </div>
    </div>
  `
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
