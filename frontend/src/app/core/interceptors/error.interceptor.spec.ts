import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { errorInterceptor } from './error.interceptor';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let toastServiceMock: { error: jest.Mock; warning: jest.Mock };
  let authServiceMock: { logout: jest.Mock };

  beforeEach(() => {
    toastServiceMock = {
      error: jest.fn(),
      warning: jest.fn(),
    };
    authServiceMock = {
      logout: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: ToastService, useValue: toastServiceMock },
        { provide: AuthService, useValue: authServiceMock },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve tratar 401 fora da rota de login chamando logout', () => {
    http.get('/api/users').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users');
    req.flush({ message: 'Token expirado' }, { status: 401, statusText: 'Unauthorized' });

    expect(toastServiceMock.error).toHaveBeenCalledWith('Sessão Expirada', expect.any(String));
    expect(authServiceMock.logout).toHaveBeenCalled();
  });

  it('deve tratar 401 na rota de login exibindo mensagem de erro sem logout', () => {
    http.post('/api/auth/login', {}).subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/auth/login');
    req.flush({ message: 'Senha incorreta' }, { status: 401, statusText: 'Unauthorized' });

    expect(toastServiceMock.error).toHaveBeenCalledWith('Falha no Login', 'Senha incorreta');
    expect(authServiceMock.logout).not.toHaveBeenCalled();
  });

  it('deve tratar 403 de acesso negado', () => {
    http.get('/api/admin').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/admin');
    req.flush({ message: 'Sem permissão' }, { status: 403, statusText: 'Forbidden' });

    expect(toastServiceMock.error).toHaveBeenCalledWith('Acesso Não Autorizado', 'Sem permissão');
  });

  it('deve tratar 404 de recurso não encontrado', () => {
    http.get('/api/users/999').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users/999');
    req.flush({ message: 'Usuário não encontrado' }, { status: 404, statusText: 'Not Found' });

    expect(toastServiceMock.warning).toHaveBeenCalledWith('Não Encontrado', 'Usuário não encontrado');
  });

  it('deve tratar 403 com mensagem padrão quando corpo não possuir mensagem', () => {
    http.get('/api/admin').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/admin');
    req.flush({}, { status: 403, statusText: 'Forbidden' });

    expect(toastServiceMock.error).toHaveBeenCalledWith(
      'Acesso Não Autorizado',
      'Você não tem permissão para esta ação.'
    );
  });

  it('deve tratar 404 com mensagem padrão quando corpo não possuir mensagem', () => {
    http.get('/api/users/999').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users/999');
    req.flush({}, { status: 404, statusText: 'Not Found' });

    expect(toastServiceMock.warning).toHaveBeenCalledWith(
      'Não Encontrado',
      'O recurso solicitado não foi localizado.'
    );
  });

  it('deve tratar 400 com lista de fieldErrors', () => {
    http.post('/api/users', {}).subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users');
    req.flush(
      {
        fieldErrors: [
          { field: 'email', message: 'obrigatório' },
          { field: 'nome', message: 'curto' },
        ],
      },
      { status: 400, statusText: 'Bad Request' }
    );

    expect(toastServiceMock.error).toHaveBeenCalledWith(
      'Erro de Validação',
      'email: obrigatório | nome: curto'
    );
  });

  it('deve tratar 500 de erro de servidor', () => {
    http.get('/api/crash').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/crash');
    req.flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(toastServiceMock.error).toHaveBeenCalledWith('Erro do Servidor', expect.any(String));
  });

  it('deve tratar status 0 de falha de rede', () => {
    http.get('/api/network').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/network');
    req.error(new ProgressEvent('error'));

    expect(toastServiceMock.error).toHaveBeenCalledWith('Sem Conexão', expect.any(String));
  });

  it('deve tratar outros status HTTP', () => {
    http.get('/api/teapot').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/teapot');
    req.flush({ message: 'Eu sou um bule' }, { status: 418, statusText: "I'm a teapot" });

    expect(toastServiceMock.error).toHaveBeenCalledWith('Erro (418)', 'Eu sou um bule');
  });

  it('deve silenciar toast se requisicao tiver cabecalho X-Silent-Error', () => {
    http.get('/api/silencioso', { headers: { 'X-Silent-Error': 'true' } }).subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/silencioso');
    req.error(new ProgressEvent('error'));

    expect(toastServiceMock.error).not.toHaveBeenCalled();
  });

  it('deve silenciar toast se a rota contiver /ping', () => {
    http.get('/api/public/ping').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/public/ping');
    req.error(new ProgressEvent('error'));

    expect(toastServiceMock.error).not.toHaveBeenCalled();
  });
});
