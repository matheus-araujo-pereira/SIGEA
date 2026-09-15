import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authServiceMock: { getToken: jest.Mock };

  beforeEach(() => {
    authServiceMock = {
      getToken: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceMock },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve anexar cabeçalho Authorization quando houver token e a rota for /api', () => {
    authServiceMock.getToken.mockReturnValue('meu-jwt-token');

    http.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBe(true);
    expect(req.request.headers.get('Authorization')).toBe('Bearer meu-jwt-token');
    req.flush({});
  });

  it('não deve anexar cabeçalho Authorization se não houver token', () => {
    authServiceMock.getToken.mockReturnValue(null);

    http.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('não deve anexar cabeçalho Authorization se a URL não iniciar com /api', () => {
    authServiceMock.getToken.mockReturnValue('meu-jwt-token');

    http.get('https://externo.com/api').subscribe();

    const req = httpMock.expectOne('https://externo.com/api');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });
});
