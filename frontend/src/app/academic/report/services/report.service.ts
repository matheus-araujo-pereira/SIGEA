/**
 * @file report.service.ts
 * @description Serviço responsável pela emissão e download de relatórios formais em PDF e bases de dados CSV para pesquisas científicas.
 * @module ReportService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { ToastService } from '../../../common/services/toast.service';

/**
 * Serviço para download de relatórios de auditoria clínica e boletins epidemiológicos.
 */
@Injectable({
  providedIn: 'root',
})
export class ReportService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Serviço de notificações Toast */
  private readonly toast = inject(ToastService);
  /** Endpoint base de relatórios */
  private readonly apiUrl = '/api/reports';

  /**
   * Realiza o download do Relatório Oficial de Auditoria Clínica Individual em PDF.
   *
   * @param submissionId Identificador único da submissão discente
   * @param filename Nome customizado do arquivo para download (opcional)
   * @returns Observable emitindo o Blob do arquivo binário PDF
   */
  downloadSubmissionPdf(submissionId: string, filename?: string): Observable<Blob> {
    const targetFilename = filename || `relatorio-auditoria-${submissionId}.pdf`;
    return this.http.get(`${this.apiUrl}/submissions/${submissionId}/pdf`, { responseType: 'blob' }).pipe(
      tap((blob) => {
        this.saveBlob(blob, targetFilename);
        this.toast.success('Relatório de auditoria clínica exportado com sucesso.');
      }),
      catchError((err) => {
        this.toast.error('Não foi possível exportar o relatório de auditoria.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Realiza o download do Boletim Epidemiológico da Turma em PDF.
   *
   * @param classId Identificador único da turma acadêmica
   * @param filename Nome customizado do arquivo para download (opcional)
   * @returns Observable emitindo o Blob do arquivo binário PDF
   */
  downloadClassBulletinPdf(classId: string, filename?: string): Observable<Blob> {
    const targetFilename = filename || `boletim-epidemiologico-${classId}.pdf`;
    return this.http.get(`${this.apiUrl}/classes/${classId}/bulletin/pdf`, { responseType: 'blob' }).pipe(
      tap((blob) => {
        this.saveBlob(blob, targetFilename);
        this.toast.success('Boletim epidemiológico da turma exportado com sucesso.');
      }),
      catchError((err) => {
        this.toast.error('Não foi possível exportar o boletim epidemiológico.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Realiza o download da base tabular de dados brutos da turma em CSV para pesquisas científicas.
   *
   * @param classId Identificador único da turma acadêmica
   * @param filename Nome customizado do arquivo para download (opcional)
   * @returns Observable emitindo o Blob do arquivo de texto CSV
   */
  downloadClassResearchCsv(classId: string, filename?: string): Observable<Blob> {
    const targetFilename = filename || `dados-pesquisa-turma-${classId}.csv`;
    return this.http.get(`${this.apiUrl}/classes/${classId}/research/csv`, { responseType: 'blob' }).pipe(
      tap((blob) => {
        this.saveBlob(blob, targetFilename);
        this.toast.success('Base de dados de pesquisa (CSV) exportada com sucesso.');
      }),
      catchError((err) => {
        this.toast.error('Não foi possível exportar a base de dados em CSV.');
        return throwError(() => err);
      })
    );
  }

  /**
   * Dispara o download do arquivo no navegador a partir de um Blob em memória.
   *
   * @param blob Objeto binário a ser baixado
   * @param filename Nome do arquivo com extensão
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
