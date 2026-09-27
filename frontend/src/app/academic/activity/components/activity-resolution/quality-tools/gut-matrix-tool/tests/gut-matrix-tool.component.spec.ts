import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { GutMatrixToolComponent } from '../gut-matrix-tool.component';
import { GutItemData } from '../../../../../models/activity.model';

describe('GutMatrixToolComponent', () => {
  let component: GutMatrixToolComponent;
  let fixture: ComponentFixture<GutMatrixToolComponent>;

  const mockItems: GutItemData[] = [
    { problem: 'Falha de identificação', gravity: 5, urgency: 4, trend: 4 }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GutMatrixToolComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(GutMatrixToolComponent);
    component = fixture.componentInstance;
    component.gutItems = mockItems;
    fixture.detectChanges();
  });

  it('deve calcular o score GUT corretamente', () => {
    const score = component.calculateGutScore(mockItems[0]);
    expect(score).toBe(80);

    const fallbackScore = component.calculateGutScore({ problem: 'Sem valores' } as any);
    expect(fallbackScore).toBe(1);
  });

  it('deve emitir addItem ao clicar em Adicionar Problema', () => {
    const addSpy = jest.spyOn(component.addItem, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removeItem ao clicar em Excluir', () => {
    const removeSpy = jest.spyOn(component.removeItem, 'emit');
    const deleteBtn = fixture.nativeElement.querySelector('button.text-slate-400') as HTMLButtonElement;
    deleteBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve renderizar estado vazio quando não houver itens', () => {
    component.gutItems = [];
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum item na Matriz GUT.');
  });
});
