import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { SwotToolComponent } from '../swot-tool.component';
import { SwotData } from '../../../../../models/activity.model';

describe('SwotToolComponent', () => {
  let component: SwotToolComponent;
  let fixture: ComponentFixture<SwotToolComponent>;

  const mockSwot: SwotData = {
    strengths: ['Equipe engajada'],
    weaknesses: ['Leitos antigos'],
    opportunities: ['Capacitação'],
    threats: ['Cortes']
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SwotToolComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(SwotToolComponent);
    component = fixture.componentInstance;
    component.swot = mockSwot;
    component.brainstormingNotes = ['Ideia 1'];
    fixture.detectChanges();
  });

  it('deve renderizar os 4 quadrantes da matriz SWOT e brainstorming', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Forças (Strengths)');
    expect(compiled.textContent).toContain('Fraquezas (Weaknesses)');
    expect(compiled.textContent).toContain('Oportunidades (Opportunities)');
    expect(compiled.textContent).toContain('Ameaças (Threats)');
    expect(compiled.textContent).toContain('Sessão Livre de Brainstorming');
  });

  it('deve emitir addSwotItem e removeSwotItem', () => {
    const addSpy = jest.spyOn(component.addSwotItem, 'emit');
    const removeSpy = jest.spyOn(component.removeSwotItem, 'emit');

    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalledWith('strengths');

    const removeBtn = fixture.nativeElement.querySelectorAll('button')[1] as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith({ quadrant: 'strengths', index: 0 });
  });

  it('deve emitir addBrainstormingNote e removeBrainstormingNote', () => {
    const addNoteSpy = jest.spyOn(component.addBrainstormingNote, 'emit');
    const removeNoteSpy = jest.spyOn(component.removeBrainstormingNote, 'emit');

    const addNoteBtn = fixture.nativeElement.querySelector('button[type="button"].px-3') as HTMLButtonElement;
    addNoteBtn.click();
    expect(addNoteSpy).toHaveBeenCalled();

    const removeNoteBtn = fixture.nativeElement.querySelector('button[title="Remover anotação"]') as HTMLButtonElement;
    removeNoteBtn.click();
    expect(removeNoteSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem quando brainstorming não tiver notas', () => {
    component.brainstormingNotes = [];
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhuma ideia de brainstorming adicionada.');
  });
});
