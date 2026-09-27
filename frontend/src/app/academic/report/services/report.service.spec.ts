import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ReportService } from './report.service';
import { ToastService } from '../../../common/services/toast.service';

describe('ReportService', () => {
  let service: ReportService;
  let httpMock: HttpTestingController;
  let toastService: jest.Mocked<ToastService>;

  beforeEach(() => {
    toastService = {
      success: jest.fn(),
      error: jest.fn(),
      warning: jest.fn(),
      info: jest.fn(),
      remove: jest.fn(),
      toasts: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    window.URL.createObjectURL = jest.fn();
    window.URL.revokeObjectURL = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        ReportService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ToastService, useValue: toastService },
      ],
    });

    service = TestBed.inject(ReportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('downloadSubmissionPdf', () => {
    it('deve realizar download do PDF da submissão com nome padrão e exibir toast de sucesso', () => {
      const submissionId = 'sub-123';
      const mockBlob = new Blob(['%PDF-1.4'], { type: 'application/pdf' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadSubmissionPdf(submissionId).subscribe((res) => {
        expect(res).toBe(mockBlob);
      });

      const req = httpMock.expectOne(`/api/reports/submissions/${submissionId}/pdf`);
      expect(req.request.method).toBe('GET');
      expect(req.request.responseType).toBe('blob');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, `relatorio-auditoria-${submissionId}.pdf`);
      expect(toastService.success).toHaveBeenCalledWith('Relatório de auditoria clínica exportado com sucesso.');
    });

    it('deve realizar download do PDF da submissão com nome customizado', () => {
      const submissionId = 'sub-123';
      const customFilename = 'meu-relatorio.pdf';
      const mockBlob = new Blob(['%PDF-1.4'], { type: 'application/pdf' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadSubmissionPdf(submissionId, customFilename).subscribe();

      const req = httpMock.expectOne(`/api/reports/submissions/${submissionId}/pdf`);
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, customFilename);
    });

    it('deve exibir toast de erro e propagar falha na requisição de PDF de submissão', () => {
      const submissionId = 'sub-123';
      let errorOccurred = false;

      service.downloadSubmissionPdf(submissionId).subscribe({
        error: () => {
          errorOccurred = true;
        },
      });

      const req = httpMock.expectOne(`/api/reports/submissions/${submissionId}/pdf`);
      req.flush(new Blob(['Error'], { type: 'application/json' }), { status: 500, statusText: 'Server Error' });

      expect(errorOccurred).toBe(true);
      expect(toastService.error).toHaveBeenCalledWith('Não foi possível exportar o relatório de auditoria.');
    });
  });

  describe('downloadClassBulletinPdf', () => {
    it('deve realizar download do Boletim em PDF com nome padrão e exibir toast de sucesso', () => {
      const classId = 'class-456';
      const mockBlob = new Blob(['%PDF-1.4'], { type: 'application/pdf' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassBulletinPdf(classId).subscribe((res) => {
        expect(res).toBe(mockBlob);
      });

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/bulletin/pdf`);
      expect(req.request.method).toBe('GET');
      expect(req.request.responseType).toBe('blob');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, `boletim-epidemiologico-${classId}.pdf`);
      expect(toastService.success).toHaveBeenCalledWith('Boletim epidemiológico da turma exportado com sucesso.');
    });

    it('deve realizar download do Boletim em PDF com nome customizado', () => {
      const classId = 'class-456';
      const customFilename = 'boletim-custom.pdf';
      const mockBlob = new Blob(['%PDF-1.4'], { type: 'application/pdf' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassBulletinPdf(classId, customFilename).subscribe();

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/bulletin/pdf`);
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, customFilename);
    });

    it('deve exibir toast de erro e propagar falha na requisição do Boletim em PDF', () => {
      const classId = 'class-456';
      let errorOccurred = false;

      service.downloadClassBulletinPdf(classId).subscribe({
        error: () => {
          errorOccurred = true;
        },
      });

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/bulletin/pdf`);
      req.flush(new Blob(['Error'], { type: 'application/json' }), { status: 403, statusText: 'Forbidden' });

      expect(errorOccurred).toBe(true);
      expect(toastService.error).toHaveBeenCalledWith('Não foi possível exportar o boletim epidemiológico.');
    });
  });

  describe('downloadClassResearchCsv', () => {
    it('deve realizar download dos dados brutos em CSV com nome padrão e exibir toast de sucesso', () => {
      const classId = 'class-789';
      const mockBlob = new Blob(['\uFEFFsubmission_id,turma\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassResearchCsv(classId).subscribe((res) => {
        expect(res).toBe(mockBlob);
      });

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/research/csv`);
      expect(req.request.method).toBe('GET');
      expect(req.request.responseType).toBe('blob');
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, `dados-pesquisa-turma-${classId}.csv`);
      expect(toastService.success).toHaveBeenCalledWith('Base de dados de pesquisa (CSV) exportada com sucesso.');
    });

    it('deve realizar download dos dados brutos em CSV com nome customizado', () => {
      const classId = 'class-789';
      const customFilename = 'pesquisa-gtt.csv';
      const mockBlob = new Blob(['\uFEFFsubmission_id,turma\n'], { type: 'text/csv' });
      const saveBlobSpy = jest.spyOn(service, 'saveBlob').mockImplementation();

      service.downloadClassResearchCsv(classId, customFilename).subscribe();

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/research/csv`);
      req.flush(mockBlob);

      expect(saveBlobSpy).toHaveBeenCalledWith(mockBlob, customFilename);
    });

    it('deve exibir toast de erro e propagar falha na requisição dos dados brutos em CSV', () => {
      const classId = 'class-789';
      let errorOccurred = false;

      service.downloadClassResearchCsv(classId).subscribe({
        error: () => {
          errorOccurred = true;
        },
      });

      const req = httpMock.expectOne(`/api/reports/classes/${classId}/research/csv`);
      req.flush(new Blob(['Error'], { type: 'application/json' }), { status: 404, statusText: 'Not Found' });

      expect(errorOccurred).toBe(true);
      expect(toastService.error).toHaveBeenCalledWith('Não foi possível exportar a base de dados em CSV.');
    });
  });

  describe('saveBlob', () => {
    it('deve criar link temporário no DOM, disparar clique e revogar URL', () => {
      const blob = new Blob(['test content'], { type: 'text/plain' });
      const filename = 'teste.txt';
      const mockUrl = 'blob:http://localhost/mock-uuid';

      const createObjectUrlSpy = jest.spyOn(window.URL, 'createObjectURL').mockReturnValue(mockUrl);
      const revokeObjectUrlSpy = jest.spyOn(window.URL, 'revokeObjectURL').mockImplementation();

      const mockAnchor = {
        href: '',
        download: '',
        click: jest.fn(),
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
