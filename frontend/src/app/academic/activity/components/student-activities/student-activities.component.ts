import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { AcademicClassService } from '../../../clazz/services/academic-class.service';
import { ToastService } from '../../../../common/services/toast.service';
import { SubmissionResponseDTO } from '../../models/activity.model';
import { PageResponse } from '../../../../common/models/page.model';
import { DataTablePaginationComponent } from '../../../../common/components/data-table/data-table.component';

/**
 * Representação de uma atividade com resolução pendente para o discente.
 */
export interface PendingActivityItem {
  /** UUID da atividade */
  id: string;
  /** Título descritivo */
  title: string;
  /** Orientações do caso clínico */
  description: string;
  /** Prazo limite de entrega */
  deadline: string;
  /** Indica se o prazo já expirou */
  isExpired: boolean;
  /** UUID da turma associada */
  classId: string;
  /** Nome formatado da turma */
  className: string;
  /** Nome do docente titular */
  professorName: string;
}

/**
 * Painel "Minhas Atividades" e histórico de resoluções do estudante.
 */
@Component({
  selector: 'app-student-activities',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DataTablePaginationComponent],
  templateUrl: './student-activities.component.html',
})
export class StudentActivitiesComponent implements OnInit {
  /** Serviço de gestão de atividades e submissões */
  private readonly activityService = inject(ActivityService);
  /** Serviço de turmas acadêmicas */
  private readonly classService = inject(AcademicClassService);
  /** Serviço de roteamento SPA */
  private readonly router = inject(Router);
  /** Serviço de notificações de toast */
  private readonly toast = inject(ToastService);

  /** Submissões clínicas realizadas pelo estudante */
  readonly submissions = signal<SubmissionResponseDTO[]>([]);
  /** Atividades pendentes de envio pelo discente */
  readonly pendingActivities = signal<PendingActivityItem[]>([]);
  /** Metadados de paginação das submissões */
  readonly pageData = signal<PageResponse<SubmissionResponseDTO> | null>(null);
  /** Filtro de exibição ativo (todas, corrigidas ou pendentes) */
  readonly selectedFilter = signal<'ALL' | 'GRADED' | 'PENDING'>('ALL');
  /** Página atual da consulta */
  readonly currentPage = signal(0);
  /** Total de submissões cadastradas */
  readonly totalElements = signal(0);
  /** Quantidade total de páginas disponíveis */
  readonly totalPages = signal(0);
  /** Quantidade padrão de registros por página */
  readonly pageSize = 10;

  /**
   * Ciclo de inicialização: dispara o carregamento das submissões do estudante.
   */
  ngOnInit(): void {
    this.loadSubmissions();
  }

  /**
   * Busca as submissões realizadas pelo estudante autenticado.
   */
  loadSubmissions(): void {
    this.activityService.getMySubmissions(this.currentPage(), this.pageSize).subscribe({
      next: (res) => {
        this.pageData.set(res.data);
        this.submissions.set(res.data.content);
        this.currentPage.set(res.data.page);
        this.totalElements.set(res.data.totalElements);
        this.totalPages.set(res.data.totalPages);
        this.loadPendingActivities();
      },
      error: () => this.toast.error('Erro ao carregar atividades do estudante.'),
    });
  }

  /**
   * Identifica atividades abertas nas turmas ativas que ainda não foram submetidas.
   */
  loadPendingActivities(): void {
    this.classService.getMyClasses(0, 10).subscribe({
      next: (res) => {
        const activeClasses = res.data.content.filter((c) => !c.isClosed);
        if (activeClasses.length === 0) {
          this.pendingActivities.set([]);
          return;
        }

        const submittedIds = new Set(this.submissions().map((s) => s.activityId));
        const pending: PendingActivityItem[] = [];
        let remaining = activeClasses.length;

        for (const clazz of activeClasses) {
          this.activityService.listActivities(clazz.id, 0, 20).subscribe({
            next: (actRes) => {
              for (const act of actRes.data.content) {
                if (!submittedIds.has(act.id)) {
                  pending.push({
                    id: act.id,
                    title: act.title,
                    description: act.description,
                    deadline: act.deadline,
                    isExpired: act.isExpired,
                    classId: clazz.id,
                    className: clazz.formattedName,
                    professorName: clazz.professorName,
                  });
                }
              }
              remaining--;
              if (remaining === 0) {
                pending.sort((a, b) => new Date(a.deadline).getTime() - new Date(b.deadline).getTime());
                this.pendingActivities.set(pending);
              }
            },
            error: () => {
              remaining--;
              if (remaining === 0) {
                this.pendingActivities.set(pending);
              }
            },
          });
        }
      },
      error: () => {
        // Ignora silenciosamente se houver erro ao buscar turmas
      },
    });
  }

  /**
   * Aplica o filtro de status selecionado às submissões.
   */
  setFilter(filter: 'ALL' | 'GRADED' | 'PENDING'): void {
    this.selectedFilter.set(filter);
  }

  /**
   * Retorna a lista de submissões filtrada de acordo com o filtro selecionado.
   */
  filteredSubmissions(): SubmissionResponseDTO[] {
    const all = this.submissions();
    const f = this.selectedFilter();
    if (f === 'GRADED') {
      return all.filter((s) => s.isGraded);
    } else if (f === 'PENDING') {
      return all.filter((s) => !s.isGraded);
    }
    return all;
  }

  /**
   * Altera a página ativa da tabela de submissões.
   */
  onPageChange(p: number): void {
    this.currentPage.set(p);
    this.loadSubmissions();
  }

  /**
   * Redireciona para a tela de minhas turmas.
   */
  goToMyClasses(): void {
    this.router.navigate(['/academic/classes/my-classes']);
  }

  /**
   * Redireciona para a tela de resolução de uma atividade.
   */
  continueResolution(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'resolve']);
  }

  /**
   * Redireciona para a tela de visualização do parecer e nota do professor.
   */
  viewFeedback(submissionId: string): void {
    this.router.navigate(['/academic/submissions', submissionId, 'feedback']);
  }
}
