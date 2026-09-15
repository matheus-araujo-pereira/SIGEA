import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AcademicClassService } from './academic-class.service';
import {
  AcademicClassCreateDTO,
  AcademicClassDetailDTO,
  AcademicClassResponseDTO,
  AcademicClassUpdateDTO,
} from '../models/academic-class.model';
import { ApiResponse } from '../models/api-response.model';
import { PageResponse } from '../models/page.model';

describe('AcademicClassService', () => {
  let service: AcademicClassService;
  let httpMock: HttpTestingController;

  const mockClass: AcademicClassResponseDTO = {
    id: 'class-1',
    subjectName: 'Enfermagem',
    classCode: 'T01',
    academicPeriod: '2026.2',
    formattedName: 'Enfermagem - T01 - 2026.2',
    professorId: 'prof-1',
    professorName: 'Prof. Waleska',
    professorEmail: 'waleska@academico.ufs.br',
    isClosed: false,
    studentCount: 10,
    activityCount: 2,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockDetail: AcademicClassDetailDTO = {
    ...mockClass,
    students: [],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AcademicClassService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AcademicClassService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve listar turmas com todos os parâmetros', () => {
    service.listClasses('enf', '2026.2', true, 1, 20, 'classCode,desc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) => r.url === '/api/academic/classes');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('search')).toBe('enf');
    expect(req.request.params.get('academicPeriod')).toBe('2026.2');
    expect(req.request.params.get('isClosed')).toBe('true');
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('20');
    expect(req.request.params.get('sort')).toBe('classCode,desc');

    req.flush({
      success: true,
      data: { content: [mockClass], page: 1, size: 20, totalElements: 1, totalPages: 1, first: false, last: true },
    });
  });

  it('deve listar turmas com parâmetros opcionais vazios ou nulos', () => {
    service.listClasses(undefined, '', null).subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) => r.url === '/api/academic/classes');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.has('search')).toBeFalsy();
    expect(req.request.params.has('academicPeriod')).toBeFalsy();
    expect(req.request.params.has('isClosed')).toBeFalsy();

    req.flush({
      success: true,
      data: { content: [mockClass], page: 0, size: 10, totalElements: 1, totalPages: 1, first: true, last: true },
    });
  });

  it('deve consultar minhas turmas', () => {
    service.getMyClasses(0, 10).subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/academic/classes/my-classes?page=0&size=10&sort=subjectName,asc');
    expect(req.request.method).toBe('GET');
    req.flush({
      success: true,
      data: { content: [mockClass], page: 0, size: 10, totalElements: 1, totalPages: 1, first: true, last: true },
    });
  });

  it('deve consultar minhas turmas com parâmetros padrão', () => {
    service.getMyClasses().subscribe();
    const req = httpMock.expectOne('/api/academic/classes/my-classes?page=0&size=10&sort=subjectName,asc');
    req.flush({ success: true, data: { content: [] } });
  });

  it('deve obter turma por id', () => {
    service.getClassById('class-1').subscribe((res) => {
      expect(res.data.id).toBe('class-1');
    });

    const req = httpMock.expectOne('/api/academic/classes/class-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockDetail });
  });

  it('deve criar turma', () => {
    const dto: AcademicClassCreateDTO = {
      subjectName: 'Física',
      classCode: 'T06',
      academicPeriod: '2026.2',
      professorId: 'prof-1',
    };

    service.createClass(dto).subscribe((res) => {
      expect(res.data.id).toBe('class-1');
    });

    const req = httpMock.expectOne('/api/academic/classes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush({ success: true, data: mockDetail });
  });

  it('deve atualizar turma', () => {
    const dto: AcademicClassUpdateDTO = {
      subjectName: 'Física Moderna',
      classCode: 'T06',
      academicPeriod: '2026.2',
      professorId: 'prof-1',
    };

    service.updateClass('class-1', dto).subscribe((res) => {
      expect(res.data.id).toBe('class-1');
    });

    const req = httpMock.expectOne('/api/academic/classes/class-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: mockDetail });
  });

  it('deve atualizar status de encerramento da turma', () => {
    service.updateStatus('class-1', true).subscribe((res) => {
      expect(res.data.isClosed).toBe(true);
    });

    const req = httpMock.expectOne('/api/academic/classes/class-1/status');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ isClosed: true });
    req.flush({ success: true, data: { ...mockClass, isClosed: true } });
  });

  it('deve excluir turma', () => {
    service.deleteClass('class-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/academic/classes/class-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });
});
