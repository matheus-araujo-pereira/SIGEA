import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
import { AcademicClassResponseDTO } from '../../../../core/models/academic-class.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ClassFormComponent } from '../class-form/class-form.component';

/**
 * Listagem paginada e gestão de Turmas Acadêmicas (Admin, Docente e Estudante).
 */
@Component({
  selector: 'app-class-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    DataTablePaginationComponent,
    ConfirmDialogComponent,
    ClassFormComponent,
  ],
  templateUrl: './class-list.component.html',
})
export class ClassListComponent implements OnInit {
  private readonly classService = inject(AcademicClassService);
  private readonly toast = inject(ToastService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly classes = signal<AcademicClassResponseDTO[]>([]);
  readonly pageData = signal<PageResponse<AcademicClassResponseDTO> | null>(null);
  readonly currentPage = signal(0);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 10;

  // Modais
  readonly isFormModalOpen = signal(false);
  readonly selectedClassId = signal<string | null>(null);

  readonly isConfirmDialogOpen = signal(false);
  readonly confirmDialogTitle = signal('');
  readonly confirmDialogMessage = signal('');
  readonly confirmDialogActionText = signal('Confirmar');
  readonly confirmDialogIsDestructive = signal(false);
  private pendingAction: (() => void) | null = null;

  searchQuery = '';
  selectedStatus: boolean | null = null;

  get isAdmin(): boolean {
    return this.auth.hasRole(['ADMIN']);
  }

  get isProfessor(): boolean {
    return this.auth.hasRole(['PROFESSOR']);
  }

  get isMyClassesView(): boolean {
    return this.router.url.includes('my-classes') || !this.isAdmin;
  }

  ngOnInit(): void {
    this.loadClasses();
  }

  loadClasses(): void {
    if (this.isMyClassesView) {
      this.classService.getMyClasses(this.currentPage(), this.pageSize).subscribe({
        next: (res) => this.handlePageResponse(res.data),
        error: () => this.toast.error('Erro ao carregar turmas.'),
      });
    } else {
      this.classService
        .listClasses(
          this.searchQuery,
          undefined,
          this.selectedStatus,
          this.currentPage(),
          this.pageSize
        )
        .subscribe({
          next: (res) => this.handlePageResponse(res.data),
          error: () => this.toast.error('Erro ao carregar turmas.'),
        });
    }
  }

  private handlePageResponse(page: PageResponse<AcademicClassResponseDTO>): void {
    this.pageData.set(page);
    this.classes.set(page.content);
    this.currentPage.set(page.page);
    this.totalElements.set(page.totalElements);
    this.totalPages.set(page.totalPages);
  }

  onSearchChange(): void {
    this.currentPage.set(0);
    this.loadClasses();
  }

  onFilterChange(): void {
    this.currentPage.set(0);
    this.loadClasses();
  }

  onPageChange(newPage: number): void {
    this.currentPage.set(newPage);
    this.loadClasses();
  }

  viewClassDetail(id: string): void {
    this.router.navigate(['/academic/classes', id]);
  }

  viewDashboard(id: string): void {
    this.router.navigate(['/academic/classes', id, 'dashboard']);
  }

  openCreateModal(): void {
    this.selectedClassId.set(null);
    this.isFormModalOpen.set(true);
  }

  openEditModal(id: string): void {
    this.selectedClassId.set(id);
    this.isFormModalOpen.set(true);
  }

  closeFormModal(): void {
    this.isFormModalOpen.set(false);
    this.selectedClassId.set(null);
  }

  onClassSaved(): void {
    this.closeFormModal();
    this.loadClasses();
  }

  confirmToggleStatus(clazz: AcademicClassResponseDTO): void {
    const isClosing = !clazz.isClosed;
    this.confirmDialogTitle.set(isClosing ? 'Encerrar Turma Acadêmica' : 'Reabrir Turma Acadêmica');
    this.confirmDialogMessage.set(
      isClosing
        ? `Tem certeza que deseja encerrar a turma "${clazz.formattedName}"? Turmas encerradas não permitem criação nem resolução de novas atividades e só podem ser fechadas se não houver correções pendentes.`
        : `Deseja reabrir a turma "${clazz.formattedName}" para novas atividades e resoluções?`
    );
    this.confirmDialogActionText.set(isClosing ? 'Encerrar Turma' : 'Reabrir Turma');
    this.confirmDialogIsDestructive.set(isClosing);

    this.pendingAction = () => {
      this.classService.updateStatus(clazz.id, isClosing).subscribe({
        next: (res) => {
          this.toast.success(isClosing ? 'Turma encerrada com sucesso!' : 'Turma reaberta com sucesso!');
          this.loadClasses();
        },
        error: (err) => {
          this.toast.error(err?.error?.message || 'Erro ao alterar status da turma.');
        },
      });
    };

    this.isConfirmDialogOpen.set(true);
  }

  confirmDelete(clazz: AcademicClassResponseDTO): void {
    this.confirmDialogTitle.set('Excluir Turma Acadêmica');
    this.confirmDialogMessage.set(
      `ATENÇÃO: Deseja realmente excluir a turma "${clazz.formattedName}"? Todas as atividades, casos clínicos e resoluções vinculadas serão permanentemente removidos.`
    );
    this.confirmDialogActionText.set('Excluir Definitivamente');
    this.confirmDialogIsDestructive.set(true);

    this.pendingAction = () => {
      this.classService.deleteClass(clazz.id).subscribe({
        next: () => {
          this.toast.success('Turma acadêmica excluída com sucesso!');
          this.loadClasses();
        },
        error: (err) => {
          this.toast.error(err?.error?.message || 'Erro ao excluir turma.');
        },
      });
    };

    this.isConfirmDialogOpen.set(true);
  }

  executeConfirmedAction(): void {
    if (this.pendingAction) {
      this.pendingAction();
      this.pendingAction = null;
    }
    this.isConfirmDialogOpen.set(false);
  }

  closeConfirmDialog(): void {
    this.isConfirmDialogOpen.set(false);
    this.pendingAction = null;
  }
}
