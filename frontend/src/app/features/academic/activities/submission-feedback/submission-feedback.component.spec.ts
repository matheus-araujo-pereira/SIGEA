import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SubmissionFeedbackComponent } from './submission-feedback.component';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';

describe('SubmissionFeedbackComponent', () => {
  let component: SubmissionFeedbackComponent;
  let fixture: ComponentFixture<SubmissionFeedbackComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockSubmission: SubmissionResponseDTO = {
    id: 'sub1',
    activityId: 'act1',
    activityTitle: 'Caso Clínico Feedback',
    studentId: 'st1',
    studentName: 'Matheus Araujo',
    studentEmail: 'matheus@academico.ufs.br',
    submissionDate: '2026-09-10T12:00:00Z',
    isGraded: true,
    grade: 9.5,
    pedagogicalFeedback: 'Parabéns pela resolução detalhada!',
    identifiedTriggers: [
      { triggerCode: 'M1', triggerName: 'Naloxona', harmCategory: 'E', rationale: 'Justificativa do gatilho' }
    ],
    qualityTools: {
      ishikawa: {
        problem: 'Problema',
        method: 'Método',
        manpower: 'Mão de obra',
        material: 'Material',
        machine: 'Máquina',
        environment: 'Ambiente',
        measurement: 'Medida'
      },
      gutItems: [
        { problem: 'Falha', gravity: 5, urgency: 5, trend: 5, tendency: 5, score: 125 }
      ],
      fiveWTwoHItems: [
        { what: 'Plano', why: 'Melhoria', where: 'UTI', when: 'Hoje', who: 'Equipe', how: 'Processo', howMuch: 'Zero' }
      ],
      pdca: {
        plan: 'P',
        doAction: 'D',
        checkAction: 'C',
        act: 'A'
      },
      swot: {
        strengths: ['S'],
        weaknesses: ['W'],
        opportunities: ['O'],
        threats: ['T']
      },
      brainstormingNotes: ['Ideias']
    }
  };

  beforeEach(async () => {
    activityServiceSpy = {
      getSubmissionById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockSubmission })),
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
      imports: [SubmissionFeedbackComponent],
      providers: [
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('sub1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmissionFeedbackComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load feedback', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getSubmissionById).toHaveBeenCalledWith('sub1');
    expect(component.submission()).toEqual(mockSubmission);
    expect(component.isLoading()).toBe(false);
  });

  it('should redirect if submissionId param is missing', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(toastSpy.error).toHaveBeenCalledWith('Identificador de submissão inválido.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);
  });

  it('should handle error when loading feedback with and without error message', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({ error: { message: 'Erro customizado' } })));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro customizado');
    expect(component.isLoading()).toBe(false);

    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({})));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar parecer pedagógico.');
  });

  it('should navigate back to student activities', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);
  });
});
