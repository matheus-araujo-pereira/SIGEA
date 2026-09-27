import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SusDashboardComponent } from './sus-dashboard.component';
import { SusService } from '../../services/sus.service';
import { ToastService } from '../../../../common/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SusClassSummaryDTO, SusGeneralSummaryDTO, SusEvaluationResponseDTO } from '../../models/sus.model';

describe('SusDashboardComponent', () => {
  let component: SusDashboardComponent;
  let fixture: ComponentFixture<SusDashboardComponent>;
  let susServiceSpy: jest.Mocked<SusService>;
  let toastServiceSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockEvaluation: SusEvaluationResponseDTO = {
    id: 'eval-1',
    studentId: 'st-1',
    studentName: 'Ana Souza',
    studentEmail: 'ana@academico.ufs.br',
    academicClassId: 'cls-1',
    className: 'Enfermagem - T01',
    q1: 5, q2: 1, q3: 5, q4: 1, q5: 5, q6: 1, q7: 5, q8: 1, q9: 5, q10: 1,
    score: 100.0,
    adjectiveRating: 'Melhor Imaginável',
    acceptability: 'Aceitável',
    gradeLevel: 'A',
    suggestions: 'Excelente usabilidade.',
    createdAt: '2026-09-27T12:00:00Z',
  };

  const mockClassSummary: SusClassSummaryDTO = {
    classId: 'cls-1',
    className: 'Enfermagem Cirúrgica - T01',
    enrolledStudentsCount: 30,
    totalEvaluations: 1,
    responseRatePercentage: 3.33,
    averageScore: 100.0,
    adjectiveRating: 'Melhor Imaginável',
    acceptability: 'Aceitável',
    gradeLevel: 'A',
    adjectiveDistribution: { 'Melhor Imaginável': 1 },
    questionAverages: [5.0, 1.0, 5.0, 1.0, 5.0, 1.0, 5.0, 1.0, 5.0, 1.0],
    evaluations: [mockEvaluation],
  };

  const mockGeneralSummary: SusGeneralSummaryDTO = {
    totalEvaluations: 5,
    averageScore: 78.5,
    adjectiveRating: 'Bom',
    acceptability: 'Aceitável',
    gradeLevel: 'B',
    adjectiveDistribution: { 'Bom': 5 },
    questionAverages: [4.2, 2.1, 4.0, 2.3, 4.1, 2.0, 4.3, 2.2, 4.0, 2.1],
  };

  beforeEach(async () => {
    susServiceSpy = {
      getClassSummary: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockClassSummary })),
      getGeneralSummary: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockGeneralSummary })),
      downloadClassSusCsv: jest.fn().mockReturnValue(of(new Blob(['csv data'], { type: 'text/csv' }))),
      downloadGlobalSusCsv: jest.fn().mockReturnValue(of(new Blob(['csv data'], { type: 'text/csv' }))),
      saveBlob: jest.fn(),
      submitEvaluation: jest.fn(),
      getMyEvaluation: jest.fn(),
      getMyEvaluations: jest.fn(),
      calculatePreview: jest.fn(),
    } as unknown as jest.Mocked<SusService>;

    toastServiceSpy = {
      success: jest.fn(),
      error: jest.fn(),
      warning: jest.fn(),
      info: jest.fn(),
      remove: jest.fn(),
      toasts: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    routerSpy = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [SusDashboardComponent],
      providers: [
        { provide: SusService, useValue: susServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('cls-1'),
              },
              queryParamMap: {
                get: jest.fn().mockReturnValue(null),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SusDashboardComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar em modo turma com paramMap classId', () => {
    component.ngOnInit();
    expect(component.classId()).toBe('cls-1');
    expect(component.isClassMode()).toBe(true);
    expect(susServiceSpy.getClassSummary).toHaveBeenCalledWith('cls-1');
    expect(component.classSummary()).toEqual(mockClassSummary);
    expect(component.totalEvaluations()).toBe(1);
    expect(component.averageScore()).toBe(100.0);
    expect(component.adjectiveRating()).toBe('Melhor Imaginável');
    expect(component.acceptability()).toBe('Aceitável');
    expect(component.gradeLevel()).toBe('A');
    expect(component.evaluationsList()).toEqual([mockEvaluation]);
    expect(component.questionAverages()).toEqual(mockClassSummary.questionAverages);
    expect(component.isLoading()).toBe(false);
  });

  it('deve inicializar em modo turma com queryParamMap classId', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);
    jest.spyOn(route.snapshot.queryParamMap, 'get').mockReturnValue('cls-query');

    component.ngOnInit();
    expect(component.classId()).toBe('cls-query');
    expect(susServiceSpy.getClassSummary).toHaveBeenCalledWith('cls-query');
  });

  it('deve inicializar em modo geral quando não houver classId', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);
    jest.spyOn(route.snapshot.queryParamMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(component.classId()).toBeNull();
    expect(component.isClassMode()).toBe(false);
    expect(susServiceSpy.getGeneralSummary).toHaveBeenCalled();
    expect(component.generalSummary()).toEqual(mockGeneralSummary);
    expect(component.totalEvaluations()).toBe(5);
    expect(component.averageScore()).toBe(78.5);
    expect(component.adjectiveRating()).toBe('Bom');
    expect(component.acceptability()).toBe('Aceitável');
    expect(component.gradeLevel()).toBe('B');
    expect(component.questionAverages()).toEqual(mockGeneralSummary.questionAverages);
  });

  it('deve tratar erro ao carregar dados da turma', () => {
    component.classId.set('cls-1');
    susServiceSpy.getClassSummary.mockReturnValue(throwError(() => new Error('Erro API')));
    component.loadData();
    expect(component.classSummary()).toBeNull();
    expect(component.isLoading()).toBe(false);
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Falha ao carregar indicadores SUS da turma.');
  });

  it('deve tratar erro ao carregar dados gerais', () => {
    component.classId.set(null);
    susServiceSpy.getGeneralSummary.mockReturnValue(throwError(() => new Error('Erro API')));
    component.loadData();
    expect(component.generalSummary()).toBeNull();
    expect(component.isLoading()).toBe(false);
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Falha ao carregar indicadores SUS gerais.');
  });

  it('deve tratar res.data indefinido ao carregar turma ou geral', () => {
    component.classId.set('cls-1');
    susServiceSpy.getClassSummary.mockReturnValue(of({ success: true, message: 'OK', data: undefined as any }));
    component.loadData();
    expect(component.classSummary()).toBeNull();

    component.classId.set(null);
    susServiceSpy.getGeneralSummary.mockReturnValue(of({ success: true, message: 'OK', data: undefined as any }));
    component.loadData();
    expect(component.generalSummary()).toBeNull();
  });

  it('deve retornar valores padrão quando resumos forem nulos', () => {
    component.classId.set('cls-1');
    component.classSummary.set(null);
    expect(component.totalEvaluations()).toBe(0);
    expect(component.averageScore()).toBe(0);
    expect(component.adjectiveRating()).toBe('—');
    expect(component.acceptability()).toBe('—');
    expect(component.gradeLevel()).toBe('—');
    expect(component.evaluationsList()).toEqual([]);
    expect(component.questionAverages()).toEqual([]);

    component.classId.set(null);
    component.generalSummary.set(null);
    expect(component.totalEvaluations()).toBe(0);
    expect(component.averageScore()).toBe(0);
    expect(component.adjectiveRating()).toBe('—');
    expect(component.acceptability()).toBe('—');
    expect(component.gradeLevel()).toBe('—');
    expect(component.questionAverages()).toEqual([]);
  });

  it('deve exportar CSV de turma com sucesso', () => {
    component.classId.set('cls-1');
    component.downloadCsv();

    expect(susServiceSpy.downloadClassSusCsv).toHaveBeenCalledWith('cls-1');
    expect(susServiceSpy.saveBlob).toHaveBeenCalledWith(expect.any(Blob), 'sigea_sus_turma_cls-1.csv');
    expect(toastServiceSpy.success).toHaveBeenCalledWith('Base de dados SUS (CSV) exportada com sucesso.');
    expect(component.isDownloading()).toBe(false);
  });

  it('deve tratar erro ao exportar CSV de turma', () => {
    component.classId.set('cls-1');
    susServiceSpy.downloadClassSusCsv.mockReturnValue(throwError(() => new Error('Erro')));

    component.downloadCsv();
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Falha ao exportar CSV de usabilidade da turma.');
    expect(component.isDownloading()).toBe(false);
  });

  it('deve exportar CSV geral com sucesso', () => {
    component.classId.set(null);
    component.downloadCsv();

    expect(susServiceSpy.downloadGlobalSusCsv).toHaveBeenCalled();
    expect(susServiceSpy.saveBlob).toHaveBeenCalledWith(expect.any(Blob), 'sigea_sus_geral.csv');
    expect(toastServiceSpy.success).toHaveBeenCalledWith('Base de dados SUS geral (CSV) exportada com sucesso.');
    expect(component.isDownloading()).toBe(false);
  });

  it('deve tratar erro ao exportar CSV geral', () => {
    component.classId.set(null);
    susServiceSpy.downloadGlobalSusCsv.mockReturnValue(throwError(() => new Error('Erro')));

    component.downloadCsv();
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Falha ao exportar CSV geral de usabilidade.');
    expect(component.isDownloading()).toBe(false);
  });

  it('deve navegar de volta dependendo do modo', () => {
    component.classId.set('cls-1');
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'cls-1', 'dashboard']);

    component.classId.set(null);
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/admin/users']);
  });
});
