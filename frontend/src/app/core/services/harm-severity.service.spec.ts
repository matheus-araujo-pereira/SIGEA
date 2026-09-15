import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { HarmSeverityService } from './harm-severity.service';
import {
  HarmSeverity,
  HarmSeverityCreateRequest,
  HarmSeverityUpdateRequest,
} from '../models/harm-severity.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';

describe('HarmSeverityService', () => {
  let service: HarmSeverityService;
  let httpMock: HttpTestingController;

  const mockSeverity: HarmSeverity = {
    id: 'sev-1',
    categoryLetter: 'E',
    name: 'Dano temporário com necessidade de intervenção',
    description: 'O erro causou dano temporário ao paciente e exigiu intervenção clínica',
    isHarm: true,
    isActive: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [HarmSeverityService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(HarmSeverityService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve consultar guia interativo de gravidades ordenadas', () => {
    service.getGuide().subscribe((res) => {
      expect(res.data.length).toBe(1);
      expect(res.data[0].categoryLetter).toBe('E');
    });

    const req = httpMock.expectOne('/api/gtt/severities/guide');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [mockSeverity] });
  });

  it('deve listar gravidades com filtros de dano, status e paginação', () => {
    const pageResp: ApiResponse<PageResponse<HarmSeverity>> = {
      success: true,
      message: 'OK',
      data: {
        content: [mockSeverity],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.listSeverities('temporário', true, true, 0, 10, 'categoryLetter,asc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/severities' &&
      r.params.get('search') === 'temporário' &&
      r.params.get('isHarm') === 'true' &&
      r.params.get('isActive') === 'true' &&
      r.params.get('page') === '0' &&
      r.params.get('size') === '10' &&
      r.params.get('sort') === 'categoryLetter,asc'
    );
    expect(req.request.method).toBe('GET');
    req.flush(pageResp);
  });

  it('deve listar gravidades com parâmetros padrão', () => {
    service.listSeverities().subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/severities' &&
      !r.params.has('search') &&
      !r.params.has('isHarm') &&
      !r.params.has('isActive')
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [] } });
  });

  it('deve obter gravidade por ID', () => {
    service.getSeverityById('sev-1').subscribe((res) => {
      expect(res.data.id).toBe('sev-1');
    });

    const req = httpMock.expectOne('/api/gtt/severities/sev-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockSeverity });
  });

  it('deve cadastrar gravidade', () => {
    const createReq: HarmSeverityCreateRequest = {
      categoryLetter: 'F',
      name: 'Dano temporário com prolongamento de internação',
      description: 'Causou dano temporário e hospitalização prolongada',
      isHarm: true,
    };

    service.createSeverity(createReq).subscribe((res) => {
      expect(res.data.categoryLetter).toBe('F');
    });

    const req = httpMock.expectOne('/api/gtt/severities');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createReq);
    req.flush({ success: true, data: { ...mockSeverity, categoryLetter: 'F' } });
  });

  it('deve atualizar gravidade', () => {
    const updateReq: HarmSeverityUpdateRequest = {
      categoryLetter: 'E',
      name: 'Dano temporário atualizado',
      description: 'Nova descrição detalhada',
      isHarm: true,
    };

    service.updateSeverity('sev-1', updateReq).subscribe((res) => {
      expect(res.data.name).toBe('Dano temporário atualizado');
    });

    const req = httpMock.expectOne('/api/gtt/severities/sev-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: { ...mockSeverity, name: 'Dano temporário atualizado' } });
  });

  it('deve alterar status de gravidade', () => {
    service.updateStatus('sev-1', { isActive: false }).subscribe((res) => {
      expect(res.data.isActive).toBe(false);
    });

    const req = httpMock.expectOne('/api/gtt/severities/sev-1/status');
    expect(req.request.method).toBe('PATCH');
    req.flush({ success: true, data: { ...mockSeverity, isActive: false } });
  });

  it('deve excluir gravidade', () => {
    service.deleteSeverity('sev-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/gtt/severities/sev-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, data: null });
  });

  it('deve listar gravidades de dano com listHarmSeverities', () => {
    service.listHarmSeverities().subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/severities' &&
      r.params.get('isHarm') === 'true' &&
      r.params.get('isActive') === 'true'
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [] } });
  });
});
