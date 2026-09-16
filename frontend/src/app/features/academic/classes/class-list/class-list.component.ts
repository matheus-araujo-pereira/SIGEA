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
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">
            {{ isMyClassesView ? 'Minhas Turmas' : 'Gestão de Turmas Acadêmicas' }}
          </h2>
          <p class="text-xs text-slate-500 mt-1">
            {{ isMyClassesView
                ? 'Turmas nas quais você está vinculado como docente titular ou estudante matriculado'
                : 'Controle de turmas, docentes responsáveis e períodos letivos da UFS'
            }}
          </p>
        </div>

        @if (isAdmin) {
          <button
            type="button"
            (click)="openCreateModal()"
            class="btn-primary gap-2 shadow-sm"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>Nova Turma</span>
          </button>
        }
      </div>

      <!-- Filtros de Pesquisa -->
      <div class="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row items-center gap-4">
        <!-- Campo Busca -->
        <div class="relative flex-1 w-full">
          <input
            type="text"
            [(ngModel)]="searchQuery"
            (ngModelChange)="onSearchChange()"
            placeholder="Buscar por disciplina, código de turma ou período letivo..."
            class="w-full pl-9 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- Filtro Status -->
        <div class="w-full md:w-48">
          <select
            [(ngModel)]="selectedStatus"
            (ngModelChange)="onFilterChange()"
            class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option [ngValue]="null">Todos os Status</option>
            <option [ngValue]="false">Apenas Ativas</option>
            <option [ngValue]="true">Apenas Encerradas</option>
          </select>
        </div>
      </div>

      <!-- Tabela Padronizada com 10 Registros por Página -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
            <thead class="bg-slate-50/80 text-slate-500 font-bold uppercase tracking-wider text-[11px]">
              <tr>
                <th scope="col" class="px-6 py-3.5">Denominação da Turma</th>
                <th scope="col" class="px-6 py-3.5">Docente Responsável</th>
                <th scope="col" class="px-6 py-3.5 text-center">Período</th>
                <th scope="col" class="px-6 py-3.5 text-center">Alunos</th>
                <th scope="col" class="px-6 py-3.5 text-center">Atividades</th>
                <th scope="col" class="px-6 py-3.5 text-center">Status</th>
                <th scope="col" class="px-6 py-3.5 text-right">Ações</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100 font-medium">
              @for (clazz of classes(); track clazz.id) {
                <tr class="hover:bg-slate-50/60 transition-colors">
                  <td class="px-6 py-4">
                    <span class="font-bold text-slate-900 block text-sm">{{ clazz.formattedName }}</span>
                    <span class="text-[11px] text-slate-500">{{ clazz.subjectName }} • Turma {{ clazz.classCode }}</span>
                  </td>
                  <td class="px-6 py-4">
                    <p class="font-bold text-slate-900">{{ clazz.professorName }}</p>
                    <p class="text-[11px] text-slate-400 font-mono">{{ clazz.professorEmail }}</p>
                  </td>
                  <td class="px-6 py-4 text-center font-bold text-slate-700">
                    {{ clazz.academicPeriod }}
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-700">
                      {{ clazz.studentCount }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-clinical-50 text-clinical-800">
                      {{ clazz.activityCount }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    @if (!clazz.isClosed) {
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        Ativa
                      </span>
                    } @else {
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">
                        Encerrada
                      </span>
                    }
                  </td>
                  <td class="px-6 py-4 text-right space-x-1 whitespace-nowrap">
                    <!-- Ver Detalhes / Turma -->
                    <button
                      type="button"
                      (click)="viewClassDetail(clazz.id)"
                      class="px-2.5 py-1.5 text-xs font-bold text-clinical-600 hover:bg-clinical-50 rounded-lg transition-colors inline-flex items-center gap-1"
                      title="Ver Turma e Atividades"
                    >
                      <span>Acessar</span>
                      <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                      </svg>
                    </button>

                    <!-- Painel Analítico da Turma (Admin ou Professor) -->
                    @if (isAdmin || isProfessor) {
                      <button
                        type="button"
                        (click)="viewDashboard(clazz.id)"
                        class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors inline-flex"
                        title="Indicadores GTT e Pedagógicos"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                        </svg>
                      </button>
                    }

                    <!-- Ações de Admin -->
                    @if (isAdmin) {
                      <!-- Editar -->
                      <button
                        type="button"
                        (click)="openEditModal(clazz.id)"
                        class="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors inline-flex"
                        title="Editar Turma"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                        </svg>
                      </button>

                      <!-- Encerrar ou Reabrir -->
                      <button
                        type="button"
                        (click)="confirmToggleStatus(clazz)"
                        class="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors inline-flex"
                        [title]="clazz.isClosed ? 'Reabrir Turma' : 'Encerrar Turma'"
                      >
                        @if (!clazz.isClosed) {
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                          </svg>
                        } @else {
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 11V7a4 4 0 118 0m-4 8v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2z" />
                          </svg>
                        }
                      </button>

                      <!-- Excluir -->
                      <button
                        type="button"
                        (click)="confirmDelete(clazz)"
                        class="p-1.5 text-rose-500 hover:bg-rose-50 rounded-lg transition-colors inline-flex"
                        title="Excluir Turma"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    }
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="7" class="px-6 py-12 text-center text-slate-400">
                    Nenhuma turma acadêmica encontrada com os filtros selecionados.
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Paginação Padronizada (10 Itens) -->
        <app-data-table-pagination
          [pageData]="pageData()"
          (pageChange)="onPageChange($event)"
        />
      </div>

      <!-- Modal de Formulário (Criar / Editar - Apenas Admin) -->
      @if (isAdmin) {
        <app-class-form
          [isOpen]="isFormModalOpen()"
          [classId]="selectedClassId()"
          (close)="closeFormModal()"
          (saved)="onClassSaved()"
        />
      }

      <!-- Modal de Confirmação para Ações Destrutivas ou Reversíveis -->
      <app-confirm-dialog
        [isOpen]="isConfirmDialogOpen()"
        [title]="confirmDialogTitle()"
        [message]="confirmDialogMessage()"
        [confirmText]="confirmDialogActionText()"
        [isDestructive]="confirmDialogIsDestructive()"
        (confirm)="executeConfirmedAction()"
        (cancel)="closeConfirmDialog()"
      />
    </div>
  `,
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
