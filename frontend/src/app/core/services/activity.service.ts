import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';
import {
  ActivityCreateDTO,
  ActivityDetailDTO,
  ActivityResponseDTO,
  ActivityUpdateDTO,
  SubmissionCreateDTO,
  SubmissionGradeDTO,
  SubmissionResponseDTO,
} from '../models/activity.model';

/**
 * Serviço responsável por Atividades Avaliativas, Prontuário Simulado e Submissões no SIGEA.
 */
@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/academic';

  /**
   * Lista atividades cadastradas em uma turma.
   */
  listActivities(
    classId: string,
    page = 0,
    size = 10,
    sort = 'deadline,desc'
  ): Observable<ApiResponse<PageResponse<ActivityResponseDTO>>> {
    const params = new HttpParams()
      .set('classId', classId)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<ActivityResponseDTO>>>(`${this.baseUrl}/activities`, { params });
  }

  /**
   * Obtém detalhes de uma atividade por ID.
   */
  getActivityById(id: string): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.get<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities/${id}`);
  }

  /**
   * Cria uma nova atividade avaliativa com prontuário fictício.
   */
  createActivity(dto: ActivityCreateDTO): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.post<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities`, dto);
  }

  /**
   * Atualiza dados de uma atividade existente.
   */
  updateActivity(id: string, dto: ActivityUpdateDTO): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.put<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities/${id}`, dto);
  }

  /**
   * Exclui uma atividade.
   */
  deleteActivity(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/activities/${id}`);
  }

  /**
   * Submete a resolução da atividade pelo estudante.
   */
  submitActivity(activityId: string, dto: SubmissionCreateDTO): Observable<ApiResponse<SubmissionResponseDTO>> {
    return this.http.post<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/activities/${activityId}/submissions`, dto);
  }

  /**
   * Lista submissões de uma atividade para correção pelo docente.
   */
  listSubmissions(
    activityId: string,
    page = 0,
    size = 10,
    sort = 'submissionDate,desc'
  ): Observable<ApiResponse<PageResponse<SubmissionResponseDTO>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<SubmissionResponseDTO>>>(
      `${this.baseUrl}/activities/${activityId}/submissions`,
      { params }
    );
  }

  /**
   * Consulta uma submissão por ID.
   */
  getSubmissionById(id: string): Observable<ApiResponse<SubmissionResponseDTO>> {
    return this.http.get<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/submissions/${id}`);
  }

  /**
   * Atribui nota e parecer pedagógico a uma submissão.
   */
  gradeSubmission(id: string, dto: SubmissionGradeDTO): Observable<ApiResponse<SubmissionResponseDTO>> {
    const payload = {
      grade: dto.grade,
      professorFeedback: dto.professorFeedback || dto.pedagogicalFeedback,
    };
    return this.http.patch<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/submissions/${id}/grade`, payload);
  }

  /**
   * Lista as submissões enviadas pelo estudante autenticado.
   */
  getMySubmissions(
    page = 0,
    size = 10,
    sort = 'submissionDate,desc'
  ): Observable<ApiResponse<PageResponse<SubmissionResponseDTO>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<SubmissionResponseDTO>>>(
      `${this.baseUrl}/submissions/my-submissions`,
      { params }
    );
  }
}
