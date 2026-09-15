import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivityGradingDetailComponent } from './activity-grading-detail.component';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';

describe('ActivityGradingDetailComponent', () => {
  let component: ActivityGradingDetailComponent;
  let fixture: ComponentFixture<ActivityGradingDetailComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockSubmission: SubmissionResponseDTO = {
    id: 'sub1',
    activityId: 'act1',
    activityTitle: 'Caso Clínico 1',
    studentId: 'st1',
    studentName: 'Matheus Araujo',
    studentEmail: 'matheus@academico.ufs.br',
    submissionDate: '2026-09-10T12:00:00Z',
    isGraded: true,
    grade: 9.0,
    pedagogicalFeedback: 'Ótima identificação e raciocínio clínico!',
    identifiedTriggers: [
      { triggerCode: 'M1', triggerName: 'Naloxona', harmCategory: 'E', rationale: 'Sedação excessiva por opioide' }
    ],
    qualityTools: {
      ishikawa: {
        problem: 'Sedação excessiva',
        method: 'Dose calculada incorretamente',
        manpower: 'Falta de dupla checagem',
        material: 'Ampola não padronizada',
        machine: 'Bomba de infusão sem alarme',
        environment: 'Unidade com ruído elevado',
        measurement: 'Sem escala de Ramsay'
      },
      gutItems: [
        { problem: 'Sedação excessiva', gravity: 5, urgency: 5, trend: 5, tendency: 4, score: 100 }
      ],
      fiveWTwoHItems: [
        { what: 'Dupla checagem', why: 'Prevenir erro', where: 'UTI', when: 'Imediato', who: 'Enfermeiro', how: 'Protocolo', howMuch: 'R$ 0,00' }
      ],
      pdca: {
        plan: 'Elaborar protocolo',
        doAction: 'Treinar equipe',
        checkAction: 'Auditar prontuários',
        act: 'Padronizar processo'
      },
      swot: {
        strengths: ['Equipe qualificada'],
        weaknesses: ['Sobrecarga'],
        opportunities: ['Treinamento contínuo'],
        threats: ['Rotatividade']
      },
      brainstormingNotes: ['Checklist de infusão']
    }
  };

  beforeEach(async () => {
    activityServiceSpy = {
      getSubmissionById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockSubmission })),
      gradeSubmission: jest.fn(),
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
      imports: [ActivityGradingDetailComponent],
      providers: [
        FormBuilder,
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

    fixture = TestBed.createComponent(ActivityGradingDetailComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load submission', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getSubmissionById).toHaveBeenCalledWith('sub1');
    expect(component.submission()).toEqual(mockSubmission);
    expect(component.gradeForm.get('grade')?.value).toBe(9.0);
    expect(component.gradeForm.get('pedagogicalFeedback')?.value).toBe('Ótima identificação e raciocínio clínico!');
  });

  it('should redirect if submissionId param is missing', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(toastSpy.error).toHaveBeenCalledWith('Submissão não informada.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('should handle error when loading submission', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao obter submissão' } })));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao obter submissão');
    expect(component.isLoading()).toBe(false);
  });

  it('should not submit grade if form is invalid', () => {
    component.gradeForm.reset();
    component.submitGrade();
    expect(activityServiceSpy.gradeSubmission).not.toHaveBeenCalled();
  });

  it('should submit grade and navigate back on success', () => {
    component.submissionId.set('sub1');
    component.submission.set(mockSubmission);
    component.gradeForm.patchValue({
      grade: 9.5,
      pedagogicalFeedback: 'Excelente resolução do caso clínico!',
    });

    activityServiceSpy.gradeSubmission.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockSubmission, grade: 9.5 },
      timestamp: '2026-09-14T00:00:00Z'
    }));

    component.submitGrade();

    expect(activityServiceSpy.gradeSubmission).toHaveBeenCalledWith('sub1', {
      grade: 9.5,
      pedagogicalFeedback: 'Excelente resolução do caso clínico!',
    });
    expect(toastSpy.success).toHaveBeenCalledWith('Avaliação registrada com sucesso!');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities', 'act1', 'grading']);
  });

  it('should handle submission with isGraded false', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockSubmission, isGraded: false, grade: null, pedagogicalFeedback: null },
      timestamp: '2026-09-14T00:00:00Z'
    }));
    component.loadSubmission();
    expect(component.gradeForm.get('grade')?.value).toBeNull();
  });

  it('should handle error fallbacks with undefined error message', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({})));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar dados da submissão.');

    component.submissionId.set('sub1');
    component.gradeForm.patchValue({ grade: 8, pedagogicalFeedback: 'Comentário longo' });
    activityServiceSpy.gradeSubmission.mockReturnValue(throwError(() => ({})));
    component.submitGrade();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao registrar avaliação.');
  });

  it('should go back to class list if activityId is null', () => {
    component.submission.set(null);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });
});
