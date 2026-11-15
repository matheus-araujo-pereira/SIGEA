import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { IshikawaToolComponent } from '../ishikawa-tool.component';
import { IshikawaData } from '../../../../../models/activity.model';

describe('IshikawaToolComponent', () => {
  let component: IshikawaToolComponent;
  let fixture: ComponentFixture<IshikawaToolComponent>;

  const mockIshikawa: IshikawaData = {
    centralProblem: 'Efeito Central',
    methodCauses: ['Falha de protocolo'],
    manpowerCauses: ['Sobrecarga'],
    materialCauses: ['Medicamento errado'],
    machineCauses: ['Bomba falhou'],
    environmentCauses: ['Iluminação fraca'],
    measurementCauses: ['Sem checagem']
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IshikawaToolComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(IshikawaToolComponent);
    component = fixture.componentInstance;
    component.ishikawa = mockIshikawa;
    fixture.detectChanges();
  });

  it('deve renderizar o problema central e as 6 categorias', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('1. Método');
    expect(compiled.textContent).toContain('2. Mão de Obra');
    expect(compiled.textContent).toContain('3. Material');
    expect(compiled.textContent).toContain('4. Máquina');
    expect(compiled.textContent).toContain('5. Meio Ambiente');
    expect(compiled.textContent).toContain('6. Medida');
  });

  it('deve emitir addCause ao clicar no botão de adicionar causa', () => {
    const addSpy = jest.spyOn(component.addCause, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalledWith('method');
  });

  it('deve emitir removeCause ao clicar no botão de excluir causa', () => {
    const removeSpy = jest.spyOn(component.removeCause, 'emit');
    const removeBtn = fixture.nativeElement.querySelectorAll('button')[1] as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith({ type: 'method', index: 0 });
  });

  it('deve ajustar altura do textarea em onAutoResize', () => {
    const textarea = document.createElement('textarea');
    Object.defineProperty(textarea, 'scrollHeight', { value: 160, configurable: true });
    component.onAutoResize({ target: textarea } as unknown as Event);
    expect(textarea.style.height).toBe('160px');
  });
});
