import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GttRatesCardsComponent } from '../gtt-rates-cards.component';
import { GttMetricsDTO } from '../../../models/class-dashboard.model';

describe('GttRatesCardsComponent', () => {
  let component: GttRatesCardsComponent;
  let fixture: ComponentFixture<GttRatesCardsComponent>;

  const mockMetrics: GttMetricsDTO = {
    adverseEventsPer1000PatientDays: 14.5,
    adverseEventsPer100Admissions: 22.0,
    percentAdmissionsWithAdverseEvents: 18.0,
    totalPatientDays: 200,
    totalAdmissions: 40,
    totalAdverseEvents: 9,
    admissionsWithAdverseEvents: 7,
    harmDistribution: { E: 5, F: 4 },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GttRatesCardsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GttRatesCardsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('metrics', mockMetrics);
    fixture.detectChanges();
  });

  it('deve renderizar as taxas e métricas epidemiológicas corretamente', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('14.50');
    expect(compiled.textContent).toContain('22.00%');
    expect(compiled.textContent).toContain('18.0%');
    expect(compiled.textContent).toContain('9');
    expect(compiled.textContent).toContain('200 pacientes-dia auditados');
  });
});
