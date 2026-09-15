import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';
import {
  GttTrigger,
  GttTriggerCreateRequest,
  GttTriggerStatusUpdateRequest,
  GttTriggerUpdateRequest,
} from '../models/gtt-trigger.model';

/**
 * Serviço responsável pela comunicação HTTP com os endpoints de Gatilhos GTT.
 */
@Injectable({
  providedIn: 'root',
})
export class GttTriggerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/gtt/triggers';

  /**
   * Consulta os gatilhos ativos para o guia educacional de consulta rápida.
   */
  getCatalog(moduleId?: string): Observable<ApiResponse<GttTrigger[]>> {
    let params = new HttpParams();
    if (moduleId) {
      params = params.set('moduleId', moduleId);
    }
    return this.http.get<ApiResponse<GttTrigger[]>>(`${this.baseUrl}/catalog`, { params });
  }

  /**
   * Lista gatilhos com paginação e filtros administrativos.
   */
  listTriggers(
    moduleId?: string,
    search: string = '',
    isActive: boolean | null = null,
    page: number = 0,
    size: number = 10,
    sort: string = 'code,asc'
  ): Observable<ApiResponse<PageResponse<GttTrigger>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (moduleId) {
      params = params.set('moduleId', moduleId);
    }
    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (isActive !== null) {
      params = params.set('isActive', isActive.toString());
    }

    return this.http.get<ApiResponse<PageResponse<GttTrigger>>>(this.baseUrl, { params });
  }

  /**
   * Obtém os detalhes de um gatilho por ID.
   */
  getTriggerById(id: string): Observable<ApiResponse<GttTrigger>> {
    return this.http.get<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra um novo gatilho clínico.
   */
  createTrigger(data: GttTriggerCreateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.post<ApiResponse<GttTrigger>>(this.baseUrl, data);
  }

  /**
   * Atualiza os dados de um gatilho existente.
   */
  updateTrigger(id: string, data: GttTriggerUpdateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.put<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de um gatilho.
   */
  updateStatus(id: string, data: GttTriggerStatusUpdateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.patch<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui um gatilho clínico.
   */
  deleteTrigger(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
