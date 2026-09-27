import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { ClassDashboardDTO } from '../models/class-dashboard.model';

/**
 * Serviço responsável pela obtenção dos indicadores analíticos e métricas GTT da turma.
 * Consome o endpoint `/api/academic/classes/{classId}/dashboard`.
 */
@Injectable({
  providedIn: 'root',
})
export class ClassDashboardService {
  /** Cliente HTTP Angular */
  private readonly http = inject(HttpClient);
  /** Endpoint base das turmas acadêmicas */
  private readonly baseUrl = '/api/academic/classes';

  /**
   * Obtém os indicadores analíticos oficiais do GTT e desempenho pedagógico da turma.
   *
   * @param classId Identificador único da turma.
   * @returns Observable com as taxas epidemiológicas e indicadores pedagógicos.
   */
  getClassDashboard(classId: string): Observable<ApiResponse<ClassDashboardDTO>> {
    return this.http.get<ApiResponse<ClassDashboardDTO>>(`${this.baseUrl}/${classId}/dashboard`);
  }
}
