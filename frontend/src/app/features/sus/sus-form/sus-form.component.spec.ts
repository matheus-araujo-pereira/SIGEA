import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SusFormComponent } from './sus-form.component';
import { SusService } from '../../../core/services/sus.service';
import { AcademicClassService } from '../../../core/services/academic-class.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SusEvaluationResponseDTO } from '../../../core/models/sus.model';
import { AcademicClassResponseDTO } from '../../../core/models/academic-class.model';

describe('SusFormComponent', () => {
  let component: SusFormComponent;
  let fixture: ComponentFixture<SusFormComponent>;
  let susServiceSpy: jest.Mocked<SusService>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let authServiceSpy: jest.Mocked<AuthService>;
  let toastServiceSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockEvaluation: SusEvaluationResponseDTO = {
    id: 'eval-1',
    studentId: 'st-1',
    studentName: 'Matheus Araujo',
    studentEmail: 'matheus@academico.ufs.br',
    academicClassId: 'cls-1',
    className: 'Enfermagem - T01',
    q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
    score: 100.0,
    adjectiveRating: 'Melhor Imaginável',
    acceptability: 'Aceitável',
    gradeLevel: 'A',
    suggestions: 'Interface muito intuitiva.',
    createdAt: '2026-09-27T12:00:00Z'
  };

  const mockClass: AcademicClassResponseDTO = {
    id: 'cls-1',
    subjectName: 'Enfermagem Cirúrgica',
    classCode: 'T01',
    academicPeriod: '2026.1',
    professorId: 'prof-1',
    professorName: 'Prof. Ana',
    isClosed: false,
    enrolledCount: 30,
    activitiesCount: 2,
    createdAt: '2026-09-01T12:00:00Z'
  };

  beforeEach(async () => {
    susServiceSpy = {
      submitEvaluation: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockEvaluation })),
      calculatePreview: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: { score: 100, adjectiveRating: 'Melhor Imaginável', gradeScale: 'A', acceptability: 'Aceitável' }
      })),
      getMyEvaluation: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: null })),
      getMyEvaluations: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: [] })),
      getClassSummary: jest.fn(),
      getGeneralSummary: jest.fn(),
      downloadClassSusCsv: jest.fn(),
      downloadGlobalSusCsv: jest.fn(),
      saveBlob: jest.fn()
    } as unknown as jest.Mocked<SusService>;

    classServiceSpy = {
      getMyClasses: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: { content: [mockClass] } as any })),
    } as unknown as jest.Mocked<AcademicClassService>;

    authServiceSpy = {
      isStudent: jest.fn().mockReturnValue(true),
      isProfessor: jest.fn().mockReturnValue(false),
      isAdmin: jest.fn().mockReturnValue(false)
    } as unknown as jest.Mocked<AuthService>;

    toastServiceSpy = {
      success: jest.fn(),
      error: jest.fn(),
      warning: jest.fn(),
      info: jest.fn(),
      remove: jest.fn(),
      toasts: jest.fn()
    } as unknown as jest.Mocked<ToastService>;

    routerSpy = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [SusFormComponent],
      providers: [
        { provide: SusService, useValue: susServiceSpy },
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: jest.fn().mockReturnValue('cls-1')
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SusFormComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar com classId vindo da rota e carregar turmas', () => {
    component.ngOnInit();
    expect(component.selectedClassId()).toBe('cls-1');
    expect(classServiceSpy.getMyClasses).toHaveBeenCalled();
    expect(component.availableClasses()).toEqual([mockClass]);
    expect(component.currentClassName()).toBe('Enfermagem Cirúrgica - T01');
    expect(susServiceSpy.getMyEvaluation).toHaveBeenCalledWith('cls-1');
    expect(component.isLoading()).toBe(false);
  });

  it('deve inicializar sem queryParam classId', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.queryParamMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(component.selectedClassId()).toBeNull();
    expect(component.currentClassName()).toBeNull();
    expect(susServiceSpy.getMyEvaluation).toHaveBeenCalledWith(undefined);
  });

  it('deve tratar erro ao carregar turmas', () => {
    classServiceSpy.getMyClasses.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadInitialData();
    expect(component.availableClasses()).toEqual([]);
  });

  it('deve tratar erro ao verificar avaliação existente', () => {
    susServiceSpy.getMyEvaluation.mockReturnValue(throwError(() => new Error('Falha')));
    component.checkExistingEvaluation();
    expect(component.existingEvaluation()).toBeNull();
    expect(component.isLoading()).toBe(false);
  });

  it('deve retornar null em currentClassName quando turma selecionada não existir na lista', () => {
    component.selectedClassId.set('non-existent-class-id');
    expect(component.currentClassName()).toBeNull();
  });

  it('deve tratar resposta com data indefinido ao carregar turmas e avaliação', () => {
    classServiceSpy.getMyClasses.mockReturnValue(of({ success: true, message: 'OK', data: undefined as any }));
    susServiceSpy.getMyEvaluation.mockReturnValue(of({ success: true, message: 'OK', data: undefined as any }));

    component.loadInitialData();

    expect(component.availableClasses()).toEqual([]);
    expect(component.existingEvaluation()).toBeNull();
  });

  it('deve atualizar avaliação ao alterar turma selecionada', () => {
    component.selectedClassId.set('cls-2');
    component.onClassChange();
    expect(susServiceSpy.getMyEvaluation).toHaveBeenCalledWith('cls-2');
  });

  it('deve definir respostas nas 10 questões e validar limites', () => {
    component.setAnswer(1, 5);
    component.setAnswer(2, 2);
    component.setAnswer(10, 4);
    component.setAnswer(0, 5); // fora de limite
    component.setAnswer(11, 5); // fora de limite

    expect(component.answers()[0]).toBe(5);
    expect(component.answers()[1]).toBe(2);
    expect(component.answers()[9]).toBe(4);
    expect(component.answeredCount()).toBe(3);
  });

  it('deve calcular escore estimado e adjetivos em tempo real delegando ao backend', () => {
    expect(component.estimatedScore()).toBe(0);
    expect(component.estimatedRating()).toBe('');

    // Preenche 9 questões
    for (let i = 1; i <= 9; i++) {
      component.setAnswer(i, 5);
    }
    expect(component.answeredCount()).toBe(9);
    expect(susServiceSpy.calculatePreview).not.toHaveBeenCalled();

    // Preenche a 10ª questão
    component.setAnswer(10, 1);
    expect(component.answeredCount()).toBe(10);
    expect(susServiceSpy.calculatePreview).toHaveBeenCalled();
    expect(component.estimatedScore()).toBe(100);
    expect(component.estimatedRating()).toBe('Melhor Imaginável');

    // Trata erro no cálculo de preview
    susServiceSpy.calculatePreview.mockReturnValueOnce(throwError(() => new Error('Preview error')));
    component.setAnswer(10, 2);
    expect(component.previewData()).toBeNull();
    expect(component.estimatedScore()).toBe(0);
    expect(component.estimatedRating()).toBe('');
  });

  it('deve exibir aviso se tentar submeter sem responder as 10 questões', () => {
    component.answers.set([5, 1, 0, 0, 0, 0, 0, 0, 0, 0]);
    component.submitEvaluation();
    expect(toastServiceSpy.warning).toHaveBeenCalledWith('Por favor, responda às 10 questões antes de enviar.');
    expect(susServiceSpy.submitEvaluation).not.toHaveBeenCalled();
  });

  it('deve submeter avaliação completa com sucesso', () => {
    component.answers.set([5, 1, 5, 1, 5, 1, 5, 1, 5, 1]);
    component.suggestions = '   Ótimo sistema!   ';
    component.selectedClassId.set('cls-1');

    component.submitEvaluation();

    expect(susServiceSpy.submitEvaluation).toHaveBeenCalledWith({
      academicClassId: 'cls-1',
      q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
      suggestions: 'Ótimo sistema!'
    });
    expect(component.isSubmitting()).toBe(false);
    expect(component.existingEvaluation()).toEqual(mockEvaluation);
  });

  it('deve submeter avaliação sem sugestões (undefined)', () => {
    component.answers.set([5, 1, 5, 1, 5, 1, 5, 1, 5, 1]);
    component.suggestions = '   ';
    component.selectedClassId.set(null);

    component.submitEvaluation();

    expect(susServiceSpy.submitEvaluation).toHaveBeenCalledWith({
      academicClassId: null,
      q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
      suggestions: undefined
    });
  });

  it('deve tratar erro na submissão da avaliação', () => {
    component.answers.set([5, 1, 5, 1, 5, 1, 5, 1, 5, 1]);
    susServiceSpy.submitEvaluation.mockReturnValue(throwError(() => new Error('Erro servidor')));

    component.submitEvaluation();
    expect(component.isSubmitting()).toBe(false);
  });

  it('deve recuperar valores das respostas dadas no objeto de avaliação', () => {
    expect(component.getAnswerValue(mockEvaluation, 1)).toBe(5);
    expect(component.getAnswerValue(mockEvaluation, 2)).toBe(1);
    expect(component.getAnswerValue(mockEvaluation, 3)).toBe(5);
    expect(component.getAnswerValue(mockEvaluation, 4)).toBe(1);
    expect(component.getAnswerValue(mockEvaluation, 5)).toBe(5);
    expect(component.getAnswerValue(mockEvaluation, 6)).toBe(1);
    expect(component.getAnswerValue(mockEvaluation, 7)).toBe(5);
    expect(component.getAnswerValue(mockEvaluation, 8)).toBe(1);
    expect(component.getAnswerValue(mockEvaluation, 9)).toBe(5);
    expect(component.getAnswerValue(mockEvaluation, 10)).toBe(1);
    expect(component.getAnswerValue(mockEvaluation, 99)).toBe(0);
  });

  it('deve retornar classes de estilo corretas para respostas', () => {
    // Positiva desejável (>= 4)
    expect(component.getAnswerClass(5, true)).toContain('bg-emerald-100');
    // Negativa desejável (<= 2)
    expect(component.getAnswerClass(1, false)).toContain('bg-emerald-100');

    // Neutro (=== 3)
    expect(component.getAnswerClass(3, true)).toContain('bg-slate-100');
    expect(component.getAnswerClass(3, false)).toContain('bg-slate-100');

    // Indesejável
    expect(component.getAnswerClass(1, true)).toContain('bg-rose-100');
    expect(component.getAnswerClass(5, false)).toContain('bg-rose-100');
  });

  it('deve navegar de volta dependendo do contexto', () => {
    // Caso com turma
    component.selectedClassId.set('cls-1');
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'cls-1']);

    // Caso estudante sem turma
    component.selectedClassId.set(null);
    authServiceSpy.isStudent.mockReturnValue(true);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/student/activities']);

    // Caso outro perfil sem turma
    authServiceSpy.isStudent.mockReturnValue(false);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/gtt/modules']);
  });
});
