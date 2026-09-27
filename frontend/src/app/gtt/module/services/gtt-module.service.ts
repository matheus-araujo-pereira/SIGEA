/**
 * @file gtt-module.service.ts
 * @description Serviço responsável pela comunicação HTTP com os endpoints de Módulos GTT.
 * @module GttModuleService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { PageResponse } from '../../../common/models/page.model';
import {
  GttModule,
  GttModuleCreateRequest,
  GttModuleStatusUpdateRequest,
  GttModuleUpdateRequest,
} from '../models/gtt-module.model';

/**
 * Serviço de comunicação HTTP para gerenciamento do ciclo de vida dos Módulos IHI-GTT.
 */
@Injectable({
  providedIn: 'root',
})
export class GttModuleService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Endpoint base da API de módulos */
  private readonly baseUrl = '/api/gtt/modules';

  /**
   * Consulta o catálogo educacional de módulos ativos.
   *
   * @returns Observable com a lista de módulos GTT ativos ordenados
   */
  getCatalog(): Observable<ApiResponse<GttModule[]>> {
    return this.http.get<ApiResponse<GttModule[]>>(`${this.baseUrl}/catalog`);
  }

  /**
   * Lista módulos com paginação e filtros administrativos.
   *
   * @param search Termo opcional para busca textual por código ou nome
   * @param isActive Filtro opcional por status ativo/inativo
   * @param page Índice da página (0-indexed)
   * @param size Quantidade de registros por página
   * @param sort Critério de ordenação (ex: 'code,asc')
   * @returns Observable com a resposta paginada de módulos
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
   * Obtém os detalhes completos de um módulo pelo seu identificador único.
   *
   * @param id UUID do módulo
   * @returns Observable com o módulo encontrado
   */
  getModuleById(id: string): Observable<ApiResponse<GttModule>> {
    return this.http.get<ApiResponse<GttModule>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra um novo módulo GTT no sistema.
   *
   * @param data Dados de criação do módulo
   * @returns Observable com o módulo cadastrado
   */
  createModule(data: GttModuleCreateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.post<ApiResponse<GttModule>>(this.baseUrl, data);
  }

  /**
   * Atualiza os dados de um módulo existente.
   *
   * @param id UUID do módulo a ser atualizado
   * @param data Dados cadastrais atualizados
   * @returns Observable com o módulo atualizado
   */
  updateModule(id: string, data: GttModuleUpdateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.put<ApiResponse<GttModule>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de um módulo.
   *
   * @param id UUID do módulo
   * @param data Payload com o novo status
   * @returns Observable com o módulo atualizado
   */
  updateStatus(id: string, data: GttModuleStatusUpdateRequest): Observable<ApiResponse<GttModule>> {
    return this.http.patch<ApiResponse<GttModule>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui um módulo GTT do sistema.
   *
   * @param id UUID do módulo
   * @returns Observable vazio de confirmação
   */
  deleteModule(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
