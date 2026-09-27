import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import {
  LoginRequest,
  LoginResponse,
  FirstLoginChangePasswordRequest,
  ChangePasswordRequest,
} from '../models/auth.model';
import { User, UserProfileUpdateRequest } from '../../user/models/user.model';
import { ApiResponse } from '../../common/models/api-response.model';

/**
 * Serviço de autenticação, governança de credenciais e gestão de sessão JWT do SIGEA via Signals.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  /** Cliente HTTP Angular para comunicação REST */
  private readonly http = inject(HttpClient);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);

  /** Chave de armazenamento local para o token JWT */
  private readonly TOKEN_KEY = 'sigea_token';
  /** Chave de armazenamento local para os dados do usuário autenticado */
  private readonly USER_KEY = 'sigea_user';

  /**
   * Signal reativo contendo o usuário autenticado na sessão atual (ou null se desconectado).
   */
  readonly currentUser = signal<User | null>(this.getStoredUser());

  /**
   * Computed reativo indicando se há uma sessão válida e não expirada.
   */
  readonly isAuthenticated = computed(
    () => !!this.currentUser() && !!this.getToken() && !this.isTokenExpired(this.getToken())
  );

  /**
   * Computed reativo indicando se o usuário deve realizar a troca obrigatória de senha no primeiro login.
   */
  readonly mustChangePassword = computed(() => !!this.currentUser()?.mustChangePassword);

  /**
   * Computed reativo indicando se o usuário autenticado possui o perfil ADMIN.
   */
  readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

  /**
   * Computed reativo indicando se o usuário autenticado possui o perfil PROFESSOR.
   */
  readonly isProfessor = computed(() => this.currentUser()?.role === 'PROFESSOR');

  /**
   * Computed reativo indicando se o usuário autenticado possui o perfil STUDENT.
   */
  readonly isStudent = computed(() => this.currentUser()?.role === 'STUDENT');

  /**
   * Verifica se o usuário autenticado possui pelo menos um dos perfis RBAC informados.
   *
   * @param roles Lista de papéis permitidos para a operação ou rota.
   * @returns Booleano indicando autorização positiva.
   */
  hasRole(roles: string[]): boolean {
    const role = this.currentUser()?.role;
    return !!role && roles.includes(role);
  }

  /**
   * Realiza a autenticação de login enviando credenciais ao backend Spring Boot.
   *
   * @param request Credenciais com e-mail institucional e senha.
   * @returns Observable emitindo ApiResponse com token JWT e dados do usuário.
   */
  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>('/api/auth/login', request).pipe(
      tap((response) => {
        if (response.success && response.data) {
          this.setSession(response.data.token, response.data.user);
        }
      })
    );
  }

  /**
   * Executa a troca obrigatória da senha provisória no primeiro acesso do usuário.
   *
   * @param request Dados contendo a senha provisória e a nova senha pessoal.
   * @returns Observable emitindo ApiResponse com novo token JWT e dados atualizados.
   */
  firstLoginChangePassword(
    request: FirstLoginChangePasswordRequest
  ): Observable<ApiResponse<LoginResponse>> {
    return this.http
      .post<ApiResponse<LoginResponse>>('/api/auth/first-login-change-password', request)
      .pipe(
        tap((response) => {
          if (response.success && response.data) {
            this.setSession(response.data.token, response.data.user);
          }
        })
      );
  }

  /**
   * Consulta os dados cadastrais do perfil do usuário autenticado no momento.
   *
   * @returns Observable emitindo ApiResponse com o registro do usuário.
   */
  getProfile(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>('/api/profile').pipe(
      tap((response) => {
        if (response.success && response.data) {
          this.updateStoredUser(response.data);
        }
      })
    );
  }

  /**
   * Atualiza as informações básicas cadastrais do perfil do próprio usuário (ex: nome completo).
   *
   * @param request DTO com os novos dados cadastrais.
   * @returns Observable emitindo ApiResponse com o usuário atualizado.
   */
  updateProfile(request: UserProfileUpdateRequest): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>('/api/profile', request).pipe(
      tap((response) => {
        if (response.success && response.data) {
          this.updateStoredUser(response.data);
        }
      })
    );
  }

  /**
   * Altera voluntariamente a senha do usuário conectado na tela de perfil.
   *
   * @param request DTO com a senha atual e a nova senha.
   * @returns Observable emitindo resposta vazia em caso de sucesso.
   */
  changePassword(request: ChangePasswordRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>('/api/profile/change-password', request);
  }

  /**
   * Encerra a sessão ativa, limpa tokens do localStorage e redireciona para a tela de login.
   *
   * @returns void
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  /**
   * Recupera o token JWT ativo do armazenamento local, verificando validade e expiração.
   *
   * @returns Token JWT string ou null se expirado ou inexistente.
   */
  getToken(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (!token) return null;
    if (this.isTokenExpired(token)) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
      this.currentUser.set(null);
      return null;
    }
    return token;
  }

  /**
   * Inspeciona a validade temporal de um token JWT decodificando o claim 'exp'.
   *
   * @param token Token JWT opcional (utiliza o armazenado se omitido).
   * @returns Booleano indicando se o token já expirou.
   */
  isTokenExpired(token?: string | null): boolean {
    const t = token !== undefined ? token : localStorage.getItem(this.TOKEN_KEY);
    if (!t) return true;
    try {
      const parts = t.split('.');
      if (parts.length < 2) return false;
      const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const payloadJson = decodeURIComponent(
        atob(payloadBase64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const payload = JSON.parse(payloadJson);
      if (!payload.exp) return false;
      return payload.exp * 1000 < Date.now();
    } catch {
      return false;
    }
  }

  /**
   * Persiste as credenciais de sessão localmente no browser e atualiza os Signals.
   *
   * @param token Token JWT recebido do servidor.
   * @param user Objeto do usuário autenticado.
   */
  private setSession(token: string, user: User): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  /**
   * Atualiza o registro do usuário armazenado localmente e reflete no Signal reativo.
   *
   * @param user Dados atualizados do usuário.
   */
  private updateStoredUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  /**
   * Recupera o usuário salvo no localStorage na inicialização da aplicação Angular.
   *
   * @returns User ou null se não houver dados válidos persistidos.
   */
  private getStoredUser(): User | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }
}
