import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingSpinnerComponent } from './loading-spinner.component';
import { LoadingService } from '../../../core/services/loading.service';

describe('LoadingSpinnerComponent', () => {
  let component: LoadingSpinnerComponent;
  let fixture: ComponentFixture<LoadingSpinnerComponent>;
  let loadingService: LoadingService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingSpinnerComponent],
      providers: [LoadingService],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingSpinnerComponent);
    component = fixture.componentInstance;
    loadingService = TestBed.inject(LoadingService);
    fixture.detectChanges();
  });

  it('não deve exibir overlay quando isLoading for falso', () => {
    loadingService.isLoading.set(false);
    fixture.detectChanges();

    const overlay = fixture.nativeElement.querySelector('[role="status"]');
    expect(overlay).toBeNull();
  });

  it('deve exibir overlay com animação quando isLoading for verdadeiro', () => {
    loadingService.isLoading.set(true);
    fixture.detectChanges();

    const overlay = fixture.nativeElement.querySelector('[role="status"]');
    expect(overlay).not.toBeNull();
    expect(overlay.textContent).toContain('Processando requisição...');
  });
});
