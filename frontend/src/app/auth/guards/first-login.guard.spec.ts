import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { firstLoginGuard } from './first-login.guard';
import { AuthService } from '../services/auth.service';

describe('firstLoginGuard', () => {
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
      firstLoginGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(router.createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toEqual({ commands: ['/login'] });
  });

  it('deve redirecionar para /profile se o usuário não precisar alterar a senha', () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.mustChangePassword.mockReturnValue(false);

    const result = TestBed.runInInjectionContext(() =>
      firstLoginGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(router.createUrlTree).toHaveBeenCalledWith(['/profile']);
    expect(result).toEqual({ commands: ['/profile'] });
  });

  it('deve permitir acesso se autenticado e mustChangePassword for verdadeiro', () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.mustChangePassword.mockReturnValue(true);

    const result = TestBed.runInInjectionContext(() =>
      firstLoginGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
  });
});
