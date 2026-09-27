/**
 * @file class-list.component.ts
 * @description Listagem paginada e gestão administrativa e pedagógica de Turmas Acadêmicas (Admin, Docente e Estudante).
 * @module ClassListComponent
 */

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AcademicClassService } from '../../services/academic-class.service';
import { ToastService } from '../../../../common/services/toast.service';
import { AuthService } from '../../../../auth/services/auth.service';
import { AcademicClassResponseDTO } from '../../models/academic-class.model';
import { PageResponse } from '../../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../../common/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { ClassFormComponent } from '../class-form/class-form.component';

/**
 * Componente da tela de listagem de turmas com paginação, filtros de período e busca textual.
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
  /** Serviço de comunicação com API de turmas */
  private readonly classService = inject(AcademicClassService);
  /** Serviço de notificações Toast */
  private readonly toast = inject(ToastService);
  /** Serviço de autenticação e RBAC */
  private readonly auth = inject(AuthService);
  /** Roteador Angular */
  private readonly router = inject(Router);

  /** Lista reativa de turmas da página atual */
  readonly classes = signal<AcademicClassResponseDTO[]>([]);
  /** Metadados de paginação recebidos do backend */
  readonly pageData = signal<PageResponse<AcademicClassResponseDTO> | null>(null);
  /** Página atual (0-indexed) */
  readonly currentPage = signal(0);
  /** Total de registros encontrados */
  readonly totalElements = signal(0);
  /** Total de páginas disponíveis */
  readonly totalPages = signal(0);
  /** Tamanho fixo da página */
  readonly pageSize = 10;

  /** Indicador de modal de formulário aberto */
  readonly isFormModalOpen = signal(false);
  /** ID da turma selecionada para edição (null se for criação) */
  readonly selectedClassId = signal<string | null>(null);

  /** Controle de visibilidade do diálogo de confirmação */
  readonly isConfirmDialogOpen = signal(false);
  /** Título do diálogo de confirmação */
  readonly confirmDialogTitle = signal('');
  /** Mensagem do diálogo de confirmação */
  readonly confirmDialogMessage = signal('');
  /** Rótulo do botão de ação */
  readonly confirmDialogActionText = signal('Confirmar');
  /** Flag de ação destrutiva */
  readonly confirmDialogIsDestructive = signal(false);
  /** Ação pendente de execução pós-confirmação */
  private pendingAction: (() => void) | null = null;

  /** Termo de busca textual */
  searchQuery = '';
  /** Filtro de status (ativo ou encerrado) */
  selectedStatus: boolean | null = null;

  /** Verifica se o usuário atual possui perfil ADMIN */
  get isAdmin(): boolean {
    return this.auth.hasRole(['ADMIN']);
  }

  /** Verifica se o usuário atual possui perfil PROFESSOR */
  get isProfessor(): boolean {
    return this.auth.hasRole(['PROFESSOR']);
  }

  /** Verifica se a tela está operando no modo restrito a "Minhas Turmas" */
  get isMyClassesView(): boolean {
    return this.router.url.includes('my-classes') || !this.isAdmin;
  }

  /**
   * Inicializa o componente carregando as turmas aplicáveis.
   */
  ngOnInit(): void {
    this.loadClasses();
  }

  /**
   * Executa a busca paginada de turmas dependendo do perfil e rota.
   */
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

  /**
   * Atualiza os sinais reativos com os dados da resposta paginada.
   *
   * @param page Resposta paginada da API
   */
  private handlePageResponse(page: PageResponse<AcademicClassResponseDTO>): void {
    this.pageData.set(page);
    this.classes.set(page.content);
    this.currentPage.set(page.page);
    this.totalElements.set(page.totalElements);
    this.totalPages.set(page.totalPages);
  }

  /**
   * Trata busca textual.
   */
  onSearchChange(): void {
    this.currentPage.set(0);
    this.loadClasses();
  }

  /**
   * Trata mudança nos filtros.
   */
  onFilterChange(): void {
    this.currentPage.set(0);
    this.loadClasses();
  }

  /**
   * Trata navegação de página.
   *
   * @param newPage Novo índice de página
   */
  onPageChange(newPage: number): void {
    this.currentPage.set(newPage);
    this.loadClasses();
  }

  /**
   * Navega para a tela de detalhes da turma.
   *
   * @param id UUID da turma
   */
  viewClassDetail(id: string): void {
    this.router.navigate(['/academic/classes', id]);
  }

  /**
   * Navega para o painel de taxas GTT e métricas epidemiológicas da turma.
   *
   * @param id UUID da turma
   */
  viewDashboard(id: string): void {
    this.router.navigate(['/academic/classes', id, 'dashboard']);
  }

  /**
   * Abre o modal no modo de criação de turma.
   */
  openCreateModal(): void {
    this.selectedClassId.set(null);
    this.isFormModalOpen.set(true);
  }

  /**
   * Abre o modal no modo de edição da turma.
   *
   * @param id UUID da turma
   */
  openEditModal(id: string): void {
    this.selectedClassId.set(id);
    this.isFormModalOpen.set(true);
  }

  /**
   * Fecha o modal de formulário e limpa seleção.
   */
  closeFormModal(): void {
    this.isFormModalOpen.set(false);
    this.selectedClassId.set(null);
  }

  /**
   * Callback executado após salvar uma turma.
   */
  onClassSaved(): void {
    this.closeFormModal();
    this.loadClasses();
  }

  /**
   * Abre o diálogo de confirmação para encerramento ou reabertura da turma.
   *
   * @param clazz Turma alvo
   */
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
        next: () => {
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

  /**
   * Abre o diálogo de confirmação para exclusão permanente de turma.
   *
   * @param clazz Turma alvo
   */
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

  /**
   * Executa a ação confirmada pelo usuário no modal.
   */
  executeConfirmedAction(): void {
    if (this.pendingAction) {
      this.pendingAction();
      this.pendingAction = null;
    }
    this.isConfirmDialogOpen.set(false);
  }

  /**
   * Cancela e fecha o diálogo de confirmação.
   */
  closeConfirmDialog(): void {
    this.isConfirmDialogOpen.set(false);
    this.pendingAction = null;
  }
}
