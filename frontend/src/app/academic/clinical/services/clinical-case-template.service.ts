/**
 * @file clinical-case-template.service.ts
 * @description Serviço para comunicação com os endpoints do Catálogo de Modelos de Casos Clínicos Simulados.
 * @module ClinicalCaseTemplateService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import {
  ClinicalCaseTemplateCreateDTO,
  ClinicalCaseTemplateResponseDTO,
  ClinicalCaseTemplateUpdateDTO,
} from '../models/clinical-case-template.model';

/**
 * Serviço de comunicação HTTP para gerenciamento e seleção de casos clínicos simulados para atividades.
 */
@Injectable({
  providedIn: 'root',
})
export class ClinicalCaseTemplateService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Endpoint da API */
  private readonly apiUrl = '/api/academic/clinical-cases/templates';

  /**
   * Lista modelos de casos clínicos cadastrados, opcionalmente filtrando por código do módulo GTT.
   *
   * @param moduleCode Código opcional do módulo (ex: 'C', 'M')
   * @returns Observable com a lista de modelos de casos clínicos
   */
  listTemplates(moduleCode?: string): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO[]>> {
    let params = new HttpParams();
    if (moduleCode && moduleCode.trim()) {
      params = params.set('moduleCode', moduleCode.trim());
    }
    return this.http.get<ApiResponse<ClinicalCaseTemplateResponseDTO[]>>(this.apiUrl, { params });
  }

  /**
   * Obtém detalhes completos de um modelo por seu identificador único.
   *
   * @param id UUID do modelo
   * @returns Observable com o modelo encontrado
   */
  getTemplateById(id: string): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.get<ApiResponse<ClinicalCaseTemplateResponseDTO>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Cadastra novo modelo de caso clínico no catálogo.
   *
   * @param dto Dados estruturados do novo modelo
   * @returns Observable com o modelo cadastrado
   */
  createTemplate(dto: ClinicalCaseTemplateCreateDTO): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.post<ApiResponse<ClinicalCaseTemplateResponseDTO>>(this.apiUrl, dto);
  }

  /**
   * Atualiza dados de um modelo de caso clínico existente.
   *
   * @param id UUID do modelo
   * @param dto Dados atualizados
   * @returns Observable com o modelo atualizado
   */
  updateTemplate(id: string, dto: ClinicalCaseTemplateUpdateDTO): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.put<ApiResponse<ClinicalCaseTemplateResponseDTO>>(`${this.apiUrl}/${id}`, dto);
  }

  /**
   * Remove permanentemente um modelo de caso clínico do catálogo.
   *
   * @param id UUID do modelo
   * @returns Observable vazio de confirmação
   */
  deleteTemplate(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
