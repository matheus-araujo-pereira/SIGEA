import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FirstLoginComponent } from './first-login.component';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { LoginResponse } from '../../../core/models/auth.model';

describe('FirstLoginComponent', () => {
  let component: FirstLoginComponent;
  let fixture: ComponentFixture<FirstLoginComponent>;
  let authServiceMock: { firstLoginChangePassword: jest.Mock };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };
  let routerMock: { navigate: jest.Mock };

  const createMockResponse = (role: 'ADMIN' | 'STUDENT' = 'STUDENT'): ApiResponse<LoginResponse> => ({
    success: true,
    message: 'Senha alterada com sucesso',
    data: {
      token: 'new-jwt-token',
      tokenType: 'Bearer',
      user: {
        id: 'user-1',
        fullName: 'Estudante Teste',
        email: 'estudante@academico.ufs.br',
        role,
        isActive: true,
        mustChangePassword: false,
        createdAt: '2026-09-14T00:00:00Z',
      },
    },
    timestamp: '2026-09-14T00:00:00Z',
  });

  beforeEach(async () => {
    authServiceMock = {
      firstLoginChangePassword: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };
    routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [FirstLoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FirstLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar com o formulário em estado inválido', () => {
    expect(component).toBeTruthy();
    expect(component.form.valid).toBe(false);
    expect(component.isPasswordPolicyMet()).toBe(false);
  });

  it('deve validar cada regra da política de senhas individualmente', () => {
    const newPwdControl = component.form.get('newPassword');

    // Senha vazia
    newPwdControl?.setValue('');
    expect(component.hasMinLength()).toBe(false);
    expect(component.hasUpper()).toBe(false);
    expect(component.hasLower()).toBe(false);
    expect(component.hasDigit()).toBe(false);
    expect(component.hasSpecial()).toBe(false);
    expect(component.isPasswordPolicyMet()).toBe(false);

    // Sem maiúscula
    newPwdControl?.setValue('password123!');
    expect(component.hasMinLength()).toBe(true);
    expect(component.hasUpper()).toBe(false);
    expect(component.hasLower()).toBe(true);
    expect(component.hasDigit()).toBe(true);
    expect(component.hasSpecial()).toBe(true);
    expect(component.isPasswordPolicyMet()).toBe(false);

    // Sem minúscula
    newPwdControl?.setValue('PASSWORD123!');
    expect(component.hasLower()).toBe(false);

    // Sem dígito
    newPwdControl?.setValue('PasswordValid!');
    expect(component.hasDigit()).toBe(false);

    // Sem caractere especial
    newPwdControl?.setValue('Password123');
    expect(component.hasSpecial()).toBe(false);

    // Senha forte e válida completa
    newPwdControl?.setValue('SigeaUFS@2026');
    expect(component.hasMinLength()).toBe(true);
    expect(component.hasUpper()).toBe(true);
    expect(component.hasLower()).toBe(true);
    expect(component.hasDigit()).toBe(true);
    expect(component.hasSpecial()).toBe(true);
    expect(component.isPasswordPolicyMet()).toBe(true);
  });

  it('deve detectar divergência entre confirmação e nova senha', () => {
    component.form.patchValue({
      newPassword: 'StrongPassword123!',
      confirmPassword: 'DifferentPassword123!',
    });

    expect(component.passwordsDoNotMatch()).toBe(true);

    component.form.patchValue({
      confirmPassword: 'StrongPassword123!',
    });

    expect(component.passwordsDoNotMatch()).toBe(false);
  });

  it('não deve submeter se o formulário for inválido ou não cumprir a política', () => {
    component.form.patchValue({
      currentPassword: 'old',
      newPassword: 'weak',
      confirmPassword: 'weak',
    });

    component.onSubmit();
    expect(authServiceMock.firstLoginChangePassword).not.toHaveBeenCalled();
  });

  it('deve submeter e redirecionar para /profile para estudante', () => {
    const mockResp = createMockResponse('STUDENT');
    authServiceMock.firstLoginChangePassword.mockReturnValue(of(mockResp));

    component.form.patchValue({
      currentPassword: 'ProvisionalPassword123!',
      newPassword: 'NewStrongPassword@2026',
      confirmPassword: 'NewStrongPassword@2026',
    });

    component.onSubmit();

    expect(component.isSubmitting()).toBe(false);
    expect(authServiceMock.firstLoginChangePassword).toHaveBeenCalledWith({
      currentPassword: 'ProvisionalPassword123!',
      newPassword: 'NewStrongPassword@2026',
      confirmPassword: 'NewStrongPassword@2026',
    });
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso!', 'Senha alterada com sucesso.');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/profile']);
  });

  it('deve redirecionar para /users para administrador', () => {
    const mockResp = createMockResponse('ADMIN');
    authServiceMock.firstLoginChangePassword.mockReturnValue(of(mockResp));

    component.form.patchValue({
      currentPassword: 'ProvisionalPassword123!',
      newPassword: 'NewStrongPassword@2026',
      confirmPassword: 'NewStrongPassword@2026',
    });

    component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/users']);
  });

  it('deve tratar erro na requisição resetando isSubmitting', () => {
    authServiceMock.firstLoginChangePassword.mockReturnValue(
      throwError(() => new Error('Senha atual incorreta'))
    );

    component.form.patchValue({
      currentPassword: 'wrongPassword',
      newPassword: 'NewStrongPassword@2026',
      confirmPassword: 'NewStrongPassword@2026',
    });

    component.onSubmit();

    expect(component.isSubmitting()).toBe(false);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
