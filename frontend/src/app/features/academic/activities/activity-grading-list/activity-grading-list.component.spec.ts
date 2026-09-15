import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivityGradingListComponent } from './activity-grading-list.component';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityDetailDTO, SubmissionResponseDTO } from '../../../../core/models/activity.model';

describe('ActivityGradingListComponent', () => {
  let component: ActivityGradingListComponent;
  let fixture: ComponentFixture<ActivityGradingListComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockActivity: ActivityDetailDTO = {
    id: 'act1',
    title: 'Caso Clínico 1',
    description: 'Instruções',
    classId: 'c1',
    className: 'Física 3 - T06 - 2026.2',
    academicClassId: 'c1',
    academicClassName: 'Física 3 - T06 - 2026.2',
    clinicalCaseData: { patientName: 'Paciente Teste' },
    createdAt: '2026-09-01T00:00:00Z',
    deadline: '2026-12-31T23:59:00Z',
    isExpired: false,
    submissionCount: 1,
  };

  const mockSubmissions: SubmissionResponseDTO[] = [
    {
      id: 'sub1',
      activityId: 'act1',
    activityTitle: 'Caso Clínico 1',
      studentId: 'st1',
      studentName: 'Matheus Araujo',
      studentEmail: 'matheus@academico.ufs.br',
      submissionDate: '2026-09-10T12:00:00Z',
      isGraded: true,
      grade: 8.5,
      pedagogicalFeedback: 'Bom trabalho!',
      identifiedTriggers: [
        { triggerCode: 'M1', triggerName: 'Naloxona', harmCategory: 'E', rationale: 'Uso de naloxona' }
      ],
      qualityTools: {} as any,
    }
  ];

  beforeEach(async () => {
    activityServiceSpy = {
      getActivityById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockActivity })),
      listSubmissions: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: {
          content: mockSubmissions,
          totalElements: 1,
          totalPages: 1,
          size: 10,
          page: 0,
        }
      })),
    } as unknown as jest.Mocked<ActivityService>;

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
      imports: [ActivityGradingListComponent],
      providers: [
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('act1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityGradingListComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load activity details and submissions', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getActivityById).toHaveBeenCalledWith('act1');
    expect(activityServiceSpy.listSubmissions).toHaveBeenCalledWith('act1', 0, 10);
    expect(component.activity()).toEqual(mockActivity);
    expect(component.submissions().length).toBe(1);
    expect(component.isLoading()).toBe(false);
  });

  it('should redirect if activityId param is missing', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(toastSpy.error).toHaveBeenCalledWith('Identificador de atividade inválido.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('should handle error when loading activity details', () => {
    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao obter atividade' } })));
    component.loadActivityDetails();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao obter atividade');

    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => ({})));
    component.loadActivityDetails();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar detalhes da atividade.');
  });

  it('should handle error when loading submissions', () => {
    activityServiceSpy.listSubmissions.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao listar submissões' } })));
    component.loadSubmissions();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao listar submissões');
    expect(component.isLoading()).toBe(false);

    activityServiceSpy.listSubmissions.mockReturnValue(throwError(() => ({})));
    component.loadSubmissions();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar lista de submissões.');
  });

  it('should change page when within bounds', () => {
    component.activityId.set('act1');
    component.totalPages.set(3);
    component.changePage(1);
    expect(component.currentPage()).toBe(1);
    expect(activityServiceSpy.listSubmissions).toHaveBeenCalledWith('act1', 1, 10);
  });

  it('should not change page when outside bounds', () => {
    component.totalPages.set(2);
    component.changePage(-1);
    expect(component.currentPage()).toBe(0);

    component.changePage(5);
    expect(component.currentPage()).toBe(0);
  });

  it('should go back to class when academicClassId is present', () => {
    component.activity.set(mockActivity);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1']);
  });

  it('should go back to classes when academicClassId is null', () => {
    component.activity.set(null);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });
});
