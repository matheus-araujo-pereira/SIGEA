import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

describe('AppComponent', () => {
  describe('sem HttpClient', () => {
    let fixture: ComponentFixture<AppComponent>;
    let component: AppComponent;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [AppComponent],
        providers: [provideRouter([])],
      }).compileComponents();

      fixture = TestBed.createComponent(AppComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('deve instanciar o componente raiz e nao quebrar se http for nulo', () => {
      expect(component).toBeTruthy();
      expect(component.title).toBe('SIGEA');
    });

    it('deve conter router-outlet, toast container e loading spinner no DOM', () => {
      const nativeElement = fixture.nativeElement as HTMLElement;
      expect(nativeElement.querySelector('router-outlet')).not.toBeNull();
      expect(nativeElement.querySelector('app-toast-container')).not.toBeNull();
      expect(nativeElement.querySelector('app-loading-spinner')).not.toBeNull();
    });
  });

  describe('com HttpClient', () => {
    let fixture: ComponentFixture<AppComponent>;
    let httpMock: HttpTestingController;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [AppComponent],
        providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
      }).compileComponents();

      httpMock = TestBed.inject(HttpTestingController);
      fixture = TestBed.createComponent(AppComponent);
    });

    afterEach(() => {
      httpMock.verify();
    });

    it('deve disparar warmupBackend com sucesso', () => {
      fixture.detectChanges();
      const req = httpMock.expectOne('/api/public/ping');
      expect(req.request.headers.get('X-Skip-Loading')).toBe('true');
      expect(req.request.headers.get('X-Silent-Error')).toBe('true');
      req.flush({ status: 'UP' });
    });

    it('deve tratar erro no warmupBackend silenciosamente', () => {
      fixture.detectChanges();
      const req = httpMock.expectOne('/api/public/ping');
      req.error(new ProgressEvent('Network error'));
    });
  });
});
