/**
 * @file trigger-list.component.ts
 * @description Componente da tela administrativa para listagem e parametrização dos 53 Gatilhos IHI-GTT.
 * @module TriggerListComponent
 */

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GttTriggerService } from '../../services/gtt-trigger.service';
import { GttModuleService } from '../../../module/services/gtt-module.service';
import { ToastService } from '../../../../common/services/toast.service';
import { GttTrigger } from '../../models/gtt-trigger.model';
import { GttModule } from '../../../module/models/gtt-module.model';
import { PageResponse } from '../../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../../common/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { TriggerFormComponent } from '../trigger-form/trigger-form.component';

/**
 * Tela de listagem paginada (10 itens/página) de Gatilhos GTT com filtros por módulo e busca textual.
 */
@Component({
  selector: 'app-trigger-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataTablePaginationComponent,
    ConfirmDialogComponent,
    TriggerFormComponent,
  ],
  templateUrl: './trigger-list.component.html',
})
export class TriggerListComponent implements OnInit {
  /** Serviço de gestão de gatilhos GTT */
  private readonly triggerService = inject(GttTriggerService);
  /** Serviço de consulta de módulos GTT */
  private readonly moduleService = inject(GttModuleService);
  /** Serviço de notificações Toast */
  private readonly toastService = inject(ToastService);

  /** Lista reativa de gatilhos exibidos na página atual */
  readonly triggers = signal<GttTrigger[]>([]);
  /** Módulos disponíveis para preenchimento de filtros e selects */
  readonly availableModules = signal<GttModule[]>([]);
  /** Metadados de paginação */
  readonly pageData = signal<PageResponse<GttTrigger> | null>(null);
  /** Indicador de carregamento */
  readonly isLoading = signal<boolean>(false);

  /** Controle de visibilidade do modal de cadastro/edição */
  readonly isFormModalOpen = signal<boolean>(false);
  /** Controle de visibilidade do diálogo de confirmação de status */
  readonly isStatusDialogOpen = signal<boolean>(false);
  /** Controle de visibilidade do diálogo de confirmação de exclusão */
  readonly isDeleteDialogOpen = signal<boolean>(false);

  /** Gatilho selecionado para edição */
  readonly selectedTrigger = signal<GttTrigger | null>(null);
  /** Gatilho alvo para alteração de status ou exclusão */
  readonly targetTrigger = signal<GttTrigger | null>(null);

  /** Termo de busca textual */
  searchQuery = '';
  /** Módulo selecionado para filtragem */
  selectedModuleId = '';
  /** Status ativo/inativo selecionado */
  selectedStatus: boolean | null = null;
  /** Página atual (0-indexed) */
  currentPage = 0;

  /** Timeout para debounce da pesquisa */
  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  /**
   * Inicializa o componente carregando os módulos do catálogo e a listagem de gatilhos.
   */
  ngOnInit(): void {
    this.loadModules();
    this.loadTriggers();
  }

  /**
   * Carrega os módulos ativos para o filtro dropdown.
   */
  loadModules(): void {
    this.moduleService.getCatalog().subscribe({
      next: (res) => this.availableModules.set(res.data),
    });
  }

  /**
   * Executa a requisição paginada de busca de gatilhos.
   */
  loadTriggers(): void {
    this.isLoading.set(true);
    const modId = this.selectedModuleId ? this.selectedModuleId : undefined;
    this.triggerService
      .listTriggers(modId, this.searchQuery, this.selectedStatus, this.currentPage, 10, 'code,asc')
      .subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.pageData.set(response.data);
          this.triggers.set(response.data.content);
        },
        error: () => this.isLoading.set(false),
      });
  }

  /**
   * Trata busca textual com debounce de 350ms.
   */
  onSearchChange(): void {
    if (this.searchDebounceTimeout) {
      clearTimeout(this.searchDebounceTimeout);
    }
    this.searchDebounceTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadTriggers();
    }, 350);
  }

  /**
   * Trata mudança nos filtros de módulo e status.
   */
  onFilterChange(): void {
    this.currentPage = 0;
    this.loadTriggers();
  }

  /**
   * Trata mudança de página.
   *
   * @param page Índice da nova página
   */
  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadTriggers();
  }

  /**
   * Abre o modal no modo de cadastro de novo gatilho.
   */
  openCreateModal(): void {
    this.selectedTrigger.set(null);
    this.isFormModalOpen.set(true);
  }

  /**
   * Abre o modal no modo de edição de gatilho.
   *
   * @param trigger Gatilho a editar
   */
  openEditModal(trigger: GttTrigger): void {
    this.selectedTrigger.set(trigger);
    this.isFormModalOpen.set(true);
  }

  /**
   * Trata confirmação de salvamento do gatilho.
   */
  onTriggerSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadTriggers();
  }

  /**
   * Abre o diálogo de confirmação para alteração de status.
   *
   * @param trigger Gatilho alvo
   */
  openStatusDialog(trigger: GttTrigger): void {
    this.targetTrigger.set(trigger);
    this.isStatusDialogOpen.set(true);
  }

  /**
   * Confirma e despacha requisição de alteração de status do gatilho.
   */
  onConfirmStatusChange(): void {
    const trig = this.targetTrigger();
    if (!trig) return;

    this.isStatusDialogOpen.set(false);
    const newStatus = !trig.isActive;

    this.triggerService.updateStatus(trig.id, { isActive: newStatus }).subscribe({
      next: () => {
        this.toastService.success(
          'Sucesso',
          `Gatilho ${trig.code} ${newStatus ? 'ativado' : 'inativado'} com sucesso.`
        );
        this.loadTriggers();
      },
    });
  }

  /**
   * Abre diálogo de exclusão de gatilho.
   *
   * @param trigger Gatilho a excluir
   */
  openDeleteDialog(trigger: GttTrigger): void {
    this.targetTrigger.set(trigger);
    this.isDeleteDialogOpen.set(true);
  }

  /**
   * Confirma e despacha exclusão permanente do gatilho.
   */
  onConfirmDelete(): void {
    const trig = this.targetTrigger();
    if (!trig) return;

    this.isDeleteDialogOpen.set(false);
    this.triggerService.deleteTrigger(trig.id).subscribe({
      next: () => {
        this.toastService.success('Sucesso', `Gatilho ${trig.code} excluído com sucesso.`);
        this.loadTriggers();
      },
    });
  }
}
