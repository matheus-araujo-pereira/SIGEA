import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { of, throwError } from 'rxjs';
import { User } from '../../core/models/user.model';
import { ApiResponse } from '../../core/models/api-response.model';
import { signal } from '@angular/core';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let authServiceMock: {
    currentUser: ReturnType<typeof signal<User | null>>;
    getProfile: jest.Mock;
    updateProfile: jest.Mock;
    changePassword: jest.Mock;
  };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockUser: User = {
    id: 'usr-1',
    fullName: 'Maria da Silva',
    email: 'maria.silva@academico.ufs.br',
    role: 'STUDENT',
    registrationNumber: '20260009999',
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(async () => {
    authServiceMock = {
      currentUser: signal<User | null>(mockUser),
      getProfile: jest.fn().mockReturnValue(of({ data: mockUser } as ApiResponse<User>)),
      updateProfile: jest.fn(),
      changePassword: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar e carregar os dados cadastrais do perfil', () => {
    expect(component).toBeTruthy();
    expect(component.profileForm.get('fullName')?.value).toBe('Maria da Silva');
    expect(authServiceMock.getProfile).toHaveBeenCalled();
  });

  it('deve inicializar sem usuário inicial se currentUser for nulo', async () => {
    authServiceMock.currentUser.set(null);
    authServiceMock.getProfile.mockReturnValue(of({ data: null } as unknown as ApiResponse<User>));

    const newFixture = TestBed.createComponent(ProfileComponent);
    const newComp = newFixture.componentInstance;
    newFixture.detectChanges();

    expect(newComp.profileForm.get('fullName')?.value).toBe('');
  });

  it('deve retornar classes de badge corretas para cada perfil', () => {
    component.user.set({ ...mockUser, role: 'ADMIN' });
    expect(component.getRoleBadgeClasses()).toContain('bg-purple-100');

    component.user.set({ ...mockUser, role: 'PROFESSOR' });
    expect(component.getRoleBadgeClasses()).toContain('bg-blue-100');

    component.user.set({ ...mockUser, role: 'STUDENT' });
    expect(component.getRoleBadgeClasses()).toContain('bg-emerald-100');

    component.user.set({ ...mockUser, role: 'OTHER' as any });
    expect(component.getRoleBadgeClasses()).toContain('bg-slate-100');
  });

  it('não deve atualizar o perfil se o formulário for inválido', () => {
    component.profileForm.patchValue({ fullName: '' });
    component.onUpdateProfile();
    expect(authServiceMock.updateProfile).not.toHaveBeenCalled();
  });

  it('deve atualizar o perfil com sucesso', () => {
    const updatedUser = { ...mockUser, fullName: 'Maria Atualizada' };
    authServiceMock.updateProfile.mockReturnValue(
      of({ success: true, data: updatedUser } as ApiResponse<User>)
    );

    component.profileForm.patchValue({ fullName: 'Maria Atualizada' });
    component.onUpdateProfile();

    expect(component.isUpdatingProfile()).toBe(false);
    expect(component.user()?.fullName).toBe('Maria Atualizada');
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Nome atualizado com sucesso.');
  });

  it('deve tratar erro ao atualizar o perfil', () => {
    authServiceMock.updateProfile.mockReturnValue(throwError(() => new Error('Falha')));

    component.profileForm.patchValue({ fullName: 'Maria Atualizada' });
    component.onUpdateProfile();

    expect(component.isUpdatingProfile()).toBe(false);
  });

  it('deve validar divergência entre confirmação e nova senha voluntária', () => {
    component.passwordForm.patchValue({
      newPassword: 'Password123!',
      confirmPassword: 'DifferentPassword123!',
    });
    expect(component.passwordMismatch()).toBe(true);

    component.passwordForm.patchValue({
      confirmPassword: 'Password123!',
    });
    expect(component.passwordMismatch()).toBe(false);
  });

  it('não deve alterar senha se o formulário for inválido ou senhas divergirem', () => {
    component.passwordForm.patchValue({
      currentPassword: '',
      newPassword: 'short',
      confirmPassword: 'short',
    });
    component.onChangePassword();
    expect(authServiceMock.changePassword).not.toHaveBeenCalled();

    component.passwordForm.patchValue({
      currentPassword: 'ValidCurrent123!',
      newPassword: 'NewPassword123!',
      confirmPassword: 'Different123!',
    });
    component.onChangePassword();
    expect(authServiceMock.changePassword).not.toHaveBeenCalled();
  });

  it('deve alterar a senha com sucesso e resetar o formulário', () => {
    authServiceMock.changePassword.mockReturnValue(of({ success: true } as ApiResponse<void>));

    component.passwordForm.patchValue({
      currentPassword: 'CurrentPassword123!',
      newPassword: 'NewPassword123!',
      confirmPassword: 'NewPassword123!',
    });

    component.onChangePassword();

    expect(component.isChangingPassword()).toBe(false);
    expect(component.passwordForm.get('currentPassword')?.value).toBeNull();
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Senha alterada com sucesso.');
  });

  it('deve tratar erro ao alterar a senha', () => {
    authServiceMock.changePassword.mockReturnValue(throwError(() => new Error('Erro')));

    component.passwordForm.patchValue({
      currentPassword: 'WrongPassword123!',
      newPassword: 'NewPassword123!',
      confirmPassword: 'NewPassword123!',
    });

    component.onChangePassword();

    expect(component.isChangingPassword()).toBe(false);
  });
});
