import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { ActivityDetailDTO, SubmissionResponseDTO } from '../../models/activity.model';
import { ToastService } from '../../../../common/services/toast.service';

/**
 * Componente para listagem de submissões de discentes para correção pedagógica pelo docente.
 */
@Component({
  selector: 'app-activity-grading-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './activity-grading-list.component.html',
})
export class ActivityGradingListComponent implements OnInit {
  /** Rota ativa para identificação da atividade */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de gestão de atividades e submissões */
  private readonly activityService = inject(ActivityService);
  /** Serviço de exibição de notificações toast */
  private readonly toastService = inject(ToastService);

  /** UUID da atividade avaliada */
  readonly activityId = signal<string>('');
  /** Dados completos da atividade */
  readonly activity = signal<ActivityDetailDTO | null>(null);
  /** Submissões dos estudantes da turma */
  readonly submissions = signal<SubmissionResponseDTO[]>([]);
  /** Indica se os dados estão sendo carregados */
  readonly isLoading = signal<boolean>(true);
  /** Página atual da consulta de submissões */
  readonly currentPage = signal<number>(0);
  /** Quantidade total de páginas disponíveis */
  readonly totalPages = signal<number>(0);
  /** Quantidade total de submissões registradas */
  readonly totalElements = signal<number>(0);

  /**
   * Ciclo de inicialização: recupera o ID da atividade na rota ou redireciona.
   */
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('activityId');
    if (id) {
      this.activityId.set(id);
      this.loadActivityDetails();
      this.loadSubmissions();
    } else {
      this.toastService.error('Identificador de atividade inválido.');
      this.router.navigate(['/academic/classes']);
    }
  }

  /**
   * Busca detalhes e título da atividade avaliativa.
   */
  loadActivityDetails(): void {
    this.activityService.getActivityById(this.activityId()).subscribe({
      next: (res) => {
        this.activity.set(res.data);
      },
      error: (err) => {
        this.toastService.error(err?.error?.message || 'Erro ao carregar detalhes da atividade.');
      },
    });
  }

  /**
   * Busca a lista paginada de submissões dos estudantes.
   */
  loadSubmissions(): void {
    this.isLoading.set(true);
    this.activityService.listSubmissions(this.activityId(), this.currentPage(), 10).subscribe({
      next: (res) => {
        this.submissions.set(res.data.content);
        this.totalPages.set(res.data.totalPages);
        this.totalElements.set(res.data.totalElements);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err?.error?.message || 'Erro ao carregar lista de submissões.');
      },
    });
  }

  /**
   * Altera a página de submissões exibida.
   */
  changePage(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages()) {
      this.currentPage.set(newPage);
      this.loadSubmissions();
    }
  }

  /**
   * Retorna para a página da turma correspondente.
   */
  goBack(): void {
    if (this.activity()?.academicClassId) {
      this.router.navigate(['/academic/classes', this.activity()!.academicClassId]);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }
}
