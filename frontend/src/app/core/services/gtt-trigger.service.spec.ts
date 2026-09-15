import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { GttTriggerService } from './gtt-trigger.service';
import { GttTrigger, GttTriggerCreateRequest, GttTriggerUpdateRequest } from '../models/gtt-trigger.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';

describe('GttTriggerService', () => {
  let service: GttTriggerService;
  let httpMock: HttpTestingController;

  const mockTrigger: GttTrigger = {
    id: 'trig-1',
    moduleId: 'mod-1',
    moduleCode: 'C',
    moduleName: 'Cuidados Gerais',
    code: 'C1',
    name: 'Transfusão de sangue',
    description: 'Investigar se houve hemorragia aguda ou reação pós-operatória',
    isActive: true,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GttTriggerService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(GttTriggerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve consultar catálogo de gatilhos sem moduleId', () => {
    service.getCatalog().subscribe((res) => {
      expect(res.data.length).toBe(1);
      expect(res.data[0].code).toBe('C1');
    });

    const req = httpMock.expectOne('/api/gtt/triggers/catalog');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [mockTrigger] });
  });

  it('deve consultar catálogo de gatilhos com moduleId informado', () => {
    service.getCatalog('mod-1').subscribe((res) => {
      expect(res.data.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/gtt/triggers/catalog?moduleId=mod-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [mockTrigger] });
  });

  it('deve listar gatilhos com todos os filtros e paginação', () => {
    const pageResp: ApiResponse<PageResponse<GttTrigger>> = {
      success: true,
      message: 'OK',
      data: {
        content: [mockTrigger],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.listTriggers('mod-1', 'sangue', true, 0, 10, 'code,asc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/triggers' &&
      r.params.get('search') === 'sangue' &&
      r.params.get('moduleId') === 'mod-1' &&
      r.params.get('isActive') === 'true' &&
      r.params.get('page') === '0' &&
      r.params.get('size') === '10' &&
      r.params.get('sort') === 'code,asc'
    );
    expect(req.request.method).toBe('GET');
    req.flush(pageResp);
  });

  it('deve listar gatilhos com parâmetros padrão', () => {
    service.listTriggers().subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/triggers' &&
      !r.params.has('search') &&
      !r.params.has('moduleId') &&
      !r.params.has('isActive')
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [] } });
  });

  it('deve obter gatilho por ID', () => {
    service.getTriggerById('trig-1').subscribe((res) => {
      expect(res.data.id).toBe('trig-1');
    });

    const req = httpMock.expectOne('/api/gtt/triggers/trig-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockTrigger });
  });

  it('deve cadastrar gatilho', () => {
    const createReq: GttTriggerCreateRequest = {
      moduleId: 'mod-1',
      code: 'C2',
      name: 'Queda do paciente',
      description: 'Investigar se houve trauma ou lesão decorrente de queda',
    };

    service.createTrigger(createReq).subscribe((res) => {
      expect(res.data.code).toBe('C2');
    });

    const req = httpMock.expectOne('/api/gtt/triggers');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createReq);
    req.flush({ success: true, data: { ...mockTrigger, code: 'C2' } });
  });

  it('deve atualizar gatilho', () => {
    const updateReq: GttTriggerUpdateRequest = {
      moduleId: 'mod-1',
      code: 'C1',
      name: 'Transfusão Sanguínea Atualizada',
      description: 'Diretrizes atualizadas',
    };

    service.updateTrigger('trig-1', updateReq).subscribe((res) => {
      expect(res.data.name).toBe('Transfusão Sanguínea Atualizada');
    });

    const req = httpMock.expectOne('/api/gtt/triggers/trig-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: { ...mockTrigger, name: 'Transfusão Sanguínea Atualizada' } });
  });

  it('deve alterar status do gatilho', () => {
    service.updateStatus('trig-1', { isActive: false }).subscribe((res) => {
      expect(res.data.isActive).toBe(false);
    });

    const req = httpMock.expectOne('/api/gtt/triggers/trig-1/status');
    expect(req.request.method).toBe('PATCH');
    req.flush({ success: true, data: { ...mockTrigger, isActive: false } });
  });

  it('deve excluir gatilho', () => {
    service.deleteTrigger('trig-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/gtt/triggers/trig-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, data: null });
  });
});
