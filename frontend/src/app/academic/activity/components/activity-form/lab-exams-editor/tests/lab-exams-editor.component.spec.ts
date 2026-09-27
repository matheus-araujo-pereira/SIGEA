import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LabExamsEditorComponent } from '../lab-exams-editor.component';

describe('LabExamsEditorComponent', () => {
  let component: LabExamsEditorComponent;
  let fixture: ComponentFixture<LabExamsEditorComponent>;
  let formArray: FormArray;

  beforeEach(async () => {
    formArray = new FormArray([
      new FormGroup({
        examName: new FormControl('Hemoglobina'),
        result: new FormControl('7.0 g/dL'),
        referenceValue: new FormControl('12 - 16 g/dL'),
        date: new FormControl('2026-09-27 08:00')
      })
    ]);

    await TestBed.configureTestingModule({
      imports: [LabExamsEditorComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(LabExamsEditorComponent);
    component = fixture.componentInstance;
    component.labExamsArray = formArray;
    fixture.detectChanges();
  });

  it('deve emitir addExam ao clicar no botão Adicionar', () => {
    const addSpy = jest.spyOn(component.addExam, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removeExam ao clicar em remover', () => {
    const removeSpy = jest.spyOn(component.removeExam, 'emit');
    const removeBtn = fixture.nativeElement.querySelector('button[title="Remover exame"]') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem de lista vazia quando formArray não tiver exames', () => {
    formArray.clear();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum exame laboratorial registrado.');
  });
});
