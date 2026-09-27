import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ActivityService } from './activity.service';
import {
  ActivityCreateDTO,
  ActivityDetailDTO,
  ActivityResponseDTO,
  ActivityUpdateDTO,
  SubmissionCreateDTO,
  SubmissionGradeDTO,
  SubmissionResponseDTO,
} from '../models/activity.model';

describe('ActivityService', () => {
  let service: ActivityService;
  let httpMock: HttpTestingController;

  const mockActivity: ActivityResponseDTO = {
    id: 'act-1',
    classId: 'class-1',
    className: 'Enfermagem - T01 - 2026.2',
    title: 'Estudo de Caso 1',
    description: 'Desc',
    deadline: '2026-09-30T00:00:00Z',
    isExpired: false,
    submissionCount: 2,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockDetail = {
    ...mockActivity,
    clinicalCaseData: { patientName: 'Maria' },
  } as ActivityDetailDTO;

  const mockSubmission: SubmissionResponseDTO = {
    id: 'sub-1',
    activityId: 'act-1',
    activityTitle: 'Estudo de Caso 1',
    studentId: 'student-1',
    studentName: 'Aluno',
    studentEmail: 'aluno@academico.ufs.br',
    identifiedTriggers: [],
    qualityToolsData: {},
    submissionDate: '2026-09-15T00:00:00Z',
    isGraded: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ActivityService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ActivityService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve listar atividades da turma', () => {
    service.listActivities('class-1', 0, 10, 'deadline,asc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/academic/activities?classId=class-1&page=0&size=10&sort=deadline,asc');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [mockActivity] } });
  });

  it('deve obter atividade por id', () => {
    service.getActivityById('act-1').subscribe((res) => {
      expect(res.data.id).toBe('act-1');
    });

    const req = httpMock.expectOne('/api/academic/activities/act-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockDetail });
  });

  it('deve criar atividade', () => {
    const dto: ActivityCreateDTO = {
      classId: 'class-1',
      title: 'Estudo de Caso 1',
      description: 'Desc',
      clinicalCaseData: { patientName: 'Maria' },
      deadline: '2026-09-30T00:00:00Z',
    };

    service.createActivity(dto).subscribe((res) => {
      expect(res.data.id).toBe('act-1');
    });

    const req = httpMock.expectOne('/api/academic/activities');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush({ success: true, data: mockDetail });
  });

  it('deve atualizar atividade', () => {
    const dto: ActivityUpdateDTO = {
      title: 'Estudo Atualizado',
      description: 'Desc',
      clinicalCaseData: { patientName: 'Maria' },
      deadline: '2026-09-30T00:00:00Z',
    };

    service.updateActivity('act-1', dto).subscribe((res) => {
      expect(res.data.id).toBe('act-1');
    });

    const req = httpMock.expectOne('/api/academic/activities/act-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: mockDetail });
  });

  it('deve excluir atividade', () => {
    service.deleteActivity('act-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/academic/activities/act-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('deve submeter atividade', () => {
    const dto: SubmissionCreateDTO = {
      identifiedTriggers: [],
      qualityToolsData: {},
    };

    service.submitActivity('act-1', dto).subscribe((res) => {
      expect(res.data.id).toBe('sub-1');
    });

    const req = httpMock.expectOne('/api/academic/activities/act-1/submissions');
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, data: mockSubmission });
  });

  it('deve listar submissões de uma atividade', () => {
    service.listSubmissions('act-1', 0, 10, 'submissionDate,desc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/academic/activities/act-1/submissions?page=0&size=10&sort=submissionDate,desc');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [mockSubmission] } });
  });

  it('deve obter submissão por ID', () => {
    service.getSubmissionById('sub-1').subscribe((res) => {
      expect(res.data.id).toBe('sub-1');
    });

    const req = httpMock.expectOne('/api/academic/submissions/sub-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockSubmission });
  });

  it('deve avaliar submissão com nota e parecer pedagógico', () => {
    const dto: SubmissionGradeDTO = {
      grade: 9.5,
      professorFeedback: 'Muito bom',
    };

    service.gradeSubmission('sub-1', dto).subscribe((res) => {
      expect(res.data.grade).toBe(9.5);
    });

    const req = httpMock.expectOne('/api/academic/submissions/sub-1/grade');
    expect(req.request.method).toBe('PATCH');
    req.flush({ success: true, data: { ...mockSubmission, grade: 9.5, isGraded: true } });
  });

  it('deve avaliar submissão utilizando pedagogicalFeedback como fallback', () => {
    const dto: SubmissionGradeDTO = {
      grade: 8.0,
      pedagogicalFeedback: 'Bom trabalho com as ferramentas',
    };

    service.gradeSubmission('sub-1', dto).subscribe((res) => {
      expect(res.data.grade).toBe(8.0);
    });

    const req = httpMock.expectOne('/api/academic/submissions/sub-1/grade');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body.professorFeedback).toBe('Bom trabalho com as ferramentas');
    req.flush({ success: true, data: { ...mockSubmission, grade: 8.0, isGraded: true } });
  });

  it('deve listar submissões do estudante logado', () => {
    service.getMySubmissions(0, 10).subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/academic/submissions/my-submissions?page=0&size=10&sort=submissionDate,desc');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [mockSubmission] } });
  });

  it('deve chamar listActivities, listSubmissions e getMySubmissions com parâmetros padrão', () => {
    service.listActivities('class-1').subscribe();
    const req1 = httpMock.expectOne('/api/academic/activities?classId=class-1&page=0&size=10&sort=deadline,desc');
    req1.flush({ success: true, data: { content: [] } });

    service.listSubmissions('act-1').subscribe();
    const req2 = httpMock.expectOne('/api/academic/activities/act-1/submissions?page=0&size=10&sort=submissionDate,desc');
    req2.flush({ success: true, data: { content: [] } });

    service.getMySubmissions().subscribe();
    const req3 = httpMock.expectOne('/api/academic/submissions/my-submissions?page=0&size=10&sort=submissionDate,desc');
    req3.flush({ success: true, data: { content: [] } });
  });
});
