import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';

/**
 * Guia Clínico Interativo de Gravidades de Dano (Índice NCC MERP adaptado pelo IHI).
 * Disponível para consulta educacional de todos os perfis (ADMIN, PROFESSOR, STUDENT).
 */
@Component({
  selector: 'app-severity-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-8">
      <!-- Cabeçalho Educacional -->
      <div class="bg-gradient-to-r from-clinical-900 via-clinical-800 to-slate-900 rounded-3xl p-8 text-white shadow-xl relative overflow-hidden">
        <div class="absolute -right-10 -bottom-10 w-80 h-80 bg-blue-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="relative z-10 max-w-3xl">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/20 text-blue-200 text-xs font-semibold mb-3 border border-blue-400/20">
            <span>Guia Metodológico NCC MERP</span>
            <span class="w-1 h-1 rounded-full bg-blue-300"></span>
            <span>Taxonomia Oficial IHI</span>
          </div>
          <h1 class="text-3xl font-black tracking-tight sm:text-4xl text-white">
            Classificação de Gravidade de Dano
          </h1>
          <p class="mt-3 text-sm text-slate-300 leading-relaxed font-normal">
            O Índice NCC MERP (National Coordinating Council for Medication Error Reporting and Prevention)
            padroniza a mensuração do impacto clínico dos eventos. No método IHI-GTT, o foco prioritário são os
            eventos com <strong>Dano Real (Categorias E a I)</strong>, distinguindo-os de quase-falhas (Categorias A a D).
          </p>
        </div>
      </div>

      <!-- Algoritmo de Decisão Clínica NCC MERP -->
      <div class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
        <h2 class="text-sm font-black text-slate-900 uppercase tracking-wider mb-4 flex items-center gap-2">
          <svg class="w-5 h-5 text-clinical-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
          <span>Algoritmo de Raciocínio Clínico para Auditoria GTT</span>
        </h2>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 text-xs">
          <!-- Passo 1 -->
          <div class="p-4 rounded-xl bg-slate-50 border border-slate-200">
            <span class="w-6 h-6 rounded-full bg-slate-200 text-slate-700 font-bold flex items-center justify-center text-[11px] mb-2">1</span>
            <div class="font-bold text-slate-800">Ocorreu o Erro/Incidente?</div>
            <p class="text-slate-500 mt-1 text-[11px] leading-relaxed">
              Se houve apenas potencial mas não ocorreu, classifica-se como <strong>Cat. A</strong>.
            </p>
          </div>

          <!-- Passo 2 -->
          <div class="p-4 rounded-xl bg-slate-50 border border-slate-200">
            <span class="w-6 h-6 rounded-full bg-slate-200 text-slate-700 font-bold flex items-center justify-center text-[11px] mb-2">2</span>
            <div class="font-bold text-slate-800">Atingiu o Paciente?</div>
            <p class="text-slate-500 mt-1 text-[11px] leading-relaxed">
              Se não atingiu, é <strong>Cat. B</strong>. Se atingiu sem dano, é <strong>Cat. C</strong> ou <strong>D</strong> (se necessitou monitoramento).
            </p>
          </div>

          <!-- Passo 3 -->
          <div class="p-4 rounded-xl bg-rose-50/70 border border-rose-200">
            <span class="w-6 h-6 rounded-full bg-rose-200 text-rose-800 font-bold flex items-center justify-center text-[11px] mb-2">3</span>
            <div class="font-bold text-rose-900">Houve Dano ao Paciente?</div>
            <p class="text-rose-700 mt-1 text-[11px] leading-relaxed">
              <strong>Sim!</strong> Entra na faixa de Evento Adverso Real (<strong>Cat. E a I</strong>). Dano temporário: E ou F.
            </p>
          </div>

          <!-- Passo 4 -->
          <div class="p-4 rounded-xl bg-rose-50/70 border border-rose-200">
            <span class="w-6 h-6 rounded-full bg-rose-200 text-rose-800 font-bold flex items-center justify-center text-[11px] mb-2">4</span>
            <div class="font-bold text-rose-900">Qual a Gravidade Final?</div>
            <p class="text-rose-700 mt-1 text-[11px] leading-relaxed">
              Dano permanente: <strong>G</strong>. Risco iminente de morte / suporte de vida: <strong>H</strong>. Óbito: <strong>I</strong>.
            </p>
          </div>
        </div>
      </div>

      <!-- Barra de Controle: Filtros e Abas -->
      <div class="flex flex-col md:flex-row items-center justify-between gap-4">
        <!-- Abas de Filtro Rápido -->
        <div class="flex items-center p-1 bg-slate-100 rounded-xl w-full md:w-auto">
          <button
            type="button"
            (click)="selectedTab.set('ALL')"
            [ngClass]="selectedTab() === 'ALL' ? 'bg-white text-slate-900 shadow-xs font-bold' : 'text-slate-600 hover:text-slate-900'"
            class="px-4 py-2 text-xs rounded-lg transition-all flex-1 md:flex-none"
          >
            Todas (A - I)
          </button>
          <button
            type="button"
            (click)="selectedTab.set('NO_HARM')"
            [ngClass]="selectedTab() === 'NO_HARM' ? 'bg-white text-emerald-800 shadow-xs font-bold' : 'text-slate-600 hover:text-emerald-700'"
            class="px-4 py-2 text-xs rounded-lg transition-all flex-1 md:flex-none"
          >
            Sem Dano (A - D)
          </button>
          <button
            type="button"
            (click)="selectedTab.set('HARM')"
            [ngClass]="selectedTab() === 'HARM' ? 'bg-white text-rose-800 shadow-xs font-bold' : 'text-slate-600 hover:text-rose-700'"
            class="px-4 py-2 text-xs rounded-lg transition-all flex-1 md:flex-none"
          >
            Com Dano Real (E - I)
          </button>
        </div>

        <!-- Busca Textual -->
        <div class="relative w-full md:w-80">
          <input
            type="text"
            [ngModel]="searchQuery()"
            (ngModelChange)="searchQuery.set($event)"
            placeholder="Filtrar categoria ou palavra-chave..."
            class="w-full pl-9 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>

      <!-- Carregamento -->
      @if (isLoading()) {
        <div class="py-20 text-center">
          <div class="inline-flex items-center gap-3 text-slate-400 text-sm">
            <svg class="w-5 h-5 animate-spin text-clinical-600" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            <span>Carregando guia de gravidades...</span>
          </div>
        </div>
      } @else {
        <!-- Seção: Sem Dano Real (A a D) -->
        @if (showNoHarmGroup()) {
          <div class="space-y-4">
            <div class="flex items-center gap-3 pb-2 border-b border-emerald-100">
              <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
              <h3 class="text-lg font-black text-slate-800">
                Categorias Sem Dano Real ao Paciente (A a D)
              </h3>
              <span class="text-xs font-semibold px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-700 border border-emerald-200">
                Circunstâncias & Quase-Falhas
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              @for (sev of filteredNoHarmList(); track sev.id) {
                <div class="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-xs hover:border-emerald-300 hover:shadow-md transition-all">
                  <div class="flex items-start gap-4">
                    <div class="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-800 border border-emerald-200 flex items-center justify-center text-xl font-black shrink-0">
                      {{ sev.categoryLetter }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center justify-between gap-2">
                        <h4 class="font-bold text-slate-900 text-sm truncate">
                          {{ sev.name }}
                        </h4>
                        <span class="text-[10px] uppercase font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 shrink-0">
                          Sem Dano
                        </span>
                      </div>
                      <p class="mt-2 text-xs text-slate-600 leading-relaxed">
                        {{ sev.description }}
                      </p>
                    </div>
                  </div>
                </div>
              }
            </div>
          </div>
        }

        <!-- Seção: Com Dano Real (E a I) -->
        @if (showHarmGroup()) {
          <div class="space-y-4">
            <div class="flex items-center gap-3 pb-2 border-b border-rose-200">
              <span class="w-3 h-3 rounded-full bg-rose-600"></span>
              <h3 class="text-lg font-black text-slate-900">
                Categorias com Dano Real ao Paciente (E a I)
              </h3>
              <span class="text-xs font-bold px-2 py-0.5 rounded-md bg-rose-100 text-rose-800 border border-rose-300">
                Eventos Adversos Clínicos (Foco IHI-GTT)
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              @for (sev of filteredHarmList(); track sev.id) {
                <div class="bg-white rounded-2xl border border-rose-100 p-5 shadow-xs hover:border-rose-400 hover:shadow-md transition-all">
                  <div class="flex items-start gap-4">
                    <div
                      class="w-12 h-12 rounded-2xl border flex items-center justify-center text-xl font-black shrink-0"
                      [ngClass]="sev.categoryLetter === 'I' ? 'bg-slate-900 text-white border-slate-900' : 'bg-rose-50 text-rose-700 border-rose-200'"
                    >
                      {{ sev.categoryLetter }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center justify-between gap-2">
                        <h4 class="font-bold text-slate-900 text-sm truncate">
                          {{ sev.name }}
                        </h4>
                        <span
                          class="text-[10px] uppercase font-black px-2 py-0.5 rounded border shrink-0"
                          [ngClass]="sev.categoryLetter === 'I' ? 'bg-slate-900 text-white border-slate-900' : 'bg-rose-100 text-rose-800 border-rose-300'"
                        >
                          Dano Real
                        </span>
                      </div>
                      <p class="mt-2 text-xs text-slate-600 leading-relaxed">
                        {{ sev.description }}
                      </p>
                    </div>
                  </div>
                </div>
              }
            </div>
          </div>
        }

        <!-- Caso nenhum registro seja retornado -->
        @if (filteredList().length === 0) {
          <div class="bg-white rounded-2xl border border-slate-200 p-12 text-center text-slate-400 text-sm">
            Nenhuma categoria encontrada correspondente aos critérios de busca informados.
          </div>
        }
      }
    </div>
  `,
})
export class SeverityGuideComponent implements OnInit {
  private readonly severityService = inject(HarmSeverityService);

  readonly severities = signal<HarmSeverity[]>([]);
  readonly isLoading = signal<boolean>(false);

  readonly selectedTab = signal<'ALL' | 'NO_HARM' | 'HARM'>('ALL');
  readonly searchQuery = signal<string>('');

  ngOnInit(): void {
    this.loadGuide();
  }

  loadGuide(): void {
    this.isLoading.set(true);
    this.severityService.getGuide().subscribe({
      next: (res) => {
        this.severities.set(res.data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  readonly filteredList = computed(() => {
    const list = this.severities();
    const query = this.searchQuery().toLowerCase().trim();

    return list.filter((sev) => {
      const matchesSearch =
        !query ||
        sev.categoryLetter.toLowerCase().includes(query) ||
        sev.name.toLowerCase().includes(query) ||
        sev.description.toLowerCase().includes(query);

      const matchesTab =
        this.selectedTab() === 'ALL' ||
        (this.selectedTab() === 'NO_HARM' && !sev.isHarm) ||
        (this.selectedTab() === 'HARM' && sev.isHarm);

      return matchesSearch && matchesTab;
    });
  });

  readonly filteredNoHarmList = computed(() => {
    return this.filteredList().filter((sev) => !sev.isHarm);
  });

  readonly filteredHarmList = computed(() => {
    return this.filteredList().filter((sev) => sev.isHarm);
  });

  readonly showNoHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'NO_HARM') &&
      this.filteredNoHarmList().length > 0
    );
  });

  readonly showHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'HARM') &&
      this.filteredHarmList().length > 0
    );
  });
}
