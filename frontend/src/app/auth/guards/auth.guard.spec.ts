import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authServiceMock: { isAuthenticated: jest.Mock; mustChangePassword: jest.Mock };
  let router: Router;

  beforeEach(() => {
    authServiceMock = {
      isAuthenticated: jest.fn(),
      mustChangePassword: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
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

  it('deve redirecionar para /login se não estiver autenticado', () => {
    authServiceMock.isAuthenticated.mockReturnValue(false);

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/users' } as RouterStateSnapshot)
    );

    expect(router.createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toEqual({ commands: ['/login'] });
  });

  it('deve redirecionar para /first-login se mustChangePassword for verdadeiro e não estiver em /first-login', () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.mustChangePassword.mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/users' } as RouterStateSnapshot)
    );

    expect(router.createUrlTree).toHaveBeenCalledWith(['/first-login']);
    expect(result).toEqual({ commands: ['/first-login'] });
  });

  it('deve permitir acesso se mustChangePassword for verdadeiro mas a rota for /first-login', () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.mustChangePassword.mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/first-login' } as RouterStateSnapshot)
    );

    expect(result).toBe(true);
  });

  it('deve permitir acesso normal se autenticado e sem pendência de primeiro acesso', () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.mustChangePassword.mockReturnValue(false);

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, { url: '/users' } as RouterStateSnapshot)
    );

    expect(result).toBe(true);
  });
});
