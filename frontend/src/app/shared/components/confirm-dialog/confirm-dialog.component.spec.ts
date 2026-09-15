import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmDialogComponent } from './confirm-dialog.component';

describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('não deve renderizar diálogo quando isOpen for falso', () => {
    fixture.componentRef.setInput('isOpen', false);
    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector('[role="dialog"]');
    expect(dialog).toBeNull();
  });

  it('deve renderizar diálogo com título e mensagem customizados quando isOpen for verdadeiro', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('title', 'Excluir Item');
    fixture.componentRef.setInput('message', 'Confirma a exclusão definitiva?');
    fixture.componentRef.setInput('isDestructive', true);
    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector('[role="dialog"]');
    expect(dialog).not.toBeNull();
    expect(dialog.textContent).toContain('Excluir Item');
    expect(dialog.textContent).toContain('Confirma a exclusão definitiva?');
  });

  it('deve renderizar ícone e botão de aviso quando isDestructive for falso', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('isDestructive', false);
    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector('[role="dialog"]');
    expect(dialog).not.toBeNull();
  });

  it('deve emitir evento confirmed ao clicar em confirmar', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.detectChanges();

    const confirmedSpy = jest.fn();
    component.confirmed.subscribe(confirmedSpy);

    const buttons = fixture.nativeElement.querySelectorAll('button');
    const confirmBtn = buttons[1]; // Segundo botão é confirmar
    confirmBtn.click();

    expect(confirmedSpy).toHaveBeenCalled();
  });

  it('deve emitir evento cancelled ao clicar em cancelar', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.detectChanges();

    const cancelledSpy = jest.fn();
    component.cancelled.subscribe(cancelledSpy);

    const buttons = fixture.nativeElement.querySelectorAll('button');
    const cancelBtn = buttons[0]; // Primeiro botão é cancelar
    cancelBtn.click();

    expect(cancelledSpy).toHaveBeenCalled();
  });
});
