import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';
import { PageResponse } from '../../../../core/models/page.model';
import { DataTablePaginationComponent } from '../../../../shared/components/data-table/data-table.component';

export interface PendingActivityItem {
  id: string;
  title: string;
  description: string;
  deadline: string;
  isExpired: boolean;
  classId: string;
  className: string;
  professorName: string;
}

/**
 * Painel "Minhas Atividades" e histórico de resoluções do estudante.
 */
@Component({
  selector: 'app-student-activities',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DataTablePaginationComponent],
  templateUrl: './student-activities.component.html'
})
export class StudentActivitiesComponent implements OnInit {
  private readonly activityService = inject(ActivityService);
  private readonly classService = inject(AcademicClassService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  readonly submissions = signal<SubmissionResponseDTO[]>([]);
  readonly pendingActivities = signal<PendingActivityItem[]>([]);
  readonly pageData = signal<PageResponse<SubmissionResponseDTO> | null>(null);
  readonly selectedFilter = signal<'ALL' | 'GRADED' | 'PENDING'>('ALL');
  readonly currentPage = signal(0);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 10;

  ngOnInit(): void {
    this.loadSubmissions();
  }

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

  setFilter(filter: 'ALL' | 'GRADED' | 'PENDING'): void {
    this.selectedFilter.set(filter);
  }

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

  onPageChange(p: number): void {
    this.currentPage.set(p);
    this.loadSubmissions();
  }

  goToMyClasses(): void {
    this.router.navigate(['/academic/classes/my-classes']);
  }

  continueResolution(activityId: string): void {
    this.router.navigate(['/academic/activities', activityId, 'resolve']);
  }

  viewFeedback(submissionId: string): void {
    this.router.navigate(['/academic/submissions', submissionId, 'feedback']);
  }
}
