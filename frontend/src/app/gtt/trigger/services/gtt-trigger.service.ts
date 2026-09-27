/**
 * @file gtt-trigger.service.ts
 * @description Serviço responsável pela comunicação HTTP com os endpoints de Gatilhos GTT (53 gatilhos clínicos).
 * @module GttTriggerService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { PageResponse } from '../../../common/models/page.model';
import {
  GttTrigger,
  GttTriggerCreateRequest,
  GttTriggerStatusUpdateRequest,
  GttTriggerUpdateRequest,
} from '../models/gtt-trigger.model';

/**
 * Serviço de comunicação HTTP para consulta, busca e parametrização dos 53 gatilhos clínicos do IHI-GTT.
 */
@Injectable({
  providedIn: 'root',
})
export class GttTriggerService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Endpoint base da API de gatilhos */
  private readonly baseUrl = '/api/gtt/triggers';

  /**
   * Consulta os gatilhos ativos para o guia educacional de consulta rápida, opcionalmente filtrados por módulo.
   *
   * @param moduleId UUID opcional do módulo para filtrar os gatilhos retornados
   * @returns Observable com a lista de gatilhos clínicos
   */
  getCatalog(moduleId?: string): Observable<ApiResponse<GttTrigger[]>> {
    let params = new HttpParams();
    if (moduleId) {
      params = params.set('moduleId', moduleId);
    }
    return this.http.get<ApiResponse<GttTrigger[]>>(`${this.baseUrl}/catalog`, { params });
  }

  /**
   * Lista gatilhos com paginação e filtros administrativos avançados.
   *
   * @param moduleId Filtro opcional por módulo
   * @param search Termo opcional de busca por código ou nome
   * @param isActive Filtro opcional por status ativo/inativo
   * @param page Índice da página (0-indexed)
   * @param size Quantidade de itens por página
   * @param sort Critério de ordenação
   * @returns Observable com a resposta paginada de gatilhos
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
   * Obtém os detalhes completos de um gatilho por seu identificador único.
   *
   * @param id UUID do gatilho
   * @returns Observable com o gatilho encontrado
   */
  getTriggerById(id: string): Observable<ApiResponse<GttTrigger>> {
    return this.http.get<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra um novo gatilho clínico no sistema.
   *
   * @param data DTO com dados de criação do gatilho
   * @returns Observable com o gatilho cadastrado
   */
  createTrigger(data: GttTriggerCreateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.post<ApiResponse<GttTrigger>>(this.baseUrl, data);
  }

  /**
   * Atualiza os dados de um gatilho existente.
   *
   * @param id UUID do gatilho
   * @param data DTO com os novos dados
   * @returns Observable com o gatilho atualizado
   */
  updateTrigger(id: string, data: GttTriggerUpdateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.put<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de um gatilho clínico.
   *
   * @param id UUID do gatilho
   * @param data Payload com o novo status
   * @returns Observable com o gatilho atualizado
   */
  updateStatus(id: string, data: GttTriggerStatusUpdateRequest): Observable<ApiResponse<GttTrigger>> {
    return this.http.patch<ApiResponse<GttTrigger>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui um gatilho clínico do sistema.
   *
   * @param id UUID do gatilho
   * @returns Observable vazio de confirmação
   */
  deleteTrigger(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
