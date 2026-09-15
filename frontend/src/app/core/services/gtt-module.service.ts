import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';
import {
  GttModule,
  GttModuleCreateRequest,
  GttModuleStatusUpdateRequest,
  GttModuleUpdateRequest,
} from '../models/gtt-module.model';

/**
 * Serviço responsável pela comunicação HTTP com os endpoints de Módulos GTT.
 */
@Injectable({
  providedIn: 'root',
})
export class GttModuleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/gtt/modules';

  /**
   * Consulta o catálogo educacional de módulos ativos.
   */
  getCatalog(): Observable<ApiResponse<GttModule[]>> {
    return this.http.get<ApiResponse<GttModule[]>>(`${this.baseUrl}/catalog`);
  }

  /**
   * Lista módulos com paginação e filtros administrativos.
   */
  listModules(
    search: string = '',
    isActive: boolean | null = null,
    page: number = 0,
    size: number = 10,
    sort: string = 'code,asc'
  ): Observable<ApiResponse<PageResponse<GttModule>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (isActive !== null) {
      params = params.set('isActive', isActive.toString());
    }

    return this.http.get<ApiResponse<PageResponse<GttModule>>>(this.baseUrl, { params });
  }

  /**
   * Obtém os detalhes de um módulo pelo ID.
   */
  getModuleById(id: string): Observable<ApiResponse<GttModule>> {
    return this.http.get<ApiResponse<GttModule>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra um novo módulo GTT.
   */
  createModule(data: GttModuleCreateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.post<ApiResponse<GttModule>>(this.baseUrl, data);
  }

  /**
   * Atualiza os dados de um módulo existente.
   */
  updateModule(id: string, data: GttModuleUpdateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.put<ApiResponse<GttModule>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de um módulo.
   */
  updateStatus(id: string, data: GttModuleStatusUpdateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.patch<ApiResponse<GttModule>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui um módulo GTT.
   */
  deleteModule(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
