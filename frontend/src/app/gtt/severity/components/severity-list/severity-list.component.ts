/**
 * @file severity-list.component.ts
 * @description Componente da tela administrativa para listagem paginada e parametrização das Categorias de Gravidade NCC MERP (A a I).
 * @module SeverityListComponent
 */

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HarmSeverityService } from '../../services/harm-severity.service';
import { ToastService } from '../../../../common/services/toast.service';
import { HarmSeverity } from '../../models/harm-severity.model';
import { PageResponse } from '../../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../../common/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { SeverityFormComponent } from '../severity-form/severity-form.component';

/**
 * Tela de listagem paginada (10 itens/página) para gestão administrativa das categorias NCC MERP.
 */
@Component({
  selector: 'app-severity-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataTablePaginationComponent,
    ConfirmDialogComponent,
    SeverityFormComponent,
  ],
  templateUrl: './severity-list.component.html',
})
export class SeverityListComponent implements OnInit {
  /** Serviço de comunicação com API de gravidades de dano */
  private readonly severityService = inject(HarmSeverityService);
  /** Serviço de notificações Toast */
  private readonly toastService = inject(ToastService);

  /** Lista reativa de gravidades exibidas na tabela */
  readonly severities = signal<HarmSeverity[]>([]);
  /** Metadados de paginação recebidos do backend */
  readonly pageData = signal<PageResponse<HarmSeverity> | null>(null);
  /** Indicador de carregamento em andamento */
  readonly isLoading = signal<boolean>(false);

  /** Controle de visibilidade do modal de cadastro/edição */
  readonly isFormModalOpen = signal<boolean>(false);
  /** Gravidade selecionada para edição */
  readonly selectedSeverity = signal<HarmSeverity | null>(null);

  /** Controle de visibilidade do diálogo de confirmação de status */
  readonly isStatusDialogOpen = signal<boolean>(false);
  /** Indicador de processamento da alteração de status */
  readonly isProcessingStatus = signal<boolean>(false);

  /** Controle de visibilidade do diálogo de confirmação de exclusão */
  readonly isDeleteDialogOpen = signal<boolean>(false);
  /** Indicador de processamento da exclusão */
  readonly isDeleting = signal<boolean>(false);

  /** Gravidade alvo para operações destrutivas ou de status */
  readonly targetSeverity = signal<HarmSeverity | null>(null);

  /** Termo de busca textual */
  searchQuery: string = '';
  /** Filtro de dano (true: dano, false: sem dano, null: todos) */
  selectedHarmFilter: boolean | null = null;
  /** Filtro de status ativo/inativo */
  selectedStatus: boolean | null = null;

  /** Página atual (0-indexed) */
  private currentPage = 0;
  /** Quantidade de itens por página */
  private readonly pageSize = 10;
  /** Timeout para debounce da busca textual */
  private searchTimeout: any;

  /**
   * Inicializa o componente carregando a primeira página de gravidades.
   */
  ngOnInit(): void {
    this.loadSeverities();
  }

  /**
   * Executa a requisição de busca paginada com os filtros ativos.
   *
   * @param page Índice da página a ser consultada
   */
  loadSeverities(page: number = this.currentPage): void {
    this.currentPage = page;
    this.isLoading.set(true);

    this.severityService
      .listSeverities(
        this.searchQuery,
        this.selectedHarmFilter,
        this.selectedStatus,
        page,
        this.pageSize,
        'categoryLetter,asc'
      )
      .subscribe({
        next: (res) => {
          this.severities.set(res.data.content);
          this.pageData.set(res.data);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
  }

  /**
   * Trata modificação no input de busca com debounce de 300ms.
   */
  onSearchChange(): void {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => {
      this.loadSeverities(0);
    }, 300);
  }

  /**
   * Trata alteração nos seletores de filtro recarregando a primeira página.
   */
  onFilterChange(): void {
    this.loadSeverities(0);
  }

  /**
   * Trata mudança de página disparada pelo paginador.
   *
   * @param page Novo índice de página
   */
  onPageChange(page: number): void {
    this.loadSeverities(page);
  }

  /**
   * Abre o modal no modo de cadastro de nova categoria.
   */
  openCreateModal(): void {
    this.selectedSeverity.set(null);
    this.isFormModalOpen.set(true);
  }

  /**
   * Abre o modal no modo de edição preenchendo os dados da categoria selecionada.
   *
   * @param sev Instância de gravidade a editar
   */
  openEditModal(sev: HarmSeverity): void {
    this.selectedSeverity.set(sev);
    this.isFormModalOpen.set(true);
  }

  /**
   * Trata evento de salvamento fechando o modal e recarregando a tabela.
   */
  onSeveritySaved(): void {
    this.isFormModalOpen.set(false);
    this.loadSeverities(this.currentPage);
  }

  /**
   * Abre o diálogo de confirmação para alteração de status ativo/inativo.
   *
   * @param sev Gravidade alvo
   */
  openStatusDialog(sev: HarmSeverity): void {
    this.targetSeverity.set(sev);
    this.isStatusDialogOpen.set(true);
  }

  /**
   * Confirma e despacha requisição de alteração de status.
   */
  onConfirmStatusChange(): void {
    const sev = this.targetSeverity();
    if (!sev) return;

    this.isProcessingStatus.set(true);
    const newStatus = !sev.isActive;

    this.severityService.updateStatus(sev.id, { isActive: newStatus }).subscribe({
      next: () => {
        this.isProcessingStatus.set(false);
        this.isStatusDialogOpen.set(false);
        this.toastService.success(
          'Status Atualizado',
          `Categoria ${sev.categoryLetter} foi ${newStatus ? 'ativada' : 'inativada'} com sucesso.`
        );
        this.loadSeverities(this.currentPage);
      },
      error: () => this.isProcessingStatus.set(false),
    });
  }

  /**
   * Abre o diálogo de confirmação para exclusão da categoria.
   *
   * @param sev Gravidade alvo
   */
  openDeleteDialog(sev: HarmSeverity): void {
    this.targetSeverity.set(sev);
    this.isDeleteDialogOpen.set(true);
  }

  /**
   * Confirma e despacha requisição de exclusão permanente da categoria.
   */
  onConfirmDelete(): void {
    const sev = this.targetSeverity();
    if (!sev) return;

    this.isDeleting.set(true);
    this.severityService.deleteSeverity(sev.id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.isDeleteDialogOpen.set(false);
        this.toastService.success('Exclusão Concluída', `Categoria ${sev.categoryLetter} foi excluída permanentemente.`);
        this.loadSeverities(this.currentPage);
      },
      error: () => this.isDeleting.set(false),
    });
  }
}
