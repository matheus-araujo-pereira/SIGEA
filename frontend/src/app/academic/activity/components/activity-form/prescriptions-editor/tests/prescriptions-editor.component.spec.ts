import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PrescriptionsEditorComponent } from '../prescriptions-editor.component';

describe('PrescriptionsEditorComponent', () => {
  let component: PrescriptionsEditorComponent;
  let fixture: ComponentFixture<PrescriptionsEditorComponent>;
  let formArray: FormArray;

  beforeEach(async () => {
    formArray = new FormArray([
      new FormGroup({
        medication: new FormControl('Dipirona'),
        dosage: new FormControl('1g'),
        route: new FormControl('EV'),
        frequency: new FormControl('6/6h'),
        administrationCheck: new FormControl('Administrado')
      })
    ]);

    await TestBed.configureTestingModule({
      imports: [PrescriptionsEditorComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PrescriptionsEditorComponent);
    component = fixture.componentInstance;
    component.prescriptionsArray = formArray;
    fixture.detectChanges();
  });

  it('deve emitir addPrescription ao clicar no botão', () => {
    const addSpy = jest.spyOn(component.addPrescription, 'emit');
    const addBtn = fixture.nativeElement.querySelector('button[type="button"]') as HTMLButtonElement;
    addBtn.click();
    expect(addSpy).toHaveBeenCalled();
  });

  it('deve emitir removePrescription ao remover um item', () => {
    const removeSpy = jest.spyOn(component.removePrescription, 'emit');
    const removeBtn = fixture.nativeElement.querySelector('button[title="Remover medicamento"]') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem de lista vazia quando formArray não tiver elementos', () => {
    formArray.clear();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum medicamento prescrito cadastrado.');
  });
});
