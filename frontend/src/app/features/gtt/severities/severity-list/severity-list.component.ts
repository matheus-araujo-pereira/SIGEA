import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { SeverityFormComponent } from '../severity-form/severity-form.component';

/**
 * Tela administrativa para gestão e listagem paginada (10 itens) de Categorias de Gravidade (NCC MERP).
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
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Categorias de Gravidade de Dano</h2>
          <p class="text-xs text-slate-500 mt-1">
            Classificação do NCC MERP (Categorias A a I) para mensuração do impacto clínico de eventos adversos
          </p>
        </div>
        <button
          type="button"
          (click)="openCreateModal()"
          class="btn-primary gap-2 shadow-sm"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          <span>Nova Categoria</span>
        </button>
      </div>

      <!-- Barra de Filtros -->
      <div class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row items-center gap-4">
        <!-- Campo Busca -->
        <div class="relative flex-1 w-full">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Buscar por categoria (A-I), nome ou definição NCC MERP..."
            class="w-full pl-9 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Filtro Enquadramento de Dano -->
        <div class="w-full md:w-48">
          <select
            [(ngModel)]="selectedHarmFilter"
            (ngModelChange)="onFilterChange()"
            class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option [ngValue]="null">Todos os Enquadramentos</option>
            <option [ngValue]="false">Sem Dano Real (A a D)</option>
            <option [ngValue]="true">Com Dano Real (E a I)</option>
          </select>
        </div>

        <!-- Filtro Status -->
        <div class="w-full md:w-40">
          <select
            [(ngModel)]="selectedStatus"
            (ngModelChange)="onFilterChange()"
            class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option [ngValue]="null">Todos os Status</option>
            <option [ngValue]="true">Apenas Ativas</option>
            <option [ngValue]="false">Apenas Inativas</option>
          </select>
        </div>
      </div>

      <!-- Tabela Padronizada -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
            <thead class="bg-slate-50/80 text-slate-500 font-bold uppercase tracking-wider text-[11px]">
              <tr>
                <th scope="col" class="px-6 py-3.5 w-20 text-center">Cat.</th>
                <th scope="col" class="px-6 py-3.5 w-36">Enquadramento</th>
                <th scope="col" class="px-6 py-3.5">Nome da Gravidade</th>
                <th scope="col" class="px-6 py-3.5">Definição Oficial NCC MERP</th>
                <th scope="col" class="px-6 py-3.5 w-24">Status</th>
                <th scope="col" class="px-6 py-3.5 text-right w-28">Ações</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100 font-medium text-slate-700">
              @if (isLoading()) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-slate-400">
                    <div class="inline-flex items-center gap-2">
                      <svg class="w-4 h-4 animate-spin text-clinical-600" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                      </svg>
                      <span>Carregando categorias...</span>
                    </div>
                  </td>
                </tr>
              } @else if (severities().length === 0) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-slate-400">
                    Nenhuma categoria de gravidade encontrada para os filtros selecionados.
                  </td>
                </tr>
              } @else {
                @for (sev of severities(); track sev.id) {
                  <tr class="hover:bg-slate-50/70 transition-colors">
                    <!-- Letra da Categoria -->
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <span
                        class="inline-flex items-center justify-center w-8 h-8 rounded-lg text-sm font-black border"
                        [ngClass]="sev.isHarm ? 'bg-rose-50 text-rose-700 border-rose-200' : 'bg-blue-50 text-blue-700 border-blue-200'"
                      >
                        {{ sev.categoryLetter }}
                      </span>
                    </td>

                    <!-- Enquadramento de Dano -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      @if (sev.isHarm) {
                        <span class="inline-flex items-center px-2.5 py-1 rounded-md text-[11px] font-bold bg-rose-100 text-rose-800 border border-rose-200">
                          Com Dano Real
                        </span>
                      } @else {
                        <span class="inline-flex items-center px-2.5 py-1 rounded-md text-[11px] font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
                          Sem Dano
                        </span>
                      }
                    </td>

                    <!-- Nome -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="font-bold text-slate-900 text-sm max-w-xs truncate" [title]="sev.name">
                        {{ sev.name }}
                      </div>
                    </td>

                    <!-- Definição NCC MERP -->
                    <td class="px-6 py-4 max-w-md truncate text-slate-500 text-xs" [title]="sev.description">
                      {{ sev.description }}
                    </td>

                    <!-- Status -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span [ngClass]="sev.isActive ? 'badge-active' : 'badge-inactive'">
                        {{ sev.isActive ? 'Ativa' : 'Inativa' }}
                      </span>
                    </td>

                    <!-- Ações -->
                    <td class="px-6 py-4 whitespace-nowrap text-right space-x-1">
                      <!-- Editar -->
                      <button
                        type="button"
                        (click)="openEditModal(sev)"
                        class="p-1.5 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Editar categoria"
                        aria-label="Editar"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>

                      <!-- Toggle Status -->
                      <button
                        type="button"
                        (click)="openStatusDialog(sev)"
                        class="p-1.5 text-slate-500 hover:text-amber-600 hover:bg-slate-100 rounded-lg transition-colors"
                        [title]="sev.isActive ? 'Inativar categoria' : 'Ativar categoria'"
                        aria-label="Alterar status"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                        </svg>
                      </button>

                      <!-- Excluir -->
                      <button
                        type="button"
                        (click)="openDeleteDialog(sev)"
                        class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Excluir categoria"
                        aria-label="Excluir"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>

        <!-- Paginador Padronizado -->
        <app-data-table-pagination
          [pageData]="pageData()"
          [loading]="isLoading()"
          (pageChange)="onPageChange($event)"
        />
      </div>
    </div>

    <!-- Modal de Cadastro / Edição -->
    <app-severity-form-modal
      [isOpen]="isFormModalOpen()"
      [severity]="selectedSeverity()"
      (saved)="onSeveritySaved()"
      (closed)="isFormModalOpen.set(false)"
    />

    <!-- Diálogo de Status -->
    <app-confirm-dialog
      [isOpen]="isStatusDialogOpen()"
      [title]="targetSeverity()?.isActive ? 'Inativar Categoria' : 'Ativar Categoria'"
      [message]="
        targetSeverity()?.isActive
          ? 'Tem certeza de que deseja inativar a Categoria ' + targetSeverity()?.categoryLetter + ' - ' + targetSeverity()?.name + '?'
          : 'Deseja reativar a Categoria ' + targetSeverity()?.categoryLetter + ' - ' + targetSeverity()?.name + '?'
      "
      [confirmText]="targetSeverity()?.isActive ? 'Inativar' : 'Ativar'"
      [isDestructive]="!!targetSeverity()?.isActive"
      (confirmed)="onConfirmStatusChange()"
      (cancelled)="isStatusDialogOpen.set(false)"
    />

    <!-- Diálogo de Exclusão -->
    <app-confirm-dialog
      [isOpen]="isDeleteDialogOpen()"
      title="Excluir Categoria de Gravidade"
      [message]="
        'Esta ação é irreversível. Deseja realmente excluir permanentemente a Categoria ' +
        targetSeverity()?.categoryLetter +
        ' (' +
        targetSeverity()?.name +
        ')?'
      "
      confirmText="Excluir Categoria"
      [isDestructive]="true"
      (confirmed)="onConfirmDelete()"
      (cancelled)="isDeleteDialogOpen.set(false)"
    />
  `,
})
export class SeverityListComponent implements OnInit {
  private readonly severityService = inject(HarmSeverityService);
  private readonly toastService = inject(ToastService);

  readonly severities = signal<HarmSeverity[]>([]);
  readonly pageData = signal<PageResponse<HarmSeverity> | null>(null);
  readonly isLoading = signal<boolean>(false);

  // Estados dos Modais
  readonly isFormModalOpen = signal<boolean>(false);
  readonly selectedSeverity = signal<HarmSeverity | null>(null);

  readonly isStatusDialogOpen = signal<boolean>(false);
  readonly isProcessingStatus = signal<boolean>(false);

  readonly isDeleteDialogOpen = signal<boolean>(false);
  readonly isDeleting = signal<boolean>(false);

  readonly targetSeverity = signal<HarmSeverity | null>(null);

  // Filtros
  searchQuery: string = '';
  selectedHarmFilter: boolean | null = null;
  selectedStatus: boolean | null = null;

  private currentPage = 0;
  private readonly pageSize = 10;
  private searchTimeout: any;

  ngOnInit(): void {
    this.loadSeverities();
  }

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

  onSearchChange(): void {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => {
      this.loadSeverities(0);
    }, 300);
  }

  onFilterChange(): void {
    this.loadSeverities(0);
  }

  onPageChange(page: number): void {
    this.loadSeverities(page);
  }

  openCreateModal(): void {
    this.selectedSeverity.set(null);
    this.isFormModalOpen.set(true);
  }

  openEditModal(sev: HarmSeverity): void {
    this.selectedSeverity.set(sev);
    this.isFormModalOpen.set(true);
  }

  onSeveritySaved(): void {
    this.isFormModalOpen.set(false);
    this.loadSeverities(this.currentPage);
  }

  openStatusDialog(sev: HarmSeverity): void {
    this.targetSeverity.set(sev);
    this.isStatusDialogOpen.set(true);
  }

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

  openDeleteDialog(sev: HarmSeverity): void {
    this.targetSeverity.set(sev);
    this.isDeleteDialogOpen.set(true);
  }

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
