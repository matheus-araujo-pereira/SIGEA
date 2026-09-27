import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import {
  ClinicalCaseTemplateCreateDTO,
  ClinicalCaseTemplateResponseDTO,
  ClinicalCaseTemplateUpdateDTO,
} from '../models/clinical-case-template.model';

/**
 * Serviço para comunicação com os endpoints do Catálogo de Casos Clínicos Simulados.
 */
@Injectable({
  providedIn: 'root',
})
export class ClinicalCaseTemplateService {
  private http = inject(HttpClient);
  private apiUrl = '/api/academic/clinical-cases/templates';

  /**
   * Lista modelos de casos clínicos cadastrados, opcionalmente filtrando por código do módulo GTT.
   */
  listTemplates(moduleCode?: string): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO[]>> {
    let params = new HttpParams();
    if (moduleCode && moduleCode.trim()) {
      params = params.set('moduleCode', moduleCode.trim());
    }
    return this.http.get<ApiResponse<ClinicalCaseTemplateResponseDTO[]>>(this.apiUrl, { params });
  }

  /**
   * Obtém detalhes de um modelo por ID.
   */
  getTemplateById(id: string): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.get<ApiResponse<ClinicalCaseTemplateResponseDTO>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Cadastra novo modelo de caso clínico.
   */
  createTemplate(dto: ClinicalCaseTemplateCreateDTO): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.post<ApiResponse<ClinicalCaseTemplateResponseDTO>>(this.apiUrl, dto);
  }

  /**
   * Atualiza modelo existente.
   */
  updateTemplate(id: string, dto: ClinicalCaseTemplateUpdateDTO): Observable<ApiResponse<ClinicalCaseTemplateResponseDTO>> {
    return this.http.put<ApiResponse<ClinicalCaseTemplateResponseDTO>>(`${this.apiUrl}/${id}`, dto);
  }

  /**
   * Remove modelo do catálogo.
   */
  deleteTemplate(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
