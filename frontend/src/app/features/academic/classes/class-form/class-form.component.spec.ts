import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassFormComponent } from './class-form.component';
import { FormBuilder } from '@angular/forms';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { UserService } from '../../../../core/services/user.service';
import { ToastService } from '../../../../core/services/toast.service';
import { of, throwError } from 'rxjs';
import { SimpleChange } from '@angular/core';

describe('ClassFormComponent', () => {
  let component: ClassFormComponent;
  let fixture: ComponentFixture<ClassFormComponent>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let userServiceSpy: jest.Mocked<UserService>;
  let toastSpy: jest.Mocked<ToastService>;

  const mockProfessors = [
    { id: 'p1', fullName: 'Prof. Gilton', email: 'gilton@academico.ufs.br', role: 'PROFESSOR', active: true, mustChangePassword: false, createdAt: '2026-01-01', updatedAt: '2026-01-01' }
  ];

  const mockStudents = [
    { id: 's1', fullName: 'Matheus Araujo', email: 'matheus@academico.ufs.br', role: 'STUDENT', registrationNumber: '2026001', active: true, mustChangePassword: false, createdAt: '2026-01-01', updatedAt: '2026-01-01' }
  ];

  beforeEach(async () => {
    classServiceSpy = {
      getClassById: jest.fn(),
      createClass: jest.fn(),
      updateClass: jest.fn(),
    } as unknown as jest.Mocked<AcademicClassService>;

    userServiceSpy = {
      listUsers: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: { content: [], totalElements: 0, totalPages: 0, size: 10, page: 0, first: true, last: true }, timestamp: '2026-09-14T00:00:00Z' })),
    } as unknown as jest.Mocked<UserService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    await TestBed.configureTestingModule({
      imports: [ClassFormComponent],
      providers: [
        FormBuilder,
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: ToastService, useValue: toastSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassFormComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load users', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (userServiceSpy.listUsers as any).mockImplementation((_: any, role: any) => {
      if (role === 'PROFESSOR') {
        return of({ success: true, message: 'OK', data: { content: mockProfessors, totalElements: 1, totalPages: 1, size: 10, page: 0, first: true, last: true }, timestamp: '2026-09-14T00:00:00Z' });
      }
      return of({ success: true, message: 'OK', data: { content: mockStudents, totalElements: 1, totalPages: 1, size: 10, page: 0, first: true, last: true }, timestamp: '2026-09-14T00:00:00Z' });
    });

    component.loadUsers();
    expect(userServiceSpy.listUsers).toHaveBeenCalledTimes(2);
    expect(component.professors().length).toBe(1);
    expect(component.students().length).toBe(1);
  });

  it('should handle ngOnChanges when isOpen becomes true without classId', () => {
    component.isOpen = true;
    component.classId = null;
    component.ngOnChanges({
      isOpen: new SimpleChange(false, true, true),
    });
    expect(component.form.get('subjectName')?.value).toBe('');
    expect(component.selectedStudentIds().length).toBe(0);
  });

  it('should handle ngOnChanges when isOpen is false', () => {
    component.isOpen = false;
    component.ngOnChanges({
      isOpen: new SimpleChange(true, false, false),
    });
    expect(classServiceSpy.getClassById).not.toHaveBeenCalled();
  });

  it('should load class details on edit mode when isOpen is true', () => {
    component.isOpen = true;
    component.classId = 'c1';
    classServiceSpy.getClassById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: {
        id: 'c1',
        subjectName: 'Física 3',
        classCode: 'T06',
        academicPeriod: '2026.2',
        formattedName: 'Física 3 - T06 - 2026.2',
        professorId: 'p1',
        professorName: 'Prof. Gilton',
        professorEmail: 'gilton@academico.ufs.br',
        isClosed: false,
        studentCount: 1,
        students: [{ id: 's1', fullName: 'Matheus', email: 'matheus@academico.ufs.br', role: 'STUDENT', registrationNumber: '2026001', active: true, mustChangePassword: false, createdAt: '2026-01-01', updatedAt: '2026-01-01' }],
        activities: []
      },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));

    component.ngOnChanges({
      isOpen: new SimpleChange(false, true, true),
    });

    expect(classServiceSpy.getClassById).toHaveBeenCalledWith('c1');
    expect(component.form.get('subjectName')?.value).toBe('Física 3');
    expect(component.selectedStudentIds()).toEqual(['s1']);
  });

  it('should handle error when loading class details', () => {
    component.isOpen = true;
    component.classId = 'c1';
    jest.spyOn(component, 'onCancel');
    classServiceSpy.getClassById.mockReturnValue(throwError(() => new Error('Error')));

    component.loadClassDetail('c1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar detalhes da turma.');
    expect(component.onCancel).toHaveBeenCalled();
  });

  it('should toggle student selection', () => {
    expect(component.isStudentSelected('s1')).toBe(false);
    component.toggleStudent('s1');
    expect(component.isStudentSelected('s1')).toBe(true);
    component.toggleStudent('s1');
    expect(component.isStudentSelected('s1')).toBe(false);
  });

  it('should format preview name correctly', () => {
    expect(component.previewFormattedName()).toBe('Disciplina - TURMA - Período');

    component.form.patchValue({
      subjectName: 'Física 3',
      classCode: 't06',
      academicPeriod: '2026.2',
    });
    expect(component.previewFormattedName()).toBe('Física 3 - T06 - 2026.2');
  });

  it('should not submit if form is invalid', () => {
    component.form.reset();
    component.onSubmit();
    expect(classServiceSpy.createClass).not.toHaveBeenCalled();
    expect(classServiceSpy.updateClass).not.toHaveBeenCalled();
  });

  it('should create a new class when form is valid', () => {
    component.classId = null;
    component.form.patchValue({
      subjectName: 'Física 3',
      classCode: 'T06',
      academicPeriod: '2026.2',
      professorId: 'p1',
    });
    component.selectedStudentIds.set(['s1']);

    classServiceSpy.createClass.mockReturnValue(of({
      success: true,
      message: 'Criada',
      data: {} as any,
      timestamp: '2026-09-14T00:00:00Z'
    } as any));

    const savedSpy = jest.spyOn(component.saved, 'emit');
    component.onSubmit();

    expect(classServiceSpy.createClass).toHaveBeenCalledWith({
      subjectName: 'Física 3',
      classCode: 'T06',
      academicPeriod: '2026.2',
      professorId: 'p1',
      studentIds: ['s1'],
    });
    expect(toastSpy.success).toHaveBeenCalledWith('Turma acadêmica criada com sucesso!');
    expect(savedSpy).toHaveBeenCalled();
  });

  it('should handle error when creating a class', () => {
    component.classId = null;
    component.form.patchValue({
      subjectName: 'Física 3',
      classCode: 'T06',
      academicPeriod: '2026.2',
      professorId: 'p1',
    });

    classServiceSpy.createClass.mockReturnValue(throwError(() => ({ error: { message: 'Erro na criação' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro na criação');
    expect(component.isSaving()).toBe(false);

    // Fallback error without message
    classServiceSpy.createClass.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao criar turma.');
  });

  it('should update an existing class when in edit mode', () => {
    component.classId = 'c1';
    component.form.patchValue({
      subjectName: 'Física 3 Modificada',
      classCode: 'T07',
      academicPeriod: '2026.2',
      professorId: 'p1',
    });
    component.selectedStudentIds.set(['s1']);

    classServiceSpy.updateClass.mockReturnValue(of({
      success: true,
      message: 'Atualizada',
      data: {} as any,
      timestamp: '2026-09-14T00:00:00Z'
    } as any));

    const savedSpy = jest.spyOn(component.saved, 'emit');
    component.onSubmit();

    expect(classServiceSpy.updateClass).toHaveBeenCalledWith('c1', {
      subjectName: 'Física 3 Modificada',
      classCode: 'T07',
      academicPeriod: '2026.2',
      professorId: 'p1',
      studentIds: ['s1'],
    });
    expect(toastSpy.success).toHaveBeenCalledWith('Turma acadêmica atualizada com sucesso!');
    expect(savedSpy).toHaveBeenCalled();
  });

  it('should handle error when updating a class', () => {
    component.classId = 'c1';
    component.form.patchValue({
      subjectName: 'Física 3 Modificada',
      classCode: 'T07',
      academicPeriod: '2026.2',
      professorId: 'p1',
    });

    classServiceSpy.updateClass.mockReturnValue(throwError(() => ({ error: { message: 'Erro no update' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro no update');
    expect(component.isSaving()).toBe(false);

    // Fallback error without message
    classServiceSpy.updateClass.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao atualizar turma.');
  });

  it('should cancel and emit close', () => {
    const closeSpy = jest.spyOn(component.close, 'emit');
    component.onCancel();
    expect(closeSpy).toHaveBeenCalled();
  });
});
