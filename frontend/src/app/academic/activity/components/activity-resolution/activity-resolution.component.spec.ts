import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityResolutionComponent } from './activity-resolution.component';
import { ActivityService } from '../../services/activity.service';
import { GttTriggerService } from '../../../../gtt/trigger/services/gtt-trigger.service';
import { HarmSeverityService } from '../../../../gtt/severity/services/harm-severity.service';
import { ToastService } from '../../../../common/services/toast.service';
import { ActivityDetailDTO, ClinicalCaseData } from '../../models/activity.model';

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
    className: 'Enfermagem - T01 - 2026.1',
    academicClassId: 'c1',
    academicClassName: 'Enfermagem - T01 - 2026.1',
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

  it('deve inicializar e carregar detalhes da atividade e submissão do aluno', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getActivityById).toHaveBeenCalledWith('a1');
    expect(triggerServiceSpy.listTriggers).toHaveBeenCalled();
    expect(severityServiceSpy.listHarmSeverities).toHaveBeenCalled();
    expect(component.activity()).toEqual(mockActivity);
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.qualityTools.ishikawa?.centralProblem).toBe('Problema Teste');
    expect(component.patientInitial()).toBe('S');
  });

  it('deve retornar P padrão caso o nome do paciente esteja indefinido', () => {
    component.activity.set({
      ...mockActivity,
      clinicalCaseData: undefined as unknown as ClinicalCaseData,
    });
    expect(component.patientInitial()).toBe('P');
  });

  it('deve tratar erro ao carregar atividade', () => {
    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => new Error('Error')));
    component.loadActivity('a1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar detalhes da atividade.');
  });

  it('deve gerenciar o cronômetro regressivo de 20 minutos', fakeAsync(() => {
    component.startTimer();
    expect(component.timerSeconds()).toBe(1200);
    expect(component.formattedTime()).toBe('20:00');

    tick(1000);
    expect(component.timerSeconds()).toBe(1199);
    expect(component.formattedTime()).toBe('19:59');

    component.toggleTimer();
    expect(component.isTimerPaused()).toBe(true);
    tick(1000);
    expect(component.timerSeconds()).toBe(1199);

    component.toggleTimer();
    expect(component.isTimerPaused()).toBe(false);
    component.ngOnDestroy();
  }));

  it('deve adicionar e remover gatilhos identificados', () => {
    component.availableTriggers.set(mockTriggers as any);

    // Sem trigger selecionado
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

  it('deve adicionar gatilho a partir do evento do painel onAddTriggerFromPanel', () => {
    component.onAddTriggerFromPanel({
      triggerCode: 'S1',
      triggerName: 'Hemorragia',
      isHarm: false
    });
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.identifiedTriggers()[0].triggerCode).toBe('S1');
  });

  it('deve gerenciar causas no Diagrama de Ishikawa', () => {
    component.addIshikawaCause('method');
    expect(component.qualityTools.ishikawa?.methodCauses?.length).toBe(2);

    component.removeIshikawaCause('method', 0);
    expect(component.qualityTools.ishikawa?.methodCauses?.length).toBe(1);
  });

  it('deve gerenciar itens na Matriz GUT e calcular o score ponderado', () => {
    component.addGutItem();
    expect(component.qualityTools.gutItems?.length).toBe(1);

    const score = component.calculateGutScore({ problem: 'Falha', gravity: 4, urgency: 3, trend: 5 });
    expect(score).toBe(60);

    const defaultScore = component.calculateGutScore({ problem: 'Falha' } as any);
    expect(defaultScore).toBe(1);

    component.removeGutItem(0);
    expect(component.qualityTools.gutItems?.length).toBe(0);
  });

  it('deve gerenciar itens no plano 5W2H', () => {
    component.addFiveWTwoHItem();
    expect(component.qualityTools.fiveWTwoHItems?.length).toBe(1);

    component.removeFiveWTwoHItem(0);
    expect(component.qualityTools.fiveWTwoHItems?.length).toBe(0);
  });

  it('deve gerenciar itens na Matriz SWOT', () => {
    component.addSwotItem('strengths');
    expect(component.qualityTools.swot?.strengths?.length).toBe(2);

    component.removeSwotItem('strengths', 0);
    expect(component.qualityTools.swot?.strengths?.length).toBe(1);
  });

  it('deve gerenciar anotações de Brainstorming', () => {
    component.addBrainstormingNote();
    expect(component.qualityTools.brainstormingNotes?.length).toBe(1);

    component.removeBrainstormingNote(0);
    expect(component.qualityTools.brainstormingNotes?.length).toBe(0);
  });

  it('deve submeter a resolução com sucesso', () => {
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

  it('deve tratar loadActivity quando studentSubmission não tiver gatilhos ou qualidade', () => {
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

  it('deve adicionar gatilho sem dano (isHarm = false)', () => {
    component.availableTriggers.set(mockTriggers as any);
    component.selectedTriggerCode = 'M1';
    component.isHarmSelected = false;
    component.triggerNotes = 'Gatilho sem dano';

    component.addIdentifiedTrigger();
    expect(component.identifiedTriggers().length).toBe(1);
    expect(component.identifiedTriggers()[0].isHarm).toBe(false);
    expect(component.identifiedTriggers()[0].harmSeverityLetter).toBeUndefined();
  });

  it('deve tratar erro ao submeter com e sem mensagem', () => {
    component.activityId = 'a1';
    activityServiceSpy.submitActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro na submissão' } })));
    component.submitResolution();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro na submissão');

    activityServiceSpy.submitActivity.mockReturnValue(throwError(() => ({})));
    component.submitResolution();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao submeter resolução.');
  });

  it('não deve submeter se activityId for nulo', () => {
    component.activityId = null;
    component.submitResolution();
    expect(activityServiceSpy.submitActivity).not.toHaveBeenCalled();
    expect(toastSpy.error).toHaveBeenCalledWith('Identificador de atividade não encontrado.');
  });

  it('deve navegar de volta em goBack', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);
  });

  it('deve se inscrever em route.paramMap caso snapshot retorne nulo', () => {
    const routeMock = {
      snapshot: { paramMap: { get: () => null } },
      paramMap: of({ get: (key: string) => (key === 'id' ? 'a1' : null) }),
    };
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [ActivityResolutionComponent],
      providers: [
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: GttTriggerService, useValue: triggerServiceSpy },
        { provide: HarmSeverityService, useValue: severityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: routeMock },
      ],
    });
    const fix = TestBed.createComponent(ActivityResolutionComponent);
    fix.detectChanges();
    expect(fix.componentInstance.activityId).toBe('a1');
  });
});
