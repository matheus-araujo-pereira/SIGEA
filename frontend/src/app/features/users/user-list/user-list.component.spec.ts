import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserListComponent } from './user-list.component';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/user.model';
import { PageResponse } from '../../../core/models/page.model';
import { ApiResponse } from '../../../core/models/api-response.model';
import { signal } from '@angular/core';

describe('UserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;
  let userServiceMock: {
    listUsers: jest.Mock;
    updateStatus: jest.Mock;
    deleteUser: jest.Mock;
    resetPassword: jest.Mock;
  };
  let authServiceMock: {
    currentUser: ReturnType<typeof signal<User | null>>;
  };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const loggedUser: User = {
    id: 'admin-id',
    fullName: 'Administrador Sistema',
    email: 'admin.sigea@academico.ufs.br',
    role: 'ADMIN',
    isActive: true,
    mustChangePassword: false,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const sampleUsers: User[] = [
    loggedUser,
    {
      id: 'prof-id',
      fullName: 'Professora Ana',
      email: 'ana.prof@academico.ufs.br',
      role: 'PROFESSOR',
      isActive: true,
      mustChangePassword: false,
      createdAt: '2026-09-14T00:00:00Z',
    },
    {
      id: 'student-id',
      fullName: 'Estudante Carlos',
      email: 'carlos.aluno@academico.ufs.br',
      role: 'STUDENT',
      registrationNumber: '20260012345',
      isActive: false,
      mustChangePassword: true,
      createdAt: '2026-09-14T00:00:00Z',
    },
  ];

  const mockPageData: PageResponse<User> = {
    content: sampleUsers,
    totalElements: 3,
    totalPages: 1,
    size: 10,
    page: 0,
    first: true,
    last: true,
  };

  beforeEach(async () => {
    userServiceMock = {
      listUsers: jest.fn().mockReturnValue(of({ success: true, data: mockPageData } as ApiResponse<PageResponse<User>>)),
      updateStatus: jest.fn().mockReturnValue(of({ success: true } as ApiResponse<any>)),
      deleteUser: jest.fn().mockReturnValue(of({ success: true } as ApiResponse<any>)),
      resetPassword: jest.fn().mockReturnValue(of({ success: true, data: { provisionalPassword: 'Sigea@123456' } } as ApiResponse<any>)),
    };
    authServiceMock = {
      currentUser: signal<User | null>(loggedUser),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [UserListComponent],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar e carregar a lista de usuários com paginação de 10 registros', () => {
    expect(component).toBeTruthy();
    expect(userServiceMock.listUsers).toHaveBeenCalledWith('', '', null, 0, 10, 'fullName,asc');
    expect(component.users().length).toBe(3);
    expect(component.isLoading()).toBe(false);
  });

  it('deve tratar erro na listagem de usuários', () => {
    userServiceMock.listUsers.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadUsers();
    expect(component.isLoading()).toBe(false);
  });

  it('deve realizar busca com debounce e resetar página para 0', fakeAsync(() => {
    component.searchQuery = 'Carlos';
    component.onSearchChange();

    // Outro toque rápido para testar cancelamento do timeout anterior
    component.searchQuery = 'Carlos Aluno';
    component.onSearchChange();

    expect(component.currentPage).toBe(0);
    tick(350);

    expect(userServiceMock.listUsers).toHaveBeenCalledWith(
      'Carlos Aluno',
      '',
      null,
      0,
      10,
      'fullName,asc'
    );
  }));

  it('deve filtrar por perfil e status resetando página para 0', () => {
    component.selectedRole = 'STUDENT';
    component.selectedStatus = true;
    component.onFilterChange();

    expect(component.currentPage).toBe(0);
    expect(userServiceMock.listUsers).toHaveBeenCalledWith(
      '',
      'STUDENT',
      true,
      0,
      10,
      'fullName,asc'
    );
  });

  it('deve mudar de página ao acionar onPageChange', () => {
    component.onPageChange(2);
    expect(component.currentPage).toBe(2);
    expect(userServiceMock.listUsers).toHaveBeenCalledWith('', '', null, 2, 10, 'fullName,asc');
  });

  it('deve abrir modal para criar usuário', () => {
    component.openCreateModal();
    expect(component.selectedUser()).toBeNull();
    expect(component.isFormModalOpen()).toBe(true);
  });

  it('deve abrir modal para editar usuário', () => {
    const userToEdit = sampleUsers[1];
    component.openEditModal(userToEdit);
    expect(component.selectedUser()).toBe(userToEdit);
    expect(component.isFormModalOpen()).toBe(true);
  });

  it('deve recarregar lista ao receber evento saved do formulário', () => {
    component.isFormModalOpen.set(true);
    component.onUserSaved();
    expect(component.isFormModalOpen()).toBe(false);
    expect(userServiceMock.listUsers).toHaveBeenCalled();
  });

  it('deve abrir e confirmar diálogo de alteração de status', () => {
    const userToToggle = sampleUsers[1]; // Ativo
    component.openStatusDialog(userToToggle);

    expect(component.targetUser()).toBe(userToToggle);
    expect(component.isStatusDialogOpen()).toBe(true);

    component.onConfirmStatusChange();

    expect(component.isStatusDialogOpen()).toBe(false);
    expect(userServiceMock.updateStatus).toHaveBeenCalledWith('prof-id', { isActive: false });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      expect.stringContaining('inativado com sucesso')
    );
  });

  it('deve confirmar ativação quando usuário alvo estiver inativo', () => {
    const inactiveUser = sampleUsers[2]; // Carlos: isActive === false
    component.openStatusDialog(inactiveUser);

    component.onConfirmStatusChange();

    expect(userServiceMock.updateStatus).toHaveBeenCalledWith('student-id', { isActive: true });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      expect.stringContaining('ativado com sucesso')
    );
  });

  it('não deve alterar status se targetUser for nulo', () => {
    component.targetUser.set(null);
    component.onConfirmStatusChange();
    expect(userServiceMock.updateStatus).not.toHaveBeenCalled();
  });

  it('deve abrir e confirmar diálogo de exclusão', () => {
    const userToDelete = sampleUsers[2];
    component.openDeleteDialog(userToDelete);

    expect(component.targetUser()).toBe(userToDelete);
    expect(component.isDeleteDialogOpen()).toBe(true);

    component.onConfirmDelete();

    expect(component.isDeleteDialogOpen()).toBe(false);
    expect(userServiceMock.deleteUser).toHaveBeenCalledWith('student-id');
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      expect.stringContaining('excluído com sucesso')
    );
  });

  it('não deve excluir usuário se targetUser for nulo', () => {
    component.targetUser.set(null);
    component.onConfirmDelete();
    expect(userServiceMock.deleteUser).not.toHaveBeenCalled();
  });

  it('deve verificar isSelf impedindo auto-exclusão e auto-inativação', () => {
    expect(component.isSelf(loggedUser)).toBe(true);
    expect(component.isSelf(sampleUsers[1])).toBe(false);
  });

  it('deve abrir e confirmar diálogo de redefinição de senha', () => {
    const target = sampleUsers[2];
    component.openResetPasswordDialog(target);

    expect(component.targetUser()).toBe(target);
    expect(component.isResetPasswordDialogOpen()).toBe(true);

    component.onConfirmResetPassword();

    expect(component.isResetPasswordDialogOpen()).toBe(false);
    expect(userServiceMock.resetPassword).toHaveBeenCalledWith('student-id');
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Senha Redefinida',
      expect.stringContaining('Sigea@123456')
    );
  });

  it('não deve redefinir senha se targetUser for nulo', () => {
    component.targetUser.set(null);
    component.onConfirmResetPassword();
    expect(userServiceMock.resetPassword).not.toHaveBeenCalled();
  });

  it('deve retornar classes de badge corretas para cada perfil', () => {
    expect(component.getRoleBadgeClasses('ADMIN')).toBe('badge-admin');
    expect(component.getRoleBadgeClasses('PROFESSOR')).toBe('badge-professor');
    expect(component.getRoleBadgeClasses('STUDENT')).toBe('badge-student');
  });
});
