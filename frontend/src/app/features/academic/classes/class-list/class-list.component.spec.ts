import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassListComponent } from './class-list.component';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AcademicClassResponseDTO } from '../../../../core/models/academic-class.model';
import { PageResponse } from '../../../../core/models/page.model';

import { provideHttpClient } from '@angular/common/http';
import { UserService } from '../../../../core/services/user.service';

describe('ClassListComponent', () => {
  let component: ClassListComponent;
  let fixture: ComponentFixture<ClassListComponent>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let toastSpy: jest.Mocked<ToastService>;
  let authSpy: jest.Mocked<AuthService>;
  let routerSpy: jest.Mocked<Router>;
  let userServiceMock: any;

  const mockClasses: AcademicClassResponseDTO[] = [
    {
      id: 'c1',
      subjectName: 'Física 3',
      classCode: 'T06',
      academicPeriod: '2026.2',
      formattedName: 'Física 3 - T06 - 2026.2',
      professorId: 'p1',
      professorName: 'Prof. Gilton',
      professorEmail: 'gilton@academico.ufs.br',
      isClosed: false,
      studentCount: 15,
      activityCount: 3,
      createdAt: '2026-09-01T00:00:00Z',
    },
    {
      id: 'c2',
      subjectName: 'Farmacologia',
      classCode: 'T01',
      academicPeriod: '2026.1',
      formattedName: 'Farmacologia - T01 - 2026.1',
      professorId: 'p2',
      professorName: 'Prof. Waleska',
      professorEmail: 'waleska@academico.ufs.br',
      isClosed: true,
      studentCount: 20,
      activityCount: 5,
      createdAt: '2026-09-01T00:00:00Z',
    },
  ];

  const mockPageResponse: PageResponse<AcademicClassResponseDTO> = {
    content: mockClasses,
    totalElements: 2,
    totalPages: 1,
    size: 10,
    page: 0,
    first: true,
    last: true,
  };

  beforeEach(async () => {
    classServiceSpy = {
      listClasses: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockPageResponse, timestamp: '2026-09-14T00:00:00Z' })),
      getMyClasses: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockPageResponse, timestamp: '2026-09-14T00:00:00Z' })),
      updateStatus: jest.fn(),
      deleteClass: jest.fn(),
    } as unknown as jest.Mocked<AcademicClassService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    authSpy = {
      hasRole: jest.fn().mockImplementation((roles: string[]) => roles.includes('ADMIN')),
    } as unknown as jest.Mocked<AuthService>;

    routerSpy = {
      url: '/academic/classes',
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    userServiceMock = {
      listUsers: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 } })),
    };

    await TestBed.configureTestingModule({
      imports: [ClassListComponent],
      providers: [
        provideHttpClient(),
        { provide: UserService, useValue: userServiceMock },
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassListComponent);
    component = fixture.componentInstance;
  });

  it('should create and load classes on init', () => {
    component.ngOnInit();
    expect(classServiceSpy.listClasses).toHaveBeenCalledWith('', undefined, null, 0, 10);
    expect(component.classes().length).toBe(2);
    expect(component.isAdmin).toBe(true);
  });

  it('should load my-classes if url includes my-classes', () => {
    Object.defineProperty(routerSpy, 'url', { value: '/academic/my-classes' });
    expect(component.isMyClassesView).toBe(true);

    component.loadClasses();
    expect(classServiceSpy.getMyClasses).toHaveBeenCalledWith(0, 10);
  });

  it('should handle error when loading classes', () => {
    classServiceSpy.listClasses.mockReturnValue(throwError(() => new Error('API error')));
    component.loadClasses();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar turmas.');
  });

  it('should handle error when loading my-classes', () => {
    Object.defineProperty(routerSpy, 'url', { value: '/academic/my-classes' });
    classServiceSpy.getMyClasses.mockReturnValue(throwError(() => new Error('API error')));
    component.loadClasses();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar turmas.');
  });

  it('should update on search change', () => {
    component.searchQuery = 'Física';
    component.onSearchChange();
    expect(component.currentPage()).toBe(0);
    expect(classServiceSpy.listClasses).toHaveBeenCalled();
  });

  it('should update on filter change', () => {
    component.selectedStatus = false;
    component.onFilterChange();
    expect(component.currentPage()).toBe(0);
    expect(classServiceSpy.listClasses).toHaveBeenCalled();
  });

  it('should update on page change', () => {
    classServiceSpy.listClasses.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockPageResponse, page: 2 },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));
    component.onPageChange(2);
    expect(component.currentPage()).toBe(2);
    expect(classServiceSpy.listClasses).toHaveBeenCalled();
  });

  it('should navigate to class detail', () => {
    component.viewClassDetail('c1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1']);
  });

  it('should navigate to class dashboard', () => {
    component.viewDashboard('c1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1', 'dashboard']);
  });

  it('should open and close create/edit form modal', () => {
    component.openCreateModal();
    expect(component.isFormModalOpen()).toBe(true);
    expect(component.selectedClassId()).toBeNull();

    component.openEditModal('c1');
    expect(component.isFormModalOpen()).toBe(true);
    expect(component.selectedClassId()).toBe('c1');

    component.closeFormModal();
    expect(component.isFormModalOpen()).toBe(false);
    expect(component.selectedClassId()).toBeNull();
  });

  it('should handle onClassSaved', () => {
    jest.spyOn(component, 'loadClasses');
    component.onClassSaved();
    expect(component.isFormModalOpen()).toBe(false);
    expect(component.loadClasses).toHaveBeenCalled();
  });

  it('should confirm and execute toggle status (closing class)', () => {
    const activeClass = mockClasses[0];
    component.confirmToggleStatus(activeClass);
    expect(component.isConfirmDialogOpen()).toBe(true);
    expect(component.confirmDialogTitle()).toBe('Encerrar Turma Acadêmica');
    expect(component.confirmDialogIsDestructive()).toBe(true);

    classServiceSpy.updateStatus.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.executeConfirmedAction();

    expect(classServiceSpy.updateStatus).toHaveBeenCalledWith('c1', true);
    expect(toastSpy.success).toHaveBeenCalledWith('Turma encerrada com sucesso!');
    expect(component.isConfirmDialogOpen()).toBe(false);
  });

  it('should confirm and execute toggle status (reopening class)', () => {
    const closedClass = mockClasses[1];
    component.confirmToggleStatus(closedClass);
    expect(component.confirmDialogTitle()).toBe('Reabrir Turma Acadêmica');
    expect(component.confirmDialogIsDestructive()).toBe(false);

    classServiceSpy.updateStatus.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.executeConfirmedAction();

    expect(classServiceSpy.updateStatus).toHaveBeenCalledWith('c2', false);
    expect(toastSpy.success).toHaveBeenCalledWith('Turma reaberta com sucesso!');
  });

  it('should handle error when toggle status fails with and without message', () => {
    const activeClass = mockClasses[0];
    component.confirmToggleStatus(activeClass);

    classServiceSpy.updateStatus.mockReturnValue(throwError(() => ({ error: { message: 'Pendência de correção' } })));
    component.executeConfirmedAction();
    expect(toastSpy.error).toHaveBeenCalledWith('Pendência de correção');

    component.confirmToggleStatus(activeClass);
    classServiceSpy.updateStatus.mockReturnValue(throwError(() => ({})));
    component.executeConfirmedAction();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao alterar status da turma.');
  });

  it('should confirm and execute delete class with and without message', () => {
    const activeClass = mockClasses[0];
    component.confirmDelete(activeClass);
    expect(component.isConfirmDialogOpen()).toBe(true);
    expect(component.confirmDialogTitle()).toBe('Excluir Turma Acadêmica');

    classServiceSpy.deleteClass.mockReturnValue(of({ success: true, message: 'OK', data: undefined, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.executeConfirmedAction();

    expect(classServiceSpy.deleteClass).toHaveBeenCalledWith('c1');
    expect(toastSpy.success).toHaveBeenCalledWith('Turma acadêmica excluída com sucesso!');

    // Error with message
    component.confirmDelete(activeClass);
    classServiceSpy.deleteClass.mockReturnValue(throwError(() => ({ error: { message: 'Erro de integridade' } })));
    component.executeConfirmedAction();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro de integridade');

    // Error without message
    component.confirmDelete(activeClass);
    classServiceSpy.deleteClass.mockReturnValue(throwError(() => ({})));
    component.executeConfirmedAction();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao excluir turma.');
  });

  it('should close confirm dialog without action', () => {
    component.confirmDelete(mockClasses[0]);
    component.closeConfirmDialog();
    expect(component.isConfirmDialogOpen()).toBe(false);

    // Executing now should do nothing
    component.executeConfirmedAction();
    expect(classServiceSpy.deleteClass).not.toHaveBeenCalled();
  });

  it('should check isProfessor role', () => {
    authSpy.hasRole.mockImplementation((roles: string[]) => roles.includes('PROFESSOR'));
    expect(component.isProfessor).toBe(true);
  });
});
