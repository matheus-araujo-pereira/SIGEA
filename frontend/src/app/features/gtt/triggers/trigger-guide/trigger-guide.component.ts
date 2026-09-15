import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Tela de Consulta Rápida e Guia Educacional de Gatilhos IHI-GTT para todos os perfis.
 */
@Component({
  selector: 'app-trigger-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div>
        <h2 class="text-2xl font-black text-slate-900 tracking-tight">Guia Clínico de Gatilhos (IHI-GTT)</h2>
        <p class="text-xs text-slate-500 mt-1">
          Dicionário oficial dos 53 gatilhos clínicos e orientações para investigação em prontuários
        </p>
      </div>

      <!-- Barra de Pesquisa e Filtros Rápidos -->
      <div class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
        <div class="relative w-full">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            placeholder="Buscar gatilho por código (ex: C1, M4), termo clínico ou medicamento..."
            class="w-full pl-9 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Abas Rápidas de Módulos -->
        <div class="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-thin">
          <button
            type="button"
            (click)="selectModule('')"
            class="px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors"
            [ngClass]="selectedModuleId === '' ? 'bg-clinical-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
          >
            Todos os Gatilhos ({{ triggers().length }})
          </button>

          @for (mod of modules(); track mod.id) {
            <button
              type="button"
              (click)="selectModule(mod.id)"
              class="px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors flex items-center gap-1.5"
              [ngClass]="selectedModuleId === mod.id ? 'bg-clinical-600 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
            >
              <span class="font-black">{{ mod.code }}</span>
              <span>• {{ mod.name }}</span>
            </button>
          }
        </div>
      </div>

      <!-- Estado de Carregamento -->
      @if (isLoading()) {
        <div class="py-16 text-center text-slate-400">
          <div class="inline-flex items-center gap-2">
            <svg class="w-5 h-5 animate-spin text-clinical-600" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            <span>Carregando guia de gatilhos...</span>
          </div>
        </div>
      } @else if (filteredTriggers().length === 0) {
        <div class="p-12 text-center bg-white rounded-2xl border border-slate-200 text-slate-500">
          Nenhum gatilho localizado com os critérios informados.
        </div>
      } @else {
        <!-- Lista Detalhada de Gatilhos com Accordion/Card Expansível -->
        <div class="space-y-4">
          @for (trigger of filteredTriggers(); track trigger.id) {
            <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs hover:border-clinical-300 transition-all p-5">
              <div class="flex items-start justify-between gap-4">
                <div class="flex items-start gap-3.5">
                  <div class="w-10 h-10 rounded-xl bg-blue-50 text-blue-700 border border-blue-200 flex items-center justify-center font-black text-sm flex-shrink-0 mt-0.5">
                    {{ trigger.code }}
                  </div>
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded bg-slate-100 text-slate-600">
                        Módulo {{ trigger.moduleCode }} - {{ trigger.moduleName }}
                      </span>
                    </div>
                    <h3 class="text-base font-bold text-slate-900 mt-1">
                      {{ trigger.name }}
                    </h3>
                  </div>
                </div>

                <button
                  type="button"
                  (click)="toggleExpand(trigger.id)"
                  class="p-2 text-slate-400 hover:text-slate-700 rounded-lg hover:bg-slate-100 transition-colors"
                  [attr.aria-label]="isExpanded(trigger.id) ? 'Recolher detalhes' : 'Expandir detalhes'"
                >
                  <svg
                    class="w-5 h-5 transition-transform duration-200"
                    [ngClass]="isExpanded(trigger.id) ? 'rotate-180' : ''"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Conteúdo Expansível de Diretrizes de Investigação -->
              @if (isExpanded(trigger.id)) {
                <div class="mt-4 pt-4 border-t border-slate-100 animate-in fade-in duration-200">
                  <h4 class="text-xs font-bold text-clinical-900 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                    <svg class="w-4 h-4 text-clinical-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <span>Diretrizes e Investigação no Prontuário (IHI Global Trigger Tool)</span>
                  </h4>
                  <p class="text-xs text-slate-600 leading-relaxed bg-slate-50 p-3.5 rounded-xl border border-slate-100">
                    {{ trigger.description }}
                  </p>
                </div>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class TriggerGuideComponent implements OnInit {
  private readonly triggerService = inject(GttTriggerService);
  private readonly moduleService = inject(GttModuleService);
  private readonly route = inject(ActivatedRoute);

  readonly triggers = signal<GttTrigger[]>([]);
  readonly modules = signal<GttModule[]>([]);
  readonly expandedIds = signal<Set<string>>(new Set());
  readonly isLoading = signal<boolean>(false);

  searchQuery = '';
  selectedModuleId = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['moduleId']) {
        this.selectedModuleId = params['moduleId'];
      }
      this.loadData();
    });
  }

  loadData(): void {
    this.isLoading.set(true);
    this.moduleService.getCatalog().subscribe({
      next: (modRes) => {
        this.modules.set(modRes.data);
        this.triggerService.getCatalog().subscribe({
          next: (trigRes) => {
            this.isLoading.set(false);
            this.triggers.set(trigRes.data);
            // Expande os primeiros por conveniência
            if (trigRes.data.length > 0) {
              this.expandedIds.set(new Set([trigRes.data[0].id]));
            }
          },
          error: () => this.isLoading.set(false),
        });
      },
      error: () => this.isLoading.set(false),
    });
  }

  selectModule(moduleId: string): void {
    this.selectedModuleId = moduleId;
  }

  toggleExpand(id: string): void {
    const current = new Set(this.expandedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.expandedIds.set(current);
  }

  isExpanded(id: string): boolean {
    return this.expandedIds().has(id);
  }

  filteredTriggers(): GttTrigger[] {
    const q = this.searchQuery.toLowerCase().trim();
    return this.triggers().filter((t) => {
      const matchesModule = !this.selectedModuleId || t.moduleId === this.selectedModuleId;
      const matchesQuery =
        !q ||
        t.code.toLowerCase().includes(q) ||
        t.name.toLowerCase().includes(q) ||
        t.description.toLowerCase().includes(q) ||
        t.moduleName.toLowerCase().includes(q);

      return matchesModule && matchesQuery;
    });
  }
}
