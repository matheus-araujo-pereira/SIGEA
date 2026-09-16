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
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho com Título e Botão de Ação -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">Gestão de Usuários</h2>
          <p class="text-xs text-slate-500 mt-1">
            Controle de perfis, cadastros institucionais e permissões de acesso ao SIGEA-GTT
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
          <span>Novo Usuário</span>
        </button>
      </div>

      <!-- Barra de Filtros de Pesquisa -->
      <div class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row items-center gap-4">
        <!-- Campo Busca -->
        <div class="relative flex-1 w-full">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Buscar por nome, e-mail institucional ou matrícula..."
            class="w-full pl-9 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Filtro Perfil -->
        <div class="w-full md:w-48">
          <select
            [(ngModel)]="selectedRole"
            (ngModelChange)="onFilterChange()"
            class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option value="">Todos os Perfis</option>
            <option value="ADMIN">ADMINISTRADOR</option>
            <option value="PROFESSOR">PROFESSOR</option>
            <option value="STUDENT">ESTUDANTE</option>
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
            <option [ngValue]="true">Apenas Ativos</option>
            <option [ngValue]="false">Apenas Inativos</option>
          </select>
        </div>
      </div>

      <!-- Tabela de Dados Padronizada com 10 Registros -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
            <thead class="bg-slate-50/80 text-slate-500 font-bold uppercase tracking-wider text-[11px]">
              <tr>
                <th scope="col" class="px-6 py-3.5">Nome Completo</th>
                <th scope="col" class="px-6 py-3.5">E-mail Institucional</th>
                <th scope="col" class="px-6 py-3.5">Perfil</th>
                <th scope="col" class="px-6 py-3.5">Matrícula</th>
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
                      <span>Carregando usuários...</span>
                    </div>
                  </td>
                </tr>
              } @else if (users().length === 0) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-slate-400">
                    Nenhum usuário encontrado para os filtros selecionados.
                  </td>
                </tr>
              } @else {
                @for (user of users(); track user.id) {
                  <tr class="hover:bg-slate-50/70 transition-colors">
                    <!-- Nome -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="font-bold text-slate-900 text-sm">{{ user.fullName }}</div>
                    </td>

                    <!-- E-mail -->
                    <td class="px-6 py-4 whitespace-nowrap font-mono text-xs text-slate-600">
                      {{ user.email }}
                    </td>

                    <!-- Perfil -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span [ngClass]="getRoleBadgeClasses(user.role)">
                        {{ user.role }}
                      </span>
                    </td>

                    <!-- Matrícula -->
                    <td class="px-6 py-4 whitespace-nowrap font-mono text-xs text-slate-600">
                      {{ user.registrationNumber || '-' }}
                    </td>

                    <!-- Status -->
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span [ngClass]="user.isActive ? 'badge-active' : 'badge-inactive'">
                        {{ user.isActive ? 'Ativo' : 'Inativo' }}
                      </span>
                    </td>

                    <!-- Ações -->
                    <td class="px-6 py-4 whitespace-nowrap text-right space-x-1">
                      <!-- Editar -->
                      <button
                        type="button"
                        (click)="openEditModal(user)"
                        class="p-1.5 text-slate-500 hover:text-clinical-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Editar usuário"
                        aria-label="Editar"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>

                      <!-- Redefinir Senha -->
                      <button
                        type="button"
                        (click)="openResetPasswordDialog(user)"
                        class="p-1.5 text-slate-500 hover:text-indigo-600 hover:bg-slate-100 rounded-lg transition-colors"
                        title="Redefinir senha para Sigea@123456"
                        aria-label="Redefinir senha"
                      >
                        <!-- Ícone de chave -->
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                        </svg>
                      </button>

                      <!-- Toggle Ativar/Inativar -->
                      <button
                        type="button"
                        (click)="openStatusDialog(user)"
                        [disabled]="isSelf(user)"
                        class="p-1.5 rounded-lg transition-colors disabled:opacity-20 disabled:cursor-not-allowed"
                        [ngClass]="user.isActive
                          ? 'text-slate-500 hover:text-amber-600 hover:bg-slate-100'
                          : 'text-slate-500 hover:text-emerald-600 hover:bg-slate-100'"
                        [title]="user.isActive ? 'Inativar usuário' : 'Ativar usuário'"
                        aria-label="Alterar status"
                      >
                        <!-- Ícone BAN (inativar) quando ativo -->
                        @if (user.isActive) {
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                          </svg>
                        } @else {
                          <!-- Ícone CHECK-CIRCLE (ativar) quando inativo -->
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                        }
                      </button>

                      <!-- Excluir -->
                      <button
                        type="button"
                        (click)="openDeleteDialog(user)"
                        [disabled]="isSelf(user)"
                        class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-lg transition-colors disabled:opacity-20 disabled:cursor-not-allowed"
                        title="Excluir usuário"
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

    <!-- Modal de Criação / Edição -->
    <app-user-form-modal
      [isOpen]="isFormModalOpen()"
      [user]="selectedUser()"
      (saved)="onUserSaved()"
      (closed)="isFormModalOpen.set(false)"
    />

    <!-- Diálogo de Confirmação de Redefinição de Senha -->
    <app-confirm-dialog
      [isOpen]="isResetPasswordDialogOpen()"
      title="Redefinir Senha"
      [message]="'Tem certeza de que deseja redefinir a senha de ' + targetUser()?.fullName + ' para a senha padrão Sigea@123456? O usuário será obrigado a trocar a senha no próximo acesso.'"
      confirmText="Redefinir Senha"
      [isDestructive]="false"
      (confirmed)="onConfirmResetPassword()"
      (cancelled)="isResetPasswordDialogOpen.set(false)"
    />

    <!-- Diálogo de Confirmação de Status (Ativar / Inativar) -->
    <app-confirm-dialog
      [isOpen]="isStatusDialogOpen()"
      [title]="targetUser()?.isActive ? 'Inativar Usuário' : 'Ativar Usuário'"
      [message]="
        targetUser()?.isActive
          ? 'Tem certeza de que deseja inativar o usuário ' + targetUser()?.fullName + '? Ele não conseguirá mais acessar a plataforma.'
          : 'Deseja reativar o acesso de ' + targetUser()?.fullName + '?'
      "
      [confirmText]="targetUser()?.isActive ? 'Inativar' : 'Ativar'"
      [isDestructive]="!!targetUser()?.isActive"
      (confirmed)="onConfirmStatusChange()"
      (cancelled)="isStatusDialogOpen.set(false)"
    />

    <!-- Diálogo de Confirmação de Exclusão -->
    <app-confirm-dialog
      [isOpen]="isDeleteDialogOpen()"
      title="Excluir Usuário"
      [message]="
        'Atenção: Tem certeza de que deseja excluir permanentemente o usuário ' +
        targetUser()?.fullName +
        ' (' +
        targetUser()?.email +
        ')? Esta ação é irreversível.'
      "
      confirmText="Excluir Permanentemente"
      [isDestructive]="true"
      (confirmed)="onConfirmDelete()"
      (cancelled)="isDeleteDialogOpen.set(false)"
    />
  `,
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
