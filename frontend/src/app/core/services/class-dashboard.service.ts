import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { ClassDashboardDTO } from '../models/class-dashboard.model';

/**
 * Serviço responsável pela obtenção dos indicadores analíticos e métricas GTT da turma.
 */
@Injectable({
  providedIn: 'root',
})
export class ClassDashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/academic/classes';

  /**
   * Obtém os indicadores analíticos oficiais do GTT e desempenho pedagógico da turma.
   */
  getClassDashboard(classId: string): Observable<ApiResponse<ClassDashboardDTO>> {
    return this.http.get<ApiResponse<ClassDashboardDTO>>(`${this.baseUrl}/${classId}/dashboard`);
  }
}
