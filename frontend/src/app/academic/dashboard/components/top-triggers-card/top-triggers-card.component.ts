import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TriggerOccurrenceDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição dos gatilhos clínicos mais identificados
 * nos prontuários simulados da turma.
 */
@Component({
  selector: 'app-top-triggers-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
      <div class="border-b border-slate-100 pb-3">
        <h3 class="text-sm font-bold text-slate-800">Gatilhos Clínicos Mais Identificados</h3>
        <p class="text-xs text-slate-500">Ranking dos rastreadores com maior incidência nos casos simulados</p>
      </div>

      @if (triggers().length === 0) {
        <p class="text-xs text-slate-400 italic py-8 text-center">
          Nenhum gatilho registrado nas submissões até o momento.
        </p>
      } @else {
        <div class="overflow-x-auto">
          <table class="w-full text-xs text-left">
            <thead>
              <tr class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] border-b border-slate-200">
                <th class="py-2.5 px-4 w-12 text-center">#</th>
                <th class="py-2.5 px-4 w-28">Código</th>
                <th class="py-2.5 px-4">Nome do Gatilho</th>
                <th class="py-2.5 px-4 text-center w-28">Ocorrências</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              @for (trig of triggers(); track trig.triggerCode; let idx = $index) {
                <tr class="hover:bg-slate-50/60 transition-colors">
                  <td class="py-3 px-4 text-center font-bold text-slate-400">{{ idx + 1 }}</td>
                  <td class="py-3 px-4">
                    <span class="font-mono font-bold px-2 py-0.5 rounded bg-clinical-50 text-clinical-800 border border-clinical-200">
                      {{ trig.triggerCode }}
                    </span>
                  </td>
                  <td class="py-3 px-4 font-semibold text-slate-800">{{ trig.triggerName }}</td>
                  <td class="py-3 px-4 text-center font-bold font-mono text-clinical-700">
                    {{ trig.count }}x
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
})
export class TopTriggersCardComponent {
  /** Ranking dos gatilhos clínicos mais frequentes nos prontuários da turma */
  readonly triggers = input.required<TriggerOccurrenceDTO[]>();
}
