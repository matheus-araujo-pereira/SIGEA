/**
 * @file academic-class.service.ts
 * @description Serviço responsável pelo gerenciamento de Turmas Acadêmicas no SIGEA (br.ufs.sigea.academic.clazz).
 * @module AcademicClassService
 */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../common/models/api-response.model';
import { PageResponse } from '../../../common/models/page.model';
import {
  AcademicClassCloseDTO,
  AcademicClassCreateDTO,
  AcademicClassDetailDTO,
  AcademicClassResponseDTO,
  AcademicClassUpdateDTO,
} from '../models/academic-class.model';

/**
 * Serviço de comunicação HTTP para ciclo de vida de turmas acadêmicas, matrículas e encerramento letivo.
 */
@Injectable({
  providedIn: 'root',
})
export class AcademicClassService {
  /** Cliente HTTP injetado */
  private readonly http = inject(HttpClient);
  /** Endpoint base de turmas acadêmicas */
  private readonly baseUrl = '/api/academic/classes';

  /**
   * Lista turmas do sistema com filtros administrativos e paginação.
   *
   * @param search Termo opcional de busca textual por disciplina ou código
   * @param academicPeriod Período letivo opcional (ex: '2026.1')
   * @param isClosed Filtro opcional por status de encerramento
   * @param page Índice da página (0-indexed)
   * @param size Quantidade de turmas por página
   * @param sort Critério de ordenação
   * @returns Observable com a resposta paginada
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
   * Consulta as turmas associadas ao usuário autenticado (docente titular ou estudante matriculado).
   *
   * @param page Índice da página
   * @param size Quantidade por página
   * @param sort Ordenação
   * @returns Observable com as turmas do usuário
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
   * Obtém os detalhes completos de uma turma por seu identificador único.
   *
   * @param id UUID da turma
   * @returns Observable com a turma, lista de estudantes e atividades
   */
  getClassById(id: string): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.get<ApiResponse<AcademicClassDetailDTO>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cadastra uma nova turma acadêmica.
   *
   * @param dto Dados de criação da turma
   * @returns Observable com a turma cadastrada
   */
  createClass(dto: AcademicClassCreateDTO): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.post<ApiResponse<AcademicClassDetailDTO>>(this.baseUrl, dto);
  }

  /**
   * Atualiza os dados de uma turma existente.
   *
   * @param id UUID da turma
   * @param dto Dados atualizados
   * @returns Observable com a turma atualizada
   */
  updateClass(id: string, dto: AcademicClassUpdateDTO): Observable<ApiResponse<AcademicClassDetailDTO>> {
    return this.http.put<ApiResponse<AcademicClassDetailDTO>>(`${this.baseUrl}/${id}`, dto);
  }

  /**
   * Altera o status de encerramento da turma.
   *
   * @param id UUID da turma
   * @param isClosed Novo status de encerramento
   * @returns Observable com o resumo da turma atualizada
   */
  updateStatus(id: string, isClosed: boolean): Observable<ApiResponse<AcademicClassResponseDTO>> {
    const body: AcademicClassCloseDTO = { isClosed };
    return this.http.patch<ApiResponse<AcademicClassResponseDTO>>(`${this.baseUrl}/${id}/status`, body);
  }

  /**
   * Exclui uma turma acadêmica do sistema.
   *
   * @param id UUID da turma
   * @returns Observable vazio de confirmação
   */
  deleteClass(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
