import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ModuleFormComponent } from '../module-form/module-form.component';

/**
 * Tela administrativa para gestão e listagem paginada (10 itens) de Módulos GTT.
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
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Módulos do IHI-GTT</h2>
          <p class="text-xs text-slate-500 mt-1">
            Gestão dos eixos assistenciais e parametrização dos módulos da metodologia Global Trigger Tool
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
          <span>Novo Módulo</span>
        </button>
      </div>

      <!-- Filtros de Busca -->
      <div class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row items-center gap-4">
        <!-- Campo Busca -->
        <div class="relative flex-1 w-full">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Buscar por código ou nome do módulo..."
            class="w-full pl-9 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Filtro Status -->
        <div class="w-full md:w-44">
          <select
            [(ngModel)]="selectedStatus"
            (ngModelChange)="onFilterChange()"
            class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option [ngValue]="null">Todos os Status</option>
            <option [ngValue]="true">Apenas Ativos</option>
            <option [ngValue]="false">Apenas Inativos</option>
          </select>
        </div>
      </div>

      <!-- Tabela Padronizada -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
            <thead class="bg-slate-50/80 text-slate-500 font-bold uppercase tracking-wider text-[11px]">
              <tr>
                <th scope="col" class="px-6 py-3.5 w-24">Código</th>
                <th scope="col" class="px-6 py-3.5">Nome do Módulo</th>
                <th scope="col" class="px-6 py-3.5">Descrição</th>
                <th scope="col" class="px-6 py-3.5 text-center">Gatilhos</th>
                <th scope="col" class="px-6 py-3.5">Status</th>
                <th scope="col" class="px-6 py-3.5 text-right">Ações</th>
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
                      <span>Carregando módulos...</span>
                    </div>
                  </td>
                </tr>
              } @else if (modules().length === 0) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-slate-400">
                    Nenhum módulo GTT encontrado para os filtros selecionados.
                  </td>
                </tr>
              } @else {
                @for (mod of modules(); track mod.id) {
                  <tr class="hover:bg-slate-50/70 transition-colors">
                    <!-- Código -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="inline-flex items-center justify-center min-w-[32px] px-2.5 py-1 rounded-lg bg-clinical-50 text-clinical-700 font-black text-xs border border-clinical-200 uppercase tracking-wider">
                        {{ mod.code }}
                      </span>
                    </td>

                    <!-- Nome -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="font-bold text-slate-900 text-sm">{{ mod.name }}</div>
                    </td>

                    <!-- Descrição -->
                    <td class="px-6 py-4 max-w-md truncate text-slate-500 text-xs" [title]="mod.description || ''">
                      {{ mod.description || 'Sem descrição cadastrada.' }}
                    </td>

                    <!-- Total de Gatilhos -->
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-800">
                        {{ mod.triggerCount ?? 0 }} gatilhos
                      </span>
                    </td>

                    <!-- Status -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span [ngClass]="mod.isActive ? 'badge-active' : 'badge-inactive'">
                        {{ mod.isActive ? 'Ativo' : 'Inativo' }}
                      </span>
                    </td>

                    <!-- Ações -->
                    <td class="px-6 py-4 whitespace-nowrap text-right space-x-1">
                      <!-- Editar -->
                      <button
                        type="button"
                        (click)="openEditModal(mod)"
                        class="p-1.5 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Editar módulo"
                        aria-label="Editar"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>

                      <!-- Toggle Ativar/Inativar -->
                      <button
                        type="button"
                        (click)="openStatusDialog(mod)"
                        class="p-1.5 text-slate-500 hover:text-amber-600 hover:bg-slate-100 rounded-lg transition-colors"
                        [title]="mod.isActive ? 'Inativar módulo' : 'Ativar módulo'"
                        aria-label="Alterar status"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                        </svg>
                      </button>

                      <!-- Excluir -->
                      <button
                        type="button"
                        (click)="openDeleteDialog(mod)"
                        class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Excluir módulo"
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
    <app-module-form-modal
      [isOpen]="isFormModalOpen()"
      [module]="selectedModule()"
      (saved)="onModuleSaved()"
      (closed)="isFormModalOpen.set(false)"
    />

    <!-- Confirmação de Status -->
    <app-confirm-dialog
      [isOpen]="isStatusDialogOpen()"
      [title]="targetModule()?.isActive ? 'Inativar Módulo' : 'Ativar Módulo'"
      [message]="
        targetModule()?.isActive
          ? 'Tem certeza de que deseja inativar o módulo ' + targetModule()?.name + ' (' + targetModule()?.code + ')?'
          : 'Deseja reativar o módulo ' + targetModule()?.name + ' (' + targetModule()?.code + ')?'
      "
      [confirmText]="targetModule()?.isActive ? 'Inativar' : 'Ativar'"
      [isDestructive]="!!targetModule()?.isActive"
      (confirmed)="onConfirmStatusChange()"
      (cancelled)="isStatusDialogOpen.set(false)"
    />

    <!-- Confirmação de Exclusão -->
    <app-confirm-dialog
      [isOpen]="isDeleteDialogOpen()"
      title="Excluir Módulo GTT"
      [message]="
        'Atenção: Tem certeza de que deseja excluir permanentemente o módulo ' +
        targetModule()?.name +
        ' (' + targetModule()?.code + ')? Esta ação só é permitida se não houver gatilhos vinculados.'
      "
      confirmText="Excluir Permanentemente"
      [isDestructive]="true"
      (confirmed)="onConfirmDelete()"
      (cancelled)="isDeleteDialogOpen.set(false)"
    />
  `,
})
export class ModuleListComponent implements OnInit {
  private readonly moduleService = inject(GttModuleService);
  private readonly toastService = inject(ToastService);

  readonly modules = signal<GttModule[]>([]);
  readonly pageData = signal<PageResponse<GttModule> | null>(null);
  readonly isLoading = signal<boolean>(false);

  readonly isFormModalOpen = signal<boolean>(false);
  readonly isStatusDialogOpen = signal<boolean>(false);
  readonly isDeleteDialogOpen = signal<boolean>(false);

  readonly selectedModule = signal<GttModule | null>(null);
  readonly targetModule = signal<GttModule | null>(null);

  searchQuery = '';
  selectedStatus: boolean | null = null;
  currentPage = 0;

  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadModules();
  }

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

  onSearchChange(): void {
    if (this.searchDebounceTimeout) {
      clearTimeout(this.searchDebounceTimeout);
    }
    this.searchDebounceTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadModules();
    }, 350);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadModules();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadModules();
  }

  openCreateModal(): void {
    this.selectedModule.set(null);
    this.isFormModalOpen.set(true);
  }

  openEditModal(module: GttModule): void {
    this.selectedModule.set(module);
    this.isFormModalOpen.set(true);
  }

  onModuleSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadModules();
  }

  openStatusDialog(module: GttModule): void {
    this.targetModule.set(module);
    this.isStatusDialogOpen.set(true);
  }

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

  openDeleteDialog(module: GttModule): void {
    this.targetModule.set(module);
    this.isDeleteDialogOpen.set(true);
  }

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
