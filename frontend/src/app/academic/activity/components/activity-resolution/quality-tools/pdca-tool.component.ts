import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PdcaData } from '../../../models/activity.model';

/**
 * Componente interativo para estruturação de ciclos de melhoria contínua PDCA / PDSA.
 */
@Component({
  selector: 'app-pdca-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
      <div>
        <h3 class="text-sm font-black text-slate-900">Ciclo de Melhoria Contínua PDCA / PDSA</h3>
        <p class="text-[11px] text-slate-500 mt-0.5">
          Estruture a melhoria da qualidade nos 4 quadrantes clássicos
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
        <!-- Plan -->
        <div class="p-4 bg-clinical-50/50 border border-clinical-200 rounded-2xl space-y-2">
          <span class="font-black text-clinical-900 text-xs">P - Plan (Planejar)</span>
          <textarea
            rows="3"
            [(ngModel)]="pdca.plan"
            placeholder="Problema identificado, objetivos e metas a serem alcançadas..."
            class="w-full px-3 py-2 bg-white border border-clinical-200 rounded-xl text-xs"
          ></textarea>
        </div>

        <!-- Do -->
        <div class="p-4 bg-amber-50/50 border border-amber-200 rounded-2xl space-y-2">
          <span class="font-black text-amber-900 text-xs">D - Do (Executar)</span>
          <textarea
            rows="3"
            [(ngModel)]="pdca.doPhase"
            placeholder="Implementação do plano de ação e execução dos treinamentos..."
            class="w-full px-3 py-2 bg-white border border-amber-200 rounded-xl text-xs"
          ></textarea>
        </div>

        <!-- Check -->
        <div class="p-4 bg-emerald-50/50 border border-emerald-200 rounded-2xl space-y-2">
          <span class="font-black text-emerald-900 text-xs">C - Check (Checar / Estudar)</span>
          <textarea
            rows="3"
            [(ngModel)]="pdca.checkPhase"
            placeholder="Auditorias de prontuário, medição de taxas GTT e verificação de resultados..."
            class="w-full px-3 py-2 bg-white border border-emerald-200 rounded-xl text-xs"
          ></textarea>
        </div>

        <!-- Act -->
        <div class="p-4 bg-purple-50/50 border border-purple-200 rounded-2xl space-y-2">
          <span class="font-black text-purple-900 text-xs">A - Act (Agir / Padronizar)</span>
          <textarea
            rows="3"
            [(ngModel)]="pdca.actPhase"
            placeholder="Padronização do novo procedimento operacional e difusão na equipe..."
            class="w-full px-3 py-2 bg-white border border-purple-200 rounded-xl text-xs"
          ></textarea>
        </div>
      </div>
    </div>
  `
})
export class PdcaToolComponent {
  /**
   * Dados das 4 fases do ciclo PDCA.
   */
  @Input({ required: true }) pdca!: PdcaData;
}
