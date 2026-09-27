import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GradingStudentCardComponent } from '../grading-student-card.component';
import { SubmissionResponseDTO } from '../../../../models/activity.model';

describe('GradingStudentCardComponent', () => {
  let component: GradingStudentCardComponent;
  let fixture: ComponentFixture<GradingStudentCardComponent>;

  const mockSubmission: SubmissionResponseDTO = {
    id: 'sub-1',
    activityId: 'act-1',
    activityTitle: 'Atividade 1',
    studentId: 'st-1',
    studentName: 'João da Silva',
    studentEmail: 'joao@academico.ufs.br',
    submissionDate: '2026-09-27T10:00:00Z',
    isGraded: true,
    grade: 8.5,
    identifiedTriggers: []
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GradingStudentCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(GradingStudentCardComponent);
    component = fixture.componentInstance;
    component.submission = mockSubmission;
    fixture.detectChanges();
  });

  it('deve criar o componente e renderizar os dados do estudante', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('João da Silva');
    expect(compiled.textContent).toContain('joao@academico.ufs.br');
    expect(compiled.textContent).toContain('Nota Atual: 8.50');
  });

  it('deve exibir status pendente quando não corrigido', () => {
    component.submission = {
      ...mockSubmission,
      isGraded: false,
      grade: undefined
    };
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Pendente de Nota');
  });
});
