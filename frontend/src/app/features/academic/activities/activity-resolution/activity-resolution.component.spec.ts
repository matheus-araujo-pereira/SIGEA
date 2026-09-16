import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivityResolutionComponent } from './activity-resolution.component';
import { ActivityService } from '../../../../core/services/activity.service';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityDetailDTO, ClinicalCaseData } from '../../../../core/models/activity.model';

describe('ActivityResolutionComponent', () => {
  let component: ActivityResolutionComponent;
  let fixture: ComponentFixture<ActivityResolutionComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let triggerServiceSpy: jest.Mocked<GttTriggerService>;
  let severityServiceSpy: jest.Mocked<HarmSeverityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockActivity: ActivityDetailDTO = {
    id: 'a1',
    title: 'Caso Clínico 1',
    description: 'Instruções',
    classId: 'c1',
    className: 'Física 3 - T06 - 2026.2',
    academicClassId: 'c1',
    academicClassName: 'Física 3 - T06 - 2026.2',
    createdAt: '2026-09-01T00:00:00Z',
    deadline: '2026-12-31T23:59:00Z',
    isExpired: false,
    submissionCount: 1,
    clinicalCaseData: {
      patientName: 'Severino Silva',
      patientDays: 3,
    },
    studentSubmission: {
      id: 'sub1',
      activityId: 'a1',
      activityTitle: 'Caso Clínico 1',
      studentId: 'st1',
      studentName: 'Matheus Araujo',
      studentEmail: 'matheus@academico.ufs.br',
      submissionDate: '2026-09-10T12:00:00Z',
      isGraded: false,
      identifiedTriggers: [
        { triggerCode: 'M1', triggerName: 'Naloxona', isHarm: true, harmCategory: 'E' }
      ],
      qualityToolsData: {
        ishikawa: {
          centralProblem: 'Problema Teste',
          methodCauses: ['Causa Método'],
          manpowerCauses: ['Causa Mão de Obra'],
          materialCauses: ['Causa Material'],
          machineCauses: ['Causa Máquina'],
          environmentCauses: ['Causa Ambiente'],
          measurementCauses: ['Causa Medida']
        }
      } as any,
    }
  };

  const mockTriggers = [
    { id: 't1', code: 'M1', name: 'Naloxona', moduleCode: 'MED', active: true }
  ];

  const mockSeverities = [
    { id: 's1', letter: 'E', name: 'Categoria E', active: true }
  ];

  beforeEach(async () => {
    activityServiceSpy = {
      getActivityById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockActivity })),
      submitActivity: jest.fn(),
    } as unknown as jest.Mocked<ActivityService>;

    triggerServiceSpy = {
      listTriggers: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: { content: mockTriggers } })),
    } as unknown as jest.Mocked<GttTriggerService>;

    severityServiceSpy = {
      listHarmSeverities: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: { content: mockSeverities } })),
    } as unknown as jest.Mocked<HarmSeverityService>;

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
      imports: [ActivityResolutionComponent],
      providers: [
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: GttTriggerService, useValue: triggerServiceSpy },
        { provide: HarmSeverityService, useValue: severityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('a1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityResolutionComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    component.ngOnDestroy();
  });

  it('should initialize and load activity details with student submission', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getActivityById).toHaveBeenCalledWith('a1');
    expect(triggerServiceSpy.listTriggers).toHaveBeenCalled();
    expect(severityServiceSpy.listHarmSeverities).toHaveBeenCalled();
    expect(component.activity()).toEqual(mockActivity);
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.qualityTools.ishikawa?.centralProblem).toBe('Problema Teste');
    expect(component.patientInitial()).toBe('S');
  });

  it('should return default P if patientName is missing', () => {
    component.activity.set({
      ...mockActivity,
      clinicalCaseData: undefined as unknown as ClinicalCaseData,
    });
    expect(component.patientInitial()).toBe('P');
  });

  it('should handle error when loading activity', () => {
    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => new Error('Error')));
    component.loadActivity('a1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar detalhes da atividade.');
  });

  it('should manage 20-min countdown timer', fakeAsync(() => {
    component.startTimer();
    expect(component.timerSeconds()).toBe(1200);
    expect(component.formattedTime()).toBe('20:00');

    tick(1000);
    expect(component.timerSeconds()).toBe(1199);
    expect(component.formattedTime()).toBe('19:59');

    component.toggleTimer();
    expect(component.isTimerPaused()).toBe(true);
    tick(1000);
    // Should not decrease when paused
    expect(component.timerSeconds()).toBe(1199);

    component.toggleTimer();
    expect(component.isTimerPaused()).toBe(false);
    component.ngOnDestroy();
  }));

  it('should add and remove identified triggers', () => {
    component.availableTriggers.set(mockTriggers as any);

    // If triggerCode is empty, should do nothing
    component.selectedTriggerCode = '';
    component.addIdentifiedTrigger();

    component.selectedTriggerCode = 'M1';
    component.isHarmSelected = true;
    component.selectedHarmSeverityLetter = 'F';
    component.triggerNotes = 'Gatilho medicamentoso de dano';

    component.addIdentifiedTrigger();
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.identifiedTriggers()[0].harmSeverityLetter).toBe('F');
    expect(component.selectedTriggerCode).toBe('');

    component.removeIdentifiedTrigger(0);
    expect(component.identifiedTriggers().length).toBe(0);
  });

  it('should manage Ishikawa causes', () => {
    component.addIshikawaCause('method');
    expect(component.qualityTools.ishikawa?.methodCauses?.length).toBe(2);

    component.removeIshikawaCause('method', 0);
    expect(component.qualityTools.ishikawa?.methodCauses?.length).toBe(1);
  });

  it('should manage GUT matrix items and score calculation', () => {
    component.addGutItem();
    expect(component.qualityTools.gutItems?.length).toBe(1);

    const score = component.calculateGutScore({ problem: 'Falha', gravity: 4, urgency: 3, trend: 5 });
    expect(score).toBe(60);

    const defaultScore = component.calculateGutScore({ problem: 'Falha' } as any);
    expect(defaultScore).toBe(1);

    component.removeGutItem(0);
    expect(component.qualityTools.gutItems?.length).toBe(0);
  });

  it('should manage 5W2H items', () => {
    component.addFiveWTwoHItem();
    expect(component.qualityTools.fiveWTwoHItems?.length).toBe(1);

    component.removeFiveWTwoHItem(0);
    expect(component.qualityTools.fiveWTwoHItems?.length).toBe(0);
  });

  it('should manage SWOT items', () => {
    component.addSwotItem('strengths');
    expect(component.qualityTools.swot?.strengths?.length).toBe(2);

    component.removeSwotItem('strengths', 0);
    expect(component.qualityTools.swot?.strengths?.length).toBe(1);
  });

  it('should manage Brainstorming notes', () => {
    component.addBrainstormingNote();
    expect(component.qualityTools.brainstormingNotes?.length).toBe(1);

    component.removeBrainstormingNote(0);
    expect(component.qualityTools.brainstormingNotes?.length).toBe(0);
  });

  it('should submit resolution successfully', () => {
    component.activityId = 'a1';
    component.confirmSubmit();
    expect(component.isConfirmSubmitOpen()).toBe(true);

    activityServiceSpy.submitActivity.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.submitResolution();

    expect(activityServiceSpy.submitActivity).toHaveBeenCalled();
    expect(toastSpy.success).toHaveBeenCalledWith('Resolução enviada com sucesso!');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);
    expect(component.isConfirmSubmitOpen()).toBe(false);
  });

  it('should handle loadActivity when studentSubmission has no triggers or quality tools', () => {
    activityServiceSpy.getActivityById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: {
        ...mockActivity,
        studentSubmission: {
          id: 'sub2',
          activityId: 'a1',
          studentId: 'st1',
          studentName: 'Aluno',
          studentEmail: 'aluno@academico.ufs.br',
          submissionDate: '2026-09-10',
          isGraded: false,
          identifiedTriggers: undefined as any,
          qualityToolsData: undefined as any,
        }
      },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));
    component.loadActivity('a1');
    expect(component.identifiedTriggers()).toEqual([]);
  });

  it('should add trigger without harm (isHarm = false)', () => {
    component.availableTriggers.set(mockTriggers as any);
    component.selectedTriggerCode = 'M1';
    component.isHarmSelected = false;
    component.triggerNotes = 'Gatilho sem dano';

    component.addIdentifiedTrigger();
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.identifiedTriggers()[0].isHarm).toBe(false);
    expect(component.identifiedTriggers()[0].harmSeverityLetter).toBeUndefined();
  });

  it('should handle error when submitting resolution with and without message', () => {
    component.activityId = 'a1';
    activityServiceSpy.submitActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro na submissão' } })));
    component.submitResolution();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro na submissão');

    activityServiceSpy.submitActivity.mockReturnValue(throwError(() => ({})));
    component.submitResolution();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao submeter resolução.');
  });

  it('should not submit resolution if activityId is null', () => {
    component.activityId = null;
    component.submitResolution();
    expect(activityServiceSpy.submitActivity).not.toHaveBeenCalled();
  });

  it('should navigate back on goBack', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);
  });
});
