import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastContainerComponent } from './toast-container.component';
import { ToastService } from '../../../core/services/toast.service';

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

  it('deve renderizar lista de notificações', () => {
    toastService.success('Título Sucesso', 'Mensagem 1');
    toastService.error('Título Erro', 'Mensagem 2');
    toastService.warning('Título Aviso', 'Mensagem 3');
    toastService.info('Título Info', 'Mensagem 4');
    fixture.detectChanges();

    const alerts = fixture.nativeElement.querySelectorAll('[role="alert"]');
    expect(alerts.length).toBe(4);
  });

  it('deve chamar remove ao clicar no botão fechar', () => {
    jest.spyOn(toastService, 'remove');
    toastService.success('Título', 'Mensagem');
    fixture.detectChanges();

    const closeBtn = fixture.nativeElement.querySelector('button[aria-label="Fechar notificação"]');
    expect(closeBtn).not.toBeNull();
    closeBtn.click();

    expect(toastService.remove).toHaveBeenCalledWith(expect.any(String));
  });

  it('deve retornar classes CSS corretas para cada tipo de toast', () => {
    expect(component.getToastClasses({ id: '1', type: 'success', title: 'T', message: 'M' })).toContain('emerald');
    expect(component.getToastClasses({ id: '2', type: 'error', title: 'T', message: 'M' })).toContain('rose');
    expect(component.getToastClasses({ id: '3', type: 'warning', title: 'T', message: 'M' })).toContain('amber');
    expect(component.getToastClasses({ id: '4', type: 'info', title: 'T', message: 'M' })).toContain('blue');
  });
});
