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
  templateUrl: './severity-list.component.html',
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
