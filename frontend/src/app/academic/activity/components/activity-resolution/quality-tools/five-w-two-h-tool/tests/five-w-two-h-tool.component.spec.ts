import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { FiveWTwoHToolComponent } from '../five-w-two-h-tool.component';
import { FiveWTwoHItemData } from '../../../../../models/activity.model';

describe('FiveWTwoHToolComponent', () => {
  let component: FiveWTwoHToolComponent;
  let fixture: ComponentFixture<FiveWTwoHToolComponent>;

  const mockItems: FiveWTwoHItemData[] = [
    { what: 'Dupla checagem', why: 'Evitar erro de medicação', where: 'Enfermaria', when: 'Imediato', who: 'Enfermeiro', how: 'Protocolo de segurança', howMuch: 'R$ 0' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FiveWTwoHToolComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(FiveWTwoHToolComponent);
    component = fixture.componentInstance;
    component.fiveWTwoHItems = mockItems;
    fixture.detectChanges();
  });

  it('deve renderizar os campos da ação 5W2H', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('O quê (What)?');
    expect(compiled.textContent).toContain('Por quê (Why)?');
  });

  it('deve emitir addItem ao clicar em Adicionar Ação', () => {
    const addSpy = jest.spyOn(component.addItem, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removeItem ao clicar em Excluir Ação', () => {
    const removeSpy = jest.spyOn(component.removeItem, 'emit');
    const deleteBtn = fixture.nativeElement.querySelector('button.text-slate-400') as HTMLButtonElement;
    deleteBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem quando a lista de ações estiver vazia', () => {
    component.fiveWTwoHItems = [];
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhuma ação cadastrada no plano 5W2H.');
  });
});
