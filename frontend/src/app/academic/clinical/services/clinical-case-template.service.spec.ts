import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ClinicalCaseTemplateService } from './clinical-case-template.service';
import {
  ClinicalCaseTemplateCreateDTO,
  ClinicalCaseTemplateResponseDTO,
  ClinicalCaseTemplateUpdateDTO,
} from '../models/clinical-case-template.model';

describe('ClinicalCaseTemplateService', () => {
  let service: ClinicalCaseTemplateService;
  let httpMock: HttpTestingController;

  const mockTemplate: ClinicalCaseTemplateResponseDTO = {
    id: 'tmpl-1',
    title: 'Caso Clínico 01: Vancomicina',
    description: 'Descrição do caso canônico',
    moduleCode: 'M',
    primaryTriggerCode: 'M5',
    expectedSeverity: 'F',
    clinicalCaseData: { patientName: 'Givaldo' },
    isSystemTemplate: true,
    createdByName: 'Sistema SIGEA (Canônico)',
    createdAt: '2026-03-10T10:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ClinicalCaseTemplateService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ClinicalCaseTemplateService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve listar modelos sem filtro de módulo', () => {
    service.listTemplates().subscribe((res) => {
      expect(res.data).toHaveLength(1);
      expect(res.data[0].title).toBe('Caso Clínico 01: Vancomicina');
    });

    const req = httpMock.expectOne('/api/academic/clinical-cases/templates');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.has('moduleCode')).toBe(false);
    req.flush({ success: true, data: [mockTemplate] });
  });

  it('deve listar modelos filtrando por módulo GTT', () => {
    service.listTemplates('M').subscribe((res) => {
      expect(res.data[0].moduleCode).toBe('M');
    });

    const req = httpMock.expectOne(
      (r) => r.url === '/api/academic/clinical-cases/templates' && r.params.get('moduleCode') === 'M'
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [mockTemplate] });
  });

  it('deve obter modelo por ID', () => {
    service.getTemplateById('tmpl-1').subscribe((res) => {
      expect(res.data.id).toBe('tmpl-1');
    });

    const req = httpMock.expectOne('/api/academic/clinical-cases/templates/tmpl-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockTemplate });
  });

  it('deve criar novo modelo de caso clínico', () => {
    const createDTO: ClinicalCaseTemplateCreateDTO = {
      title: 'Novo Caso',
      description: 'Desc',
      moduleCode: 'C',
      primaryTriggerCode: 'C7',
      expectedSeverity: 'F',
      clinicalCaseData: { patientName: 'Severino' },
    };

    service.createTemplate(createDTO).subscribe((res) => {
      expect(res.data.title).toBe('Caso Clínico 01: Vancomicina');
    });

    const req = httpMock.expectOne('/api/academic/clinical-cases/templates');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createDTO);
    req.flush({ success: true, data: mockTemplate });
  });

  it('deve atualizar modelo existente', () => {
    const updateDTO: ClinicalCaseTemplateUpdateDTO = {
      title: 'Caso Atualizado',
      description: 'Desc',
      moduleCode: 'M',
      primaryTriggerCode: 'M5',
      expectedSeverity: 'F',
      clinicalCaseData: { patientName: 'Givaldo' },
    };

    service.updateTemplate('tmpl-1', updateDTO).subscribe((res) => {
      expect(res.data.id).toBe('tmpl-1');
    });

    const req = httpMock.expectOne('/api/academic/clinical-cases/templates/tmpl-1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateDTO);
    req.flush({ success: true, data: mockTemplate });
  });

  it('deve excluir modelo de caso clínico', () => {
    service.deleteTemplate('tmpl-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/academic/clinical-cases/templates/tmpl-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, data: null });
  });
});
