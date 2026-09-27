import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PedagogicalMetricsCardComponent } from './pedagogical-metrics-card.component';
import { PedagogicalMetricsDTO } from '../../models/class-dashboard.model';

describe('PedagogicalMetricsCardComponent', () => {
  let component: PedagogicalMetricsCardComponent;
  let fixture: ComponentFixture<PedagogicalMetricsCardComponent>;

  const mockMetrics: PedagogicalMetricsDTO = {
    classAverageGrade: 8.75,
    totalEnrolledStudents: 25,
    totalActivities: 4,
    totalSubmissions: 30,
    gradedSubmissions: 28,
    pendingGradingSubmissions: 2,
    topIdentifiedTriggers: [],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedagogicalMetricsCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PedagogicalMetricsCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('metrics', mockMetrics);
    fixture.detectChanges();
  });

  it('deve renderizar as métricas pedagógicas com estilo verde para média >= 7', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('8.75');
    expect(compiled.textContent).toContain('25');
    expect(compiled.textContent).toContain('4');
    expect(compiled.textContent).toContain('28');
    expect(compiled.textContent).toContain('2');

    const gradeEl = compiled.querySelector('.text-2xl');
    expect(gradeEl?.classList.contains('text-emerald-700')).toBe(true);
  });

  it('deve aplicar estilo âmbar para média menor que 7', () => {
    fixture.componentRef.setInput('metrics', { ...mockMetrics, classAverageGrade: 5.5 });
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const gradeEl = compiled.querySelector('.text-2xl');
    expect(gradeEl?.classList.contains('text-amber-700')).toBe(true);
  });
});
