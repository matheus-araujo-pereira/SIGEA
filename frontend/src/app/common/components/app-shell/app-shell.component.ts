import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

/**
 * Shell estrutural da aplicação hospitalar SIGEA com menu retrátil ergonômico,
 * contenção para monitores Ultrawide (21:9) e cabeçalho de perfil de usuário.
 */
@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, ConfirmDialogComponent],
  templateUrl: './app-shell.component.html',
})
export class AppShellComponent {
  /**
   * Instância injetada do serviço de autenticação e sessão.
   */
  readonly authService = inject(AuthService);
  /** Instância injetada do roteador SPA */
  private readonly router = inject(Router);

  /** Chave de armazenamento no localStorage para persistência do estado da barra lateral */
  private readonly STORAGE_KEY = 'sigea_sidebar_collapsed';

  /**
   * Signal reativo indicando se a barra lateral de navegação está colapsada.
   */
  readonly isCollapsed = signal<boolean>(localStorage.getItem(this.STORAGE_KEY) === 'true');

  /**
   * Signal reativo controlando a exibição do modal de confirmação de logout.
   */
  readonly showLogoutDialog = signal<boolean>(false);

  /**
   * Alterna a visibilidade e largura da barra lateral (expandida ou recolhida)
   * e persiste o estado na preferência do usuário via localStorage.
   *
   * @returns void
   */
  toggleCollapse(): void {
    const nextState = !this.isCollapsed();
    this.isCollapsed.set(nextState);
    localStorage.setItem(this.STORAGE_KEY, String(nextState));
  }

  /**
   * Retorna as iniciais do nome do usuário autenticado para exibição no avatar.
   *
   * @returns String com uma ou duas letras maiúsculas.
   */
  userInitials(): string {
    const name = this.authService.currentUser()?.fullName;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

  /**
   * Retorna as classes CSS utilitárias para renderizar a badge do perfil (RBAC)
   * com semiótica hospitalar (Roxo para ADMIN, Azul para PROFESSOR, Verde para STUDENT).
   *
   * @returns Classes utilitárias TailwindCSS.
   */
  getRoleBadgeClasses(): string {
    const role = this.authService.currentUser()?.role;
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-800 border border-purple-200';
      case 'PROFESSOR':
        return 'bg-blue-100 text-blue-800 border border-blue-200';
      case 'STUDENT':
        return 'bg-emerald-100 text-emerald-800 border border-emerald-200';
      default:
        return 'bg-slate-100 text-slate-800';
    }
  }

  /**
   * Abre o modal de confirmação para encerramento de sessão.
   *
   * @returns void
   */
  openLogoutDialog(): void {
    this.showLogoutDialog.set(true);
  }

  /**
   * Executa a confirmação de logout e desconecta o usuário.
   *
   * @returns void
   */
  onLogoutConfirm(): void {
    this.showLogoutDialog.set(false);
    this.authService.logout();
  }
}
