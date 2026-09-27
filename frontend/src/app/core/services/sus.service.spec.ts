import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SusService } from './sus.service';
import { ToastService } from './toast.service';
import {
  SusEvaluationCreateDTO,
  SusEvaluationResponseDTO,
  SusClassSummaryDTO,
  SusGeneralSummaryDTO,
  SusEvaluationPreviewDTO
} from '../models/sus.model';

describe('SusService', () => {
  let service: SusService;
  let httpMock: HttpTestingController;
  let toastService: jest.Mocked<ToastService>;

  const mockEvaluation: SusEvaluationResponseDTO = {
    id: 'sus-1',
    studentId: 'st-1',
    studentName: 'Matheus Araujo',
    studentEmail: 'matheus@academico.ufs.br',
    academicClassId: 'cls-1',
    className: 'Turma Teste',
    q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
    score: 100.0,
    adjectiveRating: 'Melhor Imaginável',
    acceptability: 'Aceitável',
    gradeLevel: 'A',
    suggestions: 'Excelente!',
    createdAt: '2026-09-27T12:00:00Z'
  };

  beforeEach(() => {
    toastService = {
      success: jest.fn(),
      error: jest.fn(),
      warning: jest.fn(),
      info: jest.fn(),
      remove: jest.fn(),
      toasts: jest.fn()
    } as unknown as jest.Mocked<ToastService>;

    window.URL.createObjectURL = jest.fn();
    window.URL.revokeObjectURL = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        SusService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ToastService, useValue: toastService }
      ]
    });

    service = TestBed.inject(SusService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('submitEvaluation', () => {
    it('deve submeter avaliação SUS com sucesso e exibir toast', () => {
      const dto: SusEvaluationCreateDTO = {
        academicClassId: 'cls-1',
        q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1
      };

      service.submitEvaluation(dto).subscribe((res) => {
        expect(res.data).toEqual(mockEvaluation);
      });

      const req = httpMock.expectOne('/api/sus');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(dto);
      req.flush({ success: true, message: 'OK', data: mockEvaluation });

      expect(toastService.success).toHaveBeenCalledWith('Avaliação de usabilidade enviada com sucesso!');
    });

    it('deve exibir toast com mensagem de erro customizada e padrão', () => {
      const dto: SusEvaluationCreateDTO = {
        q1: 1, q2: 1, q3: 1, q4: 1, q5: 1, q6: 1, q7: 1, q8: 1, q9: 1, q10: 1
      };

      service.submitEvaluation(dto).subscribe({
        error: () => {}
      });

      const req1 = httpMock.expectOne('/api/sus');
      req1.flush({ message: 'Erro customizado de negócio' }, { status: 400, statusText: 'Bad Request' });
      expect(toastService.error).toHaveBeenCalledWith('Erro customizado de negócio');

      service.submitEvaluation(dto).subscribe({
        error: () => {}
      });

      const req2 = httpMock.expectOne('/api/sus');
      req2.flush(null, { status: 500, statusText: 'Server Error' });
      expect(toastService.error).toHaveBeenCalledWith('Erro ao enviar avaliação de usabilidade.');
  });

  describe('calculatePreview', () => {
    it('deve solicitar cálculo prévio de psicometria SUS via POST /api/sus/preview', () => {
      const dto: SusEvaluationCreateDTO = {
        academicClassId: 'cls-1',
        q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1
      };
      const mockPreview: SusEvaluationPreviewDTO = {
        score: 100.0,
        adjectiveRating: 'Melhor Imaginável',
        gradeScale: 'A',
        acceptability: 'Aceitável'
      };

      service.calculatePreview(dto).subscribe((res) => {
        expect(res.data).toEqual(mockPreview);
      });

      const req = httpMock.expectOne('/api/sus/preview');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(dto);
      req.flush({ success: true, message: 'OK', data: mockPreview });
    });
  });

  describe('getMyEvaluation', () => {
    it('deve consultar avaliação com classId', () => {
      service.getMyEvaluation('cls-1').subscribe((res) => {
        expect(res.data).toEqual(mockEvaluation);
      });

      const req = httpMock.expectOne('/api/sus/my-evaluation?classId=cls-1');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockEvaluation });
    });

    it('deve consultar avaliação geral sem classId', () => {
      service.getMyEvaluation().subscribe((res) => {
        expect(res.data).toBeNull();
      });

      const req = httpMock.expectOne('/api/sus/my-evaluation');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: null });
    });
  });

  describe('getMyEvaluations', () => {
    it('deve listar histórico de avaliações do estudante', () => {
      service.getMyEvaluations().subscribe((res) => {
        expect(res.data).toEqual([mockEvaluation]);
      });

      const req = httpMock.expectOne('/api/sus/my-evaluations');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: [mockEvaluation] });
    });
  });

  describe('getClassSummary', () => {
    it('deve obter sumário da turma', () => {
      const mockSummary: SusClassSummaryDTO = {
        classId: 'cls-1',
        className: 'Turma Teste',
        totalEvaluations: 1,
        enrolledStudentsCount: 1,
        responseRatePercentage: 100,
        averageScore: 100,
        adjectiveRating: 'Melhor Imaginável',
        acceptability: 'Aceitável',
        gradeLevel: 'A',
        adjectiveDistribution: { 'Melhor Imaginável': 1 },
        questionAverages: [5, 1, 5, 1, 5, 1, 5, 1, 5, 1],
        evaluations: [mockEvaluation]
      };

      service.getClassSummary('cls-1').subscribe((res) => {
        expect(res.data).toEqual(mockSummary);
      });

      const req = httpMock.expectOne('/api/sus/classes/cls-1/summary');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockSummary });
    });
  });

  describe('getGeneralSummary', () => {
    it('deve obter sumário global', () => {
      const mockGeneral: SusGeneralSummaryDTO = {
        totalEvaluations: 10,
        averageScore: 85,
        adjectiveRating: 'Melhor Imaginável',
        acceptability: 'Aceitável',
        gradeLevel: 'B',
        adjectiveDistribution: { 'Melhor Imaginável': 8 },
        questionAverages: [4.5, 1.2, 4.8, 1.1, 4.6, 1.3, 4.7, 1.2, 4.6, 1.1]
      };

      service.getGeneralSummary().subscribe((res) => {
        expect(res.data).toEqual(mockGeneral);
      });

      const req = httpMock.expectOne('/api/sus/summary');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockGeneral });
    });
  });

  describe('downloadClassSusCsv', () => {
    it('deve realizar download do CSV da turma com nome padrão e exibir toast', () => {
      const mockBlob = new Blob(['\uFEFFid,escore_sus\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassSusCsv('cls-1').subscribe((blob) => {
        expect(blob).toBe(mockBlob);
      });

      const req = httpMock.expectOne('/api/sus/classes/cls-1/csv');
      expect(req.request.method).toBe('GET');
      expect(req.request.responseType).toBe('blob');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, 'pesquisa-sus-turma-cls-1.csv');
      expect(toastService.success).toHaveBeenCalledWith('Dados da pesquisa SUS exportados com sucesso em CSV.');
    });

    it('deve realizar download do CSV da turma com nome customizado', () => {
      const mockBlob = new Blob(['\uFEFFid,escore_sus\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassSusCsv('cls-1', 'meu-arquivo.csv').subscribe();

      const req = httpMock.expectOne('/api/sus/classes/cls-1/csv');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, 'meu-arquivo.csv');
    });

    it('deve tratar erro no download do CSV da turma', () => {
      service.downloadClassSusCsv('cls-1').subscribe({
        error: () => {}
      });

      const req = httpMock.expectOne('/api/sus/classes/cls-1/csv');
      req.flush(new Blob(['Error']), { status: 500, statusText: 'Server Error' });

      expect(toastService.error).toHaveBeenCalledWith('Não foi possível exportar os dados do questionário SUS.');
    });
  });

  describe('downloadGlobalSusCsv', () => {
    it('deve realizar download do CSV global com nome padrão e exibir toast', () => {
      const mockBlob = new Blob(['\uFEFFid,escore_sus\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadGlobalSusCsv().subscribe((blob) => {
        expect(blob).toBe(mockBlob);
      });

      const req = httpMock.expectOne('/api/sus/csv');
      expect(req.request.method).toBe('GET');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, 'pesquisa-sus-global.csv');
      expect(toastService.success).toHaveBeenCalledWith('Dados globais da pesquisa SUS exportados com sucesso.');
    });

    it('deve realizar download do CSV global com nome customizado', () => {
      const mockBlob = new Blob(['\uFEFFid,escore_sus\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadGlobalSusCsv('global-custom.csv').subscribe();

      const req = httpMock.expectOne('/api/sus/csv');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, 'global-custom.csv');
    });

    it('deve tratar erro no download do CSV global', () => {
      service.downloadGlobalSusCsv().subscribe({
        error: () => {}
      });

      const req = httpMock.expectOne('/api/sus/csv');
      req.flush(new Blob(['Error']), { status: 500, statusText: 'Server Error' });

      expect(toastService.error).toHaveBeenCalledWith('Não foi possível exportar os dados globais do SUS.');
    });
  });

  describe('saveBlob', () => {
    it('deve manipular elementos do DOM para disparar download e revogar URL', () => {
      const blob = new Blob(['content'], { type: 'text/plain' });
      const filename = 'teste.csv';
      const mockUrl = 'blob:http://localhost/uuid-mock';

      const createObjectUrlSpy = jest.spyOn(window.URL, 'createObjectURL').mockReturnValue(mockUrl);
      const revokeObjectUrlSpy = jest.spyOn(window.URL, 'revokeObjectURL').mockImplementation();

      const mockAnchor = {
        href: '',
        download: '',
        click: jest.fn()
      } as unknown as HTMLAnchorElement;

      const createElementSpy = jest.spyOn(document, 'createElement').mockReturnValue(mockAnchor);
      const appendChildSpy = jest.spyOn(document.body, 'appendChild').mockImplementation();
      const removeChildSpy = jest.spyOn(document.body, 'removeChild').mockImplementation();

      service.saveBlob(blob, filename);

      expect(createObjectUrlSpy).toHaveBeenCalledWith(blob);
      expect(createElementSpy).toHaveBeenCalledWith('a');
      expect(mockAnchor.href).toBe(mockUrl);
      expect(mockAnchor.download).toBe(filename);
      expect(appendChildSpy).toHaveBeenCalledWith(mockAnchor);
      expect(mockAnchor.click).toHaveBeenCalled();
      expect(removeChildSpy).toHaveBeenCalledWith(mockAnchor);
      expect(revokeObjectUrlSpy).toHaveBeenCalledWith(mockUrl);

      createObjectUrlSpy.mockRestore();
      revokeObjectUrlSpy.mockRestore();
      createElementSpy.mockRestore();
      appendChildSpy.mockRestore();
      removeChildSpy.mockRestore();
    });
  });
});
