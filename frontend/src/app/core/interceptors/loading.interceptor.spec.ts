import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { loadingInterceptor } from './loading.interceptor';
import { LoadingService } from '../services/loading.service';

describe('loadingInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let loadingServiceMock: { show: jest.Mock; hide: jest.Mock };

  beforeEach(() => {
    loadingServiceMock = {
      show: jest.fn(),
      hide: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([loadingInterceptor])),
        provideHttpClientTesting(),
        { provide: LoadingService, useValue: loadingServiceMock },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve chamar show no início da requisição e hide ao finalizar', () => {
    http.get('/api/teste').subscribe();

    expect(loadingServiceMock.show).toHaveBeenCalledTimes(1);
    expect(loadingServiceMock.hide).not.toHaveBeenCalled();

    const req = httpMock.expectOne('/api/teste');
    req.flush({});

    expect(loadingServiceMock.hide).toHaveBeenCalledTimes(1);
  });

  it('deve chamar hide mesmo em caso de erro', () => {
    http.get('/api/erro').subscribe({
      error: () => {},
    });

    const req = httpMock.expectOne('/api/erro');
    req.error(new ProgressEvent('error'));

    expect(loadingServiceMock.hide).toHaveBeenCalledTimes(1);
  });
});
