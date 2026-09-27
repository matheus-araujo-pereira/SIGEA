import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { GradingFeedbackFormComponent } from '../grading-feedback-form.component';

describe('GradingFeedbackFormComponent', () => {
  let component: GradingFeedbackFormComponent;
  let fixture: ComponentFixture<GradingFeedbackFormComponent>;
  let testForm: FormGroup;

  beforeEach(async () => {
    testForm = new FormGroup({
      grade: new FormControl(null, [Validators.required, Validators.min(0), Validators.max(10)]),
      pedagogicalFeedback: new FormControl('', [Validators.required, Validators.minLength(10)])
    });

    await TestBed.configureTestingModule({
      imports: [GradingFeedbackFormComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(GradingFeedbackFormComponent);
    component = fixture.componentInstance;
    component.gradeForm = testForm;
    fixture.detectChanges();
  });

  it('deve criar o formulário e disparar submitGrade ao submeter', () => {
    const submitSpy = jest.spyOn(component.submitGrade, 'emit');
    component.onSubmit();
    expect(submitSpy).toHaveBeenCalled();
  });

  it('deve disparar goBack ao clicar no botão Voltar', () => {
    const backSpy = jest.spyOn(component.goBack, 'emit');
    const backBtn = fixture.nativeElement.querySelector('button[type="button"]') as HTMLButtonElement;
    backBtn.click();
    expect(backSpy).toHaveBeenCalled();
  });

  it('deve exibir texto de atualização quando isGraded for verdadeiro', () => {
    component.isGraded = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Atualizar Avaliação');
  });

  it('deve exibir indicador de carregamento quando isSubmitting for verdadeiro', () => {
    component.isSubmitting = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Salvando Avaliação...');
  });
});
