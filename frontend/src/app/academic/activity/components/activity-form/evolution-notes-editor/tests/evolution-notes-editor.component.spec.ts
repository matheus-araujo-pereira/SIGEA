import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { EvolutionNotesEditorComponent } from '../evolution-notes-editor.component';

describe('EvolutionNotesEditorComponent', () => {
  let component: EvolutionNotesEditorComponent;
  let fixture: ComponentFixture<EvolutionNotesEditorComponent>;
  let formArray: FormArray;

  beforeEach(async () => {
    formArray = new FormArray([
      new FormGroup({
        dateTime: new FormControl('2026-09-27 10:00'),
        professionalRole: new FormControl('Enfermeiro'),
        note: new FormControl('Paciente estável.')
      })
    ]);

    await TestBed.configureTestingModule({
      imports: [EvolutionNotesEditorComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(EvolutionNotesEditorComponent);
    component = fixture.componentInstance;
    component.evolutionNotesArray = formArray;
    fixture.detectChanges();
  });

  it('deve emitir addNote ao clicar no botão Adicionar Evolução', () => {
    const addSpy = jest.spyOn(component.addNote, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button[type="button"]') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removeNote com o índice correspondente ao remover', () => {
    const removeSpy = jest.spyOn(component.removeNote, 'emit');
    const removeBtn = fixture.nativeElement.querySelector('button[title="Remover anotação"]') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem de lista vazia quando formArray não tiver elementos', () => {
    formArray.clear();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhuma nota de evolução adicionada.');
  });
});
