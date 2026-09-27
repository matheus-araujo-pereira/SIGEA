import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

/**
 * Shell estrutural da aplicação hospitalar com menu retrátil ergonômico e suporte a telas Ultrawide.
 */
@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, ConfirmDialogComponent],
  templateUrl: './app-shell.component.html',
})
export class AppShellComponent {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  private readonly STORAGE_KEY = 'sigea_sidebar_collapsed';

  readonly isCollapsed = signal<boolean>(localStorage.getItem(this.STORAGE_KEY) === 'true');
  readonly showLogoutDialog = signal<boolean>(false);

  toggleCollapse(): void {
    const nextState = !this.isCollapsed();
    this.isCollapsed.set(nextState);
    localStorage.setItem(this.STORAGE_KEY, String(nextState));
  }

  userInitials(): string {
    const name = this.authService.currentUser()?.fullName;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

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

  openLogoutDialog(): void {
    this.showLogoutDialog.set(true);
  }

  onLogoutConfirm(): void {
    this.showLogoutDialog.set(false);
    this.authService.logout();
  }
}
