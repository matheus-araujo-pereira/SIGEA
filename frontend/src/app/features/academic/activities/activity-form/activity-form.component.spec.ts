import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivityFormComponent } from './activity-form.component';
import { FormBuilder } from '@angular/forms';
import { ActivityService } from '../../../../core/services/activity.service';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityDetailDTO } from '../../../../core/models/activity.model';

describe('ActivityFormComponent', () => {
  let component: ActivityFormComponent;
  let fixture: ComponentFixture<ActivityFormComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockActivityDetail: ActivityDetailDTO = {
    id: 'a1',
    title: 'Caso Clínico 1',
    description: 'Instruções do caso',
    classId: 'c1',
    className: 'Física 3 - T06 - 2026.2',
    academicClassId: 'c1',
    academicClassName: 'Física 3 - T06 - 2026.2',
    createdAt: '2026-09-01T00:00:00Z',
    deadline: '2026-12-31T23:59:00Z',
    isExpired: false,
    submissionCount: 0,
    clinicalCaseData: {
      patientName: 'Maria Silva',
      age: 62,
      gender: 'Feminino',
      bed: 'Leito 12 - UTI',
      admissionDate: '2026-09-01',
      patientDays: 5,
      admissionNotes: 'Admitida com choque séptico',
      evolutionNotes: [
        { dateTime: '2026-09-02 08:00', professionalRole: 'Enfermeiro', note: 'Paciente eupneico' }
      ],
      prescriptions: [
        { medication: 'Vancomicina', dosage: '1g', route: 'EV', frequency: '12/12h', administrationCheck: 'Checado' }
      ],
      labExams: [
        { examName: 'Creatinina', result: '3.2 mg/dL', referenceValue: '0.6 - 1.2 mg/dL', date: '2026-09-02' }
      ],
      procedures: [
        { procedureName: 'Cateter Central', description: 'Inserção de CVC em subclávia', date: '2026-09-01' }
      ]
    }
  };

  beforeEach(async () => {
    activityServiceSpy = {
      getActivityById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockActivityDetail })),
      createActivity: jest.fn(),
      updateActivity: jest.fn(),
    } as unknown as jest.Mocked<ActivityService>;

    classServiceSpy = {
      getMyClasses: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: {
          content: [
            { id: 'c1', formattedName: 'Física 3 - T06 - 2026.2' }
          ],
          totalElements: 1,
          totalPages: 1,
          size: 50,
          page: 0
        },
      timestamp: '2026-09-14T00:00:00Z'
      } as any)),
    } as unknown as jest.Mocked<AcademicClassService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    routerSpy = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [ActivityFormComponent],
      providers: [
        FormBuilder,
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('a1'),
              },
              queryParamMap: {
                get: jest.fn().mockReturnValue('c1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityFormComponent);
    component = fixture.componentInstance;
  });

  it('should initialize in edit mode and load activity', () => {
    component.ngOnInit();
    expect(classServiceSpy.getMyClasses).toHaveBeenCalled();
    expect(activityServiceSpy.getActivityById).toHaveBeenCalledWith('a1');
    expect(component.isEditMode).toBe(true);
    expect(component.form.get('title')?.value).toBe('Caso Clínico 1');
    expect(component.evolutionNotesArray.length).toBe(1);
    expect(component.prescriptionsArray.length).toBe(1);
    expect(component.labExamsArray.length).toBe(1);
    expect(component.proceduresArray.length).toBe(1);
  });

  it('should handle error when loading activity', () => {
    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => new Error('Error')));
    component.loadActivity('a1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar atividade.');
  });

  it('should add and remove items in dynamic form arrays', () => {
    // Evolution Notes
    component.addEvolutionNote('2026-09-03', 'Médico', 'Evolução');
    expect(component.evolutionNotesArray.length).toBe(1);
    component.removeEvolutionNote(0);
    expect(component.evolutionNotesArray.length).toBe(0);

    // Prescriptions
    component.addPrescription('Dipirona', '1g', 'EV', '6/6h', 'OK');
    expect(component.prescriptionsArray.length).toBe(1);
    component.removePrescription(0);
    expect(component.prescriptionsArray.length).toBe(0);

    // Lab Exams
    component.addLabExam('Hemograma', '11.2', '12-16', '2026-09-02');
    expect(component.labExamsArray.length).toBe(1);
    component.removeLabExam(0);
    expect(component.labExamsArray.length).toBe(0);

    // Procedures
    component.addProcedure('Curativo', 'Troca de curativo', '2026-09-02');
    expect(component.proceduresArray.length).toBe(1);
    component.removeProcedure(0);
    expect(component.proceduresArray.length).toBe(0);

    // Default arguments
    component.addEvolutionNote();
    expect(component.evolutionNotesArray.length).toBe(1);
    component.addPrescription();
    expect(component.prescriptionsArray.length).toBe(1);
    component.addLabExam();
    expect(component.labExamsArray.length).toBe(1);
    component.addProcedure();
    expect(component.proceduresArray.length).toBe(1);
  });

  it('should handle loadActivity with null or fallback clinicalCaseData', () => {
    // null clinicalCaseData
    activityServiceSpy.getActivityById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockActivityDetail, clinicalCaseData: undefined as any },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));
    component.loadActivity('a1');
    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('');

    // fallback gender and patientDays
    activityServiceSpy.getActivityById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: {
        ...mockActivityDetail,
        clinicalCaseData: {
          patientName: 'Sem Genero',
          gender: undefined,
          patientDays: 0,
        }
      },
      timestamp: '2026-09-14T00:00:00Z'
    }));
    component.loadActivity('a1');
    expect(component.clinicalCaseGroup.get('gender')?.value).toBe('Feminino');
    expect(component.clinicalCaseGroup.get('patientDays')?.value).toBe(1);
  });

  it('should not submit if form is invalid', () => {
    component.form.reset();
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Por favor, preencha todos os campos obrigatórios.');
    expect(activityServiceSpy.createActivity).not.toHaveBeenCalled();
    expect(activityServiceSpy.updateActivity).not.toHaveBeenCalled();
  });

  it('should create a new activity when in create mode', () => {
    component.activityId = null;
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Nova Atividade',
      description: 'Descrição completa',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Teste',
        patientDays: 2,
        gender: 'Feminino',
      }
    });

    activityServiceSpy.createActivity.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' }));
    component.onSubmit();

    expect(activityServiceSpy.createActivity).toHaveBeenCalled();
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade avaliativa criada com sucesso!');
    expect(routerSpy.navigate).toHaveBeenCalled();
  });

  it('should handle error when creating activity with and without error message', () => {
    component.activityId = null;
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Nova Atividade',
      description: 'Descrição completa',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Teste',
        patientDays: 2,
      }
    });

    activityServiceSpy.createActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro na criação' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro na criação');
    expect(component.isSaving()).toBe(false);

    // Without message
    activityServiceSpy.createActivity.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao criar atividade.');
  });

  it('should update an activity when in edit mode with and without error message', () => {
    component.activityId = 'a1';
    component.targetClassId = 'c1';
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Editado',
      description: 'Descrição editada',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Editado',
        patientDays: 4,
      }
    });

    activityServiceSpy.updateActivity.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' }));
    component.onSubmit();

    expect(activityServiceSpy.updateActivity).toHaveBeenCalled();
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade avaliativa atualizada com sucesso!');

    // Error without message
    activityServiceSpy.updateActivity.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao atualizar atividade.');

    // Error with message
    activityServiceSpy.updateActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro no update' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro no update');
    expect(component.isSaving()).toBe(false);
  });

  it('should go back to class when targetClassId is set', () => {
    component.targetClassId = 'c1';
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1']);
  });

  it('should go back to classes when targetClassId is null', () => {
    component.targetClassId = null;
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });
});
