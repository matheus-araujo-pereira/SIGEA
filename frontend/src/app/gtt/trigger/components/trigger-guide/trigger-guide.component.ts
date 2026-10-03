/**
 * @file trigger-guide.component.ts
 * @description Tela de Consulta Rápida e Guia Educacional dos 53 Gatilhos IHI-GTT.
 * @module TriggerGuideComponent
 */

import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GttTriggerService } from '../../services/gtt-trigger.service';
import { GttModuleService } from '../../../module/services/gtt-module.service';
import { GttTrigger } from '../../models/gtt-trigger.model';
import { GttModule } from '../../../module/models/gtt-module.model';

/**
 * Guia clínico de gatilhos IHI-GTT em formato accordion expansível com busca e abas por módulo.
 */
@Component({
  selector: 'app-trigger-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trigger-guide.component.html',
})
export class TriggerGuideComponent implements OnInit {
  /** Referência de destruição para ciclo de vida do componente */
  private readonly destroyRef = inject(DestroyRef);
  /** Serviço de comunicação com API de gatilhos */
  private readonly triggerService = inject(GttTriggerService);
  /** Serviço de comunicação com API de módulos */
  private readonly moduleService = inject(GttModuleService);
  /** Rota ativa para capturar parâmetro de query opcional */
  private readonly route = inject(ActivatedRoute);

  /** Lista reativa de todos os gatilhos carregados */
  readonly triggers = signal<GttTrigger[]>([]);
  /** Lista reativa de módulos disponíveis */
  readonly modules = signal<GttModule[]>([]);
  /** Conjunto de IDs dos gatilhos com painel de diretrizes expandido */
  readonly expandedIds = signal<Set<string>>(new Set());
  /** Indicador de carregamento em andamento */
  readonly isLoading = signal<boolean>(false);

  /** Termo de busca textual */
  searchQuery = '';
  /** ID do módulo atualmente filtrado (vazio se todos) */
  selectedModuleId = '';

  /**
   * Inicializa o componente escutando a query da URL e carregando os dados.
   */
  ngOnInit(): void {
    this.route.queryParams.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      if (params['moduleId']) {
        this.selectedModuleId = params['moduleId'];
      }
      this.loadData();
    });
  }

  /**
   * Executa a busca em cascata de módulos e catálogo de gatilhos.
   */
  loadData(): void {
    this.isLoading.set(true);
    this.moduleService.getCatalog().subscribe({
      next: (modRes) => {
        this.modules.set(modRes.data);
        this.triggerService.getCatalog().subscribe({
          next: (trigRes) => {
            this.isLoading.set(false);
            this.triggers.set(trigRes.data);
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

  /**
   * Altera o módulo selecionado para filtragem.
   *
   * @param moduleId UUID do módulo ou string vazia para todos
   */
  selectModule(moduleId: string): void {
    this.selectedModuleId = moduleId;
  }

  /**
   * Alterna a expansão do painel de diretrizes de um gatilho.
   *
   * @param id UUID do gatilho
   */
  toggleExpand(id: string): void {
    const current = new Set(this.expandedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.expandedIds.set(current);
  }

  /**
   * Verifica se o gatilho está expandido.
   *
   * @param id UUID do gatilho
   * @returns true se expandido
   */
  isExpanded(id: string): boolean {
    return this.expandedIds().has(id);
  }

  /**
   * Filtra a lista de gatilhos com base no módulo selecionado e no termo de busca.
   *
   * @returns Array de gatilhos que atendem aos filtros
   */
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
