import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastContainerComponent } from '../toast-container.component';
import { ToastService } from '../../../services/toast.service';

describe('ToastContainerComponent', () => {
  let component: ToastContainerComponent;
  let fixture: ComponentFixture<ToastContainerComponent>;
  let toastService: ToastService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastContainerComponent],
      providers: [ToastService],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastContainerComponent);
    component = fixture.componentInstance;
    toastService = TestBed.inject(ToastService);
    fixture.detectChanges();
  });

  it('deve renderizar container vazio quando não houver toasts', () => {
    const alerts = fixture.nativeElement.querySelectorAll('[role="alert"]');
    expect(alerts.length).toBe(0);
  });

  it('deve renderizar cards com os estilos corretos para cada tipo', () => {
    toastService.success('Sucesso 1', 'Texto');
    toastService.error('Erro 1', 'Texto');
    toastService.warning('Aviso 1', 'Texto');
    toastService.info('Info 1', 'Texto');
    fixture.detectChanges();

    const alerts = fixture.nativeElement.querySelectorAll('[role="alert"]');
    expect(alerts.length).toBe(4);

    expect(component.getToastClasses({ id: '1', type: 'success', title: 'S', message: 'M' })).toContain('emerald');
    expect(component.getToastClasses({ id: '2', type: 'error', title: 'E', message: 'M' })).toContain('rose');
    expect(component.getToastClasses({ id: '3', type: 'warning', title: 'W', message: 'M' })).toContain('amber');
    expect(component.getToastClasses({ id: '4', type: 'info', title: 'I', message: 'M' })).toContain('blue');
  });

  it('deve remover toast ao clicar no botão fechar', () => {
    toastService.info('Toast para fechar', 'Msg');
    fixture.detectChanges();

    const closeBtn = fixture.nativeElement.querySelector('button[aria-label="Fechar notificação"]');
    expect(closeBtn).not.toBeNull();

    closeBtn.click();
    fixture.detectChanges();

    expect(toastService.toasts().length).toBe(0);
  });
});
