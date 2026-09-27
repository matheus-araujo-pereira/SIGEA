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
import { PageResponse } from '../../common/models/page.model';
import { ApiResponse } from '../../common/models/api-response.model';

/**
 * Serviço de integração com os endpoints RESTful de governança de usuários do SIGEA (`/api/users`).
 *
 * Exclusivo para operações administrativas: listagem paginada com busca textual, cadastro com
 * geração automática de senha provisória, edição cadastral, alternância de status ativo/inativo
 * e redefinição de senha para o padrão do sistema.
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  /** Cliente HTTP Angular para operações REST */
  private readonly http = inject(HttpClient);
  /** Endpoint base de gestão de usuários */
  private readonly baseUrl = '/api/users';

  /**
   * Consulta a lista de usuários com paginação estrita (10 registros) e múltiplos filtros acumulativos.
   *
   * @param search   Termo de busca textual (nome, e-mail institucional ou matrícula).
   * @param role     Filtro por perfil RBAC (ADMIN, PROFESSOR ou STUDENT).
   * @param isActive Filtro por status ativo/inativo (opcional).
   * @param page     Número da página (0-based, padrão: 0).
   * @param size     Quantidade de registros por página (padrão institucional: 10).
   * @param sort     Critério e direção de ordenação (padrão: 'fullName,asc').
   * @returns Observable contendo resposta paginada padronizada.
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
   * Cadastra um novo usuário no SIGEA gerando uma senha provisória aleatória com alta entropia.
   *
   * @param request Dados cadastrais com nome, e-mail UFS, perfil e matrícula se aplicável.
   * @returns Observable contendo o usuário criado e a senha provisória.
   */
  createUser(request: UserCreateRequest): Observable<ApiResponse<UserCreateResponse>> {
    return this.http.post<ApiResponse<UserCreateResponse>>(this.baseUrl, request);
  }

  /**
   * Obtém os detalhes completos de um usuário específico a partir do seu identificador UUID.
   *
   * @param id Identificador único universal do usuário.
   * @returns Observable com os dados do usuário localizado.
   */
  getUserById(id: string): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.baseUrl}/${id}`);
  }

  /**
   * Atualiza os dados cadastrais de um usuário registrado.
   *
   * @param id      Identificador UUID do usuário a ser alterado.
   * @param request DTO com as alterações cadastrais.
   * @returns Observable com o usuário atualizado.
   */
  updateUser(id: string, request: UserUpdateRequest): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.baseUrl}/${id}`, request);
  }

  /**
   * Altera pontualmente o status de ativação ou desativação de uma conta de usuário.
   *
   * @param id      Identificador UUID do usuário.
   * @param request DTO com o novo status booleano.
   * @returns Observable com o usuário atualizado.
   */
  updateStatus(id: string, request: UserStatusUpdateRequest): Observable<ApiResponse<User>> {
    return this.http.patch<ApiResponse<User>>(`${this.baseUrl}/${id}/status`, request);
  }

  /**
   * Redefine emergencialmente a senha do usuário para a senha padrão institucional
   * e reativa compulsoriamente a flag `mustChangePassword = true`.
   *
   * @param id Identificador UUID do usuário.
   * @returns Observable com os dados do usuário e a senha redefinida.
   */
  resetPassword(id: string): Observable<ApiResponse<UserCreateResponse>> {
    return this.http.patch<ApiResponse<UserCreateResponse>>(`${this.baseUrl}/${id}/reset-password`, {});
  }

  /**
   * Exclui definitivamente um registro de usuário da base de dados do SIGEA.
   *
   * @param id Identificador UUID do usuário a excluir.
   * @returns Observable vazio em caso de sucesso.
   */
  deleteUser(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
