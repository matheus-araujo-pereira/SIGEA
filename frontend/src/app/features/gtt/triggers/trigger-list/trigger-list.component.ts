import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { TriggerFormComponent } from '../trigger-form/trigger-form.component';

/**
 * Tela administrativa para gestão e listagem paginada (10 itens) de Gatilhos GTT.
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
  private readonly triggerService = inject(GttTriggerService);
  private readonly moduleService = inject(GttModuleService);
  private readonly toastService = inject(ToastService);

  readonly triggers = signal<GttTrigger[]>([]);
  readonly availableModules = signal<GttModule[]>([]);
  readonly pageData = signal<PageResponse<GttTrigger> | null>(null);
  readonly isLoading = signal<boolean>(false);

  readonly isFormModalOpen = signal<boolean>(false);
  readonly isStatusDialogOpen = signal<boolean>(false);
  readonly isDeleteDialogOpen = signal<boolean>(false);

  readonly selectedTrigger = signal<GttTrigger | null>(null);
  readonly targetTrigger = signal<GttTrigger | null>(null);

  searchQuery = '';
  selectedModuleId = '';
  selectedStatus: boolean | null = null;
  currentPage = 0;

  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadModules();
    this.loadTriggers();
  }

  loadModules(): void {
    this.moduleService.getCatalog().subscribe({
      next: (res) => this.availableModules.set(res.data),
    });
  }

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

  onSearchChange(): void {
    if (this.searchDebounceTimeout) {
      clearTimeout(this.searchDebounceTimeout);
    }
    this.searchDebounceTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadTriggers();
    }, 350);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadTriggers();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadTriggers();
  }

  openCreateModal(): void {
    this.selectedTrigger.set(null);
    this.isFormModalOpen.set(true);
  }

  openEditModal(trigger: GttTrigger): void {
    this.selectedTrigger.set(trigger);
    this.isFormModalOpen.set(true);
  }

  onTriggerSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadTriggers();
  }

  openStatusDialog(trigger: GttTrigger): void {
    this.targetTrigger.set(trigger);
    this.isStatusDialogOpen.set(true);
  }

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

  openDeleteDialog(trigger: GttTrigger): void {
    this.targetTrigger.set(trigger);
    this.isDeleteDialogOpen.set(true);
  }

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
