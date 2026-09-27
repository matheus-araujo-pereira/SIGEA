/**
 * @file harm-severity.service.ts
 * @description Serviço responsável pela comunicação HTTP com os endpoints de Gravidades de Dano (NCC MERP).
 * @module HarmSeverityService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { PageResponse } from '../../../common/models/page.model';
import {
  HarmSeverity,
  HarmSeverityCreateRequest,
  HarmSeverityStatusUpdateRequest,
  HarmSeverityUpdateRequest,
} from '../models/harm-severity.model';

/**
 * Serviço de comunicação HTTP para consulta e parametrização das categorias de gravidade NCC MERP (A a I).
 */
@Injectable({
  providedIn: 'root',
})
export class HarmSeverityService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Endpoint base da API de gravidades */
  private readonly baseUrl = '/api/gtt/severities';

  /**
   * Consulta o guia interativo de gravidades ordenadas de A a I para suporte à decisão clínica.
   *
   * @returns Observable com a lista completa e ordenada de gravidades
   */
  getGuide(): Observable<ApiResponse<HarmSeverity[]>> {
    return this.http.get<ApiResponse<HarmSeverity[]>>(`${this.baseUrl}/guide`);
  }

  /**
   * Lista gravidades com paginação e filtros administrativos.
   *
   * @param search Termo opcional de busca textual por letra ou descrição
   * @param isHarm Filtro opcional por configuração de dano (true para E a I, false para A a D)
   * @param isActive Filtro opcional por status ativo/inativo
   * @param page Índice da página (0-indexed)
   * @param size Quantidade de registros por página
   * @param sort Critério de ordenação
   * @returns Observable com a resposta paginada
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
   * Lista gravidades que configuram dano (E a I) com filtro ativo para seleção clínica.
   *
   * @param search Busca textual opcional
   * @param isActive Filtro de status ativo (padrão true)
   * @param page Índice da página
   * @param size Quantidade por página
   * @returns Observable com a listagem de gravidades de dano
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
   * Obtém os dados completos de uma gravidade por seu UUID.
   *
   * @param id UUID da gravidade
   * @returns Observable com a gravidade encontrada
   */
  getSeverityById(id: string): Observable<ApiResponse<HarmSeverity>> {
    return this.http.get<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra uma nova categoria de gravidade.
   *
   * @param data DTO de criação
   * @returns Observable com a gravidade criada
   */
  createSeverity(data: HarmSeverityCreateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.post<ApiResponse<HarmSeverity>>(this.baseUrl, data);
  }

  /**
   * Atualiza uma categoria de gravidade existente.
   *
   * @param id UUID da gravidade
   * @param data DTO com os novos dados
   * @returns Observable com a gravidade atualizada
   */
  updateSeverity(id: string, data: HarmSeverityUpdateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.put<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Altera o status ativo/inativo de uma categoria de gravidade.
   *
   * @param id UUID da gravidade
   * @param data Payload com o novo status
   * @returns Observable com a gravidade atualizada
   */
  updateStatus(id: string, data: HarmSeverityStatusUpdateRequest): Observable<ApiResponse<HarmSeverity>> {
    return this.http.patch<ApiResponse<HarmSeverity>>(`${this.baseUrl}/${id}/status`, data);
  }

  /**
   * Exclui uma categoria de gravidade do sistema.
   *
   * @param id UUID da gravidade
   * @returns Observable vazio de confirmação
   */
  deleteSeverity(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
