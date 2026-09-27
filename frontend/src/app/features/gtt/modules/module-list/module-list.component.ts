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
  templateUrl: './module-list.component.html',
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
