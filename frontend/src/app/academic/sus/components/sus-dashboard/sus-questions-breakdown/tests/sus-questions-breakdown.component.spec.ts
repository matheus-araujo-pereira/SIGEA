import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SusQuestionsBreakdownComponent } from '../sus-questions-breakdown.component';
import { SUS_QUESTIONS } from '../../../../models/sus.model';

describe('SusQuestionsBreakdownComponent', () => {
  let component: SusQuestionsBreakdownComponent;
  let fixture: ComponentFixture<SusQuestionsBreakdownComponent>;

  const mockAverages = [4.8, 1.2, 4.5, 1.5, 4.6, 1.4, 4.7, 1.3, 4.9, 1.1];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SusQuestionsBreakdownComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SusQuestionsBreakdownComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('questions', SUS_QUESTIONS);
    fixture.componentRef.setInput('questionAverages', mockAverages);
    fixture.detectChanges();
  });

  it('deve renderizar as 10 questões com suas respectivas médias', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('4.80');
    expect(compiled.textContent).toContain('1.20');
    expect(compiled.textContent).toContain('Aspecto Positivo');
    expect(compiled.textContent).toContain('Aspecto Negativo');
  });

  it('deve retornar 0 para questionId fora dos limites', () => {
    expect(component.getQuestionAverage(0)).toBe(0);
    expect(component.getQuestionAverage(99)).toBe(0);

    fixture.componentRef.setInput('questionAverages', []);
    fixture.detectChanges();
    expect(component.getQuestionAverage(1)).toBe(0);
  });

  it('deve categorizar cores e classes de barras por desejável, neutro e atenção', () => {
    // Positiva: Desejável (>= 3.5), Neutro (2.5 a 3.5), Atenção (<= 2.5)
    expect(component.getQuestionColor(4.0, true)).toBe('text-emerald-600');
    expect(component.getQuestionBarClass(4.0, true)).toBe('bg-emerald-500');

    expect(component.getQuestionColor(3.0, true)).toBe('text-slate-600');
    expect(component.getQuestionBarClass(3.0, true)).toBe('bg-slate-400');

    expect(component.getQuestionColor(2.0, true)).toBe('text-rose-600');
    expect(component.getQuestionBarClass(2.0, true)).toBe('bg-rose-500');

    // Negativa: Desejável (<= 2.5), Neutro (2.5 a 3.5), Atenção (>= 3.5)
    expect(component.getQuestionColor(1.5, false)).toBe('text-emerald-600');
    expect(component.getQuestionBarClass(1.5, false)).toBe('bg-emerald-500');

    expect(component.getQuestionColor(3.0, false)).toBe('text-slate-600');
    expect(component.getQuestionBarClass(3.0, false)).toBe('bg-slate-400');

    expect(component.getQuestionColor(4.5, false)).toBe('text-rose-600');
    expect(component.getQuestionBarClass(4.5, false)).toBe('bg-rose-500');
  });
});
