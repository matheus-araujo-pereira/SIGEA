/**
 * @file module-catalog.component.ts
 * @description Tela do Catálogo Educacional de Módulos IHI-GTT em cards interativos para exploração clínica.
 * @module ModuleCatalogComponent
 */

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { GttModuleService } from '../../services/gtt-module.service';
import { GttModule } from '../../models/gtt-module.model';

/**
 * Catálogo educacional de Módulos GTT com badges coloridos e contadores de gatilhos.
 */
@Component({
  selector: 'app-module-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './module-catalog.component.html',
})
export class ModuleCatalogComponent implements OnInit {
  /** Serviço de comunicação com API de módulos GTT */
  private readonly moduleService = inject(GttModuleService);

  /** Lista reativa de módulos ativos carregados */
  readonly modules = signal<GttModule[]>([]);
  /** Indicador de carregamento dos dados */
  readonly isLoading = signal<boolean>(false);

  /**
   * Inicializa o componente carregando o catálogo completo de módulos ativos.
   */
  ngOnInit(): void {
    this.loadCatalog();
  }

  /**
   * Executa a busca de todos os módulos ativos do protocolo IHI-GTT.
   */
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

  /**
   * Mapeia o código canônico ou nome do módulo para classes de estilização CSS temáticas.
   *
   * @param code Código do módulo (C, M, S, I, P, E)
   * @returns String com classes Tailwind para fundo, texto e borda
   */
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
