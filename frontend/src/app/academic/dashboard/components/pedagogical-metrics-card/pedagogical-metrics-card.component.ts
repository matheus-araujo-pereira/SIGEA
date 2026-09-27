import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedagogicalMetricsDTO } from '../../models/class-dashboard.model';

/**
 * Componente atômico para exibição do desempenho pedagógico da turma:
 * média geral, total de alunos, atividades, submissões avaliadas e pendentes.
 */
@Component({
  selector: 'app-pedagogical-metrics-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
      <h3 class="text-sm font-bold text-slate-800 border-b border-slate-100 pb-3">
        Desempenho Pedagógico da Turma
      </h3>

      <!-- Média Geral -->
      <div class="p-4 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between">
        <div>
          <span class="text-[10px] font-bold uppercase text-slate-400 block">Média Geral da Turma</span>
          <span
            class="text-2xl font-black font-mono"
            [ngClass]="metrics().classAverageGrade >= 7 ? 'text-emerald-700' : 'text-amber-700'"
          >
            {{ metrics().classAverageGrade | number:'1.2-2' }}
          </span>
        </div>
        <span class="text-xs font-mono text-slate-400 font-bold">/ 10.0</span>
      </div>

      <!-- Estatísticas de Submissões -->
      <div class="space-y-2 text-xs">
        <div class="flex justify-between py-1.5 border-b border-slate-50">
          <span class="text-slate-500">Estudantes Matriculados</span>
          <span class="font-bold text-slate-800">{{ metrics().totalEnrolledStudents }}</span>
        </div>
        <div class="flex justify-between py-1.5 border-b border-slate-50">
          <span class="text-slate-500">Atividades Cadastradas</span>
          <span class="font-bold text-slate-800">{{ metrics().totalActivities }}</span>
        </div>
        <div class="flex justify-between py-1.5 border-b border-slate-50">
          <span class="text-slate-500">Total de Submissões</span>
          <span class="font-bold text-slate-800">{{ metrics().totalSubmissions }}</span>
        </div>
        <div class="flex justify-between py-1.5 border-b border-slate-50">
          <span class="text-slate-500">Submissões Avaliadas</span>
          <span class="font-bold text-emerald-700">{{ metrics().gradedSubmissions }}</span>
        </div>
        <div class="flex justify-between py-1.5">
          <span class="text-slate-500">Pendentes de Correção</span>
          <span class="font-bold text-amber-700">{{ metrics().pendingGradingSubmissions }}</span>
        </div>
      </div>
    </div>
  `,
})
export class PedagogicalMetricsCardComponent {
  /** Métricas pedagógicas e acadêmicas de desempenho da turma */
  readonly metrics = input.required<PedagogicalMetricsDTO>();
}
