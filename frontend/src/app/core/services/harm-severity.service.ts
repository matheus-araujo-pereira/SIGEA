import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';
import {
  HarmSeverity,
  HarmSeverityCreateRequest,
  HarmSeverityStatusUpdateRequest,
  HarmSeverityUpdateRequest,
} from '../models/harm-severity.model';

/**
 * Serviço responsável pela comunicação HTTP com os endpoints de Gravidades de Dano (NCC MERP).
 */
@Injectable({
  providedIn: 'root',
})
export class HarmSeverityService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/gtt/severities';

  /**
   * Consulta o guia interativo de gravidades ordenadas de A a I.
   */
  getGuide(): Observable<ApiResponse<HarmSeverity[]>> {
    return this.http.get<ApiResponse<HarmSeverity[]>>(`${this.baseUrl}/guide`);
  }

  /**
   * Lista gravidades com paginação e filtros administrativos.
   */
  listSeverities(
    search: string = '',
    isHarm: boolean | null = null,
    isActive: boolean | null = null,
    page: number = 0,
    size: number = 10,
    sort: string = 'categoryLetter,asc'
  ): Observable<ApiResponse<PageResponse<HarmSeverity>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (isHarm !== null) {
      params = params.set('isHarm', isHarm.toString());
    }
    if (isActive !== null) {
      params = params.set('isActive', isActive.toString());
    }

    return this.http.get<ApiResponse<PageResponse<HarmSeverity>>>(this.baseUrl, { params });
  }

  /**
   * Lista gravidades de dano com filtro ativo para seleção clínica.
   */
  listHarmSeverities(
    search: string = '',
    isActive: boolean | null = true,
    page: number = 0,
    size: number = 20
  ): Observable<ApiResponse<PageResponse<HarmSeverity>>> {
    return this.listSeverities(search, true, isActive, page, size);
  }

  /**
   * Obtém os dados de uma gravidade por ID.
   */
  getSeverityById(id: string): Observable<ApiResponse<HarmSeverity>> {
    return this.http.get<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra uma nova categoria de gravidade.
   */
  createSeverity(data: HarmSeverityCreateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.post<ApiResponse<HarmSeverity>>(this.baseUrl, data);
  }

  /**
   * Atualiza uma categoria de gravidade existente.
   */
  updateSeverity(id: string, data: HarmSeverityUpdateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.put<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de uma categoria de gravidade.
   */
  updateStatus(id: string, data: HarmSeverityStatusUpdateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.patch<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui uma categoria de gravidade.
   */
  deleteSeverity(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
