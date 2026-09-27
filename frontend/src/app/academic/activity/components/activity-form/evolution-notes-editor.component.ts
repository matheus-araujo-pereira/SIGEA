import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para edição de anotações de evolução multiprofissional no prontuário simulado.
 */
@Component({
  selector: 'app-evolution-notes-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
      <div class="flex items-center justify-between border-b border-slate-100 pb-3">
        <div class="flex items-center gap-2">
          <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">3</span>
          <h3 class="text-sm font-black text-slate-900">Evoluções Multiprofissionais</h3>
        </div>
        <button
          type="button"
          (click)="addNote.emit()"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 font-semibold text-xs transition-colors shadow-xs"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          <span>Adicionar Evolução</span>
        </button>
      </div>

      <div class="space-y-3">
        @for (note of evolutionNotesArray.controls; track $index) {
          <div [formGroup]="getGroup(note)" class="p-3.5 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-2">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pr-8">
              <input
                type="text"
                formControlName="dateTime"
                placeholder="Data/Hora (Ex: D+2 10:00)"
                class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
              />
              <input
                type="text"
                formControlName="professionalRole"
                placeholder="Profissional (Ex: Médico Plantonista / Enfermeiro)"
                class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
              />
            </div>
            <textarea
              rows="2"
              formControlName="note"
              placeholder="Registro de evolução clínica, intercorrências, sinais vitais e condutas..."
              class="w-full px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs resize-y"
            ></textarea>
            <button
              type="button"
              (click)="removeNote.emit($index)"
              class="absolute top-3 right-3 text-slate-400 hover:text-rose-500 p-1 rounded-lg"
              title="Remover anotação"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        } @empty {
          <p class="text-center text-slate-400 py-3 text-xs">Nenhuma nota de evolução adicionada.</p>
        }
      </div>
    </div>
  `
})
export class EvolutionNotesEditorComponent {
  /**
   * Array de controles reativos com as evoluções multiprofissionais.
   */
  @Input({ required: true }) evolutionNotesArray!: FormArray;

  /**
   * Notifica a solicitação de adição de uma nova nota de evolução.
   */
  @Output() addNote = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma nota de evolução por índice.
   */
  @Output() removeNote = new EventEmitter<number>();

  /**
   * Cast auxiliar para FormGroup.
   */
  getGroup(control: any): FormGroup {
    return control as FormGroup;
  }
}
