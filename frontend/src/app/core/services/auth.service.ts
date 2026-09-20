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
import { User, UserProfileUpdateRequest } from '../models/user.model';
import { ApiResponse } from '../models/api-response.model';

/**
 * Serviço responsável pelo fluxo de autenticação, armazenamento do token JWT e estado da sessão.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly TOKEN_KEY = 'sigea_token';
  private readonly USER_KEY = 'sigea_user';

  /**
   * Signal com os dados do usuário autenticado no momento.
   */
  readonly currentUser = signal<User | null>(this.getStoredUser());

  /**
   * Computed indicando se o usuário está autenticado.
   */
  readonly isAuthenticated = computed(
    () => !!this.currentUser() && !!this.getToken() && !this.isTokenExpired(this.getToken())
  );

  /**
   * Computed indicando se o usuário deve obrigatoriamente redefinir a senha no primeiro acesso.
   */
  readonly mustChangePassword = computed(() => !!this.currentUser()?.mustChangePassword);

  /**
   * Computed indicando se o usuário conectado é Administrador.
   */
  readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

  /**
   * Computed indicando se o usuário conectado é Professor.
   */
  readonly isProfessor = computed(() => this.currentUser()?.role === 'PROFESSOR');

  /**
   * Computed indicando se o usuário conectado é Estudante.
   */
  readonly isStudent = computed(() => this.currentUser()?.role === 'STUDENT');

  /**
   * Verifica se o usuário atual possui um dos perfis fornecidos.
   *
   * @param roles Lista de papéis permitidos
   * @return true se o perfil do usuário constar na lista
   */
  hasRole(roles: string[]): boolean {
    const role = this.currentUser()?.role;
    return !!role && roles.includes(role);
  }

  /**
   * Realiza a autenticação com e-mail e senha.
   *
   * @param request Credenciais de acesso
   * @return Observable com os dados de login
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
   * Redefine obrigatoriamente a senha provisória de primeiro acesso.
   *
   * @param request Dados com senha atual e nova
   * @return Observable com a resposta e novo token
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
   * Consulta os dados cadastrais do perfil do usuário autenticado.
   *
   * @return Observable com dados do perfil
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
   * Atualiza as informações básicas do perfil (ex: nome completo).
   *
   * @param request Dados atualizados
   * @return Observable com o usuário atualizado
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
   * Altera voluntariamente a senha do usuário conectado.
   *
   * @param request Senha atual e nova senha
   * @return Observable vazio
   */
  changePassword(request: ChangePasswordRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>('/api/profile/change-password', request);
  }

  /**
   * Encerra a sessão atual e redireciona para a página de login.
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  /**
   * Obtém o token JWT armazenado localmente, verificando se não está expirado.
   *
   * @return Token JWT válido ou null se expirado/ausente
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
   * Verifica se um token JWT está expirado com base no claim exp.
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
   * Armazena as credenciais de sessão localmente.
   */
  private setSession(token: string, user: User): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  /**
   * Atualiza o usuário persistido e o Signal reativo.
   */
  private updateStoredUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  /**
   * Recupera o usuário salvo no localStorage na inicialização da aplicação.
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
