import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { IshikawaData } from '../../../models/activity.model';

/**
 * Componente interativo para preenchimento do Diagrama de Causa e Efeito (Ishikawa 6M).
 */
@Component({
  selector: 'app-ishikawa-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-6">
      <div>
        <h3 class="text-sm font-black text-slate-900">Diagrama de Causa e Efeito (Ishikawa / 6M)</h3>
        <p class="text-[11px] text-slate-500 mt-0.5">
          Analise as causas-raiz do evento adverso divididas nos 6M clássicos da engenharia da qualidade
        </p>
      </div>

      <!-- Problema Central (Cabeça do Peixe) -->
      <div class="p-4 bg-rose-50 border border-rose-200 rounded-2xl space-y-1.5">
        <label class="font-black text-rose-950 uppercase tracking-wider text-[10px]">
          Problema Central / Efeito (Cabeça do Peixe)
        </label>
        <input
          type="text"
          [(ngModel)]="ishikawa.centralProblem"
          placeholder="Ex: Hemorragia grave por sobredose de heparina não monitorada"
          class="w-full px-3 py-2 bg-white border border-rose-200 rounded-xl text-xs font-bold text-rose-950 focus:outline-none focus:ring-2 focus:ring-rose-500"
        />
      </div>

      <!-- Grade dos 6M -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <!-- Método -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">1. Método</span>
            <button type="button" (click)="addCause.emit('method')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.methodCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.methodCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'method', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>

        <!-- Mão de Obra -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">2. Mão de Obra</span>
            <button type="button" (click)="addCause.emit('manpower')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.manpowerCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.manpowerCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'manpower', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>

        <!-- Material -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">3. Material</span>
            <button type="button" (click)="addCause.emit('material')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.materialCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.materialCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'material', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>

        <!-- Máquina -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">4. Máquina / Equipamentos</span>
            <button type="button" (click)="addCause.emit('machine')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.machineCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.machineCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'machine', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>

        <!-- Meio Ambiente -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">5. Meio Ambiente</span>
            <button type="button" (click)="addCause.emit('environment')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.environmentCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.environmentCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'environment', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>

        <!-- Medida -->
        <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="font-black text-slate-900 text-xs">6. Medida</span>
            <button type="button" (click)="addCause.emit('measurement')" class="text-clinical-600 font-bold hover:underline text-xs">+ Causa</button>
          </div>
          @for (c of ishikawa.measurementCauses; track $index) {
            <div class="flex items-center gap-1.5">
              <input
                type="text"
                [(ngModel)]="ishikawa.measurementCauses![$index]"
                class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
              />
              <button type="button" (click)="removeCause.emit({ type: 'measurement', index: $index })" class="text-slate-400 hover:text-rose-500">×</button>
            </div>
          }
        </div>
      </div>
    </div>
  `
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
