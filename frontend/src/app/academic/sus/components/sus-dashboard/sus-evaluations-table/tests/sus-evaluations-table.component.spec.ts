import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SusEvaluationsTableComponent } from '../sus-evaluations-table.component';
import { SusEvaluationResponseDTO } from '../../../../models/sus.model';

describe('SusEvaluationsTableComponent', () => {
  let component: SusEvaluationsTableComponent;
  let fixture: ComponentFixture<SusEvaluationsTableComponent>;

  const mockEvaluations: SusEvaluationResponseDTO[] = [
    {
      id: 'e1',
      studentId: 's1',
      studentName: 'Ana Clara',
      studentEmail: 'ana@academico.ufs.br',
      q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
      score: 95.0,
      adjectiveRating: 'Melhor Imaginável',
      acceptability: 'Aceitável',
      gradeLevel: 'A',
      suggestions: 'Gostei muito da experiência!',
      createdAt: '2026-09-25T10:00:00Z',
    },
    {
      id: 'e2',
      studentId: 's2',
      studentName: 'Carlos Lima',
      studentEmail: 'carlos@academico.ufs.br',
      q1: 3, q2: 3, q3: 3, q4: 3, q5: 3, q6: 3, q7: 3, q8: 3, q9: 3, q10: 3,
      score: 50.0,
      adjectiveRating: 'Regular',
      acceptability: 'Marginal',
      gradeLevel: 'D',
      createdAt: '2026-09-26T14:00:00Z',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SusEvaluationsTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SusEvaluationsTableComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('evaluations', mockEvaluations);
    fixture.detectChanges();
  });

  it('deve listar as avaliações dos estudantes na tabela', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Ana Clara');
    expect(compiled.textContent).toContain('ana@academico.ufs.br');
    expect(compiled.textContent).toContain('95.0');
    expect(compiled.textContent).toContain('Carlos Lima');
    expect(compiled.textContent).toContain('50.0');
    expect(compiled.textContent).toContain('Ver Sugestão');
  });

  it('deve abrir e fechar modal de sugestão qualitativa', () => {
    component.openSuggestion('Sugestão de teste detalhada');
    fixture.detectChanges();

    expect(component.selectedSuggestion()).toBe('Sugestão de teste detalhada');
    let compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Comentários e Sugestões Qualitativas');
    expect(compiled.textContent).toContain('Sugestão de teste detalhada');

    component.closeSuggestion();
    fixture.detectChanges();

    expect(component.selectedSuggestion()).toBeNull();
    compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).not.toContain('Comentários e Sugestões Qualitativas');
  });

  it('deve testar os helpers de escore e badge', () => {
    expect(component.getScoreTextColor(90)).toBe('text-emerald-600');
    expect(component.getScoreTextColor(75)).toBe('text-blue-600');
    expect(component.getScoreTextColor(60)).toBe('text-amber-600');
    expect(component.getScoreTextColor(40)).toBe('text-rose-600');

    expect(component.getScoreBadgeClass(90)).toContain('bg-emerald-100');
    expect(component.getScoreBadgeClass(75)).toContain('bg-blue-100');
    expect(component.getScoreBadgeClass(60)).toContain('bg-amber-100');
    expect(component.getScoreBadgeClass(40)).toContain('bg-rose-100');
  });
});
