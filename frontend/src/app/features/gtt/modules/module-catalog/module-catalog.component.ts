import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Tela de Catálogo Educacional de Módulos IHI-GTT para todos os perfis.
 */
@Component({
  selector: 'app-module-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Catálogo de Módulos GTT</h2>
          <p class="text-xs text-slate-500 mt-1">
            Estrutura metodológica de rastreamento de eventos adversos do Institute for Healthcare Improvement (IHI)
          </p>
        </div>
        <a
          routerLink="/gtt/triggers"
          class="btn-secondary gap-2 text-xs font-semibold self-start"
        >
          <svg class="w-4 h-4 text-clinical-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <span>Acessar Guia de Gatilhos</span>
        </a>
      </div>

      <!-- Estado de Carregamento -->
      @if (isLoading()) {
        <div class="py-16 text-center text-slate-400">
          <div class="inline-flex items-center gap-2">
            <svg class="w-5 h-5 animate-spin text-clinical-600" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
            </svg>
            <span>Carregando módulos assistenciais...</span>
          </div>
        </div>
      } @else {
        <!-- Grid de Cartões dos Módulos -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          @for (module of modules(); track module.id) {
            <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs hover:shadow-md transition-all p-6 flex flex-col justify-between group">
              <div>
                <!-- Topo do Card: Código e Status -->
                <div class="flex items-center justify-between mb-4">
                  <div class="min-w-[48px] h-9 px-3 rounded-xl inline-flex items-center justify-center font-black text-xs tracking-wider uppercase shadow-xs" [ngClass]="getModuleColor(module.code)">
                    {{ module.code }}
                  </div>
                  <span class="text-xs font-semibold px-2.5 py-1 rounded-full bg-slate-100 text-slate-600">
                    {{ module.triggerCount ?? 0 }} gatilhos
                  </span>
                </div>

                <!-- Título do Módulo -->
                <h3 class="text-lg font-bold text-slate-900 group-hover:text-clinical-600 transition-colors">
                  {{ module.name }}
                </h3>

                <!-- Descrição Assistencial -->
                <p class="text-xs text-slate-500 mt-2 leading-relaxed">
                  {{ module.description || 'Gatilhos clínicos padronizados do módulo assistencial IHI-GTT.' }}
                </p>
              </div>

              <!-- Rodapé do Card -->
              <div class="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between">
                <span class="text-[11px] font-mono font-medium text-slate-400">
                  Módulo IHI {{ module.code }}
                </span>
                <a
                  [routerLink]="['/gtt/triggers']"
                  [queryParams]="{ moduleId: module.id }"
                  class="text-xs font-bold text-clinical-600 hover:text-clinical-800 flex items-center gap-1 group-hover:translate-x-0.5 transition-transform"
                >
                  <span>Explorar Gatilhos</span>
                  <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </a>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class ModuleCatalogComponent implements OnInit {
  private readonly moduleService = inject(GttModuleService);

  readonly modules = signal<GttModule[]>([]);
  readonly isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadCatalog();
  }

  loadCatalog(): void {
    this.isLoading.set(true);
    this.moduleService.getCatalog().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.modules.set(res.data);
      },
      error: () => this.isLoading.set(false),
    });
  }

  getModuleColor(code: string): string {
    const normalized = (code || '').trim().toUpperCase();
    switch (normalized) {
      case 'C':
      case 'CUIDADOS':
        return 'bg-blue-50 text-blue-700 border border-blue-200';
      case 'M':
      case 'MEDICACAO':
        return 'bg-amber-50 text-amber-700 border border-amber-200';
      case 'S':
      case 'CIRURGICO':
        return 'bg-emerald-50 text-emerald-700 border border-emerald-200';
      case 'I':
      case 'UTI':
        return 'bg-purple-50 text-purple-700 border border-purple-200';
      case 'P':
      case 'PERINATAL':
        return 'bg-rose-50 text-rose-700 border border-rose-200';
      case 'E':
      case 'URGENCIA':
        return 'bg-orange-50 text-orange-700 border border-orange-200';
      default:
        return 'bg-slate-50 text-slate-700 border border-slate-200';
    }
  }
}
