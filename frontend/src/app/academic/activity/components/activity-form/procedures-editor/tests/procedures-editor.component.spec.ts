import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ProceduresEditorComponent } from '../procedures-editor.component';

describe('ProceduresEditorComponent', () => {
  let component: ProceduresEditorComponent;
  let fixture: ComponentFixture<ProceduresEditorComponent>;
  let formArray: FormArray;

  beforeEach(async () => {
    formArray = new FormArray([
      new FormGroup({
        procedureName: new FormControl('Laparotomia'),
        description: new FormControl('Sem intercorrências'),
        date: new FormControl('2026-09-27')
      })
    ]);

    await TestBed.configureTestingModule({
      imports: [ProceduresEditorComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ProceduresEditorComponent);
    component = fixture.componentInstance;
    component.proceduresArray = formArray;
    fixture.detectChanges();
  });

  it('deve emitir addProcedure ao clicar no botão Adicionar', () => {
    const addSpy = jest.spyOn(component.addProcedure, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removeProcedure ao clicar em remover', () => {
    const removeSpy = jest.spyOn(component.removeProcedure, 'emit');
    const removeBtn = fixture.nativeElement.querySelector('button[title="Remover procedimento"]') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem de lista vazia quando formArray não tiver procedimentos', () => {
    formArray.clear();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum procedimento invasivo registrado.');
  });
});
