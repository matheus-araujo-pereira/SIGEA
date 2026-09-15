import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { GttModuleService } from './gtt-module.service';
import { GttModule, GttModuleCreateRequest, GttModuleUpdateRequest } from '../models/gtt-module.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';

describe('GttModuleService', () => {
  let service: GttModuleService;
  let httpMock: HttpTestingController;

  const mockModule: GttModule = {
    id: 'mod-1',
    code: 'C',
    name: 'Cuidados Gerais',
    description: 'Módulo assistencial geral',
    displayOrder: 1,
    isActive: true,
    createdAt: '2026-09-14T00:00:00Z',
    triggerCount: 10,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GttModuleService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(GttModuleService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve consultar catálogo educacional de módulos', () => {
    service.getCatalog().subscribe((res) => {
      expect(res.data.length).toBe(1);
      expect(res.data[0].code).toBe('C');
    });

    const req = httpMock.expectOne('/api/gtt/modules/catalog');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [mockModule] });
  });

  it('deve listar módulos com filtros e paginação', () => {
    const pageResp: ApiResponse<PageResponse<GttModule>> = {
      success: true,
      message: 'OK',
      data: {
        content: [mockModule],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.listModules('cuidados', true, 0, 10, 'displayOrder,asc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/modules' &&
      r.params.get('search') === 'cuidados' &&
      r.params.get('isActive') === 'true' &&
      r.params.get('page') === '0' &&
      r.params.get('size') === '10' &&
      r.params.get('sort') === 'displayOrder,asc'
    );
    expect(req.request.method).toBe('GET');
    req.flush(pageResp);
  });

  it('deve listar módulos com valores padrão', () => {
    service.listModules().subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === '/api/gtt/modules' &&
      !r.params.has('search') &&
      !r.params.has('isActive')
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [] } });
  });

  it('deve obter módulo por ID', () => {
    service.getModuleById('mod-1').subscribe((res) => {
      expect(res.data.id).toBe('mod-1');
    });

    const req = httpMock.expectOne('/api/gtt/modules/mod-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockModule });
  });

  it('deve cadastrar módulo', () => {
    const createReq: GttModuleCreateRequest = {
      code: 'M',
      name: 'Medicamentos',
      description: 'Módulo de eventos adversos a medicamentos',
      displayOrder: 2,
    };

    service.createModule(createReq).subscribe((res) => {
      expect(res.data.code).toBe('M');
    });

    const req = httpMock.expectOne('/api/gtt/modules');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createReq);
    req.flush({ success: true, data: { ...mockModule, code: 'M' } });
  });

  it('deve atualizar módulo', () => {
    const updateReq: GttModuleUpdateRequest = {
      code: 'C',
      name: 'Cuidados Gerais Atualizado',
      description: 'Nova descrição',
      displayOrder: 1,
    };

    service.updateModule('mod-1', updateReq).subscribe((res) => {
      expect(res.data.name).toBe('Cuidados Gerais Atualizado');
    });

    const req = httpMock.expectOne('/api/gtt/modules/mod-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: { ...mockModule, name: 'Cuidados Gerais Atualizado' } });
  });

  it('deve alterar status do módulo', () => {
    service.updateStatus('mod-1', { isActive: false }).subscribe((res) => {
      expect(res.data.isActive).toBe(false);
    });

    const req = httpMock.expectOne('/api/gtt/modules/mod-1/status');
    expect(req.request.method).toBe('PATCH');
    req.flush({ success: true, data: { ...mockModule, isActive: false } });
  });

  it('deve excluir módulo', () => {
    service.deleteModule('mod-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/gtt/modules/mod-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, data: null });
  });
});
