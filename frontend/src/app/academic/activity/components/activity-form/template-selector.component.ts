import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClinicalCaseTemplateResponseDTO } from '../../../clinical/models/clinical-case-template.model';

/**
 * Componente para seleção e carregamento de modelos canônicos de prontuário simulado do catálogo IHI-GTT.
 */
@Component({
  selector: 'app-template-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (!isEditMode) {
      <div class="bg-gradient-to-r from-blue-50/70 via-indigo-50/40 to-white rounded-3xl border border-blue-200/80 p-6 shadow-xs space-y-4">
        <div class="flex items-center justify-between border-b border-blue-100 pb-3">
          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold text-xs shadow-xs">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <div>
              <h3 class="text-sm font-black text-slate-900">Biblioteca de Casos Clínicos Simulados (IHI-GTT)</h3>
              <p class="text-[11px] text-slate-500 font-medium">Carregue um prontuário canônico padronizado para economizar tempo de digitação.</p>
            </div>
          </div>
          <span class="text-[10px] font-bold text-blue-700 bg-blue-100/80 px-2.5 py-1 rounded-full uppercase tracking-wider">
            Opcional
          </span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-12 gap-3 items-end">
          <div class="md:col-span-8">
            <label class="block font-bold text-slate-700 mb-1.5 text-xs">
              Selecione um Modelo Pré-configurado
            </label>
            <select
              [value]="selectedTemplateId"
              (change)="onSelectChange($event)"
              class="w-full px-3.5 py-2.5 bg-white border border-blue-200 rounded-xl text-xs font-medium text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">-- Escolha um caso clínico simulado do catálogo --</option>
              @for (tpl of templates; track tpl.id) {
                <option [value]="tpl.id">
                  [{{ tpl.moduleCode }}] {{ tpl.title }} (Gatilho: {{ tpl.primaryTriggerCode || 'N/A' }} | Categoria: {{ tpl.expectedSeverity || 'N/A' }})
                </option>
              }
            </select>
          </div>

          <div class="md:col-span-4 flex items-center gap-2">
            <button
              type="button"
              (click)="applyTemplate.emit()"
              [disabled]="!selectedTemplateId"
              class="w-full py-2.5 px-4 rounded-xl font-bold text-xs bg-blue-600 hover:bg-blue-700 text-white disabled:opacity-50 disabled:cursor-not-allowed shadow-xs transition-all flex items-center justify-center gap-2"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
              </svg>
              <span>Carregar no Prontuário</span>
            </button>
          </div>
        </div>

        @if (activeTemplate) {
          <div class="p-3 bg-white/80 rounded-2xl border border-blue-100 text-[11px] text-slate-600 flex flex-col gap-1">
            <div class="flex items-center gap-2">
              <span class="font-bold text-slate-800">{{ activeTemplate.title }}</span>
              <span class="text-blue-600 font-semibold">• Módulo: {{ activeTemplate.moduleCode }}</span>
              @if (activeTemplate.primaryTriggerCode) {
                <span class="text-amber-600 font-semibold">• Gatilho: {{ activeTemplate.primaryTriggerCode }}</span>
              }
              @if (activeTemplate.expectedSeverity) {
                <span class="text-rose-600 font-semibold">• NCC MERP: {{ activeTemplate.expectedSeverity }}</span>
              }
            </div>
            <p class="text-slate-500 line-clamp-2">{{ activeTemplate.description }}</p>
          </div>
        }
      </div>
    }
  `
})
export class TemplateSelectorComponent {
  /**
   * Catálogo de modelos de casos clínicos cadastrados.
   */
  @Input() templates: ClinicalCaseTemplateResponseDTO[] = [];

  /**
   * Identificador do modelo atualmente selecionado no dropdown.
   */
  @Input() selectedTemplateId = '';

  /**
   * Objeto do modelo ativo selecionado.
   */
  @Input() activeTemplate: ClinicalCaseTemplateResponseDTO | null = null;

  /**
   * Indica se o formulário está em modo de edição (oculta a biblioteca para não sobrescrever).
   */
  @Input() isEditMode = false;

  /**
   * Emite o ID do modelo selecionado ao alterar o dropdown.
   */
  @Output() templateChange = new EventEmitter<string>();

  /**
   * Dispara a aplicação do modelo selecionado aos campos do formulário.
   */
  @Output() applyTemplate = new EventEmitter<void>();

  /**
   * Captura o evento de mudança no elemento select.
   */
  onSelectChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.templateChange.emit(target.value);
  }
}
