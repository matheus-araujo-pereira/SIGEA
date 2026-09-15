import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, UrlTree } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { User } from '../models/user.model';

describe('roleGuard', () => {
  let authServiceMock: { currentUser: jest.Mock };
  let toastServiceMock: { error: jest.Mock };
  let router: Router;

  const mockAdmin: User = {
    id: '1',
    fullName: 'Admin',
    email: 'admin@academico.ufs.br',
    role: 'ADMIN',
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(() => {
    authServiceMock = {
      currentUser: jest.fn(),
    };
    toastServiceMock = {
      error: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
        {
          provide: Router,
          useValue: {
            createUrlTree: jest.fn((commands) => ({ commands } as unknown as UrlTree)),
          },
        },
      ],
    });

    router = TestBed.inject(Router);
  });

  it('deve permitir acesso quando o usuário possui o perfil esperado', () => {
    authServiceMock.currentUser.mockReturnValue(mockAdmin);
    const route = { data: { roles: ['ADMIN', 'PROFESSOR'] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(result).toBe(true);
    expect(toastServiceMock.error).not.toHaveBeenCalled();
  });

  it('deve bloquear e redirecionar quando o usuário não possui o perfil esperado', () => {
    authServiceMock.currentUser.mockReturnValue({ ...mockAdmin, role: 'STUDENT' });
    const route = { data: { roles: ['ADMIN'] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(toastServiceMock.error).toHaveBeenCalledWith('Acesso Restrito', expect.any(String));
    expect(router.createUrlTree).toHaveBeenCalledWith(['/profile']);
    expect(result).toEqual({ commands: ['/profile'] });
  });

  it('deve bloquear quando currentUser for nulo ou roles não estiver configurado', () => {
    authServiceMock.currentUser.mockReturnValue(null);
    const route = { data: {} } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(toastServiceMock.error).toHaveBeenCalled();
    expect(result).toEqual({ commands: ['/profile'] });
  });
});
