import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  User,
  UserCreateRequest,
  UserCreateResponse,
  UserRole,
  UserStatusUpdateRequest,
  UserUpdateRequest,
} from '../models/user.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';

/**
 * Serviço responsável pela gestão de usuários do SIGEA-GTT (exclusivo para Administradores).
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/users';

  /**
   * Lista usuários com paginação de 10 registros e filtros de pesquisa.
   *
   * @param search   Termo de busca (nome, e-mail ou matrícula)
   * @param role     Filtro de perfil (ADMIN, PROFESSOR, STUDENT)
   * @param isActive Filtro de status ativo/inativo
   * @param page     Número da página (padrão: 0)
   * @param size     Tamanho da página (padrão: 10)
   * @param sort     Ordenação (padrão: fullName,asc)
   * @return Observable com coleção paginada
   */
  listUsers(
    search?: string,
    role?: UserRole | '',
    isActive?: boolean | null,
    page = 0,
    size = 10,
    sort = 'fullName,asc'
  ): Observable<ApiResponse<PageResponse<User>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }
    if (role) {
      params = params.set('role', role);
    }
    if (isActive !== undefined && isActive !== null) {
      params = params.set('isActive', isActive.toString());
    }

    return this.http.get<ApiResponse<PageResponse<User>>>(this.baseUrl, { params });
  }

  /**
   * Cria um novo usuário gerando senha provisória automática.
   *
   * @param request Dados do novo usuário
   * @return Observable com usuário criado e senha provisória
   */
  createUser(request: UserCreateRequest): Observable<ApiResponse<UserCreateResponse>> {
    return this.http.post<ApiResponse<UserCreateResponse>>(this.baseUrl, request);
  }

  /**
   * Obtém detalhes de um usuário específico por ID.
   *
   * @param id Identificador único (UUID)
   * @return Observable com dados do usuário
   */
  getUserById(id: string): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Atualiza dados cadastrais de um usuário existente.
   *
   * @param id      Identificador único do usuário
   * @param request Dados cadastrais atualizados
   * @return Observable com dados salvos
   */
  updateUser(id: string, request: UserUpdateRequest): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.baseUrl}/${id}`, request);
  }

  /**
   * Altera o status ativo/inativo do usuário.
   *
   * @param id      Identificador do usuário
   * @param request Novo status
   * @return Observable com usuário atualizado
   */
  updateStatus(id: string, request: UserStatusUpdateRequest): Observable<ApiResponse<User>> {
    return this.http.patch<ApiResponse<User>>(`${this.baseUrl}/${id}/status`, request);
  }

  /**
   * Exclui permanentemente um usuário do sistema.
   *
   * @param id Identificador do usuário
   * @return Observable vazio
   */
  deleteUser(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
