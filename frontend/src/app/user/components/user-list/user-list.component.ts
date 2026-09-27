import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../../common/services/toast.service';
import { AuthService } from '../../../auth/services/auth.service';
import { User, UserRole } from '../../models/user.model';
import { PageResponse } from '../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../common/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog/confirm-dialog.component';
import { UserFormComponent } from '../user-form/user-form.component';

/**
 * Componente de tela única para gestão administrativa de usuários do SIGEA.
 *
 * Exibe listagem paginada (exatamente 10 registros por página), filtros acumulativos
 * por busca textual, perfil RBAC e status ativo/inativo, além de ações de ativação,
 * redefinição de senha e exclusão.
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
  /** Serviço de gestão de usuários */
  private readonly userService = inject(UserService);
  /** Serviço de autenticação e RBAC */
  private readonly authService = inject(AuthService);
  /** Serviço de notificações visuais */
  private readonly toastService = inject(ToastService);

  /** Signal reativo com os usuários exibidos na página atual. */
  readonly users = signal<User[]>([]);
  /** Signal reativo com os metadados da página atual (PageResponse). */
  readonly pageData = signal<PageResponse<User> | null>(null);
  /** Signal reativo indicando estado de carregamento assíncrono. */
  readonly isLoading = signal<boolean>(false);

  /** Controle de visibilidade do modal de formulário de usuário. */
  readonly isFormModalOpen = signal<boolean>(false);
  /** Controle de visibilidade do modal de alteração de status ativo/inativo. */
  readonly isStatusDialogOpen = signal<boolean>(false);
  /** Controle de visibilidade do modal de confirmação de exclusão. */
  readonly isDeleteDialogOpen = signal<boolean>(false);
  /** Controle de visibilidade do modal de redefinição de senha. */
  readonly isResetPasswordDialogOpen = signal<boolean>(false);

  /** Usuário selecionado para edição (null se novo cadastro). */
  readonly selectedUser = signal<User | null>(null);
  /** Usuário alvo de ações contextuais (status, reset de senha, exclusão). */
  readonly targetUser = signal<User | null>(null);

  /** Termo de busca textual (filtro). */
  searchQuery = '';
  /** Perfil selecionado no filtro. */
  selectedRole: UserRole | '' = '';
  /** Status ativo/inativo selecionado no filtro. */
  selectedStatus: boolean | null = null;
  /** Página atual selecionada (0-based). */
  currentPage = 0;

  /** Identificador do temporizador para debounce da busca textual */
  private searchDebounceTimeout: ReturnType<typeof setTimeout> | null = null;

  /** Inicialização: dispara a carga da primeira página de usuários */
  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Consulta a API do backend buscando a lista paginada de usuários com filtros aplicados.
   *
   * @returns void
   */
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

  /**
   * Executa busca textual com debounce de 350ms para evitar requisições redundantes.
   *
   * @returns void
   */
  onSearchChange(): void {
    if (this.searchDebounceTimeout) {
      clearTimeout(this.searchDebounceTimeout);
    }
    this.searchDebounceTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadUsers();
    }, 350);
  }

  /**
   * Dispara a recarga ao alterar filtros de perfil ou status.
   *
   * @returns void
   */
  onFilterChange(): void {
    this.currentPage = 0;
    this.loadUsers();
  }

  /**
   * Manipula a alteração de página vinda do componente de paginação.
   *
   * @param page Novo índice de página selecionado (0-based).
   * @returns void
   */
  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadUsers();
  }

  /**
   * Abre o modal para cadastro de novo usuário.
   *
   * @returns void
   */
  openCreateModal(): void {
    this.selectedUser.set(null);
    this.isFormModalOpen.set(true);
  }

  /**
   * Abre o modal para edição cadastral do usuário selecionado.
   *
   * @param user Usuário a editar.
   * @returns void
   */
  openEditModal(user: User): void {
    this.selectedUser.set(user);
    this.isFormModalOpen.set(true);
  }

  /**
   * Callback invocado após persistência bem-sucedida de usuário no modal.
   *
   * @returns void
   */
  onUserSaved(): void {
    this.isFormModalOpen.set(false);
    this.loadUsers();
  }

  /**
   * Abre o diálogo de confirmação para alteração de status ativo/inativo.
   *
   * @param user Usuário alvo.
   * @returns void
   */
  openStatusDialog(user: User): void {
    this.targetUser.set(user);
    this.isStatusDialogOpen.set(true);
  }

  /**
   * Confirma e executa a alteração do status ativo/inativo do usuário alvo.
   *
   * @returns void
   */
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

  /**
   * Abre o diálogo de confirmação para redefinição emergencial de senha.
   *
   * @param user Usuário alvo.
   * @returns void
   */
  openResetPasswordDialog(user: User): void {
    this.targetUser.set(user);
    this.isResetPasswordDialogOpen.set(true);
  }

  /**
   * Confirma e executa a redefinição da senha do usuário para o padrão institucional.
   *
   * @returns void
   */
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

  /**
   * Abre o diálogo de confirmação para exclusão definitiva de usuário.
   *
   * @param user Usuário alvo.
   * @returns void
   */
  openDeleteDialog(user: User): void {
    this.targetUser.set(user);
    this.isDeleteDialogOpen.set(true);
  }

  /**
   * Confirma e executa a exclusão definitiva do usuário na base de dados.
   *
   * @returns void
   */
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

  /**
   * Verifica se o registro de usuário corresponde ao próprio usuário logado na sessão ativa.
   *
   * @param user Usuário a comparar.
   * @returns Booleano.
   */
  isSelf(user: User): boolean {
    return user.id === this.authService.currentUser()?.id;
  }

  /**
   * Retorna a classe de estilização da badge de perfil com semiótica hospitalar.
   *
   * @param role Perfil do usuário.
   * @returns Classes CSS utilitárias.
   */
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
