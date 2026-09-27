import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PatientDemographicsFormComponent } from '../patient-demographics-form.component';

describe('PatientDemographicsFormComponent', () => {
  let component: PatientDemographicsFormComponent;
  let fixture: ComponentFixture<PatientDemographicsFormComponent>;
  let clinicalCaseGroup: FormGroup;

  beforeEach(async () => {
    clinicalCaseGroup = new FormGroup({
      patientName: new FormControl('', Validators.required),
      age: new FormControl(null),
      gender: new FormControl('Feminino'),
      bed: new FormControl(''),
      admissionDate: new FormControl(''),
      patientDays: new FormControl(1, [Validators.required, Validators.min(1)]),
      admissionNotes: new FormControl('')
    });

    await TestBed.configureTestingModule({
      imports: [PatientDemographicsFormComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PatientDemographicsFormComponent);
    component = fixture.componentInstance;
    component.clinicalCaseGroup = clinicalCaseGroup;
    fixture.detectChanges();
  });

  it('deve renderizar campos de identificação do paciente', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[formControlName="patientName"]')).toBeTruthy();
    expect(compiled.querySelector('select[formControlName="gender"]')).toBeTruthy();
    expect(compiled.querySelector('input[formControlName="patientDays"]')).toBeTruthy();
  });
});
