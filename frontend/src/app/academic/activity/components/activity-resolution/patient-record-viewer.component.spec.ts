import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PatientRecordViewerComponent } from './patient-record-viewer.component';
import { ClinicalCaseData } from '../../models/activity.model';

describe('PatientRecordViewerComponent', () => {
  let component: PatientRecordViewerComponent;
  let fixture: ComponentFixture<PatientRecordViewerComponent>;

  const mockCase: ClinicalCaseData = {
    patientName: 'Severino Silva',
    age: 55,
    gender: 'Masculino',
    bed: 'Leito 12',
    admissionDate: '10/09/2026',
    patientDays: 4,
    admissionNotes: 'Paciente admitido com dispneia',
    evolutionNotes: [{ dateTime: '11/09 08:00', professionalRole: 'Enfermeiro', note: 'Evolução estável' }],
    prescriptions: [{ medication: 'Ceftriaxona', dosage: '1g', route: 'EV', frequency: '12/12h', administrationCheck: 'Checado' }],
    labExams: [{ examName: 'PCR', result: '12 mg/L', referenceValue: '< 5 mg/L', date: '11/09' }],
    procedures: [{ procedureName: 'Toracocentese', description: 'Drenagem pleural', date: '11/09' }]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientRecordViewerComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(PatientRecordViewerComponent);
    component = fixture.componentInstance;
    component.clinicalCase = mockCase;
    component.patientInitial = 'S';
    fixture.detectChanges();
  });

  it('deve renderizar dados do paciente e seções do prontuário', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Severino Silva');
    expect(compiled.textContent).toContain('55 anos');
    expect(compiled.textContent).toContain('Ceftriaxona');
    expect(compiled.textContent).toContain('PCR');
    expect(compiled.textContent).toContain('Toracocentese');
  });

  it('deve exibir mensagens de seções vazias quando não houver dados', () => {
    component.clinicalCase = { patientName: 'Vazio' };
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhuma anotação de evolução cadastrada.');
    expect(compiled.textContent).toContain('Nenhum medicamento prescrito registrado.');
    expect(compiled.textContent).toContain('Sem exames registrados.');
    expect(compiled.textContent).toContain('Sem procedimentos registrados.');
  });
});
