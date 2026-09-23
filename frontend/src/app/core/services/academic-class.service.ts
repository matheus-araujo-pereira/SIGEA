import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';
import {
  AcademicClassCloseDTO,
  AcademicClassCreateDTO,
  AcademicClassDetailDTO,
  AcademicClassResponseDTO,
  AcademicClassUpdateDTO,
} from '../models/academic-class.model';

/**
 * Serviço responsável pelo gerenciamento de Turmas Acadêmicas no SIGEA.
 */
@Injectable({
  providedIn: 'root',
})
export class AcademicClassService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/academic/classes';

  /**
   * Lista turmas do sistema com filtros administrativos e paginação.
   */
  listClasses(
    search?: string,
    academicPeriod?: string,
    isClosed?: boolean | null,
    page = 0,
    size = 10,
    sort = 'subjectName,asc'
  ): Observable<ApiResponse<PageResponse<AcademicClassResponseDTO>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (academicPeriod && academicPeriod.trim()) {
      params = params.set('academicPeriod', academicPeriod.trim());
    }
    if (isClosed !== undefined && isClosed !== null) {
      params = params.set('isClosed', isClosed.toString());
    }

    return this.http.get<ApiResponse<PageResponse<AcademicClassResponseDTO>>>(this.baseUrl, { params });
  }

  /**
   * Consulta as turmas do usuário autenticado (docente titular ou estudante matriculado).
   */
  getMyClasses(
    page = 0,
    size = 10,
    sort = 'subjectName,asc'
  ): Observable<ApiResponse<PageResponse<AcademicClassResponseDTO>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<ApiResponse<PageResponse<AcademicClassResponseDTO>>>(`${this.baseUrl}/my-classes`, { params });
  }

  /**
   * Obtém detalhes completos de uma turma por ID.
   */
  getClassById(id: string): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.get<ApiResponse<AcademicClassDetailDTO>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra uma nova turma acadêmica.
   */
  createClass(dto: AcademicClassCreateDTO): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.post<ApiResponse<AcademicClassDetailDTO>>(this.baseUrl, dto);
  }

  /**
   * Atualiza dados de uma turma existente.
   */
  updateClass(id: string, dto: AcademicClassUpdateDTO): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.put<ApiResponse<AcademicClassDetailDTO>>(`${this.baseUrl}/${id}`, dto);
  }

  /**
   * Altera status de encerramento da turma (apenas se não houver correções pendentes).
   */
  updateStatus(id: string, isClosed: boolean): Observable<ApiResponse<AcademicClassResponseDTO>> {
    const body: AcademicClassCloseDTO = { isClosed };
    return this.http.patch<ApiResponse<AcademicClassResponseDTO>>(`${this.baseUrl}/${id}/status`, body);
  }

  /**
   * Exclui uma turma acadêmica.
   */
  deleteClass(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
