import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GttMetricsDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição dos 4 cartões de indicadores oficiais do IHI-GTT:
 * 1. EAs por 1.000 Pacientes-Dia
 * 2. EAs por 100 Admissões
 * 3. % Admissões com >= 1 EA
 * 4. Total de Danos Detectados (NCC MERP E a I)
 */
@Component({
  selector: 'app-gtt-rates-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h2 class="text-sm font-bold text-slate-700 uppercase tracking-wider mb-3">
        Métricas Oficiais Global Trigger Tool (IHI)
      </h2>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Taxa 1: EAs por 1.000 Pacientes-Dia -->
        <div class="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-xs relative overflow-hidden">
          <div class="w-1.5 h-full bg-clinical-600 absolute left-0 top-0"></div>
          <span class="text-[10px] uppercase font-bold text-slate-400 tracking-wider block">
            EAs / 1.000 Pacientes-Dia
          </span>
          <div class="mt-2 flex items-baseline gap-1">
            <span class="text-3xl font-black font-mono text-slate-900">
              {{ metrics().adverseEventsPer1000PatientDays | number:'1.2-2' }}
            </span>
            <span class="text-xs font-semibold text-slate-400">taxa</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-2 font-medium">
            Base: {{ metrics().totalPatientDays }} pacientes-dia auditados
          </p>
        </div>

        <!-- Taxa 2: EAs por 100 Admissões -->
        <div class="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-xs relative overflow-hidden">
          <div class="w-1.5 h-full bg-blue-500 absolute left-0 top-0"></div>
          <span class="text-[10px] uppercase font-bold text-slate-400 tracking-wider block">
            EAs / 100 Admissões
          </span>
          <div class="mt-2 flex items-baseline gap-1">
            <span class="text-3xl font-black font-mono text-slate-900">
              {{ metrics().adverseEventsPer100Admissions | number:'1.2-2' }}%
            </span>
          </div>
          <p class="text-[11px] text-slate-500 mt-2 font-medium">
            {{ metrics().totalAdverseEvents }} EAs em {{ metrics().totalAdmissions }} admissões
          </p>
        </div>

        <!-- Taxa 3: % Admissões com >= 1 EA -->
        <div class="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-xs relative overflow-hidden">
          <div class="w-1.5 h-full bg-amber-500 absolute left-0 top-0"></div>
          <span class="text-[10px] uppercase font-bold text-slate-400 tracking-wider block">
            Admissões com &ge; 1 EA
          </span>
          <div class="mt-2 flex items-baseline gap-1">
            <span class="text-3xl font-black font-mono text-slate-900">
              {{ metrics().percentAdmissionsWithAdverseEvents | number:'1.1-1' }}%
            </span>
          </div>
          <p class="text-[11px] text-slate-500 mt-2 font-medium">
            {{ metrics().admissionsWithAdverseEvents }} de {{ metrics().totalAdmissions }} prontuários afetados
          </p>
        </div>

        <!-- Taxa 4: Total de Eventos Adversos -->
        <div class="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-xs relative overflow-hidden">
          <div class="w-1.5 h-full bg-emerald-500 absolute left-0 top-0"></div>
          <span class="text-[10px] uppercase font-bold text-slate-400 tracking-wider block">
            Total de Danos Detectados
          </span>
          <div class="mt-2 flex items-baseline gap-1">
            <span class="text-3xl font-black font-mono text-slate-900">
              {{ metrics().totalAdverseEvents }}
            </span>
            <span class="text-xs font-semibold text-slate-400">eventos</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-2 font-medium">
            Categorias E a I (NCC MERP)
          </p>
        </div>
      </div>
    </div>
  `,
})
export class GttRatesCardsComponent {
  /** Métricas epidemiológicas oficiais da metodologia IHI Global Trigger Tool */
  readonly metrics = input.required<GttMetricsDTO>();
}
