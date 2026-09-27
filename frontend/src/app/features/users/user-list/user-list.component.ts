import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { User, UserRole } from '../../../core/models/user.model';
import { PageResponse } from '../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { UserFormComponent } from '../user-form/user-form.component';

/**
 * Tela administrativa para listagem paginada (10 itens), busca e gestão completa de usuários.
 */
@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataTablePaginationComponent,
    ConfirmDialogComponent,
    UserFormComponent,
  ],
  templateUrl: './user-list.component.html',
})
export class UserListComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);

  readonly users = signal<User[]>([]);
  readonly pageData = signal<PageResponse<User> | null>(null);
  readonly isLoading = signal<boolean>(false);

  // Estados dos modais
  readonly isFormModalOpen = signal<boolean>(false);
  readonly isStatusDialogOpen = signal<boolean>(false);
  readonly isDeleteDialogOpen = signal<boolean>(false);
  readonly isResetPasswordDialogOpen = signal<boolean>(false);

  readonly selectedUser = signal<User | null>(null);
  readonly targetUser = signal<User | null>(null);

  // Filtros
  searchQuery = '';
  selectedRole: UserRole | '' = '';
  selectedStatus: boolean | null = null;
  currentPage = 0;

  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.userService
      .listUsers(
        this.searchQuery,
        this.selectedRole,
        this.selectedStatus,
        this.currentPage,
        10,
        'fullName,asc'
      )
      .subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.pageData.set(response.data);
          this.users.set(response.data.content);
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
      this.loadUsers();
    }, 350);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadUsers();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadUsers();
  }

  openCreateModal(): void {
    this.selectedUser.set(null);
    this.isFormModalOpen.set(true);
  }

  openEditModal(user: User): void {
    this.selectedUser.set(user);
    this.isFormModalOpen.set(true);
  }

  onUserSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadUsers();
  }

  openStatusDialog(user: User): void {
    this.targetUser.set(user);
    this.isStatusDialogOpen.set(true);
  }

  onConfirmStatusChange(): void {
    const user = this.targetUser();
    if (!user) return;

    this.isStatusDialogOpen.set(false);
    const newStatus = !user.isActive;

    this.userService.updateStatus(user.id, { isActive: newStatus }).subscribe({
      next: () => {
        this.toastService.success(
          'Sucesso',
          `Usuário ${user.fullName} ${newStatus ? 'ativado' : 'inativado'} com sucesso.`
        );
        this.loadUsers();
      },
    });
  }

  openResetPasswordDialog(user: User): void {
    this.targetUser.set(user);
    this.isResetPasswordDialogOpen.set(true);
  }

  onConfirmResetPassword(): void {
    const user = this.targetUser();
    if (!user) return;

    this.isResetPasswordDialogOpen.set(false);
    this.userService.resetPassword(user.id).subscribe({
      next: () => {
        this.toastService.success(
          'Senha Redefinida',
          `Senha de ${user.fullName} redefinida para Sigea@123456. O usuário deverá trocá-la no próximo acesso.`
        );
        this.loadUsers();
      },
    });
  }

  openDeleteDialog(user: User): void {
    this.targetUser.set(user);
    this.isDeleteDialogOpen.set(true);
  }

  onConfirmDelete(): void {
    const user = this.targetUser();
    if (!user) return;

    this.isDeleteDialogOpen.set(false);
    this.userService.deleteUser(user.id).subscribe({
      next: () => {
        this.toastService.success('Sucesso', `Usuário ${user.fullName} excluído com sucesso.`);
        this.loadUsers();
      },
    });
  }

  isSelf(user: User): boolean {
    return user.id === this.authService.currentUser()?.id;
  }

  getRoleBadgeClasses(role: UserRole): string {
    switch (role) {
      case 'ADMIN':
        return 'badge-admin';
      case 'PROFESSOR':
        return 'badge-professor';
      case 'STUDENT':
        return 'badge-student';
    }
  }
}
