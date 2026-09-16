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
  template: `
    <div class="min-h-screen flex bg-slate-50 text-slate-900">
      <!-- SIDEBAR RETRÁTIL -->
      <aside
        class="fixed inset-y-0 left-0 z-30 flex flex-col bg-clinical-900 text-white transition-all duration-300 shadow-xl"
        [ngClass]="isCollapsed() ? 'w-20' : 'w-64'"
      >
        <!-- Topo da Sidebar: Logotipo e Botão de Colapso -->
        @if (isCollapsed()) {
          <div class="h-16 flex items-center justify-center border-b border-clinical-800/80">
            <button
              type="button"
              (click)="toggleCollapse()"
              class="p-2 text-blue-200 hover:text-white hover:bg-clinical-800 rounded-lg transition-colors"
              title="Expandir menu"
              aria-label="Expandir menu"
            >
              <svg class="w-5 h-5 rotate-180" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
              </svg>
            </button>
          </div>
        } @else {
          <div class="h-16 flex items-center justify-between px-4 border-b border-clinical-800/80">
            <div class="flex items-center gap-3 overflow-hidden">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-clinical-600 to-blue-400 flex items-center justify-center flex-shrink-0 shadow-md">
                <span class="text-white font-black text-lg tracking-tight">S</span>
              </div>
              <div class="flex flex-col whitespace-nowrap overflow-hidden transition-opacity duration-200">
                <span class="font-extrabold text-sm tracking-wider text-white">SIGEA-GTT</span>
                <span class="text-[10px] text-blue-200 uppercase tracking-widest font-medium">UFS • DCOMP</span>
              </div>
            </div>

            <!-- Botão Colapso da Sidebar -->
            <button
              type="button"
              (click)="toggleCollapse()"
              class="p-1.5 text-blue-200 hover:text-white hover:bg-clinical-800 rounded-lg transition-colors flex-shrink-0"
              title="Recolher menu"
              aria-label="Recolher menu"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
              </svg>
            </button>
          </div>
        }

        <!-- Links de Navegação -->
        <nav class="flex-1 px-3 py-4 space-y-4 overflow-y-auto">
          <!-- SEÇÃO 1: Metodologia GTT (Consulta Clínica / Educacional - Todos) -->
          <div>
            @if (!isCollapsed()) {
              <div class="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-blue-300/80">
                Metodologia GTT
              </div>
            }
            <div class="space-y-1">
              <!-- Catálogo de Módulos -->
              <a
                routerLink="/gtt/modules"
                routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                [title]="isCollapsed() ? 'Módulos GTT' : ''"
              >
                <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                </svg>
                @if (!isCollapsed()) {
                  <span class="text-xs">Módulos GTT</span>
                }
              </a>

              <!-- Guia de Gatilhos -->
              <a
                routerLink="/gtt/triggers"
                routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                [title]="isCollapsed() ? 'Gatilhos Clínicos' : ''"
              >
                <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                @if (!isCollapsed()) {
                  <span class="text-xs">Gatilhos Clínicos</span>
                }
              </a>

              <!-- Guia de Gravidades NCC MERP -->
              <a
                routerLink="/gtt/severities"
                routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                [title]="isCollapsed() ? 'Gravidades NCC MERP' : ''"
              >
                <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
                @if (!isCollapsed()) {
                  <span class="text-xs">Gravidades (A-I)</span>
                }
              </a>
            </div>
          </div>

          <!-- SEÇÃO: Módulo Acadêmico & Educacional -->
          <div>
            @if (!isCollapsed()) {
              <div class="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-blue-300/80">
                Módulo Acadêmico
              </div>
            }
            <div class="space-y-1">
              <!-- Turmas Acadêmicas (Admin) -->
              @if (authService.isAdmin()) {
                <a
                  routerLink="/academic/classes"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Turmas Acadêmicas' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Turmas Acadêmicas</span>
                  }
                </a>
              }

              <!-- Minhas Turmas (Professor e Estudante) -->
              @if (authService.isProfessor() || authService.isStudent()) {
                <a
                  routerLink="/academic/classes/my-classes"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Minhas Turmas' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Minhas Turmas</span>
                  }
                </a>
              }

              <!-- Minhas Atividades (Apenas Estudante) -->
              @if (authService.isStudent()) {
                <a
                  routerLink="/academic/student/activities"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Minhas Atividades' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Minhas Atividades</span>
                  }
                </a>
              }
            </div>
          </div>

          <!-- SEÇÃO 2: Gestão Administrativa GTT (Apenas ADMIN) -->
          @if (authService.isAdmin()) {
            <div>
              @if (!isCollapsed()) {
                <div class="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-blue-300/80">
                  Gestão GTT
                </div>
              }
              <div class="space-y-1">
                <!-- Gestão de Módulos -->
                <a
                  routerLink="/admin/gtt/modules"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Gerenciar Módulos' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Gerenciar Módulos</span>
                  }
                </a>

                <!-- Gestão de Gatilhos -->
                <a
                  routerLink="/admin/gtt/triggers"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Gerenciar Gatilhos' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Gerenciar Gatilhos</span>
                  }
                </a>

                <!-- Gestão de Gravidades -->
                <a
                  routerLink="/admin/gtt/severities"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Gerenciar Gravidades' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Gerenciar Gravidades</span>
                  }
                </a>

                <!-- Usuários do Sistema -->
                <a
                  routerLink="/users"
                  routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                  class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                  [title]="isCollapsed() ? 'Gestão de Usuários' : ''"
                >
                  <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                  @if (!isCollapsed()) {
                    <span class="text-xs">Gestão de Usuários</span>
                  }
                </a>
              </div>
            </div>
          }

          <!-- SEÇÃO 3: Conta -->
          <div>
            @if (!isCollapsed()) {
              <div class="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-blue-300/80">
                Conta
              </div>
            }
            <div class="space-y-1">
              <a
                routerLink="/profile"
                routerLinkActive="bg-clinical-800 text-white font-semibold shadow-inner"
                class="flex items-center gap-3 px-3 py-2 rounded-xl text-blue-100 hover:text-white hover:bg-clinical-800/50 transition-all group"
                [title]="isCollapsed() ? 'Meu Perfil' : ''"
              >
                <svg class="w-5 h-5 flex-shrink-0 text-blue-300 group-hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                @if (!isCollapsed()) {
                  <span class="text-xs">Meu Perfil</span>
                }
              </a>
            </div>
          </div>
        </nav>

        <!-- Rodapé do Menu: Card de Perfil & Logout -->
        @if (isCollapsed()) {
          <div class="h-16 flex items-center justify-center border-t border-clinical-800/80 bg-clinical-950/40">
            <button
              type="button"
              (click)="openLogoutDialog()"
              class="p-2 text-blue-300 hover:text-rose-400 hover:bg-clinical-800/60 rounded-lg transition-colors"
              title="Encerrar sessão"
              aria-label="Sair"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </div>
        } @else {
          <div class="p-3 border-t border-clinical-800/80 bg-clinical-950/40">
            <div class="flex items-center justify-between gap-3">
              <div class="flex items-center gap-3 overflow-hidden">
                <div class="w-9 h-9 rounded-full bg-clinical-700 border border-blue-400/30 flex items-center justify-center text-white font-bold text-xs flex-shrink-0">
                  {{ userInitials() }}
                </div>
                <div class="flex flex-col min-w-0">
                  <span class="text-xs font-semibold text-white truncate">{{ authService.currentUser()?.fullName }}</span>
                  <span class="text-[10px] text-blue-300 uppercase tracking-wider font-mono">
                    {{ authService.currentUser()?.role }}
                  </span>
                </div>
              </div>

              <!-- Botão Sair -->
              <button
                type="button"
                (click)="openLogoutDialog()"
                class="p-2 text-blue-300 hover:text-rose-400 hover:bg-clinical-800/60 rounded-lg transition-colors flex-shrink-0"
                title="Encerrar sessão"
                aria-label="Sair"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </button>
            </div>
          </div>
        }
      </aside>

      <!-- ÁREA PRINCIPAL COM SUPORTE A RESOLUÇÕES ERGONÔMICAS -->
      <div
        class="flex-1 flex flex-col min-h-screen transition-all duration-300"
        [ngClass]="isCollapsed() ? 'pl-20' : 'pl-64'"
      >
        <!-- Topo da Aplicação -->
        <header class="h-16 bg-white border-b border-slate-200 sticky top-0 z-20 px-6 flex items-center justify-between shadow-xs">
          <!-- Nome do Sistema & Contexto -->
          <div class="flex items-center gap-4">
            <h1 class="text-base font-bold text-slate-800 tracking-tight">
              SIGEA-GTT <span class="hidden sm:inline text-xs font-normal text-slate-500">| Global Trigger Tool</span>
            </h1>
          </div>

          <!-- Perfil Pill no Topo -->
          <div class="flex items-center gap-3">
            <div class="hidden md:flex flex-col text-right">
              <span class="text-xs font-bold text-slate-800">{{ authService.currentUser()?.fullName }}</span>
              <span class="text-[10px] text-slate-500 font-medium">{{ authService.currentUser()?.email }}</span>
            </div>
            <span
              class="px-2.5 py-1 text-xs font-semibold rounded-md uppercase tracking-wider"
              [ngClass]="getRoleBadgeClasses()"
            >
              {{ authService.currentUser()?.role }}
            </span>
          </div>
        </header>

        <!-- Conteúdo Central com Contenção Ergonômica para Ultrawide -->
        <main class="flex-1 p-6 md:p-8 bg-slate-50 overflow-x-hidden">
          <div class="max-w-ultrawide mx-auto w-full">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
    </div>

    <!-- Modal de Confirmação de Logout -->
    <app-confirm-dialog
      [isOpen]="showLogoutDialog()"
      title="Encerrar Sessão"
      message="Deseja realmente sair do SIGEA-GTT?"
      confirmText="Sair do Sistema"
      cancelText="Permanecer Conectado"
      [isDestructive]="false"
      (confirmed)="onLogoutConfirm()"
      (cancelled)="showLogoutDialog.set(false)"
    />
  `,
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
