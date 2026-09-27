import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivityInfoFormComponent } from './activity-info-form.component';

describe('ActivityInfoFormComponent', () => {
  let component: ActivityInfoFormComponent;
  let fixture: ComponentFixture<ActivityInfoFormComponent>;
  let testForm: FormGroup;

  beforeEach(async () => {
    testForm = new FormGroup({
      classId: new FormControl('', Validators.required),
      title: new FormControl('', Validators.required),
      deadline: new FormControl('', Validators.required),
      description: new FormControl('', Validators.required)
    });

    await TestBed.configureTestingModule({
      imports: [ActivityInfoFormComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityInfoFormComponent);
    component = fixture.componentInstance;
    component.form = testForm;
    component.classes = [{ id: 'c1', formattedName: 'Turma A', code: 'T1', academicPeriod: '2026.1', active: true, studentCount: 10, activityCount: 1 }];
    fixture.detectChanges();
  });

  it('deve renderizar campos de turma, título, prazo e descrição', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('select')).toBeTruthy();
    expect(compiled.querySelector('input[type="datetime-local"]')).toBeTruthy();
    expect(compiled.querySelector('textarea')).toBeTruthy();
  });

  it('não deve exibir campo de turma em modo de edição', () => {
    component.isEditMode = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('select')).toBeNull();
  });

  it('deve exibir mensagens de erro quando campos forem tocados e inválidos', () => {
    testForm.get('classId')?.markAsTouched();
    testForm.get('title')?.markAsTouched();
    testForm.get('deadline')?.markAsTouched();
    testForm.get('description')?.markAsTouched();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Selecione uma turma');
    expect(compiled.textContent).toContain('Título obrigatório');
    expect(compiled.textContent).toContain('Informe uma data futura');
    expect(compiled.textContent).toContain('Orientações obrigatórias');
  });
});
