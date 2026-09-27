import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityGradingDetailComponent } from './activity-grading-detail.component';
import { ActivityService } from '../../services/activity.service';
import { ReportService } from '../../../report/services/report.service';
import { ToastService } from '../../../../common/services/toast.service';
import { SubmissionResponseDTO } from '../../models/activity.model';

describe('ActivityGradingDetailComponent', () => {
  let component: ActivityGradingDetailComponent;
  let fixture: ComponentFixture<ActivityGradingDetailComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;
  let reportServiceSpy: jest.Mocked<ReportService>;

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
        { problem: 'Sedação excessiva', gravity: 5, urgency: 5, tendency: 4, score: 100 }
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
      brainstormingNotes: 'Checklist de infusão'
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

    reportServiceSpy = {
      downloadSubmissionPdf: jest.fn().mockReturnValue(of(new Blob())),
      downloadClassBulletinPdf: jest.fn().mockReturnValue(of(new Blob())),
      downloadClassResearchCsv: jest.fn().mockReturnValue(of(new Blob())),
      saveBlob: jest.fn(),
    } as unknown as jest.Mocked<ReportService>;

    await TestBed.configureTestingModule({
      imports: [ActivityGradingDetailComponent],
      providers: [
        FormBuilder,
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ReportService, useValue: reportServiceSpy },
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

  it('deve inicializar e carregar submissão', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getSubmissionById).toHaveBeenCalledWith('sub1');
    expect(component.submission()).toEqual(mockSubmission);
    expect(component.gradeForm.get('grade')?.value).toBe(9.0);
    expect(component.gradeForm.get('pedagogicalFeedback')?.value).toBe('Ótima identificação e raciocínio clínico!');
  });

  it('deve mapear propriedades legadas e alternativas de trigger e feedback', () => {
    const legacySubmission: any = {
      ...mockSubmission,
      qualityTools: undefined,
      qualityToolsData: { ishikawa: { problem: 'Prob' } },
      pedagogicalFeedback: undefined,
      professorFeedback: 'Parecer do professor legado',
      identifiedTriggers: [
        {
          triggerCode: 'C1',
          triggerName: 'Reinternação',
          harmCategory: undefined,
          harmSeverityLetter: 'F',
          rationale: undefined,
          clinicalJustification: 'Justificativa clínica legada'
        }
      ]
    };
    activityServiceSpy.getSubmissionById.mockReturnValue(of({ success: true, message: 'OK', data: legacySubmission }));
    component.loadSubmission();

    const sub = component.submission();
    expect(sub?.qualityTools).toBeDefined();
    expect(sub?.pedagogicalFeedback).toBe('Parecer do professor legado');
    expect(sub?.identifiedTriggers[0].harmCategory).toBe('F');
    expect(sub?.identifiedTriggers[0].rationale).toBe('Justificativa clínica legada');
  });

  it('deve redirecionar se submissionId não estiver presente na rota', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(toastSpy.error).toHaveBeenCalledWith('Submissão não informada.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('deve tratar erro ao carregar submissão', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao obter submissão' } })));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao obter submissão');
    expect(component.isLoading()).toBe(false);
  });

  it('não deve submeter avaliação com formulário inválido', () => {
    component.gradeForm.reset();
    component.submitGrade();
    expect(activityServiceSpy.gradeSubmission).not.toHaveBeenCalled();
  });

  it('deve submeter avaliação e redirecionar com sucesso', () => {
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

  it('deve lidar com submissão sem nota prévia (isGraded: false)', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockSubmission, isGraded: false, grade: undefined, pedagogicalFeedback: undefined },
      timestamp: '2026-09-14T00:00:00Z'
    }));
    component.loadSubmission();
    expect(component.gradeForm.get('grade')?.value).toBeNull();
  });

  it('deve tratar mensagens de erro padrão quando error.message for indefinido', () => {
    activityServiceSpy.getSubmissionById.mockReturnValue(throwError(() => ({})));
    component.loadSubmission();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar dados da submissão.');

    component.submissionId.set('sub1');
    component.gradeForm.patchValue({ grade: 8, pedagogicalFeedback: 'Comentário longo suficiente' });
    activityServiceSpy.gradeSubmission.mockReturnValue(throwError(() => ({})));
    component.submitGrade();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao registrar avaliação.');
  });

  it('deve voltar para a lista de turmas se activityId for nulo', () => {
    component.submission.set(null);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  describe('Download do Relatório Clínico Oficial', () => {
    it('deve chamar downloadSubmissionPdf quando submission estiver carregada', () => {
      component.submission.set(mockSubmission);
      component.downloadSubmissionPdf();
      expect(reportServiceSpy.downloadSubmissionPdf).toHaveBeenCalledWith(mockSubmission.id);
    });

    it('não deve chamar downloadSubmissionPdf quando submission for nula', () => {
      component.submission.set(null);
      component.downloadSubmissionPdf();
      expect(reportServiceSpy.downloadSubmissionPdf).not.toHaveBeenCalled();
    });
  });
});
