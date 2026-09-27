/**
 * @file severity-guide.component.ts
 * @description Guia Clínico Interativo de Gravidades de Dano (NCC MERP adaptado pelo IHI).
 * @module SeverityGuideComponent
 */

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HarmSeverityService } from '../../services/harm-severity.service';
import { HarmSeverity } from '../../models/harm-severity.model';

/**
 * Guia educativo e de consulta rápida para o Índice NCC MERP com filtros por presença de dano e busca textual.
 */
@Component({
  selector: 'app-severity-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './severity-guide.component.html',
})
export class SeverityGuideComponent implements OnInit {
  /** Serviço de comunicação com API de gravidades */
  private readonly severityService = inject(HarmSeverityService);

  /** Lista reativa de todas as categorias de gravidade ativas */
  readonly severities = signal<HarmSeverity[]>([]);
  /** Indicador de carregamento em andamento */
  readonly isLoading = signal<boolean>(false);

  /** Aba selecionada de filtro (ALL, NO_HARM, HARM) */
  readonly selectedTab = signal<'ALL' | 'NO_HARM' | 'HARM'>('ALL');
  /** Termo de busca textual digitado pelo usuário */
  readonly searchQuery = signal<string>('');

  /**
   * Inicializa o componente carregando o guia interativo de gravidades.
   */
  ngOnInit(): void {
    this.loadGuide();
  }

  /**
   * Executa a requisição ao serviço para obter o guia completo ordenado de A a I.
   */
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

  /**
   * Computa a lista de gravidades filtradas pela aba ativa e pelo termo de busca.
   */
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

  /**
   * Sublista de gravidades sem dano real (A a D) resultante do filtro.
   */
  readonly filteredNoHarmList = computed(() => {
    return this.filteredList().filter((sev) => !sev.isHarm);
  });

  /**
   * Sublista de gravidades com dano real (E a I) resultante do filtro.
   */
  readonly filteredHarmList = computed(() => {
    return this.filteredList().filter((sev) => sev.isHarm);
  });

  /**
   * Determina se o agrupamento Sem Dano deve ser renderizado no template.
   */
  readonly showNoHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'NO_HARM') &&
      this.filteredNoHarmList().length > 0
    );
  });

  /**
   * Determina se o agrupamento Com Dano deve ser renderizado no template.
   */
  readonly showHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'HARM') &&
      this.filteredHarmList().length > 0
    );
  });
}
