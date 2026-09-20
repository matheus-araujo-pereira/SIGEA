import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { User } from '../models/user.model';
import { ApiResponse } from '../models/api-response.model';
import { LoginResponse } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock: { navigate: jest.Mock };

  const mockUser: User = {
    id: '123',
    fullName: 'Administrador Teste',
    email: 'admin@academico.ufs.br',
    role: 'ADMIN',
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(() => {
    localStorage.clear();
    routerMock = { navigate: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerMock },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('deve ser instanciado e inicializar sem usuário se storage estiver vazio', () => {
    expect(service).toBeTruthy();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.mustChangePassword()).toBe(false);
  });

  it('deve carregar usuário válido salvo no localStorage', () => {
    TestBed.resetTestingModule();
    localStorage.setItem('sigea_user', JSON.stringify(mockUser));
    localStorage.setItem('sigea_token', 'valid-jwt');

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerMock },
      ],
    });

    const freshService = TestBed.inject(AuthService);
    expect(freshService.getToken()).toBe('valid-jwt');
    expect(freshService.currentUser()?.email).toBe(mockUser.email);
  });

  it('deve tratar JSON corrompido no localStorage retornando null', () => {
    TestBed.resetTestingModule();
    localStorage.setItem('sigea_user', '{invalido');

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerMock },
      ],
    });

    const freshService = TestBed.inject(AuthService);
    expect(freshService.currentUser()).toBeNull();
  });

  it('deve realizar login, armazenar sessão e atualizar signals', () => {
    const loginResp: ApiResponse<LoginResponse> = {
      success: true,
      message: 'Login realizado',
      data: {
        token: 'token-jwt-123',
        tokenType: 'Bearer',
        user: mockUser,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.login({ email: 'admin@academico.ufs.br', password: 'pwd' }).subscribe((res) => {
      expect(res.data.token).toBe('token-jwt-123');
      expect(service.currentUser()?.email).toBe('admin@academico.ufs.br');
      expect(service.getToken()).toBe('token-jwt-123');
      expect(service.isAuthenticated()).toBe(true);
      expect(service.isAdmin()).toBe(true);
      expect(service.isProfessor()).toBe(false);
      expect(service.isStudent()).toBe(false);
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(loginResp);
  });

  it('deve processar alteração de primeiro login e atualizar token', () => {
    const updatedUser = { ...mockUser, mustChangePassword: false };
    const resp: ApiResponse<LoginResponse> = {
      success: true,
      message: 'Sucesso',
      data: {
        token: 'new-token',
        tokenType: 'Bearer',
        user: updatedUser,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service
      .firstLoginChangePassword({
        currentPassword: 'old',
        newPassword: 'new',
        confirmPassword: 'new',
      })
      .subscribe((res) => {
        expect(res.data.token).toBe('new-token');
        expect(service.currentUser()?.mustChangePassword).toBe(false);
      });

    const req = httpMock.expectOne('/api/auth/first-login-change-password');
    expect(req.request.method).toBe('POST');
    req.flush(resp);
  });

  it('deve buscar perfil e atualizar usuário salvo', () => {
    const resp: ApiResponse<User> = {
      success: true,
      message: 'Perfil obtido',
      data: mockUser,
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.getProfile().subscribe((res) => {
      expect(res.data.fullName).toBe(mockUser.fullName);
      expect(service.currentUser()?.fullName).toBe(mockUser.fullName);
    });

    const req = httpMock.expectOne('/api/profile');
    expect(req.request.method).toBe('GET');
    req.flush(resp);
  });

  it('deve atualizar perfil e sincronizar localStorage', () => {
    const updatedUser = { ...mockUser, fullName: 'Nome Atualizado' };
    const resp: ApiResponse<User> = {
      success: true,
      message: 'Perfil atualizado',
      data: updatedUser,
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.updateProfile({ fullName: 'Nome Atualizado' }).subscribe((res) => {
      expect(res.data.fullName).toBe('Nome Atualizado');
      expect(service.currentUser()?.fullName).toBe('Nome Atualizado');
    });

    const req = httpMock.expectOne('/api/profile');
    expect(req.request.method).toBe('PUT');
    req.flush(resp);
  });

  it('deve alterar senha voluntariamente', () => {
    const resp: ApiResponse<void> = {
      success: true,
      message: 'Senha alterada',
      data: undefined as unknown as void,
      timestamp: '2026-09-14T00:00:00Z',
    };

    service
      .changePassword({
        currentPassword: 'c',
        newPassword: 'n',
        confirmPassword: 'n',
      })
      .subscribe((res) => {
        expect(res.success).toBe(true);
      });

    const req = httpMock.expectOne('/api/profile/change-password');
    expect(req.request.method).toBe('POST');
    req.flush(resp);
  });

  it('deve encerrar sessão ao chamar logout', () => {
    localStorage.setItem('sigea_token', 'token');
    localStorage.setItem('sigea_user', JSON.stringify(mockUser));

    service.logout();

    expect(service.getToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('deve calcular corretamento isProfessor e isStudent', () => {
    const profUser: User = { ...mockUser, role: 'PROFESSOR' };
    localStorage.setItem('sigea_user', JSON.stringify(profUser));
    localStorage.setItem('sigea_token', 'token');

    // Força atualização manual no signal
    service.currentUser.set(profUser);
    expect(service.isProfessor()).toBe(true);
    expect(service.isAdmin()).toBe(false);
    expect(service.isStudent()).toBe(false);

    const studentUser: User = { ...mockUser, role: 'STUDENT', mustChangePassword: true };
    service.currentUser.set(studentUser);
    expect(service.isStudent()).toBe(true);
    expect(service.isProfessor()).toBe(false);
    expect(service.mustChangePassword()).toBe(true);
  });

  it('deve verificar perfis com hasRole corretamente', () => {
    service.currentUser.set(null);
    expect(service.hasRole(['ADMIN', 'PROFESSOR'])).toBe(false);

    service.currentUser.set(mockUser); // role ADMIN
    expect(service.hasRole(['ADMIN'])).toBe(true);
    expect(service.hasRole(['PROFESSOR', 'STUDENT'])).toBe(false);
  });

  it('deve verificar isTokenExpired para tokens validos, expirados e malformados', () => {
    expect(service.isTokenExpired(null)).toBe(true);
    expect(service.isTokenExpired()).toBe(true);
    expect(service.isTokenExpired('opaque-token')).toBe(false);

    const noExpToken = 'header.' + btoa(JSON.stringify({ sub: 'test' })) + '.sig';
    expect(service.isTokenExpired(noExpToken)).toBe(false);

    const expiredPayload = btoa(JSON.stringify({ exp: Math.floor(Date.now() / 1000) - 3600 }));
    const expiredToken = 'header.' + expiredPayload + '.sig';
    expect(service.isTokenExpired(expiredToken)).toBe(true);

    const validPayload = btoa(JSON.stringify({ exp: Math.floor(Date.now() / 1000) + 3600 }));
    const validToken = 'header.' + validPayload + '.sig';
    expect(service.isTokenExpired(validToken)).toBe(false);

    localStorage.setItem('sigea_token', validToken);
    expect(service.isTokenExpired()).toBe(false);

    const malformedToken = 'header.%%%invalidbase64%%%.sig';
    expect(service.isTokenExpired(malformedToken)).toBe(false);
  });

  it('deve limpar credenciais e retornar null em getToken() quando token estiver expirado no localStorage', () => {
    const expiredPayload = btoa(JSON.stringify({ exp: Math.floor(Date.now() / 1000) - 3600 }));
    const expiredToken = 'header.' + expiredPayload + '.sig';
    localStorage.setItem('sigea_token', expiredToken);
    localStorage.setItem('sigea_user', JSON.stringify(mockUser));
    service.currentUser.set(mockUser);

    const token = service.getToken();
    expect(token).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(localStorage.getItem('sigea_token')).toBeNull();
    expect(localStorage.getItem('sigea_user')).toBeNull();
  });
});
