import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarmDistributionCardComponent } from '../harm-distribution-card.component';
import { GttMetricsDTO } from '../../../models/class-dashboard.model';

describe('HarmDistributionCardComponent', () => {
  let component: HarmDistributionCardComponent;
  let fixture: ComponentFixture<HarmDistributionCardComponent>;

  const mockMetrics: GttMetricsDTO = {
    adverseEventsPer1000PatientDays: 10,
    adverseEventsPer100Admissions: 20,
    percentAdmissionsWithAdverseEvents: 15,
    totalPatientDays: 100,
    totalAdmissions: 20,
    totalAdverseEvents: 4,
    admissionsWithAdverseEvents: 3,
    harmDistribution: { E: 2, F: 1, G: 1, H: 0, I: 0 },
    harmPercentages: { E: 50, F: 25, G: 25, H: 0, I: 0 },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HarmDistributionCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HarmDistributionCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('metrics', mockMetrics);
    fixture.detectChanges();
  });

  it('deve renderizar a distribuição dos danos por gravidade', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Total: 4 Danos');
    expect(compiled.textContent).toContain('Categoria E');
    expect(compiled.textContent).toContain('2 (50%)');
  });

  it('deve retornar 0 quando o total de eventos for zero ou a letra não existir', () => {
    expect(component.getHarmCount('Z')).toBe(0);

    fixture.componentRef.setInput('metrics', {
      ...mockMetrics,
      totalAdverseEvents: 0,
      harmPercentages: undefined,
    });
    fixture.detectChanges();

    expect(component.getHarmPercentage('E')).toBe(0);

    fixture.componentRef.setInput('metrics', {
      ...mockMetrics,
      totalAdverseEvents: 4,
      harmPercentages: undefined,
    });
    fixture.detectChanges();
    expect(component.getHarmPercentage('E')).toBe(0);

    fixture.componentRef.setInput('metrics', {
      ...mockMetrics,
      totalAdverseEvents: 4,
      harmPercentages: { E: 50 },
    });
    fixture.detectChanges();
    expect(component.getHarmPercentage('Z')).toBe(0);
  });
});
