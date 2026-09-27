/**
 * @file module-list.component.ts
 * @description Componente da tela administrativa para gestão e listagem paginada de Módulos IHI-GTT.
 * @module ModuleListComponent
 */

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GttModuleService } from '../../services/gtt-module.service';
import { ToastService } from '../../../../common/services/toast.service';
import { GttModule } from '../../models/gtt-module.model';
import { PageResponse } from '../../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../../common/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { ModuleFormComponent } from '../module-form/module-form.component';

/**
 * Tela administrativa de gestão e listagem paginada (10 itens/página) de Módulos GTT.
 */
@Component({
  selector: 'app-module-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataTablePaginationComponent,
    ConfirmDialogComponent,
    ModuleFormComponent,
  ],
  templateUrl: './module-list.component.html',
})
export class ModuleListComponent implements OnInit {
  /** Serviço de gestão de módulos GTT */
  private readonly moduleService = inject(GttModuleService);
  /** Serviço de notificações Toast */
  private readonly toastService = inject(ToastService);

  /** Lista reativa de módulos exibidos na página atual */
  readonly modules = signal<GttModule[]>([]);
  /** Metadados de paginação recebidos do backend */
  readonly pageData = signal<PageResponse<GttModule> | null>(null);
  /** Indicador de carregamento em andamento */
  readonly isLoading = signal<boolean>(false);

  /** Controle de visibilidade do modal de cadastro/edição */
  readonly isFormModalOpen = signal<boolean>(false);
  /** Controle de visibilidade do diálogo de confirmação de status */
  readonly isStatusDialogOpen = signal<boolean>(false);
  /** Controle de visibilidade do diálogo de confirmação de exclusão */
  readonly isDeleteDialogOpen = signal<boolean>(false);

  /** Módulo selecionado para edição (null se for criação) */
  readonly selectedModule = signal<GttModule | null>(null);
  /** Módulo alvo para alteração de status ou exclusão */
  readonly targetModule = signal<GttModule | null>(null);

  /** Termo de busca textual */
  searchQuery = '';
  /** Filtro de status ativo/inativo selecionado */
  selectedStatus: boolean | null = null;
  /** Página atual (0-indexed) */
  currentPage = 0;

  /** Timeout de debounce para pesquisa textual */
  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  /**
   * Inicializa o componente carregando a primeira página de módulos.
   */
  ngOnInit(): void {
    this.loadModules();
  }

  /**
   * Executa a requisição de busca paginada de módulos com filtros ativos.
   */
  loadModules(): void {
    this.isLoading.set(true);
    this.moduleService
      .listModules(this.searchQuery, this.selectedStatus, this.currentPage, 10, 'code,asc')
      .subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.pageData.set(response.data);
          this.modules.set(response.data.content);
        },
        error: () => this.isLoading.set(false),
      });
  }

  /**
   * Trata alterações no campo de busca textual aplicando debounce de 350ms.
   */
  onSearchChange(): void {
    if (this.searchDebounceTimeout) {
      clearTimeout(this.searchDebounceTimeout);
    }
    this.searchDebounceTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadModules();
    }, 350);
  }

  /**
   * Trata alteração no filtro de status recarregando da página inicial.
   */
  onFilterChange(): void {
    this.currentPage = 0;
    this.loadModules();
  }

  /**
   * Trata mudança de página solicitada pelo paginador.
   *
   * @param page Novo índice de página selecionado
   */
  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadModules();
  }

  /**
   * Abre o modal de formulário no modo de criação.
   */
  openCreateModal(): void {
    this.selectedModule.set(null);
    this.isFormModalOpen.set(true);
  }

  /**
   * Abre o modal de formulário preenchido para edição de um módulo.
   *
   * @param module Instância do módulo a editar
   */
  openEditModal(module: GttModule): void {
    this.selectedModule.set(module);
    this.isFormModalOpen.set(true);
  }

  /**
   * Trata o evento de salvamento com sucesso fechando o modal e recarregando a listagem.
   */
  onModuleSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadModules();
  }

  /**
   * Abre o diálogo de confirmação para alteração de status ativo/inativo.
   *
   * @param module Módulo cujo status será alternado
   */
  openStatusDialog(module: GttModule): void {
    this.targetModule.set(module);
    this.isStatusDialogOpen.set(true);
  }

  /**
   * Confirma e despacha a requisição de alteração de status.
   */
  onConfirmStatusChange(): void {
    const mod = this.targetModule();
    if (!mod) return;

    this.isStatusDialogOpen.set(false);
    const newStatus = !mod.isActive;

    this.moduleService.updateStatus(mod.id, { isActive: newStatus }).subscribe({
      next: () => {
        this.toastService.success(
          'Sucesso',
          `Módulo ${mod.name} ${newStatus ? 'ativado' : 'inativado'} com sucesso.`
        );
        this.loadModules();
      },
    });
  }

  /**
   * Abre o diálogo de confirmação para exclusão de um módulo.
   *
   * @param module Módulo a ser excluído
   */
  openDeleteDialog(module: GttModule): void {
    this.targetModule.set(module);
    this.isDeleteDialogOpen.set(true);
  }

  /**
   * Confirma e despacha a requisição de exclusão do módulo.
   */
  onConfirmDelete(): void {
    const mod = this.targetModule();
    if (!mod) return;

    this.isDeleteDialogOpen.set(false);
    this.moduleService.deleteModule(mod.id).subscribe({
      next: () => {
        this.toastService.success('Sucesso', `Módulo ${mod.name} excluído com sucesso.`);
        this.loadModules();
      },
    });
  }
}
