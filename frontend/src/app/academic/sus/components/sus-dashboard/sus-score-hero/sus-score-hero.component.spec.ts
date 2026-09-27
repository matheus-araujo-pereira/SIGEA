import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SusScoreHeroComponent } from './sus-score-hero.component';

describe('SusScoreHeroComponent', () => {
  let component: SusScoreHeroComponent;
  let fixture: ComponentFixture<SusScoreHeroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SusScoreHeroComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SusScoreHeroComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('averageScore', 88.5);
    fixture.componentRef.setInput('adjectiveRating', 'Melhor Imaginável');
    fixture.componentRef.setInput('acceptability', 'Aceitável');
    fixture.componentRef.setInput('gradeLevel', 'A');
    fixture.componentRef.setInput('totalEvaluations', 24);
    fixture.componentRef.setInput('isClassMode', true);
    fixture.componentRef.setInput('enrolledStudentsCount', 30);
    fixture.componentRef.setInput('responseRatePercentage', 80.0);
    fixture.detectChanges();
  });

  it('deve renderizar os indicadores principais da escala SUS', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('88.5');
    expect(compiled.textContent).toContain('Melhor Imaginável');
    expect(compiled.textContent).toContain('Aceitável');
    expect(compiled.textContent).toContain('Grau A');
    expect(compiled.textContent).toContain('24');
    expect(compiled.textContent).toContain('/ 30 alunos');
    expect(compiled.textContent).toContain('Taxa de resposta: 80.0%');
  });

  it('deve formatar indicadores para modo geral institucional (sem turma)', () => {
    fixture.componentRef.setInput('isClassMode', false);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Total de Respondentes');
    expect(compiled.textContent).toContain('respostas');
    expect(compiled.textContent).toContain('Amostra acumulada para análise estatística');
  });

  it('deve retornar cores e badges corretos para diferentes faixas de pontuação', () => {
    // Score >= 85
    expect(component.getScoreTextColor(90)).toBe('text-emerald-600');
    expect(component.getScoreBadgeClass(90)).toContain('bg-emerald-100');

    // Score 70-84
    expect(component.getScoreTextColor(75)).toBe('text-blue-600');
    expect(component.getScoreBadgeClass(75)).toContain('bg-blue-100');

    // Score 50-69
    expect(component.getScoreTextColor(60)).toBe('text-amber-600');
    expect(component.getScoreBadgeClass(60)).toContain('bg-amber-100');

    // Score < 50
    expect(component.getScoreTextColor(40)).toBe('text-rose-600');
    expect(component.getScoreBadgeClass(40)).toContain('bg-rose-100');
  });
});
