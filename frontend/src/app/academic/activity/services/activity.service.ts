import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { PageResponse } from '../../../common/models/page.model';
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
 * Comunica-se com os endpoints REST de `/api/academic/activities` e `/api/academic/submissions`.
 */
@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  /** Cliente HTTP Angular para chamadas REST */
  private readonly http = inject(HttpClient);
  /** Caminho base dos recursos acadêmicos na API */
  private readonly baseUrl = '/api/academic';

  /**
   * Lista atividades cadastradas em uma turma específica com paginação.
   *
   * @param classId Identificador único da turma acadêmica.
   * @param page Índice da página (0-indexed, padrão: 0).
   * @param size Quantidade de registros por página (padrão: 10).
   * @param sort Critério de ordenação (padrão: 'deadline,desc').
   * @returns Observable contendo a página de atividades.
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
   * Obtém detalhes completos de uma atividade avaliativa por ID, incluindo o prontuário simulado.
   *
   * @param id Identificador da atividade.
   * @returns Observable com os detalhes da atividade.
   */
  getActivityById(id: string): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.get<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities/${id}`);
  }

  /**
   * Cria uma nova atividade avaliativa com prontuário fictício estruturado em JSONB.
   *
   * @param dto Dados de criação da atividade.
   * @returns Observable com a atividade criada.
   */
  createActivity(dto: ActivityCreateDTO): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.post<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities`, dto);
  }

  /**
   * Atualiza dados e prontuário de uma atividade existente.
   *
   * @param id Identificador da atividade.
   * @param dto Dados de atualização.
   * @returns Observable com os detalhes atualizados.
   */
  updateActivity(id: string, dto: ActivityUpdateDTO): Observable<ApiResponse<ActivityDetailDTO>> {
    return this.http.put<ApiResponse<ActivityDetailDTO>>(`${this.baseUrl}/activities/${id}`, dto);
  }

  /**
   * Exclui uma atividade e todas as suas resoluções associadas.
   *
   * @param id Identificador da atividade.
   * @returns Observable vazio com confirmação da operação.
   */
  deleteActivity(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/activities/${id}`);
  }

  /**
   * Submete a resolução da auditoria clínica realizada pelo estudante.
   *
   * @param activityId Identificador da atividade.
   * @param dto Dados da resolução com gatilhos identificados e ferramentas da qualidade.
   * @returns Observable com a submissão registrada.
   */
  submitActivity(activityId: string, dto: SubmissionCreateDTO): Observable<ApiResponse<SubmissionResponseDTO>> {
    return this.http.post<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/activities/${activityId}/submissions`, dto);
  }

  /**
   * Lista submissões de uma atividade para auditoria e correção pelo docente.
   *
   * @param activityId Identificador da atividade.
   * @param page Índice da página (padrão: 0).
   * @param size Quantidade por página (padrão: 10).
   * @param sort Ordenação (padrão: 'submissionDate,desc').
   * @returns Observable com a página de submissões.
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
   * Consulta uma submissão individual por ID.
   *
   * @param id Identificador da submissão.
   * @returns Observable com a submissão e dados pedagógicos.
   */
  getSubmissionById(id: string): Observable<ApiResponse<SubmissionResponseDTO>> {
    return this.http.get<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/submissions/${id}`);
  }

  /**
   * Atribui nota (0.00 a 10.00) e parecer pedagógico a uma resolução enviada.
   *
   * @param id Identificador da submissão.
   * @param dto Nota e parecer do docente.
   * @returns Observable com a submissão avaliada.
   */
  gradeSubmission(id: string, dto: SubmissionGradeDTO): Observable<ApiResponse<SubmissionResponseDTO>> {
    const payload = {
      grade: dto.grade,
      professorFeedback: dto.professorFeedback || dto.pedagogicalFeedback,
    };
    return this.http.patch<ApiResponse<SubmissionResponseDTO>>(`${this.baseUrl}/submissions/${id}/grade`, payload);
  }

  /**
   * Lista as submissões realizadas pelo estudante autenticado.
   *
   * @param page Índice da página (padrão: 0).
   * @param size Quantidade por página (padrão: 10).
   * @param sort Ordenação (padrão: 'submissionDate,desc').
   * @returns Observable com as submissões do discente.
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
