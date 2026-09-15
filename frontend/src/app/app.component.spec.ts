import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';

describe('AppComponent', () => {
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

  it('deve instanciar o componente raiz da aplicação', () => {
    expect(component).toBeTruthy();
    expect(component.title).toBe('SIGEA-GTT');
  });

  it('deve conter router-outlet, toast container e loading spinner no DOM', () => {
    const nativeElement = fixture.nativeElement as HTMLElement;
    expect(nativeElement.querySelector('router-outlet')).not.toBeNull();
    expect(nativeElement.querySelector('app-toast-container')).not.toBeNull();
    expect(nativeElement.querySelector('app-loading-spinner')).not.toBeNull();
  });
});
