import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IdentifiedTriggerDTO } from '../../models/activity.model';

/**
 * Componente que exibe a listagem de gatilhos clínicos identificados pelo estudante na auditoria.
 */
@Component({
  selector: 'app-grading-triggers-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
      <div class="flex items-center justify-between border-b border-slate-100 pb-3">
        <div class="flex items-center gap-2">
          <span class="w-6 h-6 rounded-lg bg-clinical-50 text-clinical-700 flex items-center justify-center font-bold text-xs">
            1
          </span>
          <h3 class="text-sm font-bold text-slate-800">
            Gatilhos Clínicos Identificados ({{ triggers.length }})
          </h3>
        </div>
      </div>

      @if (triggers.length === 0) {
        <p class="text-xs text-slate-400 italic py-2">Nenhum gatilho foi identificado pelo estudante.</p>
      } @else {
        <div class="space-y-3">
          @for (trig of triggers; track trig.triggerCode) {
            <div class="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-2">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="font-mono text-xs font-bold px-2 py-0.5 rounded bg-clinical-100 text-clinical-800 border border-clinical-200">
                    {{ trig.triggerCode }}
                  </span>
                  <span class="text-xs font-bold text-slate-800">{{ trig.triggerName }}</span>
                </div>
                <span class="px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-100 text-amber-900 border border-amber-300">
                  {{ trig.harmCategory }}
                </span>
              </div>
              <div class="text-xs text-slate-600 bg-white p-3 rounded-lg border border-slate-100">
                <span class="font-semibold text-slate-700 block mb-0.5 text-[11px] uppercase">Justificativa Clínica:</span>
                {{ trig.rationale }}
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class GradingTriggersListComponent {
  /**
   * Coleção de gatilhos clínicos identificados na resolução.
   */
  @Input() triggers: IdentifiedTriggerDTO[] = [];
}
