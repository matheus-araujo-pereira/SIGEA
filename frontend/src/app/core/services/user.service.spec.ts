import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User, UserCreateRequest, UserUpdateRequest } from '../models/user.model';
import { PageResponse } from '../models/page.model';
import { ApiResponse } from '../models/api-response.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 'user-uuid-1',
    fullName: 'Aluno 1',
    email: 'aluno1@academico.ufs.br',
    role: 'STUDENT',
    registrationNumber: '20260001',
    isActive: true,
    mustChangePassword: true,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve listar usuários com todos os parâmetros e paginação', () => {
    const pageResp: ApiResponse<PageResponse<User>> = {
      success: true,
      message: 'OK',
      data: {
        content: [mockUser],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
      },
      timestamp: '2026-09-14T00:00:00Z',
    };

    service.listUsers('aluno', 'STUDENT', true, 0, 10, 'fullName,asc').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });

    const req = httpMock.expectOne((r) =>
      r.url === '/api/users' &&
      r.params.get('search') === 'aluno' &&
      r.params.get('role') === 'STUDENT' &&
      r.params.get('isActive') === 'true' &&
      r.params.get('page') === '0' &&
      r.params.get('size') === '10'
    );
    expect(req.request.method).toBe('GET');
    req.flush(pageResp);
  });

  it('deve listar usuários com parâmetros padrão e vazios', () => {
    service.listUsers().subscribe();

    const req = httpMock.expectOne((r) =>
      r.url === '/api/users' &&
      !r.params.has('search') &&
      !r.params.has('role') &&
      !r.params.has('isActive')
    );
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: { content: [] } });
  });

  it('deve criar usuário', () => {
    const createReq: UserCreateRequest = {
      fullName: 'Novo Aluno',
      email: 'novo@academico.ufs.br',
      role: 'STUDENT',
      registrationNumber: '123',
    };

    service.createUser(createReq).subscribe((res) => {
      expect(res.data.fullName).toBe('Novo Aluno');
    });

    const req = httpMock.expectOne('/api/users');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(createReq);
    req.flush({ success: true, data: { ...mockUser, fullName: 'Novo Aluno' } });
  });

  it('deve buscar usuário por ID', () => {
    service.getUserById('user-uuid-1').subscribe((res) => {
      expect(res.data.id).toBe('user-uuid-1');
    });

    const req = httpMock.expectOne('/api/users/user-uuid-1');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockUser });
  });

  it('deve atualizar usuário existente', () => {
    const updateReq: UserUpdateRequest = {
      fullName: 'Nome Atualizado',
      email: 'atual@academico.ufs.br',
      role: 'PROFESSOR',
    };

    service.updateUser('user-uuid-1', updateReq).subscribe((res) => {
      expect(res.data.fullName).toBe('Nome Atualizado');
    });

    const req = httpMock.expectOne('/api/users/user-uuid-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, data: { ...mockUser, fullName: 'Nome Atualizado' } });
  });

  it('deve atualizar status do usuário', () => {
    service.updateStatus('user-uuid-1', { isActive: false }).subscribe((res) => {
      expect(res.data.isActive).toBe(false);
    });

    const req = httpMock.expectOne('/api/users/user-uuid-1/status');
    expect(req.request.method).toBe('PATCH');
    req.flush({ success: true, data: { ...mockUser, isActive: false } });
  });

  it('deve excluir usuário', () => {
    service.deleteUser('user-uuid-1').subscribe((res) => {
      expect(res.success).toBe(true);
    });

    const req = httpMock.expectOne('/api/users/user-uuid-1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, data: null });
  });

  it('deve redefinir a senha do usuário', () => {
    service.resetPassword('user-uuid-1').subscribe((res) => {
      expect(res.data.provisionalPassword).toBe('Sigea@123456');
    });

    const req = httpMock.expectOne('/api/users/user-uuid-1/reset-password');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush({ success: true, data: { ...mockUser, provisionalPassword: 'Sigea@123456' } });
  });
});
