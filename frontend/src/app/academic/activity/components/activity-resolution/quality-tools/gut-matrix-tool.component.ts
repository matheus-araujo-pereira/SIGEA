import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GutItemData } from '../../../models/activity.model';

/**
 * Componente interativo para priorização de problemas assistenciais via Matriz GUT (Gravidade × Urgência × Tendência).
 */
@Component({
  selector: 'app-gut-matrix-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h3 class="text-sm font-black text-slate-900">Matriz GUT (Gravidade × Urgência × Tendência)</h3>
          <p class="text-[11px] text-slate-500 mt-0.5">
            Priorização de problemas com cálculo automático imediato de 1 a 125
          </p>
        </div>
        <button
          type="button"
          (click)="addItem.emit()"
          class="px-3 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 font-semibold text-xs transition-colors shadow-xs"
        >
          + Adicionar Problema
        </button>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
          <thead class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px]">
            <tr>
              <th class="px-4 py-2.5">Problema / Hipótese</th>
              <th class="px-4 py-2.5 text-center">G (1 a 5)</th>
              <th class="px-4 py-2.5 text-center">U (1 a 5)</th>
              <th class="px-4 py-2.5 text-center">T (1 a 5)</th>
              <th class="px-4 py-2.5 text-center">Score (G×U×T)</th>
              <th class="px-4 py-2.5 text-right">Ação</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 font-medium">
            @for (item of gutItems; track $index) {
              <tr>
                <td class="px-4 py-3">
                  <input
                    type="text"
                    [(ngModel)]="item.problem"
                    placeholder="Descreva o problema assistencial..."
                    class="w-full px-2.5 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs"
                  />
                </td>
                <td class="px-4 py-3 text-center">
                  <select [(ngModel)]="item.gravity" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                    <option [ngValue]="1">1 - Sem gravidade</option>
                    <option [ngValue]="2">2 - Pouco grave</option>
                    <option [ngValue]="3">3 - Grave</option>
                    <option [ngValue]="4">4 - Muito grave</option>
                    <option [ngValue]="5">5 - Extremamente grave</option>
                  </select>
                </td>
                <td class="px-4 py-3 text-center">
                  <select [(ngModel)]="item.urgency" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                    <option [ngValue]="1">1 - Pode esperar</option>
                    <option [ngValue]="2">2 - Pouco urgente</option>
                    <option [ngValue]="3">3 - Urgente</option>
                    <option [ngValue]="4">4 - Muito urgente</option>
                    <option [ngValue]="5">5 - Imediata</option>
                  </select>
                </td>
                <td class="px-4 py-3 text-center">
                  <select [(ngModel)]="item.trend" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                    <option [ngValue]="1">1 - Não vai mudar</option>
                    <option [ngValue]="2">2 - Vai piorar a longo prazo</option>
                    <option [ngValue]="3">3 - Vai piorar a médio prazo</option>
                    <option [ngValue]="4">4 - Vai piorar rápido</option>
                    <option [ngValue]="5">5 - Vai piorar imediatamente</option>
                  </select>
                </td>
                <td class="px-4 py-3 text-center">
                  <span
                    class="px-2.5 py-1 rounded-xl text-xs font-black font-mono inline-block"
                    [ngClass]="{
                      'bg-rose-100 text-rose-800': calculateGutScore(item) >= 60,
                      'bg-amber-100 text-amber-800': calculateGutScore(item) >= 20 && calculateGutScore(item) < 60,
                      'bg-slate-100 text-slate-800': calculateGutScore(item) < 20
                    }"
                  >
                    {{ calculateGutScore(item) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-right">
                  <button type="button" (click)="removeItem.emit($index)" class="text-slate-400 hover:text-rose-500">
                    Excluir
                  </button>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="6" class="px-4 py-6 text-center text-slate-400">Nenhum item na Matriz GUT.</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class GutMatrixToolComponent {
  /**
   * Coleção de problemas avaliados na Matriz GUT.
   */
  @Input() gutItems: GutItemData[] = [];

  /**
   * Notifica a adição de um novo problema à matriz.
   */
  @Output() addItem = new EventEmitter<void>();

  /**
   * Notifica a remoção de um problema por índice.
   */
  @Output() removeItem = new EventEmitter<number>();

  /**
   * Calcula o score ponderado (G × U × T), variando de 1 a 125.
   */
  calculateGutScore(item: GutItemData): number {
    const g = item.gravity || 1;
    const u = item.urgency || 1;
    const t = item.trend || 1;
    return g * u * t;
  }
}
