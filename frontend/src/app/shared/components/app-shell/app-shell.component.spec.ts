import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppShellComponent } from './app-shell.component';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

describe('AppShellComponent', () => {
  let component: AppShellComponent;
  let fixture: ComponentFixture<AppShellComponent>;
  let authServiceMock: {
    currentUser: jest.Mock;
    isAdmin: jest.Mock;
    isProfessor: jest.Mock;
    isStudent: jest.Mock;
    logout: jest.Mock;
  };

  const mockAdmin: User = {
    id: 'user-1',
    fullName: 'Matheus Araujo',
    email: 'matheus.araujo@academico.ufs.br',
    role: 'ADMIN',
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(async () => {
    localStorage.clear();
    authServiceMock = {
      currentUser: jest.fn().mockReturnValue(mockAdmin),
      isAdmin: jest.fn().mockReturnValue(true),
      isProfessor: jest.fn().mockReturnValue(false),
      isStudent: jest.fn().mockReturnValue(false),
      logout: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [AppShellComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('deve alternar estado de colapso da sidebar e persistir no localStorage', () => {
    expect(component.isCollapsed()).toBe(false);

    component.toggleCollapse();
    expect(component.isCollapsed()).toBe(true);
    expect(localStorage.getItem('sigea_sidebar_collapsed')).toBe('true');

    component.toggleCollapse();
    expect(component.isCollapsed()).toBe(false);
    expect(localStorage.getItem('sigea_sidebar_collapsed')).toBe('false');
  });

  it('deve extrair iniciais do usuário corretamente', () => {
    expect(component.userInitials()).toBe('MA');

    authServiceMock.currentUser.mockReturnValue({ ...mockAdmin, fullName: 'Gilton' });
    expect(component.userInitials()).toBe('G');

    authServiceMock.currentUser.mockReturnValue(null);
    expect(component.userInitials()).toBe('U');
  });

  it('deve retornar classes de badge de acordo com o perfil', () => {
    authServiceMock.currentUser.mockReturnValue({ ...mockAdmin, role: 'ADMIN' });
    expect(component.getRoleBadgeClasses()).toContain('purple');

    authServiceMock.currentUser.mockReturnValue({ ...mockAdmin, role: 'PROFESSOR' });
    expect(component.getRoleBadgeClasses()).toContain('blue');

    authServiceMock.currentUser.mockReturnValue({ ...mockAdmin, role: 'STUDENT' });
    expect(component.getRoleBadgeClasses()).toContain('emerald');

    authServiceMock.currentUser.mockReturnValue(null);
    expect(component.getRoleBadgeClasses()).toContain('slate');
  });

  it('deve abrir modal de logout e confirmar logout', () => {
    expect(component.showLogoutDialog()).toBe(false);

    component.openLogoutDialog();
    expect(component.showLogoutDialog()).toBe(true);

    component.onLogoutConfirm();
    expect(component.showLogoutDialog()).toBe(false);
    expect(authServiceMock.logout).toHaveBeenCalled();
  });
});
