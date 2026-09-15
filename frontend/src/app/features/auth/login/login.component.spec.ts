import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/user.model';
import { ApiResponse } from '../../../core/models/api-response.model';
import { LoginResponse } from '../../../core/models/auth.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: { login: jest.Mock };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };
  let routerMock: { navigate: jest.Mock };

  const createMockLoginResponse = (
    role: 'ADMIN' | 'PROFESSOR' | 'STUDENT',
    mustChangePassword = false
  ): ApiResponse<LoginResponse> => ({
    success: true,
    message: 'Login realizado com sucesso',
    data: {
      token: 'jwt-test-token',
      tokenType: 'Bearer',
      user: {
        id: 'user-1',
        fullName: 'Usuário Teste',
        email: 'usuario@academico.ufs.br',
        role,
        isActive: true,
        mustChangePassword,
        createdAt: '2026-09-14T00:00:00Z',
      },
    },
    timestamp: '2026-09-14T00:00:00Z',
  });

  beforeEach(async () => {
    authServiceMock = {
      login: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };
    routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve ser instanciado com o formulário inválido inicialmente', () => {
    expect(component).toBeTruthy();
    expect(component.form.valid).toBe(false);
  });

  it('deve validar e-mail institucional @academico.ufs.br corretamente', () => {
    const emailControl = component.form.get('email');

    // Vazio
    emailControl?.setValue('');
    expect(emailControl?.valid).toBe(false);

    // Domínio externo não permitido
    emailControl?.setValue('aluno@gmail.com');
    emailControl?.markAsDirty();
    expect(emailControl?.hasError('invalidUfsEmail')).toBe(true);
    expect(component.hasEmailError()).toBe(true);

    // Domínio institucional correto
    emailControl?.setValue('aluno.teste@academico.ufs.br');
    expect(emailControl?.hasError('invalidUfsEmail')).toBe(false);
    expect(component.hasEmailError()).toBe(false);
  });

  it('deve alternar a visibilidade da senha ao chamar showPassword', () => {
    expect(component.showPassword()).toBe(false);
    component.showPassword.set(true);
    expect(component.showPassword()).toBe(true);
    component.showPassword.set(false);
    expect(component.showPassword()).toBe(false);
  });

  it('não deve submeter o formulário se inválido e deve marcar campos como tocados', () => {
    component.onSubmit();
    expect(component.form.touched).toBe(true);
    expect(authServiceMock.login).not.toHaveBeenCalled();
  });

  it('deve redirecionar para /first-login se mustChangePassword for verdadeiro', () => {
    const mockResp = createMockLoginResponse('STUDENT', true);
    authServiceMock.login.mockReturnValue(of(mockResp));

    component.form.patchValue({
      email: 'aluno@academico.ufs.br',
      password: 'ProvisionalPassword123!',
    });

    component.onSubmit();

    expect(component.isSubmitting()).toBe(false);
    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'aluno@academico.ufs.br',
      password: 'ProvisionalPassword123!',
    });
    expect(toastServiceMock.success).toHaveBeenCalledWith('Bem-vindo!', 'Login realizado com sucesso');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/first-login']);
  });

  it('deve redirecionar para /users se usuário for ADMIN e não precisar alterar senha', () => {
    const mockResp = createMockLoginResponse('ADMIN', false);
    authServiceMock.login.mockReturnValue(of(mockResp));

    component.form.patchValue({
      email: 'admin.sigea@academico.ufs.br',
      password: 'AdminPassword123!',
    });

    component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/users']);
  });

  it('deve redirecionar para /profile se usuário for PROFESSOR ou STUDENT comum', () => {
    const mockResp = createMockLoginResponse('PROFESSOR', false);
    authServiceMock.login.mockReturnValue(of(mockResp));

    component.form.patchValue({
      email: 'professor@academico.ufs.br',
      password: 'ProfessorPassword123!',
    });

    component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/profile']);
  });

  it('deve tratar erro na requisição de login resetando isSubmitting', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Credenciais inválidas')));

    component.form.patchValue({
      email: 'admin.sigea@academico.ufs.br',
      password: 'wrongpassword',
    });

    component.onSubmit();

    expect(component.isSubmitting()).toBe(false);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('deve usar mensagem padrão quando response.message for vazio', () => {
    const mockResp = createMockLoginResponse('ADMIN', false);
    mockResp.message = '';
    authServiceMock.login.mockReturnValue(of(mockResp));

    component.form.patchValue({
      email: 'admin.sigea@academico.ufs.br',
      password: 'password',
    });

    component.onSubmit();

    expect(toastServiceMock.success).toHaveBeenCalledWith('Bem-vindo!', 'Login efetuado com sucesso.');
  });
});
