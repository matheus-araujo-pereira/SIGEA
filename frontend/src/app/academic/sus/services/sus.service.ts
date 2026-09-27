import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import {
  SusEvaluationCreateDTO,
  SusEvaluationResponseDTO,
  SusClassSummaryDTO,
  SusGeneralSummaryDTO,
  SusEvaluationPreviewDTO,
} from '../models/sus.model';
import { ToastService } from '../../../common/services/toast.service';

/**
 * Serviço responsável pela gestão de avaliações da Escala SUS (System Usability Scale),
 * cálculo psicométrico de usabilidade e exportação de dados para SPSS, R e Python.
 */
@Injectable({
  providedIn: 'root',
})
export class SusService {
  /** Cliente HTTP do Angular */
  private readonly http = inject(HttpClient);
  /** Serviço de mensagens toast */
  private readonly toast = inject(ToastService);
  /** Endpoint base da API de avaliação SUS */
  private readonly apiUrl = '/api/sus';

  /**
   * Envia uma avaliação respondida da Escala SUS.
   */
  submitEvaluation(dto: SusEvaluationCreateDTO): Observable<ApiResponse<SusEvaluationResponseDTO>> {
    return this.http.post<ApiResponse<SusEvaluationResponseDTO>>(this.apiUrl, dto).pipe(
      tap(() => this.toast.success('Avaliação de usabilidade enviada com sucesso!')),
      catchError((err) => {
        this.toast.error(err?.error?.message || 'Erro ao enviar avaliação de usabilidade.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Solicita o cálculo prévio do diagnóstico psicométrico SUS diretamente à camada de serviço do backend.
   */
  calculatePreview(dto: SusEvaluationCreateDTO): Observable<ApiResponse<SusEvaluationPreviewDTO>> {
    return this.http.post<ApiResponse<SusEvaluationPreviewDTO>>(`${this.apiUrl}/preview`, dto);
  }

  /**
   * Consulta a avaliação do estudante logado (para uma turma ou geral).
   */
  getMyEvaluation(classId?: string): Observable<ApiResponse<SusEvaluationResponseDTO | null>> {
    let params = new HttpParams();
    if (classId) {
      params = params.set('classId', classId);
    }
    return this.http.get<ApiResponse<SusEvaluationResponseDTO | null>>(`${this.apiUrl}/my-evaluation`, { params });
  }

  /**
   * Lista o histórico de avaliações do estudante autenticado.
   */
  getMyEvaluations(): Observable<ApiResponse<SusEvaluationResponseDTO[]>> {
    return this.http.get<ApiResponse<SusEvaluationResponseDTO[]>>(`${this.apiUrl}/my-evaluations`);
  }

  /**
   * Obtém o sumário psicométrico consolidado do SUS para uma turma.
   */
  getClassSummary(classId: string): Observable<ApiResponse<SusClassSummaryDTO>> {
    return this.http.get<ApiResponse<SusClassSummaryDTO>>(`${this.apiUrl}/classes/${classId}/summary`);
  }

  /**
   * Obtém o sumário psicométrico global institucional do SUS (Admin).
   */
  getGeneralSummary(): Observable<ApiResponse<SusGeneralSummaryDTO>> {
    return this.http.get<ApiResponse<SusGeneralSummaryDTO>>(`${this.apiUrl}/summary`);
  }

  /**
   * Realiza o download dos dados brutos do questionário da turma em CSV para SPSS, R e Python.
   */
  downloadClassSusCsv(classId: string, filename?: string): Observable<Blob> {
    const targetFilename = filename || `pesquisa-sus-turma-${classId}.csv`;
    return this.http.get(`${this.apiUrl}/classes/${classId}/csv`, { responseType: 'blob' }).pipe(
      tap((blob) => {
        this.saveBlob(blob, targetFilename);
        this.toast.success('Dados da pesquisa SUS exportados com sucesso em CSV.');
      }),
      catchError((err) => {
        this.toast.error('Não foi possível exportar os dados do questionário SUS.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Realiza o download global de todas as avaliações SUS em CSV.
   */
  downloadGlobalSusCsv(filename?: string): Observable<Blob> {
    const targetFilename = filename || `pesquisa-sus-global.csv`;
    return this.http.get(`${this.apiUrl}/csv`, { responseType: 'blob' }).pipe(
      tap((blob) => {
        this.saveBlob(blob, targetFilename);
        this.toast.success('Dados globais da pesquisa SUS exportados com sucesso.');
      }),
      catchError((err) => {
        this.toast.error('Não foi possível exportar os dados globais do SUS.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Dispara o download de arquivo a partir de um Blob no navegador.
   */
  saveBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }
}
