import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserFormComponent } from './user-form.component';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/user.model';
import { ApiResponse } from '../../../core/models/api-response.model';
import { SimpleChange } from '@angular/core';

describe('UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;
  let userServiceMock: { createUser: jest.Mock; updateUser: jest.Mock };
  let toastServiceMock: { success: jest.Mock; info: jest.Mock; error: jest.Mock };

  const mockStudentUser: User = {
    id: 'student-1',
    fullName: 'Aluno João',
    email: 'joao@academico.ufs.br',
    role: 'STUDENT',
    registrationNumber: '20261234567',
    isActive: true,
    mustChangePassword: true,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockAdminUser: User = {
    id: 'admin-1',
    fullName: 'Professor Doutor',
    email: 'prof@academico.ufs.br',
    role: 'PROFESSOR',
    registrationNumber: null,
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(async () => {
    userServiceMock = {
      createUser: jest.fn(),
      updateUser: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      info: jest.fn(),
      error: jest.fn(),
    };

    // Mock clipboard
    Object.assign(navigator, {
      clipboard: {
        writeText: jest.fn().mockImplementation(() => Promise.resolve()),
      },
    });

    await TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar com o formulário padrão configurado para STUDENT', () => {
    expect(component).toBeTruthy();
    expect(component.isStudentSelected()).toBe(true);
    expect(component.isEditing()).toBe(false);
  });

  it('deve popular o formulário em modo edição quando user for fornecido', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('user', mockStudentUser);
    component.ngOnChanges({
      user: new SimpleChange(null, mockStudentUser, true),
      isOpen: new SimpleChange(false, true, true),
    });

    expect(component.isEditing()).toBe(true);
    expect(component.form.get('fullName')?.value).toBe('Aluno João');
    expect(component.form.get('registrationNumber')?.value).toBe('20261234567');
  });

  it('deve alternar a obrigatoriedade da matrícula de acordo com o perfil selecionado', () => {
    const regControl = component.form.get('registrationNumber');

    // Inicial é STUDENT
    expect(component.isStudentSelected()).toBe(true);
    regControl?.setValue('');
    expect(regControl?.valid).toBe(false);

    // Muda para ADMIN
    component.form.get('role')?.setValue('ADMIN');
    expect(component.isStudentSelected()).toBe(false);
    expect(regControl?.valid).toBe(true);
    expect(regControl?.value).toBe('');

    // Volta para STUDENT
    component.form.get('role')?.setValue('STUDENT');
    expect(component.isStudentSelected()).toBe(true);
    expect(regControl?.valid).toBe(false);
  });

  it('deve validar e-mail institucional e flag hasEmailError', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalido@gmail.com');
    emailControl?.markAsDirty();

    expect(component.hasEmailError()).toBe(true);

    emailControl?.setValue('valido@academico.ufs.br');
    expect(component.hasEmailError()).toBe(false);
  });

  it('não deve submeter se o formulário for inválido', () => {
    component.onSubmit();
    expect(component.form.touched).toBe(true);
    expect(userServiceMock.createUser).not.toHaveBeenCalled();
    expect(userServiceMock.updateUser).not.toHaveBeenCalled();
  });

  it('deve criar novo usuário e exibir modal com senha provisória se retornada', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('user', null);
    component.ngOnChanges({
      isOpen: new SimpleChange(false, true, true),
    });

    userServiceMock.createUser.mockReturnValue(
      of({
        success: true,
        data: {
          id: 'new-id',
          fullName: 'Novo Estudante',
          email: 'novo@academico.ufs.br',
          role: 'STUDENT',
          registrationNumber: '20260011223',
          provisionalPassword: 'TempPassword123!',
        },
      } as ApiResponse<any>)
    );

    component.form.patchValue({
      fullName: 'Novo Estudante',
      email: 'novo@academico.ufs.br',
      role: 'STUDENT',
      registrationNumber: '20260011223',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
    expect(component.createdPassword()).toBe('TempPassword123!');
  });

  it('deve criar novo usuário sem senha provisória emitindo saved diretamente', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    userServiceMock.createUser.mockReturnValue(
      of({
        success: true,
        data: {
          id: 'new-id',
          fullName: 'Novo Estudante',
          email: 'novo@academico.ufs.br',
          role: 'STUDENT',
        },
      } as ApiResponse<any>)
    );

    component.form.patchValue({
      fullName: 'Novo Estudante',
      email: 'novo@academico.ufs.br',
      role: 'STUDENT',
      registrationNumber: '20260011223',
    });

    component.onSubmit();

    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Usuário cadastrado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
  });

  it('deve tratar erro na criação de usuário', () => {
    userServiceMock.createUser.mockReturnValue(throwError(() => new Error('Falha')));

    component.form.patchValue({
      fullName: 'Novo Estudante',
      email: 'novo@academico.ufs.br',
      role: 'STUDENT',
      registrationNumber: '20260011223',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve atualizar usuário existente em modo de edição', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('user', mockAdminUser);
    component.ngOnChanges({
      user: new SimpleChange(null, mockAdminUser, true),
      isOpen: new SimpleChange(false, true, true),
    });

    userServiceMock.updateUser.mockReturnValue(
      of({
        success: true,
        data: mockAdminUser,
      } as ApiResponse<any>)
    );

    component.form.patchValue({
      fullName: 'Professor Doutor Editado',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
    expect(userServiceMock.updateUser).toHaveBeenCalledWith(
      'admin-1',
      expect.objectContaining({
        fullName: 'Professor Doutor Editado',
        registrationNumber: null,
      })
    );
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Usuário atualizado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
  });

  it('deve criar novo usuário não-estudante com matrícula nula', () => {
    userServiceMock.createUser.mockReturnValue(
      of({
        success: true,
        data: {
          id: 'admin-id',
          fullName: 'Novo Admin',
          email: 'novo.admin@academico.ufs.br',
          role: 'ADMIN',
        },
      } as ApiResponse<any>)
    );

    component.form.patchValue({
      fullName: 'Novo Admin',
      email: 'novo.admin@academico.ufs.br',
      role: 'ADMIN',
    });

    component.onSubmit();

    expect(userServiceMock.createUser).toHaveBeenCalledWith(
      expect.objectContaining({
        fullName: 'Novo Admin',
        registrationNumber: null,
      })
    );
  });

  it('deve atualizar usuário estudante preservando matrícula', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('user', mockStudentUser);
    component.ngOnChanges({
      user: new SimpleChange(null, mockStudentUser, true),
      isOpen: new SimpleChange(false, true, true),
    });

    userServiceMock.updateUser.mockReturnValue(
      of({
        success: true,
        data: mockStudentUser,
      } as ApiResponse<any>)
    );

    component.form.patchValue({
      fullName: 'Aluno João Editado',
      registrationNumber: '20269999999',
    });

    component.onSubmit();

    expect(userServiceMock.updateUser).toHaveBeenCalledWith(
      'student-1',
      expect.objectContaining({
        fullName: 'Aluno João Editado',
        registrationNumber: '20269999999',
      })
    );
  });

  it('deve tratar erro na atualização de usuário', () => {
    fixture.componentRef.setInput('user', mockAdminUser);

    userServiceMock.updateUser.mockReturnValue(throwError(() => new Error('Falha')));

    component.form.patchValue({
      fullName: 'Professor Doutor Editado',
      email: 'prof@academico.ufs.br',
      role: 'PROFESSOR',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve copiar senha provisória e resetar flag copiado após timeout', fakeAsync(() => {
    component.createdPassword.set('TemporaryPass123!');
    component.copyPassword();

    expect(navigator.clipboard.writeText).toHaveBeenCalledWith('TemporaryPass123!');
    expect(component.copied()).toBe(true);
    expect(toastServiceMock.info).toHaveBeenCalledWith(
      'Copiado',
      'Senha provisória copiada para a área de transferência.'
    );

    tick(2600);
    expect(component.copied()).toBe(false);
  }));

  it('não deve copiar se createdPassword for nulo', () => {
    component.createdPassword.set(null);
    component.copyPassword();
    expect(navigator.clipboard.writeText).not.toHaveBeenCalled();
  });

  it('deve emitir saved ao chamar finishCreation', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    component.createdPassword.set('Pwd');
    component.finishCreation();

    expect(component.createdPassword()).toBeNull();
    expect(savedSpy).toHaveBeenCalled();
  });

  it('deve emitir closed ao chamar onClose', () => {
    const closedSpy = jest.fn();
    component.closed.subscribe(closedSpy);

    component.createdPassword.set('Pwd');
    component.onClose();

    expect(component.createdPassword()).toBeNull();
    expect(closedSpy).toHaveBeenCalled();
  });
});
