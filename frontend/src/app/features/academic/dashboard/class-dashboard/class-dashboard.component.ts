import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassDashboardService } from '../../../../core/services/class-dashboard.service';
import { ClassDashboardDTO } from '../../../../core/models/class-dashboard.model';
import { ToastService } from '../../../../core/services/toast.service';

/**
 * Painel analítico de indicadores oficiais IHI-GTT e desempenho pedagógico da turma.
 */
@Component({
  selector: 'app-class-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6 max-w-7xl mx-auto">
      <!-- Topo & Navegação -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Voltar para Turma"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <div>
            <div class="flex items-center gap-2">
              <h1 class="text-2xl font-black text-slate-900 tracking-tight">Dashboard Analítico GTT</h1>
              @if (dashboard()?.isClosed) {
                <span class="px-2 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
                  Turma Encerrada
                </span>
              } @else {
                <span class="px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  Turma Ativa
                </span>
              }
            </div>
            <p class="text-xs text-slate-500 font-medium">
              {{ dashboard()?.className }} • Prof. {{ dashboard()?.professorName }} • {{ dashboard()?.academicPeriod }}
            </p>
          </div>
        </div>

        <button
          type="button"
          (click)="loadDashboard()"
          class="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold text-slate-700 bg-white border border-slate-200 hover:bg-slate-50 transition-colors shadow-xs"
        >
          <svg class="w-4 h-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Atualizar Indicadores
        </button>
      </div>

      @if (isLoading()) {
        <div class="py-24 text-center text-slate-400 bg-white rounded-2xl border border-slate-200/80">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-clinical-600 border-t-transparent mb-3"></div>
          <p class="text-xs font-medium">Consolidando métricas e taxas epidemiológicas GTT...</p>
        </div>
      } @else {
        @if (dashboard(); as d) {
          <!-- 1. INDICADORES EPIDEMIOLÓGICOS OFICIAIS IHI-GTT -->
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
                  {{ d.gttMetrics.adverseEventsPer1000PatientDays | number:'1.2-2' }}
                </span>
                <span class="text-xs font-semibold text-slate-400">taxa</span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2 font-medium">
                Base: {{ d.gttMetrics.totalPatientDays }} pacientes-dia auditados
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
                  {{ d.gttMetrics.adverseEventsPer100Admissions | number:'1.2-2' }}%
                </span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2 font-medium">
                {{ d.gttMetrics.totalAdverseEvents }} EAs em {{ d.gttMetrics.totalAdmissions }} admissões
              </p>
            </div>

            <!-- Taxa 3: % Admissões com $\ge$ 1 EA -->
            <div class="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-xs relative overflow-hidden">
              <div class="w-1.5 h-full bg-amber-500 absolute left-0 top-0"></div>
              <span class="text-[10px] uppercase font-bold text-slate-400 tracking-wider block">
                Admissões com $\ge$ 1 EA
              </span>
              <div class="mt-2 flex items-baseline gap-1">
                <span class="text-3xl font-black font-mono text-slate-900">
                  {{ d.gttMetrics.percentAdmissionsWithAdverseEvents | number:'1.1-1' }}%
                </span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2 font-medium">
                {{ d.gttMetrics.admissionsWithAdverseEvents }} de {{ d.gttMetrics.totalAdmissions }} prontuários afetados
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
                  {{ d.gttMetrics.totalAdverseEvents }}
                </span>
                <span class="text-xs font-semibold text-slate-400">eventos</span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2 font-medium">
                Categorias E a I (NCC MERP)
              </p>
            </div>
          </div>
        </div>

        <!-- 2. DISTRIBUIÇÃO POR CATEGORIA DE DANO (NCC MERP E a I) -->
        <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
          <div class="flex items-center justify-between border-b border-slate-100 pb-3">
            <div>
              <h3 class="text-sm font-bold text-slate-800">Distribuição por Categoria de Gravidade do Dano</h3>
              <p class="text-xs text-slate-500">Taxonomia oficial NCC MERP para Danos ao Paciente (Categorias E a I)</p>
            </div>
            <span class="text-xs font-mono font-bold text-slate-600 bg-slate-100 px-2.5 py-1 rounded-lg">
              Total: {{ d.gttMetrics.totalAdverseEvents }} Danos
            </span>
          </div>

          <div class="space-y-3">
            <!-- Categoria E -->
            <div>
              <div class="flex items-center justify-between text-xs mb-1">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-md bg-amber-100 text-amber-900 font-bold flex items-center justify-center text-xs">E</span>
                  <span class="font-semibold text-slate-800">Categoria E</span>
                  <span class="text-slate-500 hidden sm:inline">- Dano temporário com necessidade de intervenção</span>
                </div>
                <span class="font-mono font-bold text-slate-700">
                  {{ getHarmCount('E') }} ({{ getHarmPercentage('E') | number:'1.0-1' }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                <div class="bg-amber-400 h-2.5 rounded-full transition-all duration-500" [style.width.%]="getHarmPercentage('E')"></div>
              </div>
            </div>

            <!-- Categoria F -->
            <div>
              <div class="flex items-center justify-between text-xs mb-1">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-md bg-orange-100 text-orange-900 font-bold flex items-center justify-center text-xs">F</span>
                  <span class="font-semibold text-slate-800">Categoria F</span>
                  <span class="text-slate-500 hidden sm:inline">- Dano temporário com internação inicial ou prolongada</span>
                </div>
                <span class="font-mono font-bold text-slate-700">
                  {{ getHarmCount('F') }} ({{ getHarmPercentage('F') | number:'1.0-1' }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                <div class="bg-orange-500 h-2.5 rounded-full transition-all duration-500" [style.width.%]="getHarmPercentage('F')"></div>
              </div>
            </div>

            <!-- Categoria G -->
            <div>
              <div class="flex items-center justify-between text-xs mb-1">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-md bg-rose-100 text-rose-900 font-bold flex items-center justify-center text-xs">G</span>
                  <span class="font-semibold text-slate-800">Categoria G</span>
                  <span class="text-slate-500 hidden sm:inline">- Dano permanente ao paciente</span>
                </div>
                <span class="font-mono font-bold text-slate-700">
                  {{ getHarmCount('G') }} ({{ getHarmPercentage('G') | number:'1.0-1' }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                <div class="bg-rose-500 h-2.5 rounded-full transition-all duration-500" [style.width.%]="getHarmPercentage('G')"></div>
              </div>
            </div>

            <!-- Categoria H -->
            <div>
              <div class="flex items-center justify-between text-xs mb-1">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-md bg-red-100 text-red-900 font-bold flex items-center justify-center text-xs">H</span>
                  <span class="font-semibold text-slate-800">Categoria H</span>
                  <span class="text-slate-500 hidden sm:inline">- Intervenção necessária para sustentar a vida</span>
                </div>
                <span class="font-mono font-bold text-slate-700">
                  {{ getHarmCount('H') }} ({{ getHarmPercentage('H') | number:'1.0-1' }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                <div class="bg-red-600 h-2.5 rounded-full transition-all duration-500" [style.width.%]="getHarmPercentage('H')"></div>
              </div>
            </div>

            <!-- Categoria I -->
            <div>
              <div class="flex items-center justify-between text-xs mb-1">
                <div class="flex items-center gap-2">
                  <span class="w-6 h-6 rounded-md bg-slate-900 text-white font-bold flex items-center justify-center text-xs">I</span>
                  <span class="font-semibold text-slate-800">Categoria I</span>
                  <span class="text-slate-500 hidden sm:inline">- Óbito relacionado ao evento adverso</span>
                </div>
                <span class="font-mono font-bold text-slate-700">
                  {{ getHarmCount('I') }} ({{ getHarmPercentage('I') | number:'1.0-1' }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                <div class="bg-slate-900 h-2.5 rounded-full transition-all duration-500" [style.width.%]="getHarmPercentage('I')"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 3. DESEMPENHO PEDAGÓGICO & TOP GATILHOS -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Métricas Pedagógicas (1/3) -->
          <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <h3 class="text-sm font-bold text-slate-800 border-b border-slate-100 pb-3">
              Desempenho Pedagógico da Turma
            </h3>

            <!-- Média Geral -->
            <div class="p-4 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between">
              <div>
                <span class="text-[10px] font-bold uppercase text-slate-400 block">Média Geral da Turma</span>
                <span class="text-2xl font-black font-mono" [ngClass]="d.pedagogicalMetrics.classAverageGrade >= 7 ? 'text-emerald-700' : 'text-amber-700'">
                  {{ d.pedagogicalMetrics.classAverageGrade | number:'1.2-2' }}
                </span>
              </div>
              <span class="text-xs font-mono text-slate-400 font-bold">/ 10.0</span>
            </div>

            <!-- Estatísticas de Submissões -->
            <div class="space-y-2 text-xs">
              <div class="flex justify-between py-1.5 border-b border-slate-50">
                <span class="text-slate-500">Estudantes Matriculados</span>
                <span class="font-bold text-slate-800">{{ d.pedagogicalMetrics.totalEnrolledStudents }}</span>
              </div>
              <div class="flex justify-between py-1.5 border-b border-slate-50">
                <span class="text-slate-500">Atividades Cadastradas</span>
                <span class="font-bold text-slate-800">{{ d.pedagogicalMetrics.totalActivities }}</span>
              </div>
              <div class="flex justify-between py-1.5 border-b border-slate-50">
                <span class="text-slate-500">Total de Submissões</span>
                <span class="font-bold text-slate-800">{{ d.pedagogicalMetrics.totalSubmissions }}</span>
              </div>
              <div class="flex justify-between py-1.5 border-b border-slate-50">
                <span class="text-slate-500">Submissões Avaliadas</span>
                <span class="font-bold text-emerald-700">{{ d.pedagogicalMetrics.gradedSubmissions }}</span>
              </div>
              <div class="flex justify-between py-1.5">
                <span class="text-slate-500">Pendentes de Correção</span>
                <span class="font-bold text-amber-700">{{ d.pedagogicalMetrics.pendingGradingSubmissions }}</span>
              </div>
            </div>
          </div>

          <!-- Top Gatilhos Mais Identificados (2/3) -->
          <div class="lg:col-span-2 bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <div class="border-b border-slate-100 pb-3">
              <h3 class="text-sm font-bold text-slate-800">Gatilhos Clínicos Mais Identificados</h3>
              <p class="text-xs text-slate-500">Ranking dos rastreadores com maior incidência nos casos simulados</p>
            </div>

            @if (d.pedagogicalMetrics.topIdentifiedTriggers.length === 0) {
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
                    @for (trig of d.pedagogicalMetrics.topIdentifiedTriggers; track trig.triggerCode; let idx = $index) {
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
        </div>
      }
    }
  </div>
  `
})
export class ClassDashboardComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly classDashboardService = inject(ClassDashboardService);
  private readonly toastService = inject(ToastService);

  readonly classId = signal<string>('');
  readonly dashboard = signal<ClassDashboardDTO | null>(null);
  readonly isLoading = signal<boolean>(true);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('classId');
    if (id) {
      this.classId.set(id);
      this.loadDashboard();
    } else {
      this.toastService.error('Turma não identificada.');
      this.router.navigate(['/academic/classes']);
    }
  }

  loadDashboard(): void {
    this.isLoading.set(true);
    this.classDashboardService.getClassDashboard(this.classId()).subscribe({
      next: (res) => {
        this.dashboard.set(res.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar indicadores da turma.');
      }
    });
  }

  getHarmCount(letter: string): number {
    const dist = this.dashboard()?.gttMetrics.harmDistribution;
    return dist && dist[letter] ? dist[letter] : 0;
  }

  getHarmPercentage(letter: string): number {
    const total = this.dashboard()?.gttMetrics.totalAdverseEvents || 0;
    if (total === 0) return 0;
    const count = this.getHarmCount(letter);
    return (count / total) * 100;
  }

  goBack(): void {
    this.router.navigate(['/academic/classes', this.classId()]);
  }
}
